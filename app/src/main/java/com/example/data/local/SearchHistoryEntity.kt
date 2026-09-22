package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val pincode: String,
    val placeName: String,
    val district: String?,
    val state: String?,
    val timestamp: Long = System.currentTimeMillis()
)
