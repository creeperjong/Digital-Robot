package com.example.digitalrobot.domain.model.llm

data class Response(
    val response: Message?,
    val toolCalls: List<Run.ToolCall>?
)
