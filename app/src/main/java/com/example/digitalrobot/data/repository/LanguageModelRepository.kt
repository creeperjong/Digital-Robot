package com.example.digitalrobot.data.repository

import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.data.remote.LanguageModelApi
import com.example.digitalrobot.data.remote.dto.request.CreateMessageRequest
import com.example.digitalrobot.data.remote.dto.request.CreateRunRequest
import com.example.digitalrobot.domain.model.Attachment
import com.example.digitalrobot.data.remote.dto.request.CreateThreadRequest
import com.example.digitalrobot.domain.model.Assistant
import com.example.digitalrobot.domain.model.Message
import com.example.digitalrobot.domain.model.Run
import com.example.digitalrobot.domain.model.Thread
import com.example.digitalrobot.domain.model.ToolResources
import com.example.digitalrobot.domain.repository.ILanguageModelRepository

class LanguageModelRepository(
    private val languageModelApi: LanguageModelApi
): ILanguageModelRepository {

    private val authToken = "Bearer ${BuildConfig.GPT_API_KEY}"

    override suspend fun retrieveAssistant(assistantId: String): Assistant {
        return try {
            languageModelApi.retrieveAssistant(
                assistantId = assistantId,
                authToken = authToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createThread(toolResources: ToolResources): Thread {
        return try {
            languageModelApi.createThread(
                request = CreateThreadRequest(tool_resources = toolResources),
                authToken = authToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createMessage(
        threadId: String,
        role: String,
        content: Any,
        attachments: List<Attachment>?
    ): Message {
        return try {
            languageModelApi.createMessage(
                request = CreateMessageRequest(
                    role = role,
                    content = content,
                    attachments = attachments
                ),
                threadId = threadId,
                authToken = authToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createRun(
        threadId: String,
        assistantId: String,
        instructions: String?
    ): Run {
        return try {
            languageModelApi.createRun(
                request = CreateRunRequest(assistant_id = assistantId, instructions = instructions),
                threadId = threadId,
                authToken = authToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun retrieveRun(threadId: String, runId: String): Run {
        return try {
            languageModelApi.retrieveRun(
                threadId = threadId,
                runId = runId,
                authToken = authToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun listMessages(threadId: String): List<Message> {
        return try {
            languageModelApi.listMessages(
                threadId = threadId,
                authToken = authToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


}