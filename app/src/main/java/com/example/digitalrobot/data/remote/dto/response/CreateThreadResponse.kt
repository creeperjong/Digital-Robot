package com.example.digitalrobot.data.remote.dto.response

import com.example.digitalrobot.data.remote.dto.ToolResources

data class CreateThreadResponse(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val toolResources: ToolResources,
    val metadata: Any
)
