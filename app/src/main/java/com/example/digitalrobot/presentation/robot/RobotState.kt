package com.example.digitalrobot.presentation.robot

import androidx.annotation.RawRes
import com.example.digitalrobot.R
import java.util.Locale

data class RobotState(
    val deviceId: String = "",

    @RawRes val faceResId: Int = R.raw.smile,

    // TTS
    val isSpeaking: Boolean = false,
    val lastSpokenText: String = "",
    val currentTTSLanguage: Locale = Locale.US

    // STT
    // TODO: STT state
)