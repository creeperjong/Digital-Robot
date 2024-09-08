package com.example.digitalrobot.presentation.robot

import android.speech.tts.TextToSpeech
import androidx.annotation.RawRes
import com.example.digitalrobot.R
import java.util.Locale

data class RobotState(
    val deviceId: String = "",

    @RawRes val faceResId: Int = R.raw.smile,

    // TTS
    val isSpeaking: Boolean = false,
    val lastSpokenText: String = "",
    val currentLanguage: Locale = Locale.US
)