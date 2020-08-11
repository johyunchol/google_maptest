package kr.co.kkensu.maptest

import kr.co.kkensu.maptest.GetSearchResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HyundaiApi {
    @GET("/api/v1/car-remote/status/response/2384b86e-51a5-4ee0-8dc5-c9b6c0a4e55a/5aee060a-50e6-4379-a2e9-19f2161ff7b3")
    fun search(
        @Header("Authorization") auth: String
    ): Call<GetSearchResponse>
}