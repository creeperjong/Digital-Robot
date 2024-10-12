package com.example.digitalrobot.domain.repository

import com.example.digitalrobot.domain.model.rcsl.Robot

interface IRcslRepository {

    suspend fun getRobotList(): List<Robot>

}