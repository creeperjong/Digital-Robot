package com.example.digitalrobot.data.remote.dto.request

import com.example.digitalrobot.domain.model.Attachment
import com.example.digitalrobot.domain.model.TruncationStrategy

data class CreateRunRequest(
    val assistant_id: String,
    val model: String? = null,
    val instructions: String? = null,
    val additional_instructions: String? = null,
    val additional_messages: AdditionalMessage? = null,
    val tools: List<Any>? = null,
    val metadata: Any? = null,
    val temperature: Double? = null,
    val top_p: Double? = null,
    val stream: Boolean? = null,
    val max_prompt_tokens: Int? = null,
    val truncation_strategy: TruncationStrategy? = null,
    val tool_choice: Any? = null,
    val parallel_tool_calls: Boolean? = null,
    val response_format: Any? = null
)  {
    data class AdditionalMessage(
        val role: String,
        val content: Any,
        val attachments: List<Attachment>? = null,
        val metadata: Any? = null
    )


}
