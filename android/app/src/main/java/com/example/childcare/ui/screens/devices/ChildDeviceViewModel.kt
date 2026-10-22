package com.example.childcare.ui.screens.devices

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcare.data.model.ChildDeviceDto
import com.example.childcare.data.model.RequestExtensionDto
import com.example.childcare.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DeviceUiState {
    object Loading : DeviceUiState()
    data class Success(val devices: List<ChildDeviceDto>) : DeviceUiState()
    data class Error(val message: String) : DeviceUiState()
}

class ChildDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.provideChildManagementApi(application)

    private val _uiState = MutableStateFlow<DeviceUiState>(DeviceUiState.Loading)
    val uiState: StateFlow<DeviceUiState> = _uiState

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage

    init {
        loadDevices()
    }

    fun loadDevices() {
        viewModelScope.launch {
            _uiState.value = DeviceUiState.Loading
            try {
                val response = api.getDevices()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = DeviceUiState.Success(response.body()!!)
                } else {
                    _uiState.value = DeviceUiState.Error("Failed to load devices: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = DeviceUiState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun instantLock(deviceId: String, reason: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.instantLock(deviceId)
                if (response.isSuccessful) {
                    _actionMessage.value = "Device locked successfully. Reason: $reason"
                    loadDevices()
                    onSuccess()
                } else {
                    _actionMessage.value = "Failed to lock device"
                }
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Error locking device"
            }
        }
    }

    fun requestExtension(deviceId: String, minutes: Int, reason: String) {
        viewModelScope.launch {
            try {
                val request = RequestExtensionDto(childDeviceId = deviceId, extraMinutes = minutes, reason = reason)
                val response = api.requestExtension(deviceId, request)
                if (response.isSuccessful) {
                    _actionMessage.value = "Extension requested successfully"
                } else {
                    _actionMessage.value = "Failed to request extension"
                }
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Error requesting extension"
            }
        }
    }

    fun addDevice(
        childName: String,
        age: Int,
        deviceName: String,
        lat: Double,
        lng: Double,
        radius: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val newDevice = ChildDeviceDto(
                    id = null,
                    childName = childName,
                    age = age,
                    deviceName = deviceName,
                    parentUserId = null,
                    pairingQrCode = null,
                    latitude = lat,
                    longitude = lng,
                    currentNoiseDecibel = 40.0,
                    isFallDetected = false,
                    isSOSActive = false,
                    safeZoneLatitude = lat,
                    safeZoneLongitude = lng,
                    safeZoneRadiusMeters = radius,
                    isInstantLocked = false,
                    lockMessage = null,
                    hasRequestedExtension = false,
                    lastUpdated = null
                )
                val response = api.addDevice(newDevice)
                if (response.isSuccessful) {
                    _actionMessage.value = "Device added successfully"
                    loadDevices()
                    onSuccess()
                } else {
                    _actionMessage.value = "Failed to add device"
                }
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Error adding device"
            }
        }
    }

    fun updateDevice(
        id: String,
        childName: String,
        age: Int,
        deviceName: String,
        lat: Double,
        lng: Double,
        radius: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val updatedDevice = ChildDeviceDto(
                    id = id,
                    childName = childName,
                    age = age,
                    deviceName = deviceName,
                    parentUserId = null,
                    pairingQrCode = null,
                    latitude = lat,
                    longitude = lng,
                    currentNoiseDecibel = null,
                    isFallDetected = null,
                    isSOSActive = null,
                    safeZoneLatitude = lat,
                    safeZoneLongitude = lng,
                    safeZoneRadiusMeters = radius,
                    isInstantLocked = null,
                    lockMessage = null,
                    hasRequestedExtension = null,
                    lastUpdated = null
                )
                val response = api.updateDevice(id, updatedDevice)
                if (response.isSuccessful) {
                    _actionMessage.value = "Device updated successfully"
                    loadDevices()
                    onSuccess()
                } else {
                    _actionMessage.value = "Failed to update device"
                }
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Error updating device"
            }
        }
    }

    fun deleteDevice(id: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteDevice(id)
                if (response.isSuccessful) {
                    _actionMessage.value = "Device deleted successfully"
                    loadDevices()
                } else {
                    _actionMessage.value = "Failed to delete device"
                }
            } catch (e: Exception) {
                _actionMessage.value = e.localizedMessage ?: "Error deleting device"
            }
        }
    }

    fun clearMessage() {
        _actionMessage.value = null
    }
}
