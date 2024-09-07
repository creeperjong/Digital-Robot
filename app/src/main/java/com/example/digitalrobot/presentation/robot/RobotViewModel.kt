package com.example.digitalrobot.presentation.robot

import androidx.lifecycle.ViewModel
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.util.Constants.Mqtt
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RobotViewModel @Inject constructor(
    private val mqttUseCase: MqttUseCase
): ViewModel() {

    fun connectMqtt() {
        mqttUseCase.bindService {
            mqttUseCase.connect(
                host = Mqtt.BROKER_URL,
                onConnected = {
                    // TODO: Subscribe multiple necessary topics
                },
                onMessageArrived = {

                }
            )
        }
    }

    fun disconnectMqtt() {
        mqttUseCase.disconnect()
        mqttUseCase.unbindService()
    }

    fun publishMessage(topic: String, message: String, qos: Int) {
        mqttUseCase.publish(topic, message, qos)
    }

    fun subscribeTopic(topic: String, qos: Int) {
        mqttUseCase.subscribe(topic, qos)
    }

}