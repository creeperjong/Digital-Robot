package com.example.digitalrobot.domain.repository

interface IMqttRepository {
    fun connect(
        host: String,
        deviceId: String,
        onConnected: () -> Unit,
        onMessageArrived: (String, String) -> Unit
    )
    fun disconnect()
    fun subscribe(topic: String, qos: Int)
    fun publish(topic: String, message: String, qos: Int)
    fun bindService(onServiceConnected: () -> Unit)
    fun unbindService()
}