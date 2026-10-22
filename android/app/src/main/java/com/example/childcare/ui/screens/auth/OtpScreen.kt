package com.example.childcare.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    username: String,
    authViewModel: AuthViewModel,
    onVerified: (String) -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    var timeLeftSeconds by remember { mutableStateOf(300) }
    LaunchedEffect(key1 = timeLeftSeconds) {
        if (timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("OTP Verification", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter the 6-digit code sent for user: $username", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Time remaining: $timeFormatted", color = if (timeLeftSeconds > 60) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6) otpCode = it },
            label = { Text("6-Digit OTP Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.verifyOtp(username, otpCode) },
            enabled = otpCode.length == 6,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify OTP")
        }

        when (authState) {
            is AuthState.Loading -> {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            is AuthState.LoggedIn -> {
                val role = (authState as AuthState.LoggedIn).role
                LaunchedEffect(role) {
                    onVerified(role)
                }
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text((authState as AuthState.Error).error, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
