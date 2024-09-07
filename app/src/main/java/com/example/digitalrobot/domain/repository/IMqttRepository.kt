package com.example.digitalrobot.domain.repository

interface IMqttRepository {
    fun connect(host: String)
    fun disconnect()
    fun subscribe(topic: String, qos: Int)
    fun publish(topic: String, message: String, qos: Int)
    fun bindService(onServiceConnected: () -> Unit)
    fun unbindService()
}