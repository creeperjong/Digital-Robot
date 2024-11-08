package com.example.digitalrobot.data.repository

import android.util.Log
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

    override suspend fun executeSqlQuery(queryString: String): List<Map<String, String>> {
        return try {
            rcslApi.executeSqlQuery(queryString).result ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }


}