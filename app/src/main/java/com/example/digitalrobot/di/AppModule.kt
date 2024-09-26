package com.example.digitalrobot.di

import android.app.Application
import android.content.Context
import android.speech.SpeechRecognizer
import androidx.media3.common.C.Priority
import com.example.digitalrobot.data.remote.LanguageModelApi
import com.example.digitalrobot.data.repository.LanguageModelRepository
import com.example.digitalrobot.data.repository.MqttRepository
import com.example.digitalrobot.domain.repository.ILanguageModelRepository
import com.example.digitalrobot.domain.repository.IMqttRepository
import com.example.digitalrobot.domain.usecase.MqttUseCase
import com.example.digitalrobot.domain.usecase.SpeechToTextUseCase
import com.example.digitalrobot.domain.usecase.TextToSpeechUseCase
import com.example.digitalrobot.util.Constants.LanguageModel.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMqttRepository(
        application: Application
    ): IMqttRepository = MqttRepository(application)

    @Provides
    @Singleton
    fun provideMqttUseCase(
        repository: IMqttRepository
    ): MqttUseCase = MqttUseCase(repository)

    @Provides
    @Singleton
    fun provideTextToSpeechUseCase(): TextToSpeechUseCase = TextToSpeechUseCase()

    @Provides
    @Singleton
    fun provideSpeechRecognizer(
        application: Application
    ): SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)

    @Provides
    @Singleton
    fun provideSpeechToTextUseCase(
        speechRecognizer: SpeechRecognizer
    ): SpeechToTextUseCase = SpeechToTextUseCase(speechRecognizer)

    @Provides
    @Singleton
    fun provideLanguageModelApi(): LanguageModelApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LanguageModelApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLanguageModelRepository(
        languageModelApi: LanguageModelApi
    ): ILanguageModelRepository = LanguageModelRepository(languageModelApi)

}