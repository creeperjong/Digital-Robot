package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.domain.model.rcsl.Robot
import com.example.digitalrobot.domain.repository.IRcslRepository

class RcslUseCase(
    private val rcslRepository: IRcslRepository
) {
    suspend fun getRobotList(): List<Robot> {
        return rcslRepository.getRobotList()
    }
}