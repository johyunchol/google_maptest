package kr.co.kkensu.maptest

import kr.co.kkensu.maptest.GetSearchResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceApi {
    @GET("/hyundai/getVehicleStatus")
    fun getVehicleStatus(
        @Query("userId") userId: String,
        @Query("vehicleId") vehicleId: String
    ): Call<GetSearchResponse>

    @GET("/hyundai/getVehicleStatus2")
    fun getVehicleStatus2(
        @Query("userId") userId: String,
        @Query("vehicleId") vehicleId: String
    ): Call<GetSearchResponse>

    @GET("/hyundai/getParkLocation")
    fun getParkLocation(
        @Query("userId") userId: String,
        @Query("vehicleId") vehicleId: String
    ): Call<GetSearchResponse>
}