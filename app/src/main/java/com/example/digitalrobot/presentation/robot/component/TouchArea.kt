package com.example.digitalrobot.presentation.robot.component

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun TouchArea(
    areaName: String = "",
    heightPercent: Float,
    widthPercent: Float,
    offsetXPercent: Float = 0f,
    offsetYPercent: Float = 0f,
    color: Color = Color.Red.copy(alpha = 0.3f),
    onClick: () -> Unit
) {

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val density = LocalDensity.current
        val parentWidth = with(density) { constraints.maxWidth.toDp() }
        val parentHeight = with(density) { constraints.maxHeight.toDp() }

        Box(
            modifier = Modifier
                .offset(
                    x = parentWidth * offsetXPercent,
                    y = parentHeight * offsetYPercent
                )
                .fillMaxWidth(widthPercent)
                .fillMaxHeight(heightPercent)
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = areaName, color = Color.White)
        }
    }

}