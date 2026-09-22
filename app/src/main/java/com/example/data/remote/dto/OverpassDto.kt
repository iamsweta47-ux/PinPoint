package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OverpassResponse(
    @Json(name = "version") val version: Double?,
    @Json(name = "generator") val generator: String?,
    @Json(name = "elements") val elements: List<OverpassElementDto>?
)

@JsonClass(generateAdapter = true)
data class OverpassCenter(
    @Json(name = "lat") val lat: Double?,
    @Json(name = "lon") val lon: Double?
)

@JsonClass(generateAdapter = true)
data class OverpassElementDto(
    @Json(name = "type") val type: String?,
    @Json(name = "id") val id: Long?,
    @Json(name = "lat") val lat: Double?,
    @Json(name = "lon") val lon: Double?,
    @Json(name = "center") val center: OverpassCenter?,
    @Json(name = "tags") val tags: Map<String, String>?
)
