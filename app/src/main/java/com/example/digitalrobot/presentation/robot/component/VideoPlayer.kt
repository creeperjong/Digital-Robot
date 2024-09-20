package com.example.digitalrobot.presentation.robot.component

import android.net.Uri
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    @RawRes videoResId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPlayerIndex by remember {
        mutableIntStateOf(0)
    }

    /*
     * Use 2 player to avoid black frame
     * when switching videos
     */

    val exoPlayer1 = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }
    val exoPlayer2 = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    LaunchedEffect(videoResId) {
        val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")
        val mediaItem = MediaItem.fromUri(videoUri)

        if (currentPlayerIndex == 0) {
            exoPlayer1.stop()
            exoPlayer2.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        } else {
            exoPlayer2.stop()
            exoPlayer1.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % 2
    }
    Crossfade(targetState = currentPlayerIndex, label = "") { index ->
        if (index == 0) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer1
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                },
                modifier = modifier
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer2
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    }
                },
                modifier = modifier
            )
        }
    }

    DisposableEffect(exoPlayer1, exoPlayer2) {
        onDispose {
            exoPlayer1.release()
            exoPlayer2.release()
        }
    }
}