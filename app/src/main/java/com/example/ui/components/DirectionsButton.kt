package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.domain.model.Hospital

@Composable
fun DirectionsButton(
    hospital: Hospital,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    val context = LocalContext.current

    Button(
        onClick = { openDirections(context, hospital) },
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .testTag("view_directions_button_${hospital.id}"),
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.filledTonalButtonColors()
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Directions Icon",
                modifier = Modifier.width(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("View Directions")
        }
    }
}

/**
 * Standard Android intent/deep-link to open hospital coordinates in Google Maps,
 * falling back gracefully to the web-based Google Maps route or browser.
 */
fun openDirections(context: Context, hospital: Hospital) {
    val lat = hospital.coordinates.latitude
    val lon = hospital.coordinates.longitude
    val label = Uri.encode(hospital.name)

    // geo intent format: geo:0,0?q=lat,lng(label)
    val geoUri = Uri.parse("geo:0,0?q=$lat,$lon($label)")
    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
        setPackage("com.google.android.apps.maps")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
            return
        }
    } catch (_: Exception) {
        // Fall back below
    }

    // Generic geo intent
    val genericGeoIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        if (genericGeoIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(genericGeoIntent)
            return
        }
    } catch (_: Exception) {
        // Fall back below
    }

    // Web fallback
    val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lon")
    val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(webIntent)
    } catch (_: Exception) {
        Toast.makeText(context, "No map or browser app available to display directions.", Toast.LENGTH_SHORT).show()
    }
}
