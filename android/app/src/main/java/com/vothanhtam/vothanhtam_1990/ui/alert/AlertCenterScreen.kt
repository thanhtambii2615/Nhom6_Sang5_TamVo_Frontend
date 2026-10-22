package com.vothanhtam.vothanhtam_1990.ui.alert

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class AlertItem(val id: String, val title: String, val time: String, val severity: String)

@Composable
fun AlertCenterScreen() {
    val alerts = listOf(
        AlertItem("1", "High Noise Level Detected (>85dB)", "10:42 AM", "WARNING"),
        AlertItem("2", "Device Locked Remotely", "09.15 AM", "INFO"),
        AlertItem("3", "Safe Zone Exited (GPS)", "Yesterday", "CRITICAL")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Real-time Alert Center", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(alerts.size) { index ->
                val alert = alerts[index]
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(alert.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Time: ${alert.time} | Severity: ${alert.severity}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
