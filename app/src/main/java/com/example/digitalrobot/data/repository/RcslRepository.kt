package com.example.digitalrobot.data.repository

import com.example.digitalrobot.data.remote.RcslApi
import com.example.digitalrobot.domain.model.rcsl.Robot
import com.example.digitalrobot.domain.repository.IRcslRepository

class RcslRepository(
    private val rcslApi: RcslApi
): IRcslRepository {

    override suspend fun getRobotList(): List<Robot> {
        return try {
            rcslApi.getRobotList().data
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getRobotCategory(robotSerialNumber: String): List<Any> {
        return try {
            rcslApi.getRobotCategory(robotSerialNumber).data.user
        } catch(e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


}