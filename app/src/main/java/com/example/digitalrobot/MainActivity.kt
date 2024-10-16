package com.example.digitalrobot

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.digitalrobot.presentation.nav.NavGraph
import com.example.digitalrobot.presentation.startup.QrCodeScannerScreen
import com.example.digitalrobot.presentation.startup.StartUpScreen
import com.example.digitalrobot.ui.theme.DigitalRobotTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        hideSystemUi(window)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            runOnUiThread {
                showExceptionDialog(throwable.message ?: "Unknown error")
            }
        }

        setContent {
            DigitalRobotTheme {
                Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)){
                    NavGraph()
                }
            }
        }
    }
    private fun showExceptionDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle("Unexpected Error")
            .setMessage("Please report below message to coder\n$errorMessage")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }
}


private fun hideSystemUi(window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}
