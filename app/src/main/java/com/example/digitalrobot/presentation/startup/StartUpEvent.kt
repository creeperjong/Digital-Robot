package com.example.digitalrobot.presentation.startup

sealed class StartUpEvent {

    data class ChangeMacAddress(val result: String): StartUpEvent()

}