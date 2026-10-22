package com.example.childcare.ui.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationScreen(
    deviceId: String,
    viewModel: LocationViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.startLocationTracking(deviceId)
        } else {
            viewModel.setPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            viewModel.startLocationTracking(deviceId)
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is LocationUiState.PermissionDenied -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Location permission is required to track location.", color = MaterialTheme.colorScheme.error)
                }
            }
            is LocationUiState.GettingLocation -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is LocationUiState.NoGps -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Unable to get GPS signal. Please check location settings.", color = MaterialTheme.colorScheme.error)
                }
            }
            is LocationUiState.LocationAvailable -> {
                val childLatLng = LatLng(state.lat, state.lng)
                val safeZoneLatLng = LatLng(state.safeZoneLat, state.safeZoneLng)
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(childLatLng, 16f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = childLatLng),
                        title = "Child Location"
                    )

                    Marker(
                        state = MarkerState(position = safeZoneLatLng),
                        title = "Safe Zone Center"
                    )
                    Circle(
                        center = safeZoneLatLng,
                        radius = state.safeZoneRadius,
                        fillColor = Color.Blue.copy(alpha = 0.2f),
                        strokeColor = Color.Blue,
                        strokeWidth = 2f
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (state.isOutOfBounds) Color.Red.copy(alpha = 0.8f) else Color.Green.copy(alpha = 0.8f))
                        .padding(16.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.isOutOfBounds) "WARNING: Child is OUTSIDE Safe Zone!" else "Safe: Within Safe Zone",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Distance to Safe Zone Center: ${state.distanceMeters.toInt()} meters",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
