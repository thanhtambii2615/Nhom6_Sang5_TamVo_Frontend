package com.vothanhtam.vothanhtam_1990.ui.parent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vothanhtam.vothanhtam_1990.data.model.ChildDeviceDto
import com.vothanhtam.vothanhtam_1990.data.model.RequestExtensionDto
import com.vothanhtam.vothanhtam_1990.data.model.TimeLimitRuleDto
import com.vothanhtam.vothanhtam_1990.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ChildManagementState {
    object Idle : ChildManagementState()
    object Loading : ChildManagementState()
    data class DevicesLoaded(val devices: List<ChildDeviceDto>) : ChildManagementState()
    data class RulesLoaded(val rules: List<TimeLimitRuleDto>) : ChildManagementState()
    data class Success(val message: String) : ChildManagementState()
    data class Error(val error: String) : ChildManagementState()
}

class ChildManagementViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.provideChildManagementApi(application)

    private val _state = MutableStateFlow<ChildManagementState>(ChildManagementState.Idle)
    val state: StateFlow<ChildManagementState> = _state

    fun loadDevices() {
        viewModelScope.launch {
            _state.value = ChildManagementState.Loading
            try {
                val response = api.getDevices()
                if (response.isSuccessful && response.body() != null) {
                    _state.value = ChildManagementState.DevicesLoaded(response.body()!!)
                } else {
                    _state.value = ChildManagementState.Error("Failed to load devices")
                }
            } catch (e: Exception) {
                _state.value = ChildManagementState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun addDevice(deviceName: String, deviceCode: String) {
        viewModelScope.launch {
            try {
                val newDevice = ChildDeviceDto(
                    id = "",
                    deviceName = deviceName,
                    deviceCode = deviceCode,
                    isLocked = false,
                    batteryLevel = 100,
                    lastActive = "Just now"
                )
                val response = api.addDevice(newDevice)
                if (response.isSuccessful) {
                    loadDevices()
                } else {
                    _state.value = ChildManagementState.Error("Failed to add device")
                }
            } catch (e: Exception) {
                _state.value = ChildManagementState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun deleteDevice(id: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteDevice(id)
                if (response.isSuccessful) {
                    loadDevices()
                } else {
                    _state.value = ChildManagementState.Error("Failed to delete device")
                }
            } catch (e: Exception) {
                _state.value = ChildManagementState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun instantLock(id: String) {
        viewModelScope.launch {
            try {
                val response = api.instantLock(id)
                if (response.isSuccessful) {
                    _state.value = ChildManagementState.Success("Device locked successfully")
                    loadDevices()
                } else {
                    _state.value = ChildManagementState.Error("Failed to lock device")
                }
            } catch (e: Exception) {
                _state.value = ChildManagementState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun requestExtension(id: String, minutes: Int, reason: String) {
        viewModelScope.launch {
            try {
                val response = api.requestExtension(id, RequestExtensionDto(id, minutes, reason))
                if (response.isSuccessful) {
                    _state.value = ChildManagementState.Success("Extension requested successfully")
                } else {
                    _state.value = ChildManagementState.Error("Failed to request extension")
                }
            } catch (e: Exception) {
                _state.value = ChildManagementState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun loadRules(childDeviceId: String) {
        viewModelScope.launch {
            try {
                val response = api.getRules(childDeviceId)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = ChildManagementState.RulesLoaded(response.body()!!)
                } else {
                    _state.value = ChildManagementState.Error("Failed to load rules")
                }
            } catch (e: Exception) {
                _state.value = ChildManagementState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}
