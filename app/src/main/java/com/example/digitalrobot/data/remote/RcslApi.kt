package com.example.digitalrobot.data.remote

import com.example.digitalrobot.data.remote.dto.response.GetRobotCategoryResponse
import com.example.digitalrobot.data.remote.dto.response.GetRobotListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RcslApi {

    @GET("robot")
    suspend fun getRobotList(): GetRobotListResponse

    @GET("robotCategoryApi")
    suspend fun getRobotCategory(
        @Query("robot_serial_num") robotSerialNumber: String
    ): GetRobotCategoryResponse

}