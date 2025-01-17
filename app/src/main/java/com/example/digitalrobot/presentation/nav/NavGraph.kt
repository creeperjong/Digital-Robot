package com.example.digitalrobot.presentation.nav

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitalrobot.presentation.robot.RobotEvent
import com.example.digitalrobot.presentation.robot.RobotScreen
import com.example.digitalrobot.presentation.robot.RobotViewModel
import com.example.digitalrobot.presentation.startup.StartUpScreen
import com.example.digitalrobot.presentation.startup.StartUpState
import com.example.digitalrobot.presentation.startup.StartUpViewModel

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf(Route.StartUpScreen.route) }
    val startUpViewModel: StartUpViewModel = hiltViewModel()
    val startUpstate by startUpViewModel.state.collectAsState()
    val robotViewModel: RobotViewModel = hiltViewModel()
    val robotState by robotViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        robotViewModel.onEvent(RobotEvent.InitNuwaSdk(context) {
            startDestination = Route.RobotScreen.route
        })
    }


    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Route.StartUpScreen.route) {
            StartUpScreen(
                state = startUpstate,
                onEvent = startUpViewModel::onEvent,
                navigateToRobot = { connectInfo ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("connectInfo", connectInfo)
                    navController.navigate(Route.RobotScreen.route)
                }
            )
        }
        composable(route = Route.RobotScreen.route) {
            if (startDestination != Route.RobotScreen.route) {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<StartUpState>("connectInfo")
                    .let { connectInfo ->
                        robotViewModel.onEvent(
                            RobotEvent.SetConnectInfos(deviceId = connectInfo?.deviceId ?: "")
                        )
                    }
            }

            RobotScreen(
                state = robotState,
                onEvent = robotViewModel::onEvent
            )
        }
    }
}