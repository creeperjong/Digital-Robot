package com.example.digitalrobot.presentation.robot

import android.content.Context
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalrobot.R
import com.example.digitalrobot.domain.usecase.LanguageModelUseCase
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.domain.usecase.SpeechToTextUseCase
import com.example.digitalrobot.domain.usecase.TextToSpeechUseCase
import com.example.digitalrobot.util.Constants.Mqtt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val mqttUseCase: MqttUseCase,
    private val textToSpeechUseCase: TextToSpeechUseCase,
    private val speechToTextUseCase: SpeechToTextUseCase,
    private val languageModelUseCase: LanguageModelUseCase
): ViewModel() {

    private val _state = MutableStateFlow(RobotState())
    val state: StateFlow<RobotState> = _state.asStateFlow()

    fun setMacAddress(macAddress: String) {
        _state.value = _state.value.copy(deviceId = macAddress)
    }

    fun onEvent(event: RobotEvent) {
        when(event) {
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
            is RobotEvent.ChangeTTSLanguage -> {
                changeTTSLanguage(event.locale)
            }
            is RobotEvent.ChangeSTTLanguage -> {
                changeSTTLanguage(event.locale)
            }
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
            subscribe(_state.value.deviceId, 0)
        }
    }

    private fun getFullTopic(topic: String): String {
        return topic.replace(Regex.escape("{{deviceId}}").toRegex(), _state.value.deviceId)
    }

    private fun onMqttMessageArrived(topic: String, message: String) {
        // TODO: Mqtt Message Received
    }

    /*
     *  TextToSpeech related functions
     */

    private fun initTTS(context: Context) {
        textToSpeechUseCase.init(
            context = context,
            language = _state.value.currentTTSLanguage,
            onTTSComplete = { _state.value = _state.value.copy(
                isSpeaking = false,
                faceResId = R.raw.smile
            )
            // TODO: TTS Completed
            }
        )
    }

    private fun startTTS(text: String, @RawRes videoResId: Int) {
        _state.value = _state.value.copy(
            isSpeaking = true,
            lastSpokenText = text,
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

    override fun onCleared() {
        super.onCleared()
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
            // TODO: STT Completed
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
            val assistant = languageModelUseCase.retrieveAssistant(assistantId)
            val toolResources = assistant.tool_resources
            val threadId = languageModelUseCase.generateThreadId(toolResources)

            _state.value = _state.value.copy(
                assistantName = assistant.name ?: "",
                threadId = threadId
            )

            fetchLanguageModelResponse(prompt = "Start")

        }
    }

    private fun fetchLanguageModelResponse(prompt: String) {
        val threadId = _state.value.threadId
        val assistantId = _state.value.assistantId
        viewModelScope.launch {
            languageModelUseCase.sendMessage(
                threadId = threadId,
                role = "user",
                content = prompt,
                attachments = null
            )

            val runId = languageModelUseCase.generateAssistantRunId(
                threadId = threadId,
                assistantId = assistantId,
                instructions = null
            )

            var status = ""
            do {
                status = languageModelUseCase.getRunStatus(
                    threadId = threadId,
                    runId = runId
                )
                if (status != "completed") {
                    delay(1000)
                }
            } while (status != "completed")

            val response = languageModelUseCase.getAssistantResponse(threadId = threadId)

            // TODO: Handle LLM response
        }
    }
}