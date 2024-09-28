package com.example.digitalrobot.domain.model.llm

import com.example.digitalrobot.domain.model.llm.common.IncompleteDetails
import com.example.digitalrobot.domain.model.llm.common.TruncationStrategy

data class Run(
    val id: String,
    val `object`: String,
    val created_at: Int,
    val thread_id: String,
    val assistant_id: String,
    val status: String,
    val required_action: RequiredAction? = null,
    val last_error: LastError? = null,
    val expires_at: Int? = null,
    val started_at: Int? = null,
    val cancelled_at: Int? = null,
    val failed_at: Int? = null,
    val completed_at: Int? = null,
    val incomplete_details: IncompleteDetails? = null,
    val model: String,
    val instructions: String,
    val tools: List<Any>,
    val metadata: Any,
    val usage: Usage? = null,
    val temperature: Double? = null,
    val top_p: Double? = null,
    val max_prompt_tokens: Int? = null,
    val max_completion_tokens: Int? = null,
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
