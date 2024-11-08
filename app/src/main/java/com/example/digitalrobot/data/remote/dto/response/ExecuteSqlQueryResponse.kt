package com.example.digitalrobot.data.remote.dto.response

data class ExecuteSqlQueryResponse(
    val data: List<Any>?,
    val msg: String?,
    val status: Boolean?,
    val result: List<Map<String, String>>?
)
