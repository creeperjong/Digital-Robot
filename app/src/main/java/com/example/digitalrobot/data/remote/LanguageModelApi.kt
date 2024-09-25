package com.example.digitalrobot.data.remote

import com.example.digitalrobot.data.remote.dto.request.CreateThreadRequest
import com.example.digitalrobot.data.remote.dto.request.RunAssistantRequest
import com.example.digitalrobot.data.remote.dto.request.SendMessageRequest
import com.example.digitalrobot.data.remote.dto.response.CreateThreadResponse
import com.example.digitalrobot.data.remote.dto.response.RetrieveAssistantResponse
import com.example.digitalrobot.data.remote.dto.response.RunAssistantResponse
import com.example.digitalrobot.data.remote.dto.response.SendMessageResponse
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
        @Header("Authorization") autoToken: String
    ): RetrieveAssistantResponse

    @POST("threads")
    suspend fun createThread(
        @Body request: CreateThreadRequest,
        @Header("Authorization") autoToken: String
    ): CreateThreadResponse

    @POST("threads/{threadId}/messages")
    suspend fun sendMessage(
        @Body request: SendMessageRequest,
        @Path("threadId") threadId: String,
        @Header("Authorization") autoToken: String
    ): SendMessageResponse

    @POST("threads/{threadId}/runs")
    suspend fun runAssistant(
        @Body request: RunAssistantRequest,
        @Path("threadId") threadId: String,
        @Header("Authorization") autoToken: String
    ): RunAssistantResponse

    @GET("threads/{threadId}/runs/{runId}")
    suspend fun getRunStatus(
        @Path("threadId") threadId: String,
        @Path("runId") runId: String,
        @Header("Authorization") autoToken: String
    ): RunAssistantResponse

}