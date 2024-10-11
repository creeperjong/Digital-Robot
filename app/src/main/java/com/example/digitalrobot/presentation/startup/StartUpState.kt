package com.example.digitalrobot.presentation.startup

import android.os.Parcelable
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class StartUpState(
    val macAddress: String = "",
    val projectName: String = "Making prompts invisible",
    val gptApiKey: String = BuildConfig.MAKING_PROMPTS_INVISIBLE,
    val assistantName: String = "AI家教老師 - 學習任何主題 (中文指令) [Default]",
    val assistantId: String = BuildConfig.DEFAULT_MPI_ASSISTANT_ID,
    val projectOptions: Map<String, String> = Constants.LanguageModel.PROJECTS,
    val assistantOptions: Map<String, String> = emptyMap()
): Parcelable
