package com.example.domain.model

data class Hospital(
    val id: String,
    val name: String,
    val category: String?,
    val address: String,
    val distanceKm: Double,
    val coordinates: Coordinates,
    val specialties: List<String>?,
    val verifiedPhoneNumber: String?,
    val openingHours: String?,
    val emergencyAvailable: Boolean?,
    val rating: Double?,
    val operator: String?,
    val website: String?
) {
    val formattedDistance: String
        get() = if (distanceKm < 1.0) {
            "${(distanceKm * 1000).toInt()} m"
        } else {
            String.format(java.util.Locale.US, "%.1f km", distanceKm)
        }

    val hasVerifiedPhone: Boolean
        get() = !verifiedPhoneNumber.isNullOrBlank()

    val hasSpecialties: Boolean
        get() = !specialties.isNullOrEmpty()
}
