package com.example.digitalrobot.data.remote

import com.example.digitalrobot.data.remote.dto.request.CreateThreadRequest
import com.example.digitalrobot.data.remote.dto.request.CreateRunRequest
import com.example.digitalrobot.data.remote.dto.request.CreateMessageRequest
import com.example.digitalrobot.data.remote.dto.request.SubmitToolOutputsRequest
import com.example.digitalrobot.domain.model.llm.Thread
import com.example.digitalrobot.domain.model.llm.Assistant
import com.example.digitalrobot.domain.model.llm.AssistantList
import com.example.digitalrobot.domain.model.llm.Run
import com.example.digitalrobot.domain.model.llm.Message
import com.example.digitalrobot.domain.model.llm.MessageList
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface LanguageModelApi {

    @GET("assistants")
    suspend fun listAssistants(
        @Header("Authorization") apiKey: String
    ): AssistantList

    @GET("assistants/{assistantId}")
    suspend fun retrieveAssistant(
        @Path("assistantId") assistantId: String,
        @Header("Authorization") apiKey: String
    ): Assistant

    @POST("threads")
    suspend fun createThread(
        @Body request: CreateThreadRequest,
        @Header("Authorization") apiKey: String
    ): Thread

    @POST("threads/{threadId}/messages")
    suspend fun createMessage(
        @Body request: CreateMessageRequest,
        @Path("threadId") threadId: String,
        @Header("Authorization") apiKey: String
    ): Message

    @GET("threads/{threadId}/messages")
    suspend fun listMessages(
        @Path("threadId") threadId: String,
        @Header("Authorization") apiKey: String
    ): MessageList

    @POST("threads/{threadId}/runs")
    suspend fun createRun(
        @Body request: CreateRunRequest,
        @Path("threadId") threadId: String,
        @Header("Authorization") apiKey: String
    ): Run

    @GET("threads/{threadId}/runs/{runId}")
    suspend fun retrieveRun(
        @Path("threadId") threadId: String,
        @Path("runId") runId: String,
        @Header("Authorization") apiKey: String
    ): Run

    @POST("threads/{threadId}/runs/{runId}/submit_tool_outputs")
    suspend fun submitToolOutputs(
        @Path("threadId") threadId: String,
        @Path("runId") runId: String,
        @Body request: SubmitToolOutputsRequest,
        @Header("Authorization") apiKey: String
    ): Run

    @POST("threads/{threadId}/runs/{runId}/cancel")
    suspend fun cancelRun(
        @Path("threadId") threadId: String,
        @Path("runId") runId: String,
        @Header("Authorization") apiKey: String
    ): Run
}