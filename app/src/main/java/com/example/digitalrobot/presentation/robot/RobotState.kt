package com.example.digitalrobot.presentation.robot

import androidx.annotation.RawRes
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.R
import java.util.Locale

data class RobotState(

    val toastMessages: List<String> = emptyList(),

    // R&T
    val username: String = "Leaner",
    val deviceId: String = "",
    @RawRes val faceResId: Int = R.raw.smile,
    val inputMode: RobotInputMode = RobotInputMode.Start,
    val ttsOn: Boolean = true,
    val displayOn: Boolean = true,

    // TTS
    val isSpeaking: Boolean = false,
    val lastSpokenText: String = "",
    @RawRes val lastSpokenFaceResId: Int = R.raw.normal,
    val currentTTSLanguage: Locale = Locale.US,

    // STT
    val isListening: Boolean = false,
    val currentSTTLanguage: Locale = Locale.US,
    val resultBuffer: String = "",

    // LLM
    val gptApiKey: String = BuildConfig.MAKING_PROMPTS_INVISIBLE,
    val assistantId: String = BuildConfig.DEFAULT_MPI_ASSISTANT_ID,
    val assistantName: String = "",
    val threadId: String = ""
)