package com.example.digitalrobot.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.digitalrobot.data.remote.mqtt.MqttMessageService
import com.example.digitalrobot.domain.repository.IMqttRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MqttRepository(
    private val context: Context
) : IMqttRepository {
    private var mqttService: MqttMessageService? = null
    private var isBound = false
    private var isConnected = false
    private var onServiceConnectedCallback: (() -> Unit)? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MqttMessageService.LocalBinder
            mqttService = binder.service
            isBound = true
            onServiceConnectedCallback?.invoke()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mqttService = null
            isBound = false
        }

    }

    override fun connect(host: String) {
        if (isBound and !isConnected) {
            mqttService?.connect(host)
            isConnected = true
        }
    }

    override fun subscribe(topic: String, qos: Int) {
        if (isBound and isConnected) {
            mqttService?.subscribe(topic, qos)
        }
    }

    override fun publish(topic: String, message: String, qos: Int) {
        if (isBound and isConnected) {
            mqttService?.publish(topic, message, qos)
        }
    }

    override fun disconnect() {
        if (isBound and isConnected) {
            mqttService?.disconnect()
            isConnected = false
        }
    }

    override fun bindService(onServiceConnected: () -> Unit) {
        if (!isBound) {
            onServiceConnectedCallback = onServiceConnected
            val intent = Intent(context, MqttMessageService::class.java)
            context.startService(intent)
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            isBound = true
        }
    }

    override fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

}