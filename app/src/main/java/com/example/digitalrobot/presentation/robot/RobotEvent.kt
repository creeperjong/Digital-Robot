package com.example.digitalrobot.presentation.robot

import android.content.Context
import androidx.annotation.RawRes
import java.util.Locale

sealed class RobotEvent {

    data object ClearToastMsg: RobotEvent()

    data class SetConnectInfos(val deviceId: String): RobotEvent()

    data class TapBodyPart(val bodyPart: RobotBodyPart): RobotEvent()

    data object ToggleTouchAreaDisplay: RobotEvent()

    data object ConnectMqttBroker: RobotEvent()

    data object DisconnectMqttBroker: RobotEvent()

    data class InitTTS(val context: Context): RobotEvent()

    data class StartTTS(
        val text: String,
        @RawRes val videoResId: Int,
        @RawRes val motionResId: Int,
    ): RobotEvent()

    data object StopTTS: RobotEvent()

    data object DestroyTTS: RobotEvent()

    data object InitAssistant: RobotEvent()
}