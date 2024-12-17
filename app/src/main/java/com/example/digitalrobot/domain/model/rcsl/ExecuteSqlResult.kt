package com.example.digitalrobot.domain.model.rcsl

data class ExecuteSqlResult(
    val data: List<Any>?,
    val msg: String?,
    val status: Boolean?,
    val result: List<Map<String, String?>>?
)
