package com.example.childcare.ui.screens.telemetry

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TelemetryScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Telemetry & Noise Measurement (dB)", style = MaterialTheme.typography.headlineMedium)
    }
}
