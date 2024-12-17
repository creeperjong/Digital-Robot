package com.example.digitalrobot.presentation.robot

import androidx.annotation.RawRes
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.R
import java.util.Locale

data class RobotState(

    // UI
    val toastMessages: List<String> = emptyList(),
    val displayTouchArea: Boolean = false,

    // R&T
    val username: String = "Learner",
    val deviceId: String = "",
    @RawRes val faceResId: Int = R.raw.e_smile,
    @RawRes val motionResId: Int = R.raw.m_idle,
    val inputMode: RobotInputMode = RobotInputMode.Start,
    val ttsOn: Boolean = true,
    val displayOn: Boolean = true,
    val nfcDefinitions: Map<String, List<Map<String, String>>> = emptyMap(),
    val timeout: Int? = null,

    // TTS
    val isSpeaking: Boolean = false,
    val lastSpokenText: String = "",
    @RawRes val lastSpokenFaceResId: Int = R.raw.e_normal,
    @RawRes val lastSpokenMotionResId: Int = R.raw.m_idle,
    val currentTTSLanguage: Locale = Locale.US,

    // STT
    val isListening: Boolean = false,
    val currentSTTLanguage: Locale = Locale.US,
    val resultBuffer: String = "",

    // LLM
    val gptApiKey: String = BuildConfig.MAKING_PROMPTS_INVISIBLE,
    val assistantId: String = BuildConfig.DEFAULT_MPI_ASSISTANT_ID,
    val assistantName: String = "",
    val threadId: String = "",
    val runId: String = "",
    val toolCallIds: List<String> = emptyList(),
    val isRunCompleted: Boolean = true
)