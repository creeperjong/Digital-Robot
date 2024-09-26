package com.example.digitalrobot.domain.model

data class TruncationStrategy(
    val type: String,
    val last_messages: Int? = null
)