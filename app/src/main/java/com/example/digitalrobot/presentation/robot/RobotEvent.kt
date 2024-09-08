package com.example.digitalrobot.presentation.robot

import android.content.Context
import java.util.Locale

sealed class RobotEvent {

    data object ConnectMqttBroker: RobotEvent()

    data object DisconnectMqttBroker: RobotEvent()

    data class InitTTS(val context: Context): RobotEvent()

    data class StartTTS(val text: String): RobotEvent()

    data object StopTTS: RobotEvent()

    data class ChangeTTSLanguage(val locale: Locale): RobotEvent()

}