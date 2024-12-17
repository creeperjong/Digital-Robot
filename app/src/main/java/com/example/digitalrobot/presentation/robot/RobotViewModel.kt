package com.example.digitalrobot.presentation.robot

import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalrobot.R
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.Run
import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.usecase.LanguageModelUseCase
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.domain.usecase.RcslUseCase
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
import kotlin.Exception

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val mqttUseCase: MqttUseCase,
    private val textToSpeechUseCase: TextToSpeechUseCase,
    private val speechToTextUseCase: SpeechToTextUseCase,
    private val languageModelUseCase: LanguageModelUseCase,
    private val rcslUseCase: RcslUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(RobotState())
    val state: StateFlow<RobotState> = _state.asStateFlow()

    fun onEvent(event: RobotEvent) {
        when (event) {
            is RobotEvent.ClearToastMsg -> {
                clearToast()
            }
            is RobotEvent.SetConnectInfos -> {
                setConnectInfos(event.deviceId)
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
                stopTTS()
                stopSTT()
                startTTS(event.text, event.videoResId, event.motionResId)
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
            is RobotEvent.TapBodyPart -> {
                onTap(bodyPart = event.bodyPart)
            }
            is RobotEvent.ToggleTouchAreaDisplay -> {
                toggleTouchAreaDisplay()
            }
        }
    }

    private fun setConnectInfos(deviceId: String) {
        _state.value = _state.value.copy(
            deviceId = deviceId
        )
    }

    private fun resetAllTempStates() {
        stopTTS()
        stopSTT()
        _state.value = _state.value.copy(
            displayTouchArea = false,
            faceResId = R.raw.e_smile,
            motionResId = R.raw.m_idle,
            inputMode = RobotInputMode.Start,
            ttsOn = true,
            displayOn = true,
            timeout = null,
            isSpeaking = false,
            lastSpokenText = "",
            lastSpokenFaceResId = R.raw.e_normal,
            isListening = false,
            resultBuffer = "",
            isRunCompleted = true,
        )
        showToast("Finished!")
    }

    /*
     *  UI related functions
     */

    private fun showToast(message: String) {
        val currentMessages = _state.value.toastMessages.toMutableList()
        currentMessages.add(message)
        _state.value = _state.value.copy(toastMessages = currentMessages)
    }

    private fun clearToast() {
        _state.value = _state.value.copy(toastMessages = emptyList())
    }

    private fun toggleTouchAreaDisplay() {
        _state.value = _state.value.copy(
            displayTouchArea = !_state.value.displayTouchArea
        )
    }

    /*
     *  R&T related functions
     */

    private fun onTap(bodyPart: RobotBodyPart) {
        // TODO: Handle multitap when input mode is TouchSensor
        val inputMode = _state.value.inputMode
        // Global handler
        when (inputMode) {
            is RobotInputMode.TouchSensor -> {
                if (bodyPart in inputMode.targetBodyParts) {
                    sendPromptAndHandleResponse(bodyPart.touchedTag)
                    return
                }
            }
            else -> {}
        }
        when (bodyPart) {
            RobotBodyPart.HEAD -> {
                if (_state.value.inputMode != RobotInputMode.Start){
                    stopTTS()
                    stopSTT()
                    startTTS(
                        text = _state.value.lastSpokenText,
                        faceResId = _state.value.lastSpokenFaceResId,
                        motionResId = _state.value.lastSpokenMotionResId
                    )
                }
            }
            RobotBodyPart.CHEST -> {
                when (inputMode) {
                    is RobotInputMode.ManualSTT -> {
                        if (_state.value.isListening) {
                            stopSTT()
                            if (_state.value.resultBuffer != "") {
                                onSTTDone(_state.value.resultBuffer)
                                _state.value = _state.value.copy(resultBuffer = "")
                            }
                        } else {
                            viewModelScope.launch { startSTT(keepListening = true) }
                        }
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
                    Locale.US -> { changeTTSLanguage(Locale.TRADITIONAL_CHINESE) }
                    Locale.TRADITIONAL_CHINESE -> { changeTTSLanguage(Locale("pl", "PL")) }
                    Locale("pl", "PL") -> { changeTTSLanguage(Locale.US) }
                    else -> {}
                }
                when (_state.value.currentSTTLanguage) {
                    Locale.US -> { changeSTTLanguage(Locale.TRADITIONAL_CHINESE) }
                    Locale.TRADITIONAL_CHINESE -> { changeSTTLanguage(Locale("pl", "PL")) }
                    Locale("pl", "PL") -> { changeSTTLanguage(Locale.US) }
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
        val inputMode = _state.value.inputMode
        when (inputMode) {
            is RobotInputMode.ScanObject -> {
                sendPromptAndHandleResponse(uid)
                sendInputResponseToTablet(uid)
            }
            else -> {}
        }
    }

    private fun onTabletResponse(message: String) {
        val inputMode = _state.value.inputMode
        stopSTT()
        when (inputMode) {
            is RobotInputMode.TouchTablet -> {
                sendPromptAndHandleResponse(message)
                sendInputResponseToTablet(message)
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

    private suspend fun getNFCDefinitions(robotName: String? = null): String {
        val userCategories = if (robotName != null) {
            rcslUseCase.getUserCategoriesByName(
                robotName = robotName
            )
        } else {
            rcslUseCase.getUserCategoriesBySerialNumber(
                deviceId = _state.value.deviceId
            )
        }
        _state.value = _state.value.copy(nfcDefinitions = userCategories)
        return userCategories.toString()
    }

    private fun sendInputResponseToTablet(result: String) {
        viewModelScope.launch {
            mqttUseCase.publish(
                topic = getFullTopic(topic = Mqtt.Topic.STT),
                message = "${_state.value.username}: $result",
                qos = 0
            )
            delay(2000)
            mqttUseCase.publish(
                topic = getFullTopic(topic = Mqtt.Topic.STT),
                message = "",
                qos = 0
            )
        }
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

    private suspend fun getDataFromDb(args: String): String {
        val robotName = getPropertyFromJsonString(
            json = args,
            propertyName = "robot_name",
            expectedType = String::class
        )
        val sqlQuery = getPropertyFromJsonString(
            json = args,
            propertyName = "sql_query",
            expectedType = String::class
        )
        return if (!robotName.isNullOrEmpty()) {
            getNFCDefinitions(robotName)
        } else if (!sqlQuery.isNullOrEmpty()) {
            executeSqlAndSendResult(sqlQuery)
        } else {
            "No robot name or SQL query provided."
        }
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
                    viewModelScope.launch {
                        initialSubscription()
                        getNFCDefinitions()
                    }
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
            subscribe(getFullTopic(Mqtt.Topic.TABLET_QR), 0)
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
                val username = getPropertyFromJsonString(
                    json = message,
                    propertyName = "USERNAME",
                    expectedType = String::class
                )
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
                val content = getPropertyFromJsonString(
                    json = message,
                    propertyName = "TAGCONTENT",
                    expectedType = String::class
                )
                val bodyPart = RobotBodyPart.fromCode(bodyPartId)
                if ( bodyPart != null ){
                    onTap(bodyPart)
                } else if ( uid != null && content == null) {
                    onScan(uid)
                }
                if (!username.isNullOrEmpty()) {
                    _state.value = _state.value.copy(username = username)
                }
            }
            getFullTopic(Mqtt.Topic.ASST_ID) -> {
                _state.value = _state.value.copy(assistantId = message)
                changeSTTLanguage(Locale.US)
                changeTTSLanguage(Locale.US)
                showToast("Set assistant ID: $message")
            }
            getFullTopic(Mqtt.Topic.API_KEY) -> {
                _state.value = _state.value.copy(gptApiKey = message)
                changeSTTLanguage(Locale.US)
                changeTTSLanguage(Locale.US)
                showToast("Set API key: $message")
            }
            getFullTopic(Mqtt.Topic.TEXT_INPUT) -> {
                stopSTT()
                if (message.isEmpty()) {
                    showToast("Warning: Response cannot be empty!")
                } else {
                    sendPromptAndHandleResponse(message)
                    sendInputResponseToTablet(message)
                }
            }
            getFullTopic(Mqtt.Topic.SEND_IMAGE) -> {
                stopSTT()
                val request = listOf(mapOf(
                    "type" to "image_file",
                    "image_file" to mapOf("file_id" to message)
                ))
                sendPromptAndHandleResponse(request)
            }
            getFullTopic(Mqtt.Topic.SEND_FILE) -> {
                stopSTT()
                val fileId = getPropertyFromJsonString(
                    json = message,
                    propertyName = "fileid",
                    expectedType = String::class
                )
                val filename = getPropertyFromJsonString(
                    json = message,
                    propertyName = "filename",
                    expectedType = String::class
                )
                if ( fileId == null ){
                    showToast("Error: Please try uploading the file again.")
                    return
                }
                val attachment = listOf(
                    Attachment(
                        file_id = fileId,
                        tools = listOf(mapOf("type" to "code_interpreter"))
                    )
                )
                sendPromptAndHandleResponse(
                    prompt = "Here is the uploaded file: $filename",
                    attachment = attachment
                )
            }
            getFullTopic(Mqtt.Topic.TABLET_QR) -> {
                onScan(message)
            }
            getFullTopic(Mqtt.Topic.NFC_TAG) -> {
                val tag = getPropertyFromJsonString(
                    json = message,
                    propertyName = "UID",
                    expectedType = String::class
                ) ?: "Tag not found"
                val username = getPropertyFromJsonString(
                    json = message,
                    propertyName = "USERNAME",
                    expectedType = String::class
                ) ?: _state.value.username
                val definitions = _state.value.nfcDefinitions.values.flatten()
                val content = definitions.find { it[tag] != null }?.get(tag)
                onScan(content ?: "Content not found")
            }
            getFullTopic(Mqtt.Topic.RESPONSE) -> {
                if (message == "[END]" || message == "[FINISH]") {
                    resetAllTempStates()
                } else {
                    onTabletResponse(message)
                }
            }
            getFullTopic(Mqtt.Topic.TABLET) -> {
                val content = getPropertyFromJsonString(
                    json = message,
                    propertyName = "TABLET",
                    expectedType = String::class
                )
                if (content != null) {
                    onTabletResponse(content)
                }
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

    private fun startTTS(text: String, @RawRes faceResId: Int, @RawRes motionResId: Int) {
        _state.value = _state.value.copy(
            isSpeaking = true,
            lastSpokenText = text,
            lastSpokenFaceResId = faceResId,
            lastSpokenMotionResId = motionResId,
            faceResId = faceResId,
            motionResId = motionResId
        )

        textToSpeechUseCase.speak(text)
    }

    private fun stopTTS() {
        textToSpeechUseCase.stop()
        _state.value = _state.value.copy(
            isSpeaking = false,
            faceResId = R.raw.e_smile,
            motionResId = R.raw.m_idle
        )
    }

    private fun changeTTSLanguage(locale: Locale) {
        textToSpeechUseCase.setLanguage(locale)
        _state.value = _state.value.copy(currentTTSLanguage = locale)
    }

    private fun onTTSComplete() {
        viewModelScope.launch {
            if (_state.value.timeout != null) {
                delay(_state.value.timeout!! * 1000L)
                if (_state.value.timeout != null) {
                    stopSTT()
                    sendPromptAndHandleResponse(if (_state.value.timeout == 0) {
                        "NEXT STEP"
                    } else {
                        "TIMEOUT"
                    })
                }
            }
        }
        val inputMode = _state.value.inputMode
        _state.value = _state.value.copy(
            isSpeaking = false,
            faceResId = R.raw.e_smile,
            motionResId = R.raw.m_idle
        )
        when(inputMode) {
            is RobotInputMode.AutoSTT -> {
                viewModelScope.launch { startSTT() }
            }
            is RobotInputMode.TouchTablet -> {
                sendArgvToTablet("wait_for_tap")
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

    private suspend fun startSTT(keepListening: Boolean = false) {
        val language = _state.value.currentSTTLanguage
        _state.value = _state.value.copy(isListening = true)
        speechToTextUseCase.startListening(
            language = language,
            keepListening = keepListening,
            onSTTPartialResult = { result -> onSTTPartialResult(result) },
            onSTTDone = { result -> onSTTDone(result) }
        )
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

    private fun onSTTDone(result: String) {
        _state.value = _state.value.copy(isListening = false)
        sendInputResponseToTablet(result)
        sendPromptAndHandleResponse(result)
    }

    private fun onSTTPartialResult(result: String) {
        val prevResult = _state.value.resultBuffer
        _state.value = _state.value.copy(resultBuffer = "${prevResult}$result")
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

    private fun sendPromptAndHandleResponse(
        prompt: Any,
        attachment: List<Attachment>? = null,
        retryCount: Int = 0,
    ) {
        val maxRetries = 3
        val threadId = _state.value.threadId
        val assistantId = _state.value.assistantId
        val gptApiKey = _state.value.gptApiKey
        if (!_state.value.isRunCompleted) {
            showToast("Warning: Please wait until Kebbi response")
            return
        }

        viewModelScope.launch {
            var response: Message? = null
            var toolCalls: List<Run.ToolCall>? = null
            try {
                _state.value = _state.value.copy(isRunCompleted = false, timeout = null)
                showToast("Message sent. Waiting for response...")
                languageModelUseCase.sendMessage(
                    threadId = threadId,
                    role = "user",
                    content = if (prompt.toString().isEmpty()) "Repeat" else prompt,
                    attachments = attachment,
                    gptApiKey = gptApiKey
                )

                val runId = languageModelUseCase.generateAssistantRunId(
                    threadId = threadId,
                    assistantId = assistantId,
                    instructions = null,
                    gptApiKey = gptApiKey
                )
                _state.value = _state.value.copy(runId = runId)

                var run: Run
                var attemptCounter = 0
                do {
                    run = languageModelUseCase.getRunStatus(
                        threadId = threadId,
                        runId = runId,
                        gptApiKey = gptApiKey
                    )
                    when (run.status) {
                        "in_progress" -> {
                            if (attemptCounter == 20) break
                            delay(1000)
                            ++attemptCounter
                        }
                        "completed",
                        "requires_action" -> {
                            break
                        }
                        else -> {
                            throw Exception("LLM: Unexpected run status")
                        }
                    }
                } while (true)

                when (run.status) {
                    "in_progress" -> {
                        showToast("Heavy server loading now. Please wait for retrying.")
                        throw Exception("LLM: No response")
                    }
                    "completed" -> {
                        _state.value = _state.value.copy(isRunCompleted = true)

                        response = languageModelUseCase.getAssistantResponse(
                            threadId = threadId,
                            gptApiKey = gptApiKey
                        )
                    }
                    "requires_action" -> {
                        toolCalls = run.required_action
                            ?.submit_tool_outputs
                            ?.tool_calls
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isRunCompleted = true)
                if (retryCount < maxRetries) {
                    sendPromptAndHandleResponse(prompt, attachment, retryCount + 1)
                } else {
                    showToast("LLM: Unknown error occurred. Please try again.")
                    if (prompt == "Start" && _state.value.lastSpokenText.isEmpty()) {
                        _state.value = _state.value.copy(inputMode = RobotInputMode.Start)
                    }
                    throw e
                }
            }
            if (response != null) {
                handleResponse(response)
            }
            if (toolCalls != null) {
                handleToolCallsAndSendResults(toolCalls)
            }
        }
    }

    private suspend fun executeSqlAndSendResult(queryString: String): String {
        return rcslUseCase.executeSqlQuery(queryString).toString()
    }

    private fun submitToolOutputsAndHandleResponse(
        toolCallIds: List<String?>,
        outputs: List<String?>,
        retryCount: Int = 0
    ) {
        val maxRetries = 3
        val threadId = _state.value.threadId
        val runId = _state.value.runId
        val gptApiKey = _state.value.gptApiKey

        viewModelScope.launch {
            var response: Message? = null
            try {
                showToast("Function message sent. Waiting for response...")
                languageModelUseCase.submitToolOutputs(
                    threadId = threadId,
                    runId = runId,
                    toolCallIds = toolCallIds,
                    outputs = outputs,
                    gptApiKey = gptApiKey
                )

                var run: Run
                var attemptCounter = 0
                do {
                    run = languageModelUseCase.getRunStatus(
                        threadId = threadId,
                        runId = runId,
                        gptApiKey = gptApiKey
                    )
                    when (run.status) {
                        "in_progress" -> {
                            if (attemptCounter == 20) break
                            delay(1000)
                            ++attemptCounter
                        }
                        "completed" -> {
                            break
                        }
                        else -> {
                            throw Exception("LLM: Unexpected run status")
                        }
                    }
                } while (true)

                _state.value = _state.value.copy(isRunCompleted = true)

                when (run.status) {
                    "in_progress" -> {
                        showToast("Heavy server loading now. Please wait for retrying.")
                        throw Exception("LLM: No response")
                    }
                    "completed" -> {
                        _state.value = _state.value.copy(isRunCompleted = true)

                        response = languageModelUseCase.getAssistantResponse(
                            threadId = threadId,
                            gptApiKey = gptApiKey
                        )
                    }
                }

            } catch (e: Exception) {
                _state.value = _state.value.copy(isRunCompleted = true)
                if (retryCount < maxRetries) {
                    submitToolOutputsAndHandleResponse(
                        toolCallIds = toolCallIds,
                        outputs = outputs,
                        retryCount = retryCount + 1
                    )
                } else {
                    showToast("LLM: Unknown error occurred. Please try again.")
                    throw e
                }
            }
            if (response != null) {
                handleResponse(response)
            }
        }
    }

    private fun handleResponse(response: Message?) {
        if (_state.value.inputMode == RobotInputMode.Start) return
        val rawText = (response?.content?.firstOrNull() as? LinkedTreeMap<*, *>)?.let {
            getNestedValueFromLinkedTreeMap(
                map = it,
                nestedKey = "text.value",
                expectedType = String::class
            )
        } ?: ""
        val imageIds = response?.let { extractImageIdsFromMessage(it) }

        // TODO: Handle annotations in Text (in ai2)

        // Handle tags in response
        val tags = extractTagsFromText(rawText)
        var expression = R.raw.e_normal
        var motion = R.raw.m_idle
        for ((i, tag) in tags.withIndex()) {
            when (tag) {
                "FINISH" -> {
                    resetAllTempStates()
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
                        val bodyPart = RobotBodyPart.fromTouchedTag(touchedTag = tags[j])
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
                "INPUT TOUCH" -> {
                    _state.value = _state.value.copy(inputMode = RobotInputMode.TouchTablet)
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
                "LANGUAGE = ENGLISH" -> {
                    changeTTSLanguage(Locale.US)
                    changeSTTLanguage(Locale.US)
                }
                "LANGUAGE = CHINESE" -> {
                    changeTTSLanguage(Locale.TRADITIONAL_CHINESE)
                    changeSTTLanguage(Locale.TRADITIONAL_CHINESE)
                }
                "LANGUAGE = POLISH" -> {
                    changeTTSLanguage(Locale("pl", "PL"))
                    changeSTTLanguage(Locale("pl", "PL"))
                }
                "KEEP_CONTENT ON" -> {
                    sendArgvToTablet("KEEP_CONTENT ON")
                }
                "KEEP_CONTENT OFF" -> {
                    sendArgvToTablet("KEEP_CONTENT OFF")
                }
                "NEXT STEP" -> {
                    _state.value = _state.value.copy(timeout = 0)
                }
                in Robot.EXPRESSION -> {
                    expression = Robot.EXPRESSION[tag] ?: R.raw.e_normal
                }
                in Robot.MOTION -> {
                    motion = Robot.MOTION[tag] ?: R.raw.m_idle
                }
                else -> {
                    if (tag.contains("TIMEOUT")) {
                        val s = tag.split(" ").last().toInt()
                        _state.value = _state.value.copy(timeout = s)
                    }
                }
            }
        }
        val ttsText = if (_state.value.ttsOn) {
            sanitizeTextForTTS(rawText)
        } else {
             when (_state.value.currentTTSLanguage) {
                 Locale.US -> "Please look at the tablet."
                 Locale.TRADITIONAL_CHINESE -> "請看平板"
                 Locale("pl", "PL") -> "Proszę spojrzeć na tablet."
                 else -> ""
            }
        }
        val captionText = sanitizeTextForCaption(rawText)

        sendImageIdsToTablet(imageIds ?: emptyList())
        sendArgvToTablet(if (_state.value.displayOn) "DISPLAY ON" else "DISPLAY OFF")
        sendCaptionToTablet(captionText)
        stopTTS()
        stopSTT()
        startTTS(text = ttsText, faceResId = expression, motionResId = motion)
    }

    private fun handleToolCallsAndSendResults(toolCalls: List<Run.ToolCall>) {
        viewModelScope.launch {
            val outputs = toolCalls.map { toolCall ->
                when (toolCall.function.name) {
                    "get_database_data" -> getDataFromDb(toolCall.function.arguments)
                    "set_timeout" -> {
                        val s = getPropertyFromJsonString(
                            json = toolCall.function.arguments,
                            propertyName = "timeout_seconds",
                            expectedType = String::class
                        )?.toInt()
                        _state.value = _state.value.copy(timeout = s)
                        "success"
                    }
                    else -> null
                }
            }
            submitToolOutputsAndHandleResponse(
                toolCallIds = toolCalls.map { it.id },
                outputs = outputs
            )
        }
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
        val regex = "(?<!!)\\[(.+?)]".toRegex()
        return regex.findAll(input).map { it.groupValues[1] }.toList()
    }

    private fun sanitizeTextForTTS(text: String): String {
        var sanitizedText = text.replace("&", "and")
        val unwantedSymbolsRegex = Regex("[`_*#-]")
        val markdownImageRegex = Regex("!?\\[.*?]\\(.*?\\)")
        val tagRegex = "\\[.*?]".toRegex()

        sanitizedText = sanitizedText.replace(unwantedSymbolsRegex, "")
        sanitizedText = sanitizedText.replace(markdownImageRegex, "")
        sanitizedText = sanitizedText.replace(tagRegex, "")

        return sanitizedText
    }

    private fun sanitizeTextForCaption(text: String): String {
        var sanitizedText = text
        val singleBracketTagRegex = Regex("(?<!!)\\[.*?](?!\\()")

        sanitizedText = sanitizedText.replace(singleBracketTagRegex, "")

        return sanitizedText
    }

}