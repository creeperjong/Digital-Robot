package com.example.digitalrobot.domain.repository

import com.example.digitalrobot.domain.model.llm.common.Attachment
import com.example.digitalrobot.domain.model.llm.Assistant
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.MessageList
import com.example.digitalrobot.domain.model.llm.Run
import com.example.digitalrobot.domain.model.llm.Thread
import com.example.digitalrobot.domain.model.llm.common.ToolResources

interface ILanguageModelRepository {

    suspend fun retrieveAssistant(
        assistantId: String,
        apiKey: String
    ): Assistant

    suspend fun createThread(
        toolResources: ToolResources?,
        apiKey: String
    ): Thread

    suspend fun createMessage(
        threadId: String,
        role: String,
        content: Any,
        attachments: List<Attachment>? = null,
        apiKey: String
    ): Message

    suspend fun createRun(
        threadId: String,
        assistantId: String,
        instructions: String? = null,
        apiKey: String
    ): Run

    suspend fun retrieveRun(
        threadId: String,
        runId: String,
        apiKey: String
    ): Run

    suspend fun listMessages(
        threadId: String,
        apiKey: String
    ): MessageList
}