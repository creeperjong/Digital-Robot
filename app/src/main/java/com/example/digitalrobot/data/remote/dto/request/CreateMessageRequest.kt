package com.example.digitalrobot.data.remote.dto.request

import com.example.digitalrobot.domain.model.llm.common.Attachment

data class CreateMessageRequest(
    val role: String,
    val content: Any,
    val attachments: List<Attachment>? = null,
    val metadata: Any? = null
)
