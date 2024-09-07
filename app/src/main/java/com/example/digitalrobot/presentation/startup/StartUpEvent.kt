package com.example.digitalrobot.presentation.startup

sealed class StartUpEvent {

    data class MacAddressChanged(val result: String): StartUpEvent()


}