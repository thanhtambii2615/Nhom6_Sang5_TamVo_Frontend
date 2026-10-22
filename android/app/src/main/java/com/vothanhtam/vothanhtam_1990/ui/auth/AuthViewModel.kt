package com.vothanhtam.vothanhtam_1990.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vothanhtam.vothanhtam_1990.data.local.TokenManager
import com.vothanhtam.vothanhtam_1990.data.model.LoginRequest
import com.vothanhtam.vothanhtam_1990.data.model.RegisterRequest
import com.vothanhtam.vothanhtam_1990.data.model.VerifyOtpRequest
import com.vothanhtam.vothanhtam_1990.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String, val requiresOtp: Boolean = false) : AuthState()
    data class OtpRequired(val email: String) : AuthState()
    data class LoggedIn(val role: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authApi = RetrofitClient.provideAuthApi(application)
    private val tokenManager = TokenManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.requiresOtp == true) {
                        _authState.value = AuthState.OtpRequired(email)
                    } else if (!body.token.isNullOrEmpty()) {
                        tokenManager.saveToken(body.token)
                        val role = body.roles?.firstOrNull() ?: "Customer"
                        tokenManager.saveUserRole(role)
                        _authState.value = AuthState.LoggedIn(role)
                    } else {
                        _authState.value = AuthState.Success(body.message ?: "Login successful")
                    }
                } else {
                    _authState.value = AuthState.Error("Login failed: ${response.errorBody()?.string() ?: "Unknown error"}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun register(email: String, pass: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.register(RegisterRequest(email, pass, role))
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success("Registration successful! Please login.")
                } else {
                    _authState.value = AuthState.Error("Registration failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun verifyOtp(email: String, otp: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.verifyOtp(VerifyOtpRequest(email, otp))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (!body.token.isNullOrEmpty()) {
                        tokenManager.saveToken(body.token)
                        val role = body.roles?.firstOrNull() ?: "Customer"
                        tokenManager.saveUserRole(role)
                        _authState.value = AuthState.LoggedIn(role)
                    } else {
                        _authState.value = AuthState.Success("OTP verified successfully")
                    }
                } else {
                    _authState.value = AuthState.Error("OTP verification failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}
