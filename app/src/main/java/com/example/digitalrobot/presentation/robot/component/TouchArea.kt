package com.example.digitalrobot.presentation.robot.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun TouchArea(
    areaName: String = "",
    heightPercent: Float,
    widthPercent: Float,
    offsetXPercent: Float = 0f,
    offsetYPercent: Float = 0f,
    color: Color = Color.Red.copy(alpha = 0.3f),
    alignment: Alignment,
    onClick: () -> Unit
) {

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = screenWidth * widthPercent,
                    height = screenHeight * heightPercent
                )
                .fillMaxSize(fraction = 1f)
                .offset(
                    x = screenWidth * offsetXPercent,  // 水平偏移量依螢幕寬度百分比
                    y = screenHeight * offsetYPercent  // 垂直偏移量依螢幕高度百分比
                )
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = areaName, color = Color.White)
        }
    }

}