package com.example.digitalrobot.domain.repository

import com.example.digitalrobot.domain.model.rcsl.Robot

interface IRcslRepository {

    suspend fun getRobotList(): List<Robot>

    suspend fun getRobotCategoryBySerialNumber(robotSerialNumber: String): List<Any>

    suspend fun getRobotCategoryByName(robotName: String): List<Any>

    suspend fun executeSqlQuery(queryString: String): List<Map<String, String>>

}