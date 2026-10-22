package com.example.childcare.ui.screens.devices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    viewModel: ChildDeviceViewModel,
    onBack: () -> Unit
) {
    var childName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("10.762622") }
    var longitude by remember { mutableStateOf("106.660172") }
    var radius by remember { mutableStateOf("100.0") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Child Device") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = childName,
                onValueChange = { childName = it },
                label = { Text("Child Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text("Device Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Safe Zone Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Safe Zone Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = radius,
                onValueChange = { radius = it },
                label = { Text("Safe Zone Radius (Meters)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addDevice(
                        childName = childName,
                        age = age.toIntOrNull() ?: 10,
                        deviceName = deviceName,
                        lat = latitude.toDoubleOrNull() ?: 10.762622,
                        lng = longitude.toDoubleOrNull() ?: 106.660172,
                        radius = radius.toDoubleOrNull() ?: 100.0,
                        onSuccess = onBack
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Device")
            }
        }
    }
}
