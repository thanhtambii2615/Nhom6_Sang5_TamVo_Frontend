package com.vothanhtam.vothanhtam_1990.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val role: String = "Customer"
)

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)

data class AuthResponse(
    val token: String?,
    val message: String?,
    val requiresOtp: Boolean?,
    val roles: List<String>?
)

data class ChildDeviceDto(
    val id: String,
    val deviceName: String,
    val deviceCode: String,
    val isLocked: Boolean,
    val batteryLevel: Int,
    val lastActive: String
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
    val deviceId: String,
    val latitude: Double,
    val longitude: Double,
    val noiseLevel: Double,
    val timestamp: String
)

data class RequestExtensionDto(
    val childDeviceId: String,
    val extraMinutes: Int,
    val reason: String
)
