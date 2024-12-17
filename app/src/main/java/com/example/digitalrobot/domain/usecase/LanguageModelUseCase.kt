package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.domain.model.llm.Assistant
import com.example.digitalrobot.domain.model.llm.AssistantList
import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.Run
import com.example.digitalrobot.domain.model.llm.common.ToolResources
import com.example.digitalrobot.domain.repository.ILanguageModelRepository

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
}