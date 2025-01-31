package com.example.digitalrobot.presentation.nav

sealed class Route(
    val route: String,
    val argName: String? = null
) {
    data object StartUpScreen: Route(route = "StartUpScreen")
    data object RobotScreen: Route(route = "RobotScreen")
}