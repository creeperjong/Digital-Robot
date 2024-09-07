package com.example.digitalrobot.di

import android.app.Application
import com.example.digitalrobot.data.repository.MqttRepository
import com.example.digitalrobot.domain.repository.IMqttRepository
import com.example.digitalrobot.domain.usecase.MqttUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

}