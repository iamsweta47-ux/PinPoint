package com.example.data.util

import com.example.domain.model.Coordinates
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GeoUtils {
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the great-circle distance between two geographic points
     * using the Haversine formula.
     */
    fun calculateDistanceKm(from: Coordinates, to: Coordinates): Double {
        val latDistance = Math.toRadians(to.latitude - from.latitude)
        val lonDistance = Math.toRadians(to.longitude - from.longitude)

        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
}
