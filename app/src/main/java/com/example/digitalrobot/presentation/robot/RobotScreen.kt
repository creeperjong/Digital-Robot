package com.example.digitalrobot.presentation.robot

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.digitalrobot.data.remote.mqtt.MqttMessageService

@Composable
fun RobotScreen() {

    val viewModel: RobotViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.connectMqtt()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.disconnectMqtt()
        }
    }


}