package com.example.childcare.data.repository

import android.content.Context
import com.example.childcare.data.model.TelemetryDto
import com.example.childcare.data.remote.RetrofitClient

class LocationRepository(context: Context) {
    private val api = RetrofitClient.provideChildManagementApi(context)

    suspend fun sendTelemetry(deviceId: String, lat: Double, lng: Double, noise: Double, fall: Boolean, sos: Boolean): Boolean {
        return try {
            val dto = TelemetryDto(
                deviceId = deviceId,
                latitude = lat,
                longitude = lng,
                noiseLevel = noise,
                isFallDetected = fall,
                isSOSActive = sos,
                timestamp = null
            )
            val response = api.sendTelemetry(deviceId, dto)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
