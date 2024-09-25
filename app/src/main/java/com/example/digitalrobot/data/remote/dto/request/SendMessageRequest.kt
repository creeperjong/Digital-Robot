package com.example.digitalrobot.data.remote.dto.request

import com.example.digitalrobot.data.remote.dto.Attachment

data class SendMessageRequest(
    val role: String,
    val content: Any,
    val attachments: List<Attachment>?,
    val metadata: Any?
)
