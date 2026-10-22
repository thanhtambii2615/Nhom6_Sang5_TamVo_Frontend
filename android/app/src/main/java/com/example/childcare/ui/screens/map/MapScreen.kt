package com.example.childcare.ui.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("GPS Location & Safe Zone Map", style = MaterialTheme.typography.headlineMedium)
    }
}
