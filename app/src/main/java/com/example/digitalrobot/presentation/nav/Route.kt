package com.example.digitalrobot.presentation.nav

sealed class Route(
    val route: String
) {
    data object StartUpScreen: Route(route = "StartUpScreen")
    data object QrCodeScannerScreen: Route(route = "QrCodeScannerScreen")
    data object RobotScreen: Route(route = "RobotScreen")
}