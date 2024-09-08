package com.example.digitalrobot.presentation.robot

import android.speech.tts.TextToSpeech
import java.util.Locale

data class RobotState(
    val deviceId: String = "",

    // TTS
    val isSpeaking: Boolean = false,
    val lastSpokenText: String = "",
    val currentLanguage: Locale = Locale.US
)