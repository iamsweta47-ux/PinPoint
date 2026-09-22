package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SearchHistoryEntity
import com.example.data.remote.NetworkClient
import com.example.data.remote.OverpassApi
import com.example.data.remote.dto.OverpassElementDto
import com.example.data.remote.dto.OverpassResponse
import com.example.data.util.GeoUtils
import com.example.domain.model.Coordinates
import com.example.domain.model.Hospital
import com.example.domain.model.PinCodeLocation
import com.example.domain.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class HospitalRepository(
    private val pinCodeRepository: PinCodeRepository = PinCodeRepository(),
    private val overpassApi: OverpassApi = NetworkClient.overpassApi,
    private val overpassBackupApi: OverpassApi = NetworkClient.overpassBackupApi,
    private val database: AppDatabase? = null
) {
    /**
     * Searches for the top 3 hospitals near the given Indian PIN code.
     * Enforces strict data integrity: no fabricated ratings, specialties, or hours.
     */
    suspend fun findTopHospitalsByPinCode(pincode: String): SearchResult = withContext(Dispatchers.IO) {
        val pinResult = pinCodeRepository.resolvePinCode(pincode)
        val location = pinResult.getOrElse { error ->
            return@withContext SearchResult.Error(
                userFriendlyMessage = error.message ?: "Could not verify PIN code location. Please check the 6 digits.",
                technicalReason = error.localizedMessage
            )
        }

        // Record search in history database
        try {
            database?.searchHistoryDao()?.insertSearch(
                SearchHistoryEntity(
                    pincode = location.pincode,
                    placeName = location.placeName,
                    district = location.district,
                    state = location.state
                )
            )
        } catch (_: Exception) {
            // Non-fatal database logging failure
        }

        return@withContext findHospitalsNearCoordinates(location.coordinates, location)
    }

    /**
     * Searches for nearby hospitals using coordinates (from PIN code or GPS).
     */
    suspend fun findHospitalsNearCoordinates(
        centerCoords: Coordinates,
        locationContext: PinCodeLocation? = null
    ): SearchResult = withContext(Dispatchers.IO) {
        try {
            val hospitals = queryOverpassHospitals(centerCoords)

            if (hospitals.isEmpty()) {
                val pin = locationContext?.pincode ?: "your location"
                return@withContext SearchResult.Empty(
                    pincode = pin,
                    location = locationContext,
                    message = "No registered hospitals were found within 25 km of $pin. Please try an adjacent PIN code or verify spelling."
                )
            }

            // Top 3 nearest hospitals
            val top3 = hospitals.take(3)

            val displayPin = locationContext?.pincode ?: "Location"
            val displayLoc = locationContext ?: PinCodeLocation(
                pincode = "GPS",
                placeName = "Current Coordinates",
                district = null,
                state = null,
                coordinates = centerCoords
            )

            SearchResult.Success(
                pincode = displayPin,
                location = displayLoc,
                hospitals = top3
            )
        } catch (e: Exception) {
            SearchResult.Error(
                userFriendlyMessage = "Unable to fetch nearby hospital records at this moment. Please check your network connection.",
                technicalReason = e.localizedMessage
            )
        }
    }

    private suspend fun queryOverpassHospitals(coords: Coordinates): List<Hospital> {
        // Query Overpass for amenities of type hospital around the coordinates (radius 25,000m)
        val radiusMeters = 25000
        val query = """
            [out:json][timeout:15];
            (
              node["amenity"="hospital"](around:$radiusMeters,${coords.latitude},${coords.longitude});
              way["amenity"="hospital"](around:$radiusMeters,${coords.latitude},${coords.longitude});
            );
            out center tags 25;
        """.trimIndent()

        var response: OverpassResponse? = null

        try {
            response = overpassApi.queryHospitals(query)
        } catch (_: Exception) {
            // Try backup Overpass instance if primary fails
            try {
                response = overpassBackupApi.queryHospitals(query)
            } catch (backupError: Exception) {
                throw backupError
            }
        }

        val elements = response.elements ?: emptyList()
        val parsedList = elements.mapNotNull { element ->
            parseOverpassHospital(element, coords)
        }

        // Deduplicate by name and coordinates, sort by distance ascending
        return parsedList
            .distinctBy { it.name.lowercase(Locale.ROOT) to it.coordinates.latitude }
            .sortedBy { it.distanceKm }
    }

    private fun parseOverpassHospital(
        element: OverpassElementDto,
        originCoords: Coordinates
    ): Hospital? {
        val tags = element.tags ?: return null
        val rawName = tags["name"] ?: tags["name:en"] ?: tags["int_name"] ?: return null
        val cleanName = rawName.trim()
        if (cleanName.isBlank()) return null

        val lat = element.lat ?: element.center?.lat ?: return null
        val lon = element.lon ?: element.center?.lon ?: return null
        val hospitalCoords = Coordinates(lat, lon)

        val distance = GeoUtils.calculateDistanceKm(originCoords, hospitalCoords)

        // Parse address strictly from verified tags without fabricating
        val fullAddr = tags["addr:full"]
        val street = tags["addr:street"]
        val city = tags["addr:city"] ?: tags["addr:district"]
        val postcode = tags["addr:postcode"]

        val assembledAddress = when {
            !fullAddr.isNullOrBlank() -> fullAddr
            !street.isNullOrBlank() && !city.isNullOrBlank() -> "$street, $city"
            !street.isNullOrBlank() -> street
            !city.isNullOrBlank() -> city
            !postcode.isNullOrBlank() -> "Near PIN $postcode"
            else -> "Address not registered in public directory"
        }

        // Specialties: parse ONLY if explicitly tagged; never infer or invent
        val specialtiesTag = tags["healthcare:speciality"] ?: tags["speciality"]
        val specialtiesList = specialtiesTag?.split(";", ",")
            ?.map { it.trim().replace('_', ' ').replaceFirstChar { c -> c.titlecase(Locale.ROOT) } }
            ?.filter { it.isNotBlank() }
            ?.takeIf { it.isNotEmpty() }

        // Category/Type
        val category = tags["healthcare"]?.replace('_', ' ')?.replaceFirstChar { it.titlecase(Locale.ROOT) }
            ?: tags["operator:type"]?.replaceFirstChar { it.titlecase(Locale.ROOT) }
            ?: "General Hospital"

        // Verified phone
        val phone = (tags["phone"] ?: tags["contact:phone"])?.trim()?.takeIf { it.isNotBlank() }

        // Opening hours
        val hours = tags["opening_hours"]?.trim()?.takeIf { it.isNotBlank() }

        // Emergency services
        val emergencyTag = tags["emergency"]?.lowercase(Locale.ROOT)
        val emergencyAvailable = when (emergencyTag) {
            "yes" -> true
            "no" -> false
            else -> null
        }

        val operator = tags["operator"]?.trim()?.takeIf { it.isNotBlank() }
        val website = (tags["website"] ?: tags["contact:website"])?.trim()?.takeIf { it.isNotBlank() }

        return Hospital(
            id = "${element.type ?: "node"}_${element.id}",
            name = cleanName,
            category = category,
            address = assembledAddress,
            distanceKm = distance,
            coordinates = hospitalCoords,
            specialties = specialtiesList,
            verifiedPhoneNumber = phone,
            openingHours = hours,
            emergencyAvailable = emergencyAvailable,
            rating = null, // Strictly null: OSM does not track ratings; never fabricate
            operator = operator,
            website = website
        )
    }
}
