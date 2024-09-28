package com.example.digitalrobot.presentation.robot

import androidx.annotation.RawRes
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.R
import com.example.digitalrobot.domain.model.llm.Assistant
import java.util.Locale

data class RobotState(
    val deviceId: String = "",

    @RawRes val faceResId: Int = R.raw.smile,

    // TTS
    val isSpeaking: Boolean = false,
    val lastSpokenText: String = "",
    val currentTTSLanguage: Locale = Locale.US,

    // STT
    val isListening: Boolean = false,
    val currentSTTLanguage: Locale = Locale.US,

    // LLM
    val gptApiKey: String = BuildConfig.GPT_API_KEY,
    val assistantId: String = "asst_KCweIllefg63HwiQvVVae9W2",
    val assistantName: String = "",
    val threadId: String = "",

)