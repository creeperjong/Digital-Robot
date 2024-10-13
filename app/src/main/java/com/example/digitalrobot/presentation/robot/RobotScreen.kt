package com.example.digitalrobot.presentation.robot

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.digitalrobot.presentation.robot.component.TouchArea
import com.example.digitalrobot.presentation.robot.component.VideoPlayer
import com.example.digitalrobot.util.ToastManager

@Composable
fun RobotScreen(
    state: RobotState,
    onEvent: (RobotEvent) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    state.toastMessages.let {
        if (it.isNotEmpty()) {
            for (msg in it){
                ToastManager.showToast(context, msg)
            }
            onEvent(RobotEvent.ClearToastMsg)
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
        onEvent(RobotEvent.ConnectMqttBroker)
        onEvent(RobotEvent.InitTTS(context))
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(RobotEvent.DisconnectMqttBroker)
            onEvent(RobotEvent.DestroyTTS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        VideoPlayer(
            videoResId = state.faceResId,
            modifier = Modifier.fillMaxSize()
        )

        val touchAreaColor = if (state.displayTouchArea) {
            Color.Red.copy(alpha = 0.3f)
        } else Color.Transparent

        TouchArea(
            heightPercent = 0.1f,
            widthPercent = 0.4f,
            color = touchAreaColor,
            alignment = Alignment.TopCenter
        ) {
            onEvent(RobotEvent.TapBodyPart(RobotBodyPart.HEAD))
        }

        TouchArea(
            heightPercent = 0.1f,
            widthPercent = 0.4f,
            color = touchAreaColor,
            alignment = Alignment.BottomCenter
        ) {
            onEvent(RobotEvent.TapBodyPart(RobotBodyPart.CHEST))
        }

        TouchArea(
            heightPercent = 0.1f,
            widthPercent = 0.1f,
            color = touchAreaColor,
            alignment = Alignment.BottomStart
        ) {
            onEvent(RobotEvent.TapBodyPart(RobotBodyPart.RIGHT_HAND))
        }

        TouchArea(
            heightPercent = 0.1f,
            widthPercent = 0.1f,
            color = touchAreaColor,
            alignment = Alignment.BottomEnd
        ) {
            onEvent(RobotEvent.TapBodyPart(RobotBodyPart.LEFT_HAND))
        }

        TouchArea(
            heightPercent = 0.5f,
            widthPercent = 0.1f,
            color = touchAreaColor,
            alignment = Alignment.CenterEnd
        ) {
            onEvent(RobotEvent.TapBodyPart(RobotBodyPart.LEFT_FACE))
        }

        TouchArea(
            heightPercent = 0.5f,
            widthPercent = 0.1f,
            color = touchAreaColor,
            alignment = Alignment.CenterStart
        ) {
            onEvent(RobotEvent.TapBodyPart(RobotBodyPart.RIGHT_FACE))
        }

        TouchArea(
            heightPercent = 0.2f,
            widthPercent = 0.3f,
            color = Color.Transparent,
            alignment = Alignment.Center,
            offsetYPercent = 0.3f
        ) {
            onEvent(RobotEvent.ToggleTouchAreaDisplay)
        }

    }


}

