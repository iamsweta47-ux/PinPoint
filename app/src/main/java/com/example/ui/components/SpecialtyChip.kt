package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpecialtySection(
    specialties: List<String>?,
    modifier: Modifier = Modifier
) {
    if (specialties.isNullOrEmpty()) {
        Text(
            text = "Specialty information unavailable.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = modifier.padding(vertical = 2.dp)
        )
    } else {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)
        ) {
            specialties.take(6).forEach { specialty ->
                SpecialtyChip(specialty = specialty)
            }
        }
    }
}

@Composable
fun SpecialtyChip(
    specialty: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("specialty_chip_${specialty.lowercase().replace(' ', '_')}")
    ) {
        Text(
            text = specialty,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
