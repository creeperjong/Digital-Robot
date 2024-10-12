package com.example.digitalrobot.data.remote

import com.example.digitalrobot.data.remote.dto.response.GetRobotListResponse
import retrofit2.http.GET

interface RcslApi {

    @GET("robot")
    suspend fun getRobotList(): GetRobotListResponse

}