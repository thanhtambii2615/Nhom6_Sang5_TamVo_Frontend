package com.vothanhtam.vothanhtam_1990.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdminDashboardScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Admin Administration Portal", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("System Statistics", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Registered Users: 124")
                Text("Active Child Devices: 85")
                Text("System Status: Operational")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Management Actions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Manage Users & Roles")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("View All Telemetry & Audit Logs")
        }
    }
}
