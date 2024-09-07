package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.data.repository.MqttRepository
import com.example.digitalrobot.domain.repository.IMqttRepository
import javax.inject.Inject

class MqttUseCase @Inject constructor(
    private val repository: IMqttRepository
) {
    fun connect(host: String) {
        repository.connect(host)
    }

    fun disconnect() {
        repository.disconnect()
    }

    fun bindService(onServiceConnected: () -> Unit) {
        repository.bindService(onServiceConnected)
    }

    fun unbindService() {
        repository.unbindService()
    }

    fun publish(topic: String, message: String, qos: Int) {
        repository.publish(topic, message, qos)
    }

    fun subscribe(topic: String, qos: Int) {
        repository.subscribe(topic, qos)
    }
}