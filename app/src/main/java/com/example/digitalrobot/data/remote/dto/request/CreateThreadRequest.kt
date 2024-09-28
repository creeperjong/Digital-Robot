package com.example.digitalrobot.data.remote.dto.request

import com.example.digitalrobot.domain.model.llm.common.ToolResources

data class CreateThreadRequest(
    val messages: List<CreateMessageRequest>? = null,
    val tool_resources: ToolResources? = null,
    val metadata: Any? = null
)
