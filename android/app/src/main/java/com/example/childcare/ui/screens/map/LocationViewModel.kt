package com.example.childcare.ui.screens.map

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcare.data.repository.LocationRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.*

sealed class LocationUiState {
    object PermissionDenied : LocationUiState()
    object GettingLocation : LocationUiState()
    object NoGps : LocationUiState()
    data class LocationAvailable(
        val lat: Double,
        val lng: Double,
        val safeZoneLat: Double,
        val safeZoneLng: Double,
        val safeZoneRadius: Double,
        val distanceMeters: Double,
        val isOutOfBounds: Boolean
    ) : LocationUiState()
}

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val repository = LocationRepository(application)

    private val _uiState = MutableStateFlow<LocationUiState>(LocationUiState.GettingLocation)
    val uiState: StateFlow<LocationUiState> = _uiState

    private val defaultSafeZoneLat = 10.762622
    private val defaultSafeZoneLng = 106.660172
    private val defaultRadiusMeters = 100.0

    @SuppressLint("MissingPermission")
    fun startLocationTracking(deviceId: String) {
        viewModelScope.launch {
            _uiState.value = LocationUiState.GettingLocation
            while (isActive) {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val lat = location.latitude
                            val lng = location.longitude
                            val distance = calculateDistance(lat, lng, defaultSafeZoneLat, defaultSafeZoneLng)
                            val isOut = distance > defaultRadiusMeters

                            _uiState.value = LocationUiState.LocationAvailable(
                                lat = lat,
                                lng = lng,
                                safeZoneLat = defaultSafeZoneLat,
                                safeZoneLng = defaultSafeZoneLng,
                                safeZoneRadius = defaultRadiusMeters,
                                distanceMeters = distance,
                                isOutOfBounds = isOut
                            )

                            viewModelScope.launch {
                                repository.sendTelemetry(deviceId, lat, lng, 45.0, false, false)
                            }
                        } else {
                            _uiState.value = LocationUiState.NoGps
                        }
                    }
                    .addOnFailureListener {
                        _uiState.value = LocationUiState.NoGps
                    }

                delay(15000L) // 15 seconds interval
            }
        }
    }

    fun setPermissionDenied() {
        _uiState.value = LocationUiState.PermissionDenied
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
