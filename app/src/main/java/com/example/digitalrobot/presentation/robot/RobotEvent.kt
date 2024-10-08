package com.example.digitalrobot.presentation.robot

import android.content.Context
import androidx.annotation.RawRes
import java.util.Locale

sealed class RobotEvent {

    data object ConnectMqttBroker: RobotEvent()

    data object DisconnectMqttBroker: RobotEvent()

    data class InitTTS(val context: Context): RobotEvent()

    data class StartTTS(val text: String, @RawRes val videoResId: Int): RobotEvent()

    data object StopTTS: RobotEvent()

    data class ChangeTTSLanguage(val locale: Locale): RobotEvent()

    data class ChangeSTTLanguage(val locale: Locale): RobotEvent()

    data object InitAssistant: RobotEvent()
}