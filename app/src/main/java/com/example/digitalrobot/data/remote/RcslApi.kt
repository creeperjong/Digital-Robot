package com.example.digitalrobot.data.remote

import com.example.digitalrobot.data.remote.dto.response.GetRobotCategoryResponse
import com.example.digitalrobot.data.remote.dto.response.GetRobotListResponse
import com.example.digitalrobot.data.remote.dto.response.ExecuteSqlQueryResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RcslApi {

    @GET("robot")
    suspend fun getRobotList(): GetRobotListResponse

    @GET("robotCategoryApi")
    suspend fun getRobotCategoryBySerialNumber(
        @Query("robot_serial_num") robotSerialNumber: String
    ): GetRobotCategoryResponse

    @GET("robotCategoryApi")
    suspend fun getRobotCategoryByName(
        @Query("robot_name") robotName: String
    ): GetRobotCategoryResponse

    @GET("sqlCommand")
    suspend fun executeSqlQuery(
        @Query("sql") queryString: String
    ): ExecuteSqlQueryResponse

}