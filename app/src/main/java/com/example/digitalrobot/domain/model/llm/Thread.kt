package com.example.digitalrobot.domain.model.llm

import com.example.digitalrobot.domain.model.llm.common.ToolResources

data class Thread(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val toolResources: ToolResources?,
    val metadata: Any
)
