package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostalPincodeResponse(
    @Json(name = "Message") val message: String?,
    @Json(name = "Status") val status: String?,
    @Json(name = "PostOffice") val postOffices: List<PostOfficeDto>?
)

@JsonClass(generateAdapter = true)
data class PostOfficeDto(
    @Json(name = "Name") val name: String?,
    @Json(name = "District") val district: String?,
    @Json(name = "State") val state: String?,
    @Json(name = "Country") val country: String?,
    @Json(name = "Pincode") val pincode: String?
)
