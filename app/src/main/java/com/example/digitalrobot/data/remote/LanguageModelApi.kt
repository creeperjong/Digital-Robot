package com.example.digitalrobot.data.remote

import com.example.digitalrobot.data.remote.dto.request.CreateThreadRequest
import com.example.digitalrobot.data.remote.dto.request.CreateRunRequest
import com.example.digitalrobot.data.remote.dto.request.CreateMessageRequest
import com.example.digitalrobot.domain.model.Thread
import com.example.digitalrobot.domain.model.Assistant
import com.example.digitalrobot.domain.model.Run
import com.example.digitalrobot.domain.model.Message
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface LanguageModelApi {

    @Headers(
        "Content-Type: application/json",
        "OpenAI-Beta: assistants=v2"
    )

    @GET("assistants/{assistantId}")
    suspend fun retrieveAssistant(
        @Path("assistantId") assistantId: String,
        @Header("Authorization") authToken: String
    ): Assistant

    @POST("threads")
    suspend fun createThread(
        @Body request: CreateThreadRequest,
        @Header("Authorization") authToken: String
    ): Thread

    @POST("threads/{threadId}/messages")
    suspend fun createMessage(
        @Body request: CreateMessageRequest,
        @Path("threadId") threadId: String,
        @Header("Authorization") authToken: String
    ): Message

    @GET("threads/{threadId}/messages")
    suspend fun listMessages(
        @Path("threadId") threadId: String,
        @Header("Authorization") authToken: String
    ): List<Message>

    @POST("threads/{threadId}/runs")
    suspend fun createRun(
        @Body request: CreateRunRequest,
        @Path("threadId") threadId: String,
        @Header("Authorization") authToken: String
    ): Run

    @GET("threads/{threadId}/runs/{runId}")
    suspend fun retrieveRun(
        @Path("threadId") threadId: String,
        @Path("runId") runId: String,
        @Header("Authorization") authToken: String
    ): Run
}