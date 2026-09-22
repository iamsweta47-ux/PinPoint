package com.example.domain.model

data class PinCodeLocation(
    val pincode: String,
    val placeName: String,
    val district: String?,
    val state: String?,
    val coordinates: Coordinates
)
