package com.example.childcare.ui.screens.qr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcare.data.model.ChildDeviceDto
import com.example.childcare.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class QrPairingState {
    object Scanning : QrPairingState()
    object Loading : QrPairingState()
    data class Success(val device: ChildDeviceDto) : QrPairingState()
    data class Error(val message: String) : QrPairingState()
}

class QrPairingViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.provideChildManagementApi(application)

    private val _state = MutableStateFlow<QrPairingState>(QrPairingState.Scanning)
    val state: StateFlow<QrPairingState> = _state

    fun pairDeviceWithQr(qrCode: String) {
        viewModelScope.launch {
            _state.value = QrPairingState.Loading
            try {
                val response = api.pairQr(qrCode)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = QrPairingState.Success(response.body()!!)
                } else {
                    _state.value = QrPairingState.Error("Ghép đôi thất bại: Mã QR không hợp lệ hoặc đã được sử dụng.")
                }
            } catch (e: Exception) {
                _state.value = QrPairingState.Error(e.localizedMessage ?: "Lỗi kết nối mạng")
            }
        }
    }

    fun resetScan() {
        _state.value = QrPairingState.Scanning
    }
}
