package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NominatimResultDto(
    @Json(name = "place_id") val placeId: Long?,
    @Json(name = "lat") val lat: String?,
    @Json(name = "lon") val lon: String?,
    @Json(name = "display_name") val displayName: String?,
    @Json(name = "type") val type: String?,
    @Json(name = "address") val address: NominatimAddressDto?
)

@JsonClass(generateAdapter = true)
data class NominatimAddressDto(
    @Json(name = "postcode") val postcode: String?,
    @Json(name = "county") val county: String?,
    @Json(name = "state_district") val stateDistrict: String?,
    @Json(name = "state") val state: String?,
    @Json(name = "city") val city: String?,
    @Json(name = "town") val town: String?,
    @Json(name = "suburb") val suburb: String?
)
