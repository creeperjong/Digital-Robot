package com.example.digitalrobot.presentation.robot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.digitalrobot.R
import com.example.digitalrobot.presentation.robot.component.VideoPlayer

@Composable
fun RobotScreen(
    state: RobotState,
    onEvent: (RobotEvent) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onEvent(RobotEvent.ConnectMqttBroker)
        onEvent(RobotEvent.InitTTS(context))
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(RobotEvent.DisconnectMqttBroker)
        }
    }

    VideoPlayer(
        videoResId = state.faceResId,
        modifier = Modifier.fillMaxSize()
    )

}

