package com.example.digitalrobot.domain.model

data class Thread(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val toolResources: ToolResources,
    val metadata: Any
)
