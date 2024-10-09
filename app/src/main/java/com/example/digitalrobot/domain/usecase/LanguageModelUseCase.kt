package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.domain.model.llm.Assistant
import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.common.ToolResources
import com.example.digitalrobot.domain.repository.ILanguageModelRepository

class LanguageModelUseCase(
    private val languageModelRepository: ILanguageModelRepository
) {
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
    ): String {
        return languageModelRepository.retrieveRun(
            threadId = threadId,
            runId = runId,
            apiKey = gptApiKey
        ).status
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
}