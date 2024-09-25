package com.example.digitalrobot.data.remote.dto.response

import com.example.digitalrobot.data.remote.dto.Attachment
import com.example.digitalrobot.data.remote.dto.IncompleteDetails

data class SendMessageResponse(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val thread_id: String,
    val status: String,
    val incomplete_details: IncompleteDetails?,
    val completed_at: Int?,
    val incompleted_at: Int?,
    val role: String,
    val content: List<Any>,
    val assistant_id: String?,
    val run_id: String?,
    val attachments: List<Attachment>?,
    val metadata: Any
)
