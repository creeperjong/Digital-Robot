package com.example.digitalrobot.presentation.startup

sealed class StartUpEvent {

    data class ScanQrCodeCompleted(val result: String): StartUpEvent()

}