package com.example.digitalrobot.domain.model.llm.common

data class Attachment(
    val file_id: String? = null,
    val tools: List<Any>? = null
)
