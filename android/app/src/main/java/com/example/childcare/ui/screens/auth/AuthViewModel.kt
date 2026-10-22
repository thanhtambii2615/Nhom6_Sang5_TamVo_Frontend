package com.example.childcare.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcare.data.local.TokenManager
import com.example.childcare.data.model.GoogleLoginRequest
import com.example.childcare.data.model.LoginRequest
import com.example.childcare.data.model.RegisterRequest
import com.example.childcare.data.model.VerifyOtpRequest
import com.example.childcare.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class OtpRequired(val username: String) : AuthState()
    data class LoggedIn(val role: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authApi = RetrofitClient.provideAuthApi(application)
    private val tokenManager = TokenManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, pass: String) {
        if (username.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Username and password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.login(LoginRequest(username, pass))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.requiresOtp == true) {
                        _authState.value = AuthState.OtpRequired(username)
                    } else if (!body.token.isNullOrEmpty()) {
                        tokenManager.saveToken(body.token)
                        val role = body.roles?.firstOrNull() ?: "Parent"
                        tokenManager.saveUserRole(role)
                        tokenManager.saveUsername(username)
                        _authState.value = AuthState.LoggedIn(role)
                    } else {
                        _authState.value = AuthState.OtpRequired(username)
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Login failed"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error during login")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.googleLogin(GoogleLoginRequest(idToken))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (!body.token.isNullOrEmpty()) {
                        tokenManager.saveToken(body.token)
                        val role = body.roles?.firstOrNull() ?: "Parent"
                        tokenManager.saveUserRole(role)
                        _authState.value = AuthState.LoggedIn(role)
                    } else {
                        _authState.value = AuthState.Error("Google login token missing")
                    }
                } else {
                    _authState.value = AuthState.Error("Google login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error during Google login")
            }
        }
    }

    fun register(username: String, email: String, pass: String, confirmPass: String) {
        if (username.isBlank()) {
            _authState.value = AuthState.Error("Username cannot be empty")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email address format")
            return
        }
        if (pass.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        if (pass != confirmPass) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = pass,
                    initials = username.take(2).uppercase(),
                    role = "Parent"
                )
                val response = authApi.register(request)
                if (response.isSuccessful) {
                    _authState.value = AuthState.Success("Registration successful! Please login.")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                    _authState.value = AuthState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error during registration")
            }
        }
    }

    fun verifyOtp(username: String, otpCode: String) {
        if (otpCode.length != 6) {
            _authState.value = AuthState.Error("OTP code must be exactly 6 digits")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = authApi.verifyOtp(VerifyOtpRequest(username, otpCode))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    if (!token.isNullOrEmpty()) {
                        tokenManager.saveToken(token)
                        val role = body.roles?.firstOrNull() ?: "Parent"
                        tokenManager.saveUserRole(role)
                        tokenManager.saveUsername(username)
                        _authState.value = AuthState.LoggedIn(role)
                    } else {
                        _authState.value = AuthState.Error("Invalid or expired OTP token response")
                    }
                } else {
                    _authState.value = AuthState.Error("OTP verification failed. Incorrect or expired code.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Network error during OTP verification")
            }
        }
    }
}
