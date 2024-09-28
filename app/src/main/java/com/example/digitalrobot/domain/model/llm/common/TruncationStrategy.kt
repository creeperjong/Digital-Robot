package com.example.digitalrobot.domain.model.llm.common

data class TruncationStrategy(
    val type: String,
    val last_messages: Int? = null
)