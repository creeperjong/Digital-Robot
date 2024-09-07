package com.example.digitalrobot.presentation.robot

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.digitalrobot.data.remote.mqtt.MqttMessageService

@Composable
fun RobotScreen(
    state: RobotState,
    onEvent: (RobotEvent) -> Unit
) {

    LaunchedEffect(Unit) {
        onEvent(RobotEvent.ConnectMqttBroker)
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(RobotEvent.DisconnectMqttBroker)
        }
    }

    Text(text = state.macAddress, modifier = Modifier.padding(64.dp))

}