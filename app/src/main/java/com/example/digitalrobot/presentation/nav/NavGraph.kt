package com.example.digitalrobot.presentation.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.digitalrobot.presentation.startup.QrCodeScannerScreen
import com.example.digitalrobot.presentation.startup.StartUpScreen
import com.example.digitalrobot.presentation.startup.StartUpViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val startUpViewModel: StartUpViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Route.StartUpScreen.route) {
        composable(route = Route.StartUpScreen.route) {
            val macAddress by startUpViewModel.macAddress.collectAsState()
            StartUpScreen(
                macAddress = macAddress,
                navigateToScanner = { navController.navigate(Route.QrCodeScannerScreen.route) },
                navigateToRobot = { /* TODO */ }
            )
        }
        composable(route = Route.QrCodeScannerScreen.route) {
            QrCodeScannerScreen(
                onEvent = startUpViewModel::onEvent,
                navigateUp = { navController.popBackStack() }
            )
        }
        composable(route = Route.RobotScreen.route) {
            // TODO: robot screen
        }
    }
}