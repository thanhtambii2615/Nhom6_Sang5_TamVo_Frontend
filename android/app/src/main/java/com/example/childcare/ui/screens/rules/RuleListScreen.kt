package com.example.childcare.ui.screens.rules

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RuleListScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Time Limit Rules (CRUD)", style = MaterialTheme.typography.headlineMedium)
    }
}
