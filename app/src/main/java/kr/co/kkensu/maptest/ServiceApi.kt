package kr.co.kkensu.maptest

import kr.co.kkensu.maptest.GetSearchResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceApi {
    @GET("/hyundai/getVehicleStatus")
    fun search(
        @Query("userId") userId: String,
        @Query("vehicleId") vehicleId: String
    ): Call<GetSearchResponse>
}