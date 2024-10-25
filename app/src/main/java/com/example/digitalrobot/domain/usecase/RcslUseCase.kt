package com.example.digitalrobot.domain.usecase

import com.example.digitalrobot.domain.model.rcsl.Robot
import com.example.digitalrobot.domain.repository.IRcslRepository
import com.example.digitalrobot.util.getValueFromLinkedTreeMap
import com.google.gson.internal.LinkedTreeMap

class RcslUseCase(
    private val rcslRepository: IRcslRepository
) {
    suspend fun getRobotList(): List<Robot> {
        return rcslRepository.getRobotList()
    }

    suspend fun getUserCategories(deviceId: String): Map<String, List<Map<String, String>>> {
        val users = rcslRepository.getRobotCategory(deviceId)
        val result: MutableMap<String, List<Map<String, String>>> = mutableMapOf()
        for (user in users) {
            val name = getValueFromLinkedTreeMap(
                map = user as LinkedTreeMap<*, *>,
                key = "name",
                expectedType = String::class
            ) ?: "Unknown user"
            val categoryNames = getValueFromLinkedTreeMap(
                map = user,
                key = "category",
                expectedType = List::class
            )?.map { it.toString() } ?: emptyList()
            val categories: MutableList<Map<String, String>> = mutableListOf()
            for (categoryName in categoryNames) {
                val category = getValueFromLinkedTreeMap(
                    map = user,
                    key = categoryName,
                    expectedType = Map::class
                )?.mapKeys { it.key.toString() }?.mapValues { it.value.toString() } ?: emptyMap()
                categories.add(category)
            }
            result[name] = categories
        }
        return result
    }
}