package com.example.digitalrobot.presentation.robot

import android.Manifest
import android.widget.Toast
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

@Composable
fun RobotScreen(
    state: RobotState,
    onEvent: (RobotEvent) -> Unit,
    viewModel: RobotViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

//    val toastMessage by viewModel.toastMessage.collectAsState()
//
//    // 如果有訊息就顯示 Toast
//    LaunchedEffect(toastMessage) {
//        toastMessage?.let {
//            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//            viewModel.clearToastMessage()  // 顯示完畢後清除訊息
//        }
//    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
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

