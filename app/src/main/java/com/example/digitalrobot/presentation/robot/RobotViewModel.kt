package com.example.digitalrobot.presentation.robot

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalrobot.R
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.usecase.LanguageModelUseCase
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.domain.usecase.SpeechToTextUseCase
import com.example.digitalrobot.domain.usecase.TextToSpeechUseCase
import com.example.digitalrobot.util.Constants.Robot
import com.example.digitalrobot.util.Constants.Mqtt
import com.example.digitalrobot.util.getNestedValueFromLinkedTreeMap
import com.example.digitalrobot.util.getPropertyFromJsonString
import com.example.digitalrobot.util.getValueFromLinkedTreeMap
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val mqttUseCase: MqttUseCase,
    private val textToSpeechUseCase: TextToSpeechUseCase,
    private val speechToTextUseCase: SpeechToTextUseCase,
    private val languageModelUseCase: LanguageModelUseCase
): ViewModel() {

    private val _state = MutableStateFlow(RobotState())
    val state: StateFlow<RobotState> = _state.asStateFlow()

    private fun showToast(message: String) {
        val currentMessages = _state.value.toastMsgs.toMutableList()
        currentMessages.add(message)
        _state.value = _state.value.copy(toastMsgs = currentMessages)
    }

    private fun clearToast() {
        _state.value = _state.value.copy(toastMsgs = emptyList())
    }

    fun setMacAddress(macAddress: String) {
        _state.value = _state.value.copy(deviceId = macAddress)
    }

    fun onEvent(event: RobotEvent) {
        when (event) {
            is RobotEvent.ClearToastMsg -> {
                clearToast()
            }
            is RobotEvent.ConnectMqttBroker -> {
                connectMqtt()
            }
            is RobotEvent.DisconnectMqttBroker -> {
                disconnectMqtt()
            }
            is RobotEvent.InitTTS -> {
                initTTS(event.context)
            }
            is RobotEvent.StartTTS -> {
                startTTS(event.text, event.videoResId)
            }
            is RobotEvent.StopTTS -> {
                stopTTS()
            }
            is RobotEvent.DestroyTTS -> {
                destroyTTS()
            }
            is RobotEvent.ChangeTTSLanguage -> {
                changeTTSLanguage(event.locale)
            }
            is RobotEvent.ChangeSTTLanguage -> {
                changeSTTLanguage(event.locale)
            }
            is RobotEvent.InitAssistant -> {
                startAssistant()
            }
        }
    }

    /*
     *  R&T related functions
     */

    private fun onTap(bodyPart: RobotBodyPart) {
        // TODO: isListening & isSpeaking mode management
        // TODO: Handle multitap when input mode is TouchSensor
        val inputMode = _state.value.inputMode
        // Global handler
        when (inputMode) {
            is RobotInputMode.TouchSensor -> {
                if (bodyPart in inputMode.targetBodyParts) {
                    _state.value = _state.value.copy(inputMode = RobotInputMode.AutoSTT)
                    sendPromptAndHandleResponse(bodyPart.touchedTag)
                }
            }
            else -> {}
        }
        when (bodyPart) {
            RobotBodyPart.HEAD -> {
                stopTTS()
                stopSTT()
                startTTS(
                    text = _state.value.lastSpokenText,
                    videoResId = _state.value.lastSpokenFaceResId
                )
            }
            RobotBodyPart.CHEST -> {
                when (inputMode) {
                    is RobotInputMode.ManualSTT -> {
                        viewModelScope.launch { startSTT() }
                    }
                    else -> {}
                }
            }
            RobotBodyPart.RIGHT_HAND -> {

            }
            RobotBodyPart.LEFT_HAND -> {

            }
            RobotBodyPart.LEFT_FACE -> {
                when (_state.value.currentTTSLanguage) {
                    Locale.US -> { changeTTSLanguage(Locale.CHINESE) }
                    Locale.CHINESE -> { changeTTSLanguage(Locale.US) }
                    else -> {}
                }
                when (_state.value.currentSTTLanguage) {
                    Locale.US -> { changeSTTLanguage(Locale.CHINESE) }
                    Locale.CHINESE -> { changeSTTLanguage(Locale.US) }
                    else -> {}
                }
                showToast("Switch TTS language to ${_state.value.currentTTSLanguage.displayLanguage}\n" +
                        "Switch STT language to ${_state.value.currentSTTLanguage.displayLanguage}")
            }
            RobotBodyPart.RIGHT_FACE -> {
                when (inputMode) {
                    is RobotInputMode.Start -> {
                        _state.value = _state.value.copy(
                            inputMode = RobotInputMode.AutoSTT,
                            ttsOn = true,
                            displayOn = true
                        )
                        startAssistant()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun onScan(uid: String) {
        // TODO: Manage scan mode
        // TODO: Scan NFC
        val inputMode = _state.value.inputMode
        when (inputMode) {
            is RobotInputMode.ScanObject -> {
                sendPromptAndHandleResponse(uid)
            }
            else -> {}
        }
    }

    private fun sendCaptionToTablet(caption: String) {
        mqttUseCase.publish(
            topic = getFullTopic(topic = Mqtt.Topic.TTS),
            message = caption,
            qos = 0
        )
    }

    private fun sendArgvToTablet(argv: String) {
        mqttUseCase.publish(
            topic = getFullTopic(topic = Mqtt.Topic.ARGV),
            message = argv,
            qos = 0
        )
    }

    private fun sendImageIdsToTablet(imageIds: List<String>) {
        val gson = Gson()
        mqttUseCase.publish(
            topic = getFullTopic(topic = Mqtt.Topic.IMAGE),
            message = gson.toJson(imageIds),
            qos = 0
        )
    }

    /*
     *  MQTT related functions
     */

    private fun connectMqtt() {
        mqttUseCase.bindService {
            mqttUseCase.connect(
                host = Mqtt.BROKER_URL,
                deviceId = _state.value.deviceId,
                onConnected = {
                    initialSubscription()
                },
                onMessageArrived = { topic, message ->
                    onMqttMessageArrived(topic, message)
                }
            )
        }
    }

    private fun disconnectMqtt() {
        mqttUseCase.disconnect()
        mqttUseCase.unbindService()
    }

    private fun initialSubscription() {
        mqttUseCase.apply {
            subscribe(getFullTopic(Mqtt.Topic.TEXT_INPUT), 0)
            subscribe(getFullTopic(Mqtt.Topic.RESPONSE), 0)
            subscribe(getFullTopic(Mqtt.Topic.GET_CATEGORY), 0)
            subscribe(getFullTopic(Mqtt.Topic.API_KEY), 0)
            subscribe(getFullTopic(Mqtt.Topic.ASST_ID), 0)
            subscribe(getFullTopic(Mqtt.Topic.SEND_IMAGE), 0)
            subscribe(getFullTopic(Mqtt.Topic.SEND_FILE), 0)
            subscribe(getFullTopic(Mqtt.Topic.NFC_TAG), 0)
            subscribe(getFullTopic(Mqtt.Topic.TABLET), 0)
            subscribe(getFullTopic(Mqtt.Topic.ROBOT), 0)
        }
    }

    private fun getFullTopic(topic: String): String {
        return topic.replace(Regex.escape("{{deviceId}}").toRegex(), _state.value.deviceId)
    }

    private fun onMqttMessageArrived(topic: String, message: String) {
        when (topic) {
            getFullTopic(Mqtt.Topic.ROBOT) -> {
                val bodyPartId = getPropertyFromJsonString(
                    json = message,
                    propertyName = "BODYPART",
                    expectedType = String::class
                )?.toInt()
                val uid = getPropertyFromJsonString(
                    json = message,
                    propertyName = "UID",
                    expectedType = String::class
                )
                val bodyPart = RobotBodyPart.fromCode(bodyPartId)
                if ( bodyPart != null ){
                    onTap(bodyPart)
                } else if ( uid != null ) {
                    onScan(uid)
                }
            }
            getFullTopic(Mqtt.Topic.ASST_ID) -> {
                _state.value = _state.value.copy(assistantId = message)
                showToast("Set assistant ID: $message")
            }
            getFullTopic(Mqtt.Topic.API_KEY) -> {
                _state.value = _state.value.copy(gptApiKey = message)
                showToast("Set API key: $message")
            }
            else -> {}
        }
    }

    /*
     *  TextToSpeech related functions
     */

    private fun initTTS(context: Context) {
        textToSpeechUseCase.init(
            context = context,
            language = _state.value.currentTTSLanguage,
            onTTSComplete = { onTTSComplete() }
        )
    }

    private fun startTTS(text: String, @RawRes videoResId: Int) {
        _state.value = _state.value.copy(
            isSpeaking = true,
            lastSpokenText = text,
            lastSpokenFaceResId = videoResId,
            faceResId = videoResId
        )

        textToSpeechUseCase.speak(text)
    }

    private fun stopTTS() {
        textToSpeechUseCase.stop()
        _state.value = _state.value.copy(
            isSpeaking = false,
            faceResId = R.raw.smile
        )
    }

    private fun changeTTSLanguage(locale: Locale) {
        textToSpeechUseCase.setLanguage(locale)
        _state.value = _state.value.copy(currentTTSLanguage = locale)
    }

    private fun onTTSComplete() {
        _state.value = _state.value.copy(
            isSpeaking = false,
            faceResId = R.raw.smile
        )
        when(_state.value.inputMode) {
            RobotInputMode.AutoSTT -> {
                viewModelScope.launch { startSTT() }
            }
            else -> {}
        }
    }

    private fun destroyTTS() {
        textToSpeechUseCase.cleanUp()
    }

    /*
     *  SpeechToText related functions
     */

    private suspend fun startSTT() {
        val language = _state.value.currentSTTLanguage
        _state.value = _state.value.copy(
            isListening = true
        )
        speechToTextUseCase.startListening(language = language) { result ->
            _state.value = _state.value.copy(isListening = false)
            sendPromptAndHandleResponse(result)
        }
    }

    private fun stopSTT() {
        speechToTextUseCase.stopListening()
        _state.value = _state.value.copy(
            isListening = false
        )
    }

    private fun changeSTTLanguage(locale: Locale) {
        _state.value = _state.value.copy(currentSTTLanguage = locale)
    }

    /*
     *  LLM connection related functions
     */

    private fun startAssistant() {
        viewModelScope.launch {
            val assistantId = _state.value.assistantId
            val gptApiKey = _state.value.gptApiKey

            showToast("Retrieving assistant...")
            val assistant = languageModelUseCase.retrieveAssistant(
                assistantId = assistantId,
                gptApiKey = gptApiKey
            )

            showToast("Creating new thread...")
            val toolResources = assistant.tool_resources
            val threadId = languageModelUseCase.generateThreadId(
                toolResources = toolResources,
                gptApiKey = gptApiKey
            )

            _state.value = _state.value.copy(
                assistantName = assistant.name ?: "",
                threadId = threadId
            )

            sendPromptAndHandleResponse(prompt = "Start")
        }
    }

    private fun sendPromptAndHandleResponse(prompt: String) {
        val threadId = _state.value.threadId
        val assistantId = _state.value.assistantId
        val gptApiKey = _state.value.gptApiKey
        viewModelScope.launch {
            showToast("Message sent. Waiting for response...")
            languageModelUseCase.sendMessage(
                threadId = threadId,
                role = "user",
                content = prompt,
                attachments = null,
                gptApiKey = gptApiKey
            )

            val runId = languageModelUseCase.generateAssistantRunId(
                threadId = threadId,
                assistantId = assistantId,
                instructions = null,
                gptApiKey = gptApiKey
            )

            var status: String
            do {
                status = languageModelUseCase.getRunStatus(
                    threadId = threadId,
                    runId = runId,
                    gptApiKey = gptApiKey
                )
                if (status != "completed") {
                    delay(1000)
                }
            } while (status != "completed")

            val response = languageModelUseCase.getAssistantResponse(
                threadId = threadId,
                gptApiKey = gptApiKey
            )
            handleResponse(response)
        }
    }

    private fun handleResponse(response: Message?) {
        val rawText = getNestedValueFromLinkedTreeMap(
            map = response?.content?.firstOrNull() as LinkedTreeMap<*, *>,
            nestedKey = "text.value",
            expectedType = String::class
        ) ?: ""
        val imageIds = extractImageIdsFromMessage(response)

        // TODO: Handle annotations in Text (in ai2)

        // Handle tags in response
        val tags = extractTagsFromText(rawText)
        val text = removeTagsFromText(rawText)
        var expression = R.raw.normal
        for ((i, tag) in tags.withIndex()) {
            when (tag) {
                "FINISH" -> {
                    _state.value = _state.value.copy(inputMode = RobotInputMode.Start)
                    break
                }
                "MANUAL STT" -> {
                    _state.value = _state.value.copy(inputMode = RobotInputMode.ManualSTT)
                }
                "AUTO STT" -> {
                    _state.value = _state.value.copy(inputMode = RobotInputMode.AutoSTT)
                }
                "INPUT SENSOR" -> {
                    val targetBodyParts = mutableListOf<RobotBodyPart>()
                    for (j in i + 1 until tags.size) {
                        val bodyPart = RobotBodyPart.fromTouchedTag(touchedTag = tags[i])
                        if (bodyPart != null) {
                            targetBodyParts.add(bodyPart)
                        }
                    }
                    _state.value = _state.value.copy(
                        inputMode = RobotInputMode.TouchSensor(targetBodyParts = targetBodyParts)
                    )
                }
                "INPUT SCAN" -> {
                    _state.value = _state.value.copy(inputMode = RobotInputMode.ScanObject)
                }
                "TTS ON" -> {
                    _state.value = _state.value.copy(ttsOn = true)
                }
                "TTS OFF" -> {
                    _state.value = _state.value.copy(ttsOn = false)
                }
                "DISPLAY ON" -> {
                    _state.value = _state.value.copy(displayOn = true)
                }
                "DISPLAY OFF" -> {
                    _state.value = _state.value.copy(displayOn = false)
                }
                in Robot.EXPRESSION -> {
                    expression = Robot.EXPRESSION[tag] ?: R.raw.normal
                }
                else -> {}
            }
        }
        // TODO: Handle motion tag
        // TODO: STT subtitle

        // TODO: Test display off function
        // TODO: Test manual stt
        // TODO: Test sending images
        // TODO: Test asstid apikey sent from tablet
        // TODO: Test scan qrcode
        val ttsText = if (_state.value.ttsOn) {
            sanitizeTextForTTS(text)
        } else {
             when (_state.value.currentTTSLanguage) {
                 Locale.US -> "The result has shown on the tablet."
                 Locale.CHINESE -> "結果顯示於平板"
                 else -> ""
            }
        }
        sendImageIdsToTablet(imageIds)
        sendArgvToTablet(if (_state.value.displayOn) "DISPLAY ON" else "DISPLAY OFF")
        sendCaptionToTablet(text)
        startTTS(text = ttsText, videoResId = expression)

    }

    private fun extractImageIdsFromMessage(message: Message): List<String> {
        val result = mutableListOf<String>()

        message.attachments?.forEach { attachment: Attachment ->
            result.add(attachment.file_id ?: "")
        }
        message.content.forEach { content ->
            val type = getValueFromLinkedTreeMap(
                map = content as LinkedTreeMap<*, *>,
                key = "type",
                expectedType = String::class
            )
            if (type == "image_file") {
                val fileId = getNestedValueFromLinkedTreeMap(
                    map = content,
                    nestedKey = "image_file.file_id",
                    expectedType = String::class
                )
                result.add(fileId ?: "")
            }
        }

        return result.filterNot { it.isEmpty() }
    }

    private fun extractTagsFromText(input: String): List<String> {
        val regex = "\\[(.+?)]".toRegex()
        return regex.findAll(input).map { it.groupValues[1] }.toList()
    }

    private fun removeTagsFromText(input: String): String {
        val regex = "\\[.*?]".toRegex()
        return input.replace(regex, "")
    }

    private fun sanitizeTextForTTS(text: String): String {
        var sanitizedText = text.replace("&", "and")
        val unwantedSymbolsRegex = Regex("[_*#]")
        val markdownImageRegex = Regex("!\\[.*?]\\(.*?\\)")

        sanitizedText = sanitizedText.replace(unwantedSymbolsRegex, "")
        sanitizedText = sanitizedText.replace(markdownImageRegex, "")

        return sanitizedText
    }

}