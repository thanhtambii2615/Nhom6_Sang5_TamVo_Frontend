package com.vothanhtam.vothanhtam_1990.data.remote

import com.vothanhtam.vothanhtam_1990.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/Authenticate/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/Authenticate/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/Authenticate/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>
}

interface ChildManagementApi {
    @GET("api/ChildManagement/devices")
    suspend fun getDevices(): Response<List<ChildDeviceDto>>

    @POST("api/ChildManagement/devices")
    suspend fun addDevice(@Body device: ChildDeviceDto): Response<ChildDeviceDto>

    @PUT("api/ChildManagement/devices/{id}")
    suspend fun updateDevice(@Path("id") id: String, @Body device: ChildDeviceDto): Response<ChildDeviceDto>

    @DELETE("api/ChildManagement/devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String): Response<Unit>

    @GET("api/ChildManagement/rules/{childDeviceId}")
    suspend fun getRules(@Path("childDeviceId") childDeviceId: String): Response<List<TimeLimitRuleDto>>

    @POST("api/ChildManagement/rules")
    suspend fun addRule(@Body rule: TimeLimitRuleDto): Response<TimeLimitRuleDto>

    @PUT("api/ChildManagement/rules/{id}")
    suspend fun updateRule(@Path("id") id: String, @Body rule: TimeLimitRuleDto): Response<TimeLimitRuleDto>

    @DELETE("api/ChildManagement/rules/{id}")
    suspend fun deleteRule(@Path("id") id: String): Response<Unit>

    @POST("api/ChildManagement/devices/{id}/instant-lock")
    suspend fun instantLock(@Path("id") id: String): Response<Unit>

    @POST("api/ChildManagement/devices/{id}/request-extension")
    suspend fun requestExtension(@Path("id") id: String, @Body request: RequestExtensionDto): Response<Unit>

    @POST("api/ChildManagement/pair-qr/{qrCode}")
    suspend fun pairQr(@Path("qrCode") qrCode: String): Response<ChildDeviceDto>

    @POST("api/ChildManagement/telemetry/{deviceId}")
    suspend fun sendTelemetry(@Path("deviceId") deviceId: String, @Body telemetry: TelemetryDto): Response<Unit>
}
