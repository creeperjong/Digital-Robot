package com.example.digitalrobot.data.repository

import com.example.digitalrobot.data.remote.RcslApi
import com.example.digitalrobot.domain.model.rcsl.ExecuteSqlResult
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

    override suspend fun getRobotCategoryBySerialNumber(robotSerialNumber: String): List<Any> {
        return try {
            rcslApi.getRobotCategoryBySerialNumber(robotSerialNumber).data.user
        } catch(e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun getRobotCategoryByName(robotName: String): List<Any> {
        return try {
            rcslApi.getRobotCategoryByName(robotName).data.user
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun executeSqlQuery(queryString: String): ExecuteSqlResult {
        return try {
            rcslApi.executeSqlQuery(queryString)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


}