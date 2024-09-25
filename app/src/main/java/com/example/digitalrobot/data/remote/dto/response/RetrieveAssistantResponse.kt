package com.example.digitalrobot.data.remote.dto.response

import com.example.digitalrobot.data.remote.dto.ToolResources

data class RetrieveAssistantResponse(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val name: String?,
    val description: String?,
    val model: String,
    val instructions: String?,
    val tools: List<Any>,
    val tool_resources: ToolResources,
    val metadata: Any,
    val temperature: Double?,
    val top_p: Double?,
    val response_format: Any
)