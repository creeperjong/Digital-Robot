package com.example.digitalrobot.domain.repository

import com.example.digitalrobot.domain.model.Attachment
import com.example.digitalrobot.domain.model.Assistant
import com.example.digitalrobot.domain.model.Message
import com.example.digitalrobot.domain.model.Run
import com.example.digitalrobot.domain.model.Thread
import com.example.digitalrobot.domain.model.ToolResources

interface ILanguageModelRepository {

    suspend fun retrieveAssistant(
        assistantId: String
    ): Assistant

    suspend fun createThread(
        toolResources: ToolResources
    ): Thread

    suspend fun createMessage(
        threadId: String,
        role: String,
        content: Any,
        attachments: List<Attachment>? = null
    ): Message

    suspend fun createRun(
        threadId: String,
        assistantId: String,
        instructions: String? = null
    ): Run

    suspend fun retrieveRun(
        threadId: String,
        runId: String
    ): Run

    suspend fun listMessages(
        threadId: String
    ): List<Message>
}