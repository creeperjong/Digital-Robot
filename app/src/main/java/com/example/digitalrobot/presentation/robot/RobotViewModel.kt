package com.example.digitalrobot.presentation.robot

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.domain.usecase.TextToSpeechUseCase
import com.example.digitalrobot.util.Constants.Mqtt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val mqttUseCase: MqttUseCase,
    private val textToSpeechUseCase: TextToSpeechUseCase
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
                speak(event.text)
            }
            is RobotEvent.StopTTS -> {
                stopSpeaking()
            }
            is RobotEvent.ChangeTTSLanguage -> {
                changeLanguage(event.locale)
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
        // TODO: Parser
        Log.d("ViewModel", message)
    }

    /*
     *  TextToSpeech related functions
     */

    private fun initTTS(context: Context) {
        textToSpeechUseCase.init(
            context = context,
            language = _state.value.currentLanguage,
            onComplete = { _state.value = _state.value.copy(isSpeaking = false) }
        )
    }

    private fun speak(text: String) {
        textToSpeechUseCase.speak(text)
        _state.value = _state.value.copy(isSpeaking = true, lastSpokenText = text)
    }

    private fun stopSpeaking() {
        textToSpeechUseCase.stop()
        _state.value = _state.value.copy(isSpeaking = false)
    }

    private fun changeLanguage(locale: Locale) {
        textToSpeechUseCase.setLanguage(locale)
        _state.value = _state.value.copy(currentLanguage = locale)
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeechUseCase.cleanUp()
    }
}