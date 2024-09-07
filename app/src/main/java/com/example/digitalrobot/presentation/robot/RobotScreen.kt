package com.example.digitalrobot.presentation.robot

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore.Video
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.digitalrobot.R
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

    VideoPlayer(
        videoResId = R.raw.smile,
        modifier = Modifier.fillMaxSize()
    )

}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayer(
    @RawRes videoResId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL

            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        },
        modifier = modifier
    )
    
    DisposableEffect(exoPlayer) {
        onDispose { 
            exoPlayer.release()
        }
    }
}