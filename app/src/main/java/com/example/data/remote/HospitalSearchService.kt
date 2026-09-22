package com.example.data.remote

import com.example.data.remote.dto.OverpassResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApi {
    @FormUrlEncoded
    @POST("api/interpreter")
    suspend fun queryHospitals(
        @Field("data") query: String
    ): OverpassResponse
}
