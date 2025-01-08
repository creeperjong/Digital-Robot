package com.example.digitalrobot.presentation.robot

import android.Manifest
import android.provider.MediaStore.Video
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.digitalrobot.R
import com.example.digitalrobot.presentation.robot.component.TouchArea
import com.example.digitalrobot.presentation.robot.component.VideoPlayer
import com.example.digitalrobot.util.ToastManager
import kotlinx.coroutines.delay

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
        onEvent(RobotEvent.InitNuwaSdk(context))
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(RobotEvent.DisconnectMqttBroker)
            onEvent(RobotEvent.DestroyTTS)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        val density = LocalDensity.current
        val screenWidth = with(density) { constraints.maxWidth.toDp() }
        val screenHeight = with(density) { constraints.maxHeight.toDp() }

        if (state.isDigitalKebbi) {
            VideoPlayer(
                videoResId = state.motionResId,
                repeat = false,
                modifier = Modifier.fillMaxSize()
            )
        }

        VideoPlayer(
            videoResId = state.faceResId,
            repeat = true,
            modifier = if (state.isDigitalKebbi) {
                Modifier
                    .offset(
                        x = screenWidth * 0.4f,
                        y = screenHeight * 0.195f
                    )
                    .fillMaxWidth(0.215f)
                    .fillMaxHeight(0.215f)
            } else {
                Modifier.fillMaxSize()
            }
        )

        if (state.isDigitalKebbi) {
            Box (modifier = Modifier.fillMaxSize()) {

                val touchAreaColor = if ( state.displayTouchArea ) {
                    Color.Red.copy(alpha = 0.3f)
                } else Color.Transparent

                TouchArea(
                    heightPercent = 0.1f,
                    widthPercent = 0.3f,
                    color = touchAreaColor,
                    offsetXPercent = 0.35f,
                    offsetYPercent = 0.05f
                ) {
                    onEvent(RobotEvent.TapBodyPart(RobotBodyPart.HEAD))
                }

                TouchArea(
                    heightPercent = 0.3f,
                    widthPercent = 0.2f,
                    color = touchAreaColor,
                    offsetXPercent = 0.4f,
                    offsetYPercent = 0.65f
                ) {
                    onEvent(RobotEvent.TapBodyPart(RobotBodyPart.CHEST))
                }

                TouchArea(
                    heightPercent = 0.1f,
                    widthPercent = 0.075f,
                    color = touchAreaColor,
                    offsetXPercent = 0.275f,
                    offsetYPercent = 0.8f
                ) {
                    onEvent(RobotEvent.TapBodyPart(RobotBodyPart.RIGHT_HAND))
                }

                TouchArea(
                    heightPercent = 0.1f,
                    widthPercent = 0.075f,
                    color = touchAreaColor,
                    offsetXPercent = 0.675f,
                    offsetYPercent = 0.8f
                ) {
                    onEvent(RobotEvent.TapBodyPart(RobotBodyPart.LEFT_HAND))
                }

                TouchArea(
                    heightPercent = 0.25f,
                    widthPercent = 0.05f,
                    color = touchAreaColor,
                    offsetXPercent = 0.62f,
                    offsetYPercent = 0.2f

                ) {
                    onEvent(RobotEvent.TapBodyPart(RobotBodyPart.LEFT_FACE))
                }

                TouchArea(
                    heightPercent = 0.25f,
                    widthPercent = 0.05f,
                    color = touchAreaColor,
                    offsetXPercent = 0.35f,
                    offsetYPercent = 0.2f
                ) {
                    onEvent(RobotEvent.TapBodyPart(RobotBodyPart.RIGHT_FACE))
                }

                TouchArea(
                    heightPercent = 0.1f,
                    widthPercent = 0.2f,
                    color = Color.Transparent,
                    offsetXPercent = 0.4f,
                    offsetYPercent = 0.45f
                ) {
                    onEvent(RobotEvent.ToggleTouchAreaDisplay)
                }

            }
        }

    }
}

