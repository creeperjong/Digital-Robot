package com.example.digitalrobot.data.remote.dto.response

import com.example.digitalrobot.domain.model.rcsl.Robot

data class GetRobotListResponse (
    val data: List<Robot>,
    val msg: String,
    val status: Boolean
)