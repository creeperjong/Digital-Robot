package com.example.digitalrobot.data.remote.dto.request

import android.health.connect.datatypes.units.Temperature
import com.example.digitalrobot.data.remote.dto.Attachment
import com.example.digitalrobot.data.remote.dto.TruncationStrategy

data class RunAssistantRequest(
    val assistant_id: String,
    val model: String?,
    val instructions: String?,
    val additional_instructions: String?,
    val additional_messages: AdditionalMessage?,
    val tools: List<Any>?,
    val metadata: Any?,
    val temperature: Double?,
    val top_p: Double?,
    val stream: Boolean?,
    val max_prompt_tokens: Int?,
    val truncation_strategy: TruncationStrategy?,
    val tool_choice: Any?,
    val parallel_tool_calls: Boolean?,
    val response_format: Any?
)  {
    data class AdditionalMessage(
        val role: String,
        val content: Any,
        val attachments: List<Attachment>?,
        val metadata: Any?
    )


}
