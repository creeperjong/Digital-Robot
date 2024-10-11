package com.example.digitalrobot.presentation.robot

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
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

    state.toastMsgs.let {
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

    VideoPlayer(
        videoResId = state.faceResId,
        modifier = Modifier.fillMaxSize()
    )

}

