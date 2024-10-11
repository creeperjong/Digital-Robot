package com.example.digitalrobot.presentation.startup

import android.content.Context

sealed class StartUpEvent {

    data class SetMacAddress(val result: String): StartUpEvent()

    data class SetProject(val projectName: String): StartUpEvent()

    data class SetAssistant(val assistantName: String): StartUpEvent()

    data class SetAssistantList(val gptApiKey: String): StartUpEvent()

    data class InitSharedPreferences(val context: Context): StartUpEvent()
}