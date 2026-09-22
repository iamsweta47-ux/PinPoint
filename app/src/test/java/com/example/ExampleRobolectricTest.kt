package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.PinCodeRepository
import com.example.data.util.GeoUtils
import com.example.domain.model.Coordinates
import com.example.domain.model.Hospital
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pincode Hospital Finder", appName)
    }

    @Test
    fun `verify Indian PIN code validation logic`() {
        val repo = PinCodeRepository()

        // Valid PIN codes
        assertTrue(repo.isValidPinCode("250001"))
        assertTrue(repo.isValidPinCode("110001"))
        assertTrue(repo.isValidPinCode("400001"))
        assertTrue(repo.isValidPinCode("560001"))
        assertTrue(repo.isValidPinCode(" 700001 "))

        // Invalid PIN codes
        assertFalse(repo.isValidPinCode("012345")) // Starts with 0
        assertFalse(repo.isValidPinCode("12345"))  // 5 digits
        assertFalse(repo.isValidPinCode("1234567"))// 7 digits
        assertFalse(repo.isValidPinCode("25000A"))// Contains letter
        assertFalse(repo.isValidPinCode(""))       // Empty
        assertFalse(repo.isValidPinCode("abcdef")) // Non-digits
    }

    @Test
    fun `verify Haversine distance calculation`() {
        // New Delhi to Meerut (~62 km)
        val delhi = Coordinates(28.6139, 77.2090)
        val meerut = Coordinates(28.9845, 77.7064)
        val distance = GeoUtils.calculateDistanceKm(delhi, meerut)

        assertTrue(distance in 60.0..75.0)
    }

    @Test
    fun `verify Hospital model distance formatting and verified phone check`() {
        val hospitalWithPhone = Hospital(
            id = "node_123",
            name = "City Care Hospital",
            category = "General Hospital",
            address = "Civil Lines, Meerut",
            distanceKm = 2.456,
            coordinates = Coordinates(28.98, 77.70),
            specialties = listOf("Cardiology", "Pediatrics"),
            verifiedPhoneNumber = "+91 121 2345678",
            openingHours = "24/7",
            emergencyAvailable = true,
            rating = null
        )

        assertEquals("2.5 km", hospitalWithPhone.formattedDistance)
        assertTrue(hospitalWithPhone.hasVerifiedPhone)

        val hospitalWithoutPhone = hospitalWithPhone.copy(
            distanceKm = 0.82,
            verifiedPhoneNumber = null
        )
        assertEquals("0.8 km", hospitalWithoutPhone.formattedDistance)
        assertFalse(hospitalWithoutPhone.hasVerifiedPhone)
    }
}

