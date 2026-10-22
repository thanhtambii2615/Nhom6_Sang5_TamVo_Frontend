package com.example.childcare.ui.screens.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.childcare.data.model.ChildDeviceDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    device: ChildDeviceDto,
    viewModel: ChildDeviceViewModel,
    onBack: () -> Unit
) {
    var showLockDialog by remember { mutableStateOf(false) }
    var lockReason by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Detail: ${device.childName}") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Child Name: ${device.childName}", style = MaterialTheme.typography.titleMedium)
            Text("Age: ${device.age}")
            Text("Device Name: ${device.deviceName}")
            Text("Lock Status: ${if (device.isInstantLocked == true) "LOCKED" else "Unlocked"}", color = if (device.isInstantLocked == true) Color.Red else Color.Green)
            Text("Current Noise: ${device.currentNoiseDecibel ?: 40.0} dB")
            Text("Fall Detected: ${if (device.isFallDetected == true) "YES" else "No"}")
            Text("SOS Active: ${if (device.isSOSActive == true) "ACTIVE!" else "Normal"}")
            Text("Safe Zone Radius: ${device.safeZoneRadiusMeters ?: 100.0} meters")
            Text("Extension Requested: ${if (device.hasRequestedExtension == true) "YES (Pending Review)" else "No"}")
            Text("Last Updated: ${device.lastUpdated ?: "N/A"}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showLockDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("KHÓA THIẾT BỊ NGAY (Instant Safety Lock)")
            }
        }
    }

    if (showLockDialog) {
        AlertDialog(
            onDismissRequest = { showLockDialog = false },
            title = { Text("Instant Safety Lock") },
            text = {
                Column {
                    Text("Enter reason for locking this device immediately:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = lockReason,
                        onValueChange = { lockReason = it },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    device.id?.let { id ->
                        viewModel.instantLock(id, lockReason) {
                            showLockDialog = false
                            onBack()
                        }
                    }
                }) {
                    Text("Lock Now", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
