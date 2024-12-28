package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.domain.model.llm.Assistant
import com.example.digitalrobot.domain.model.llm.AssistantList
import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.Response
import com.example.digitalrobot.domain.model.llm.Run
import com.example.digitalrobot.domain.model.llm.common.ToolResources
import com.example.digitalrobot.domain.repository.ILanguageModelRepository
import kotlinx.coroutines.delay

class LanguageModelUseCase(
    private val languageModelRepository: ILanguageModelRepository
) {
    suspend fun getAssistantList(gptApiKey: String): List<Assistant> {
        return languageModelRepository.listAssistants(
            apiKey = gptApiKey
        ).data
    }

    suspend fun retrieveAssistant(assistantId: String, gptApiKey: String): Assistant {
        return languageModelRepository.retrieveAssistant(
            assistantId = assistantId,
            apiKey = gptApiKey
        )
    }

    suspend fun generateThreadId(toolResources: ToolResources?, gptApiKey: String): String {
        return languageModelRepository.createThread(
            toolResources = toolResources,
            apiKey = gptApiKey
        ).id
    }

    suspend fun sendMessage(
        threadId: String,
        role: String,
        content: Any,
        attachments: List<Attachment>? = null,
        gptApiKey: String
    ) {
        languageModelRepository.createMessage(
            threadId = threadId,
            role = role,
            content = content,
            attachments = attachments,
            apiKey = gptApiKey
        )
    }

    suspend fun generateAssistantRunId(
        threadId: String,
        assistantId: String,
        instructions: String?,
        gptApiKey: String
    ): String {
        return languageModelRepository.createRun(
            threadId = threadId,
            assistantId = assistantId,
            instructions = instructions,
            apiKey = gptApiKey
        ).id
    }

    suspend fun getRunStatus(
        threadId: String,
        runId: String,
        gptApiKey: String
    ): Run {
        return languageModelRepository.retrieveRun(
            threadId = threadId,
            runId = runId,
            apiKey = gptApiKey
        )
    }

    suspend fun getAssistantResponse(
        threadId: String,
        gptApiKey: String
    ): Message? {
        return languageModelRepository.listMessages(
            threadId = threadId,
            apiKey = gptApiKey
        ).data.firstOrNull()
    }

    suspend fun getResponseWhenAvailable(
        threadId: String,
        runId: String,
        gptApiKey: String,
        showToast: (String) -> Unit
    ): Response {
        var response: Message? = null
        var toolCalls: List<Run.ToolCall>? = null
        var run: Run
        var attemptCounter = 0
        do {
            run = getRunStatus(
                threadId = threadId,
                runId = runId,
                gptApiKey = gptApiKey
            )
            when (run.status) {
                "in_progress", "queued", "cancelling" -> {
                    if (attemptCounter == 20) break
                    delay(1000)
                    ++attemptCounter
                }
                "completed", "requires_action", "cancelled",
                "failed", "incomplete", "expired" -> {
                    break
                }
                else -> {
                    throw Exception("LLM: Unexpected run status: ${run.status}")
                }
            }
        } while (true)

        when (run.status) {
            "in_progress", "queued", "cancelling" -> {
                showToast("Heavy server loading now. Please wait for retry.")
                throw Exception("LLM: No response")
            }
            "cancelled", "failed", "incomplete", "expired" -> {
                showToast("Run ${run.status}. Please wait for retry.")
                throw Exception("LLM: Run ${run.status}")
            }
            "completed" -> {
                response = getAssistantResponse(
                    threadId = threadId,
                    gptApiKey = gptApiKey
                )
            }
            "requires_action" -> {
                toolCalls = run.required_action
                    ?.submit_tool_outputs
                    ?.tool_calls
            }
        }

        return Response(response = response, toolCalls = toolCalls)
    }

    suspend fun submitToolOutputs(
        threadId: String,
        runId: String,
        toolCallIds: List<String?>,
        outputs: List<String?>,
        gptApiKey: String
    ): Run {
        return languageModelRepository.submitToolOutputs(
            threadId = threadId,
            runId = runId,
            toolCallIds = toolCallIds,
            outputs = outputs,
            apiKey = gptApiKey
        )
    }

    suspend fun cancelRun(
        threadId: String,
        runId: String,
        gptApiKey: String
    ): Boolean {
        var attemptCounter = 0
        var run: Run
        languageModelRepository.cancelRun(
            threadId = threadId,
            runId = runId,
            apiKey = gptApiKey
        )

        do {
            run = getRunStatus(
                threadId = threadId,
                runId = runId,
                gptApiKey = gptApiKey
            )
            when (run.status) {
                "cancelled", "completed", "failed",
                "incomplete", "expired"-> {
                    return true
                }
                else -> {
                    if (attemptCounter == 20) break
                    delay(1000)
                    ++attemptCounter
                }
            }
        } while (true)
        return false
    }
}