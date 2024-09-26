package com.example.digitalrobot.domain.model

data class Message(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val thread_id: String,
    val status: String,
    val incomplete_details: IncompleteDetails? = null,
    val completed_at: Int? = null,
    val incompleted_at: Int? = null,
    val role: String,
    val content: List<Any>,
    val assistant_id: String? = null,
    val run_id: String? = null,
    val attachments: List<Attachment>? = null,
    val metadata: Any
)
