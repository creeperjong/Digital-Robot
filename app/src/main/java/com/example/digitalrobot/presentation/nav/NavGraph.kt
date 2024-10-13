package com.example.digitalrobot.presentation.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.presentation.robot.RobotEvent
import com.example.digitalrobot.presentation.robot.RobotScreen
import com.example.digitalrobot.presentation.robot.RobotViewModel
import com.example.digitalrobot.presentation.startup.QrCodeScannerScreen
import com.example.digitalrobot.presentation.startup.StartUpScreen
import com.example.digitalrobot.presentation.startup.StartUpState
import com.example.digitalrobot.presentation.startup.StartUpViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val startUpViewModel: StartUpViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Route.StartUpScreen.route) {
        composable(route = Route.StartUpScreen.route) {
            val state by startUpViewModel.state.collectAsState()
            StartUpScreen(
                state = state,
                onEvent = startUpViewModel::onEvent,
                navigateToScanner = { navController.navigate(Route.QrCodeScannerScreen.route) },
                navigateToRobot = { connectInfo ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("connectInfo", connectInfo)
                    navController.navigate(Route.RobotScreen.route)
                }
            )
        }
        composable(route = Route.QrCodeScannerScreen.route) {
            QrCodeScannerScreen(
                onEvent = startUpViewModel::onEvent,
                navigateUp = { navController.popBackStack() }
            )
        }
        composable(route = Route.RobotScreen.route) {
            val robotViewModel: RobotViewModel = hiltViewModel()
            val state by robotViewModel.state.collectAsState()

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<StartUpState>("connectInfo")
                .let { connectInfo ->
                    robotViewModel.onEvent(
                        RobotEvent.SetConnectInfos(deviceId = connectInfo?.deviceId ?: "")
                    )
                }

            RobotScreen(
                state = state,
                onEvent = robotViewModel::onEvent
            )
        }
    }
}