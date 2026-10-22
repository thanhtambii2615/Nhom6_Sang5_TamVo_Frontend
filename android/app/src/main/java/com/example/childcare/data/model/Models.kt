package com.example.childcare.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val initials: String = "PC",
    val role: String = "Parent"
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class GoogleLoginRequest(
    val idToken: String
)

data class VerifyOtpRequest(
    val username: String,
    val otpCode: String
)

data class AuthResponse(
    val token: String?,
    val message: String?,
    val requiresOtp: Boolean?,
    val roles: List<String>?
)

data class ChildDeviceDto(
    val id: String?,
    val childName: String,
    val age: Int,
    val deviceName: String,
    val parentUserId: String?,
    val pairingQrCode: String?,
    val latitude: Double?,
    val longitude: Double?,
    val cleanNoiseDecibel: Double?,
    val isFallDetected: Boolean?,
    val isSOSActive: Boolean?,
    val safeZoneLatitude: Double?,
    val safeZoneLongitude: Double?,
    val safeZoneRadiusMeters: Double?,
    val isInstantLocked: Boolean?,
    val lockMessage: String?,
    val hasRequestedExtension: Boolean?,
    val lastUpdated: String?,
    val currentNoiseDecibel: Double? = null
)

data class TimeLimitRuleDto(
    val id: String,
    val childDeviceId: String,
    val startTime: String,
    val endTime: String,
    val allowedDays: String,
    val isEnabled: Boolean
)

data class TelemetryDto(
    val deviceId: String?,
    val latitude: Double,
    val longitude: Double,
    val noiseLevel: Double,
    val isFallDetected: Boolean?,
    val isSOSActive: Boolean?,
    val timestamp: String?
)

data class RequestExtensionDto(
    val childDeviceId: String,
    val extraMinutes: Int,
    val reason: String
)
