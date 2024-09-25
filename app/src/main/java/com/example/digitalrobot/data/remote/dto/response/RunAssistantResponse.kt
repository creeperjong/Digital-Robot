package com.example.digitalrobot.data.remote.dto.response

import com.example.digitalrobot.data.remote.dto.IncompleteDetails
import com.example.digitalrobot.data.remote.dto.TruncationStrategy

data class RunAssistantResponse(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val thread_id: Int,
    val assistant_id: String,
    val status: String,
    val required_action: RequiredAction?,
    val last_error: LastError?,
    val expires_at: Int?,
    val started_at: Int?,
    val cancelled_at: Int?,
    val failed_at: Int?,
    val completed_at: Int?,
    val incomplete_details: IncompleteDetails?,
    val model: String,
    val instructions: String,
    val tools: List<Any>,
    val metadata: Any,
    val usage: Usage?,
    val temperature: Double?,
    val top_p: Double?,
    val max_prompt_tokens: Int?,
    val max_completion_tokens: Int?,
    val truncation_strategy: TruncationStrategy,
    val tool_choice: Any,
    val parallel_tool_calls: Boolean,
    val response_format: Any
) {
    data class RequiredAction(
        val type: String,
        val submit_tool_outputs: SubmitToolOutputs
    )
    data class SubmitToolOutputs(
        val tool_calls: List<ToolCall>
    )
    data class ToolCall(
        val id: String,
        val type: String,
        val function: Function
    )
    data class Function(
        val name: String,
        val arguments: String
    )
    data class LastError(
        val code: String,
        val message: String
    )
    data class Usage(
        val completion_tokens: Int,
        val prompt_tokens: Int,
        val total_tokens: Int
    )
}
