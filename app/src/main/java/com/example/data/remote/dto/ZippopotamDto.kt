package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ZippopotamResponse(
    @Json(name = "post code") val postCode: String?,
    @Json(name = "country") val country: String?,
    @Json(name = "places") val places: List<ZippoPlaceDto>?
)

@JsonClass(generateAdapter = true)
data class ZippoPlaceDto(
    @Json(name = "place name") val placeName: String?,
    @Json(name = "longitude") val longitude: String?,
    @Json(name = "state") val state: String?,
    @Json(name = "state abbreviation") val stateAbbreviation: String?,
    @Json(name = "latitude") val latitude: String?
)
