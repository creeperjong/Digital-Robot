package com.example.digitalrobot.data.remote.dto.response

data class GetRobotCategoryResponse(
    val data: Data,
    val msg: String,
    val status: Boolean
) {
    data class Data (
        val user: List<Any>
    )
}
