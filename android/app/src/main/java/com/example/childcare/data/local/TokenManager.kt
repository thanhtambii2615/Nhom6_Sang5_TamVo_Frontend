package com.example.childcare.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenManager(private val context: Context) {
    companion object {
        val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_ROLE_KEY = stringPreferencesKey("user_role")
        val USER_USERNAME_KEY = stringPreferencesKey("user_username")
        val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[JWT_TOKEN_KEY] }

    val roleFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_ROLE_KEY] ?: "" }

    val biometricEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[BIOMETRIC_ENABLED_KEY] ?: false }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role
        }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_USERNAME_KEY] = username
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(USER_USERNAME_KEY)
            preferences.remove(BIOMETRIC_ENABLED_KEY)
        }
    }
}
