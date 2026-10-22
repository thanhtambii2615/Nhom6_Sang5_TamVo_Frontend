package com.example.childcare.ui.screens.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.childcare.data.model.ChildDeviceDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceListScreen(
    viewModel: ChildDeviceViewModel,
    onNavigateAdd: () -> Unit,
    onNavigateEdit: (ChildDeviceDto) -> Unit,
    onNavigateDetail: (ChildDeviceDto) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var deviceToDelete by remember { mutableStateOf<ChildDeviceDto?>(null) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Child Devices Management") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateAdd) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is DeviceUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DeviceUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadDevices() }) {
                            Text("Retry")
                        }
                    }
                }
                is DeviceUiState.Success -> {
                    if (state.devices.isEmpty()) {
                        Text("No child devices found. Add one!", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.devices) { device ->
                                DeviceItemCard(
                                    device = device,
                                    onClick = { onNavigateDetail(device) },
                                    onEdit = { onNavigateEdit(device) },
                                    onDelete = { deviceToDelete = device }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    deviceToDelete?.let { device ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text("Delete Device") },
            text = { Text("Are you sure you want to delete ${device.childName}'s device (${device.deviceName})?") },
            confirmButton = {
                TextButton(onClick = {
                    device.id?.let { viewModel.deleteDevice(it) }
                    deviceToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DeviceItemCard(
    device: ChildDeviceDto,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(device.childName, style = MaterialTheme.typography.titleMedium)
                Text("Age: ${device.age}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Device: ${device.deviceName}", style = MaterialTheme.typography.bodySmall)
            Text("Battery / Status: ${if (device.isInstantLocked == true) "LOCKED" else "Active"}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Edit") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
