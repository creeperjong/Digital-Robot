package com.example.digitalrobot

import android.Manifest
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.digitalrobot.presentation.nav.NavGraph
import com.example.digitalrobot.ui.theme.DigitalRobotTheme
import com.example.digitalrobot.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import org.eclipse.paho.client.mqttv3.MqttException
import retrofit2.HttpException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        hideSystemUi(window)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), 1)
        }

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            runOnUiThread {

                var errorMessage = throwable.message ?: "Unknown error"
                val stackTrace = throwable.stackTrace.joinToString("\n")

                if (throwable is HttpException) {
                    val statusCode = throwable.code()
                    val api = throwable.response()?.raw()?.request()?.url()?.toString()
                    val errorBody = throwable.response()?.errorBody()?.string()

                    errorMessage = """
                        HTTP Exception occurred:
                        Status code: $statusCode
                        API: $api
                        Error body: ${errorBody ?: "No additional information"}
                    """.trimIndent()
                } else if (throwable is MqttException) {
                    errorMessage = """
                        MQTT Exception occurred:
                        Reason code: ${throwable.reasonCode}
                        Message: ${throwable.message ?: "No additional information"}
                    """.trimIndent()
                }

                val detailedError = "$errorMessage\n\nStackTrace:\n$stackTrace"

                showExceptionDialog(detailedError)
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
