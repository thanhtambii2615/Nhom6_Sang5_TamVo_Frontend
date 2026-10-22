package com.example.childcare.ui.screens.telemetry

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun SafetyMonitorScreen(
    deviceId: String,
    viewModel: SafetyMonitorViewModel
) {
    val context = LocalContext.current
    val noiseLevel by viewModel.noiseLevel.collectAsState()
    val isFallDetected by viewModel.isFallDetected.collectAsState()
    val isSOSActive by viewModel.isSOSActive.collectAsState()
    val isOutOfBounds by viewModel.isOutOfBounds.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted) {
            viewModel.startAudioMeasurement()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudioMeasurement()
            viewModel.stopSensorListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Child Safety Monitor", style = MaterialTheme.typography.headlineMedium)

        if (noiseLevel > 85.0) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text("WARNING: High Noise Level (> 85 dB) detected!", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
        if (isFallDetected) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("CRITICAL: Fall Detected!", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { viewModel.resetFall() }) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            }
        }
        if (isSOSActive) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.Red)) {
                Text("EMERGENCY: SOS Active!", modifier = Modifier.padding(16.dp), color = Color.White, style = MaterialTheme.typography.headlineSmall)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Environment & Sensors", style = MaterialTheme.typography.titleMedium)
                Text("Estimated Noise: ${noiseLevel.toInt()} dB ${if (noiseLevel > 85) "(Loud!)" else "(Normal)"}")
                Text("Disclaimer: Estimated acoustic level, not professional measurement equipment.")
                Text("Fall Sensor Status: ${if (isFallDetected) "Fall Triggered" else "Monitoring..."}")
                Text("Safe Zone Status: ${if (isOutOfBounds) "OUT OF BOUNDS" else "Inside Safe Zone"}")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(if (isSOSActive) Color.DarkGray else Color.Red)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            viewModel.triggerSOS(!isSOSActive, deviceId)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSOSActive) "TAP/LONG PRESS TO CANCEL SOS" else "HOLD FOR SOS EMERGENCY",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
