package com.example.digitalrobot.data.remote

import com.example.digitalrobot.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class LanguageModelHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("OpenAI-Beta", "assistants=v2")
            .addHeader("Authorization", "Bearer ${BuildConfig.GPT_API_KEY}")
            .build()
        return chain.proceed(newRequest)
    }
}