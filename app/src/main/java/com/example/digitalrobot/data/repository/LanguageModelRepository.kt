package com.example.digitalrobot.data.repository

import android.util.Log
import com.example.digitalrobot.BuildConfig
import com.example.digitalrobot.data.remote.LanguageModelApi
import com.example.digitalrobot.data.remote.dto.request.CreateMessageRequest
import com.example.digitalrobot.data.remote.dto.request.CreateRunRequest
import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.data.remote.dto.request.CreateThreadRequest
import com.example.digitalrobot.data.remote.dto.request.SubmitToolOutputsRequest
import com.example.digitalrobot.domain.model.llm.Assistant
import com.example.digitalrobot.domain.model.llm.AssistantList
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.MessageList
import com.example.digitalrobot.domain.model.llm.Run
import com.example.digitalrobot.domain.model.llm.Thread
import com.example.digitalrobot.domain.model.llm.common.ToolResources
import com.example.digitalrobot.domain.repository.ILanguageModelRepository

class LanguageModelRepository(
    private val languageModelApi: LanguageModelApi
): ILanguageModelRepository {

    override suspend fun listAssistants(apiKey: String): AssistantList {
        return try {
            languageModelApi.listAssistants(
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun retrieveAssistant(assistantId: String, apiKey: String): Assistant {
        return try {
            languageModelApi.retrieveAssistant(
                assistantId = assistantId,
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createThread(toolResources: ToolResources?, apiKey: String): Thread {
        return try {
            languageModelApi.createThread(
                request = CreateThreadRequest(tool_resources = toolResources),
                apiKey = apiKey
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
        attachments: List<Attachment>?,
        apiKey: String
    ): Message {
        return try {
            languageModelApi.createMessage(
                request = CreateMessageRequest(
                    role = role,
                    content = content,
                    attachments = attachments
                ),
                threadId = threadId,
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun createRun(
        threadId: String,
        assistantId: String,
        instructions: String?,
        apiKey: String
    ): Run {
        return try {
            languageModelApi.createRun(
                request = CreateRunRequest(assistant_id = assistantId, instructions = instructions),
                threadId = threadId,
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun retrieveRun(threadId: String, runId: String, apiKey: String): Run {
        return try {
            languageModelApi.retrieveRun(
                threadId = threadId,
                runId = runId,
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun listMessages(threadId: String, apiKey: String): MessageList {
        return try {
            languageModelApi.listMessages(
                threadId = threadId,
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun submitToolOutputs(
        threadId: String,
        runId: String,
        toolCallId: String,
        output: String,
        apiKey: String,
    ): Run {
        return try {
            languageModelApi.submitToolOutputs(
                threadId = threadId,
                runId = runId,
                request = SubmitToolOutputsRequest(
                    tool_outputs = listOf(SubmitToolOutputsRequest.ToolOutput(tool_call_id = toolCallId, output = output))
                ),
                apiKey = apiKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


}