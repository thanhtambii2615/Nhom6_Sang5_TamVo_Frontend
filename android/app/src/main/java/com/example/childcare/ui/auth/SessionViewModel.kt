package com.example.childcare.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcare.data.local.TokenManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)

    val tokenFlow: StateFlow<String?> = tokenManager.tokenFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val roleFlow: StateFlow<String?> = tokenManager.roleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearSession()
            onLoggedOut()
        }
    }
}
