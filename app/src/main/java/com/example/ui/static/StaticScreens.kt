package com.example.ui.static

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("privacy_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            PolicyHeaderCard(
                icon = Icons.Default.Security,
                title = "Privacy by Design",
                subtitle = "We respect your digital privacy. Here is an honest disclosure of our data practices."
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("1. No Personal or Medical Record Collection")
            BodyText(
                "Pincode Hospital Finder does NOT request, collect, store, or transmit any sensitive personal data, medical records, symptoms, diagnoses, or prescription details. The application operates strictly as a discovery tool for registered medical institutions."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("2. How PIN Codes Are Processed")
            BodyText(
                "When you input a 6-digit Indian PIN code, the code is sent to open geocoding services (such as OpenStreetMap Nominatim and the India Postal PIN directory) strictly to resolve approximate geographic center coordinates (latitude and longitude). We do not correlate your searches with any user profile or device identifier."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("3. Optional Device Location (GPS)")
            BodyText(
                "If you choose to use the 'Use My Location' shortcut, location permission (ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION) is requested exclusively to look up coordinates for immediate nearby hospital discovery. Location data is ephemeral, never stored on our servers, and never tracked in the background. You can decline this permission at any time and use the PIN code search freely."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("4. Local Search History Storage")
            BodyText(
                "For your convenience, recently searched PIN codes are saved locally on your device's internal SQLite database. You can clear this history completely at any time from the app settings."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("5. Third-Party Services & Open Data")
            BodyText(
                "Hospital records and geographical mappings are retrieved via OpenStreetMap Overpass API and India Postal public records. When you tap 'View Directions', the coordinates are opened via standard Android intents into your preferred maps application (such as Google Maps)."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfUseScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Use", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("terms_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            PolicyHeaderCard(
                icon = Icons.Default.Policy,
                title = "Important Medical Disclaimer",
                subtitle = "Please read these terms carefully before using this application."
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("1. Discovery & Informational Purpose Only")
            BodyText(
                "Pincode Hospital Finder is designed exclusively to facilitate discovery of nearby medical facilities based on Indian postal PIN codes. It is NOT a medical consultation app, emergency dispatch service, or doctor booking platform."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("2. Emergency Situations")
            BodyText(
                "In case of acute medical emergencies, immediately call national emergency services in India: 112 (National Emergency Number) or 108 (Ambulance / Emergency Services), or visit the nearest emergency room directly."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("3. Information Verification")
            BodyText(
                "Data displayed (names, addresses, specialties, telephone numbers, and operating hours) originates from open public directories (OpenStreetMap). Facilities may change their hours, bed availability, emergency status, or specialties without notice. Always contact the hospital directly to verify availability before traveling."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("4. Limitation of Liability")
            BodyText(
                "The developers and contributors of Pincode Hospital Finder assume no liability for medical decisions, transit delays, service unavailability, or inaccuracies in publicly reported directory entries."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("about_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            PolicyHeaderCard(
                icon = Icons.Default.Info,
                title = "Pincode Hospital Finder",
                subtitle = "Discover hospitals near any PIN code."
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Application Mission")
            BodyText(
                "Finding nearby medical centers during travel, relocation, or emergencies should be instant and reliable. Pincode Hospital Finder enables anyone across India to enter a 6-digit postal PIN code and identify the top 3 closest healthcare facilities with calculated driving distance, specialties, and navigation."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Open Data Attribution")
            BodyText(
                "• Hospital mapping and location data © OpenStreetMap contributors (under the Open Database License).\n" +
                "• Geocoding provided through OSM Nominatim and Zippopotam.\n" +
                "• Postal division references provided via the India Postal Directory."
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Version & Architecture")
            BodyText(
                "Version: 1.0.0 (Production Build)\n" +
                "Built with Kotlin, Jetpack Compose, Material 3, Clean Architecture, and Room local persistence."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact & Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("contact_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            PolicyHeaderCard(
                icon = Icons.Default.Email,
                title = "Support & Feedback",
                subtitle = "Have feedback, found a discrepancy, or want to suggest improvements?"
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionTitle("Email Support")
            BodyText("support@pincodehospitals.app\nResponses typically within 24–48 hours.")

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle("Reporting Outdated Hospital Info")
            BodyText(
                "Because our data is linked with OpenStreetMap, you can also directly contribute or update hospital operating hours, phone numbers, and emergency departments on openstreetmap.org."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PolicyHeaderCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 22.sp
    )
}
