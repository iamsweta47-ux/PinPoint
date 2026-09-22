package com.example.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SearchHistoryDao
import com.example.data.local.SearchHistoryEntity
import com.example.data.repository.HospitalRepository
import com.example.data.repository.PinCodeRepository
import com.example.domain.model.Coordinates
import com.example.domain.model.Hospital
import com.example.domain.model.SearchResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val pincodeInput: String = "",
    val isLoading: Boolean = false,
    val searchResult: SearchResult? = null,
    val validationError: String? = null,
    val selectedHospital: Hospital? = null,
    val locationErrorMessage: String? = null
)

class HomeViewModel(
    private val hospitalRepository: HospitalRepository,
    private val pinCodeRepository: PinCodeRepository,
    private val searchHistoryDao: SearchHistoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val recentSearches: StateFlow<List<SearchHistoryEntity>> = searchHistoryDao.getRecentSearches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onPincodeChange(input: String) {
        val filtered = input.filter { it.isDigit() }.take(6)
        val error = when {
            filtered.isEmpty() -> null
            filtered.first() == '0' -> "Indian PIN codes cannot start with 0."
            filtered.length in 1..5 -> null // In progress of typing
            filtered.length == 6 -> null
            else -> "Invalid PIN code format."
        }
        _uiState.value = _uiState.value.copy(
            pincodeInput = filtered,
            validationError = error
        )
    }

    fun searchHospitals(targetPin: String = _uiState.value.pincodeInput) {
        val cleanPin = targetPin.trim()
        if (!pinCodeRepository.isValidPinCode(cleanPin)) {
            _uiState.value = _uiState.value.copy(
                validationError = "Please enter a valid 6-digit Indian PIN code (e.g. 110001, 250001)."
            )
            return
        }

        if (_uiState.value.isLoading) return // Prevent duplicate requests in flight

        _uiState.value = _uiState.value.copy(
            pincodeInput = cleanPin,
            isLoading = true,
            validationError = null,
            locationErrorMessage = null
        )

        viewModelScope.launch {
            val result = hospitalRepository.findTopHospitalsByPinCode(cleanPin)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                searchResult = result
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun searchByDeviceLocation(context: Context) {
        if (_uiState.value.isLoading) return

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            locationErrorMessage = null,
            validationError = null
        )

        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val cts = CancellationTokenSource()

        fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val coords = Coordinates(location.latitude, location.longitude)
                    viewModelScope.launch {
                        val result = hospitalRepository.findHospitalsNearCoordinates(coords, null)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            searchResult = result
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        locationErrorMessage = "Could not retrieve GPS coordinates. Please enter a 6-digit PIN code manually."
                    )
                }
            }
            .addOnFailureListener { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locationErrorMessage = "Location lookup failed (${error.localizedMessage}). You can search using your PIN code directly."
                )
            }
    }

    fun setLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            locationErrorMessage = "Location permission is optional. You can search anytime using your 6-digit PIN code."
        )
    }

    fun clearLocationError() {
        _uiState.value = _uiState.value.copy(locationErrorMessage = null)
    }

    fun selectRecentSearch(pincode: String) {
        onPincodeChange(pincode)
        searchHospitals(pincode)
    }

    fun deleteRecentSearch(pincode: String) {
        viewModelScope.launch {
            searchHistoryDao.deleteByPincode(pincode)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            searchHistoryDao.clearAll()
        }
    }

    fun selectHospital(hospital: Hospital) {
        _uiState.value = _uiState.value.copy(selectedHospital = hospital)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(context)
            val pinRepo = PinCodeRepository()
            val hospitalRepo = HospitalRepository(
                pinCodeRepository = pinRepo,
                database = db
            )
            return HomeViewModel(hospitalRepo, pinRepo, db.searchHistoryDao()) as T
        }
    }
}
