package com.example.digitalrobot.presentation.robot

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.util.Constants.Mqtt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val mqttUseCase: MqttUseCase
): ViewModel() {

    private val _state = MutableStateFlow<RobotState>(RobotState())
    val state: StateFlow<RobotState> = _state.asStateFlow()

    fun setMacAddress(macAddress: String) {
        _state.value = _state.value.copy(macAddress = macAddress)
    }

    fun onEvent(event: RobotEvent) {
        when(event) {
            is RobotEvent.ConnectMqttBroker -> {
                connectMqtt()
            }
            is RobotEvent.DisconnectMqttBroker -> {
                disconnectMqtt()
            }
        }
    }

    private fun onMessageArrived(message: String) {
        // TODO: Parser
    }

    private fun connectMqtt() {
        mqttUseCase.bindService {
            mqttUseCase.connect(
                host = Mqtt.BROKER_URL,
                deviceId = _state.value.macAddress,
                onConnected = {
                    // TODO: Initialize MQTT topic subscription
                },
                onMessageArrived = { message ->
                    onMessageArrived(message)
                }
            )
        }
    }

    private fun disconnectMqtt() {
        mqttUseCase.disconnect()
        mqttUseCase.unbindService()
    }

}