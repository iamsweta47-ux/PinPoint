package com.example.data.remote

import com.example.data.remote.dto.NominatimResultDto
import com.example.data.remote.dto.PostalPincodeResponse
import com.example.data.remote.dto.ZippopotamResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface NominatimApi {
    @Headers("User-Agent: PincodeHospitalFinder-Android/1.0 (contact: info@pincodehospitals.app)")
    @GET("search")
    suspend fun searchPostalCode(
        @Query("postalcode") postalcode: String,
        @Query("country") country: String = "India",
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 3
    ): List<NominatimResultDto>
}

interface ZippopotamApi {
    @GET("in/{pincode}")
    suspend fun getPincodeLocation(
        @Path("pincode") pincode: String
    ): ZippopotamResponse
}

interface IndiaPostalApi {
    @GET("pincode/{pincode}")
    suspend fun getPincodeDetails(
        @Path("pincode") pincode: String
    ): List<PostalPincodeResponse>
}
