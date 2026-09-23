package com.example.data.repository

import com.example.data.remote.IndiaPostalApi
import com.example.data.remote.NetworkClient
import com.example.data.remote.NominatimApi
import com.example.data.remote.ZippopotamApi
import com.example.domain.model.Coordinates
import com.example.domain.model.PinCodeLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class PinCodeRepository(
    private val nominatimApi: NominatimApi = NetworkClient.nominatimApi,
    private val zippopotamApi: ZippopotamApi = NetworkClient.zippopotamApi,
    private val postalApi: IndiaPostalApi = NetworkClient.postalApi
) {
    /**
     * Validates that the input is a valid 6-digit Indian PIN code.
     * Indian PIN codes are 6 numeric digits and start with digits 1-9.
     */
    fun isValidPinCode(pincode: String): Boolean {
        val trimmed = pincode.trim()
        return trimmed.length == 6 &&
                trimmed.all { it.isDigit() } &&
                trimmed.first() in '1'..'9'
    }

    /**
     * Resolves the coordinates and regional metadata for a 6-digit Indian PIN code.
     */
    suspend fun resolvePinCode(pincode: String): Result<PinCodeLocation> = withContext(Dispatchers.IO) {
        val cleanPin = pincode.trim()
        if (!isValidPinCode(cleanPin)) {
            return@withContext Result.failure(
                IllegalArgumentException("Invalid PIN code format. Indian PIN codes must be 6 digits starting with 1-9.")
            )
        }

        try {
            // First try resolving via Nominatim or Zippopotam
            var coordinates: Coordinates? = null
            var placeName: String? = null
            var district: String? = null
            var state: String? = null

            // Query Postal API in parallel for high-fidelity postal names
            val postalDeferred = coroutineScope {
                async {
                    runCatching { postalApi.getPincodeDetails(cleanPin) }.getOrNull()
                }
            }

            // 1. Try Nominatim
            try {
                val nominatimResults = nominatimApi.searchPostalCode(cleanPin)
                if (nominatimResults.isNotEmpty()) {
                    val first = nominatimResults.first()
                    val lat = first.lat?.toDoubleOrNull()
                    val lon = first.lon?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        coordinates = Coordinates(lat, lon)
                        district = first.address?.stateDistrict ?: first.address?.county ?: first.address?.city
                        state = first.address?.state
                        placeName = first.address?.suburb ?: first.address?.town ?: first.address?.city ?: district
                    }
                }
            } catch (_: Exception) {
                // Ignore and try fallback below
            }

            // 2. Fallback to Zippopotam if coordinates still missing
            if (coordinates == null) {
                try {
                    val zippo = zippopotamApi.getPincodeLocation(cleanPin)
                    val firstPlace = zippo.places?.firstOrNull()
                    val lat = firstPlace?.latitude?.toDoubleOrNull()
                    val lon = firstPlace?.longitude?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        coordinates = Coordinates(lat, lon)
                        if (placeName == null) placeName = firstPlace.placeName
                        if (state == null) state = firstPlace.state
                    }
                } catch (_: Exception) {
                    // Ignore and inspect postal details
                }
            }

            // Inspect postal API response to enrich district and place name
            val postalList = postalDeferred.await()
            val postalItem = postalList?.firstOrNull()
            if (postalItem != null && postalItem.status.equals("Success", ignoreCase = true)) {
                val firstOffice = postalItem.postOffices?.firstOrNull()
                if (firstOffice != null) {
                    if (placeName == null || placeName == district) {
                        placeName = firstOffice.name ?: placeName
                    }
                    if (district == null) district = firstOffice.district
                    if (state == null) state = firstOffice.state
                }
            }

            // 3. Final fallback: use place/district/state name (from Postal API) to search Nominatim
            if (coordinates == null && (placeName != null || district != null)) {
                try {
                    val searchQuery = listOfNotNull(placeName, district, state, "India")
                        .distinct()
                        .joinToString(", ")
                    val freeformResults = nominatimApi.searchFreeform(searchQuery)
                    if (freeformResults.isNotEmpty()) {
                        val first = freeformResults.first()
                        val lat = first.lat?.toDoubleOrNull()
                        val lon = first.lon?.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            coordinates = Coordinates(lat, lon)
                        }
                    }
                } catch (_: Exception) {
                    // Ignore; will fall through to failure below
                }
            }

            if (coordinates == null) {
                return@withContext Result.failure(
                    NoSuchElementException("No location coordinates found for PIN code $cleanPin. Please verify the code.")
                )
            }

            val finalPlaceName = placeName ?: district ?: "PIN $cleanPin Area"

            Result.success(
                PinCodeLocation(
                    pincode = cleanPin,
                    placeName = finalPlaceName,
                    district = district,
                    state = state,
                    coordinates = coordinates
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
