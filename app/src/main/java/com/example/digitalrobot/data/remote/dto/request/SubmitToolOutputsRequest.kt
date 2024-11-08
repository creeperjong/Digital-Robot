package com.example.digitalrobot.data.remote.dto.request

data class SubmitToolOutputsRequest (
    val tool_outputs: List<ToolOutput>,
    val stream: Boolean? = null
) {
    data class ToolOutput (
        val tool_call_id: String? = null,
        val output: String? = null
    )
}