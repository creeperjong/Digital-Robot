package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.domain.model.Assistant
import com.example.digitalrobot.domain.model.Attachment
import com.example.digitalrobot.domain.model.Message
import com.example.digitalrobot.domain.model.ToolResources
import com.example.digitalrobot.domain.repository.ILanguageModelRepository
import org.w3c.dom.ProcessingInstruction

class LanguageModelUseCase(
    private val languageModelRepository: ILanguageModelRepository
) {
    suspend fun retrieveAssistant(assistantId: String): Assistant {
        return languageModelRepository.retrieveAssistant(assistantId)
    }

    suspend fun generateThreadId(toolResources: ToolResources): String {
        return languageModelRepository.createThread(toolResources).id
    }

    suspend fun sendMessage(
        threadId: String,
        role: String,
        content: Any,
        attachments: List<Attachment>? = null
    ) {
        languageModelRepository.createMessage(
            threadId = threadId,
            role = role,
            content = content,
            attachments = attachments
        )
    }

    suspend fun generateAssistantRunId(
        threadId: String,
        assistantId: String,
        instructions: String?
    ): String {
        return languageModelRepository.createRun(
            threadId = threadId,
            assistantId = assistantId,
            instructions = instructions
        ).id
    }

    suspend fun getRunStatus(
        threadId: String,
        runId: String
    ): String {
        return languageModelRepository.retrieveRun(
            threadId = threadId,
            runId = runId
        ).status
    }

    suspend fun getAssistantResponse(
        threadId: String
    ): Message? {
        return languageModelRepository.listMessages(
            threadId = threadId
        ).lastOrNull()
    }
}