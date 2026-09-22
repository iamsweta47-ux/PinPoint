package com.example.domain.model

sealed interface SearchResult {
    data class Success(
        val pincode: String,
        val location: PinCodeLocation,
        val hospitals: List<Hospital>
    ) : SearchResult

    data class Empty(
        val pincode: String,
        val location: PinCodeLocation?,
        val message: String
    ) : SearchResult

    data class Error(
        val userFriendlyMessage: String,
        val technicalReason: String? = null
    ) : SearchResult
}
