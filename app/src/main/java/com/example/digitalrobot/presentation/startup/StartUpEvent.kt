package com.example.digitalrobot.presentation.startup

import android.content.Context

sealed class StartUpEvent {

    data class ChangeMacAddress(val result: String): StartUpEvent()

    data class InitSharedPreferences(val context: Context): StartUpEvent()
}