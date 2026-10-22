package com.example.childcare.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    onNavigateDevices: () -> Unit,
    onNavigateRules: () -> Unit,
    onNavigateMap: () -> Unit,
    onNavigateQr: () -> Unit,
    onNavigateTelemetry: () -> Unit,
    onNavigateSettings: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xin chào, Phụ huynh", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                    label = { Text("Trang chủ") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; onNavigateMap() },
                    icon = { Icon(Icons.Default.Place, contentDescription = "Bản đồ") },
                    label = { Text("Bản đồ") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; onNavigateDevices() },
                    icon = { Icon(Icons.Default.Phone, contentDescription = "Thiết bị") },
                    label = { Text("Thiết bị") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3; onNavigateRules() },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Quy tắc") },
                    label = { Text("Quy tắc") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4; onNavigateSettings() },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Cá nhân") },
                    label = { Text("Cá nhân") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SafetyStatusCard(
                    title = "Trạng thái an toàn",
                    value = "Tất cả thiết bị đều an toàn",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF16A34A)
                )
            }
            item {
                SafetyStatusCard(
                    title = "Vị trí hiện tại",
                    value = "Đang cập nhật GPS (Quận 1, TP.HCM)",
                    icon = Icons.Default.Place,
                    color = Color(0xFF0284C7),
                    onClick = onNavigateMap
                )
            }
            item {
                SafetyStatusCard(
                    title = "Mức tiếng ồn",
                    value = "42 dB (Bình thường)",
                    icon = Icons.Default.VolumeUp,
                    color = Color(0xFF0D9488),
                    onClick = onNavigateTelemetry
                )
            }
            item {
                SafetyStatusCard(
                    title = "Safe Zone",
                    value = "Bán kính 100m quanh nhà - Đang hoạt động",
                    icon = Icons.Default.Shield,
                    color = Color(0xFF0284C7)
                )
            }
            item {
                SafetyStatusCard(
                    title = "SOS Khẩn cấp",
                    value = "Không có tín hiệu SOS",
                    icon = Icons.Default.Warning,
                    color = Color(0xFF16A34A)
                )
            }
            item {
                SafetyStatusCard(
                    title = "Cảnh báo Té ngã",
                    value = "Không phát hiện va chạm bất thường",
                    icon = Icons.Default.Info,
                    color = Color(0xFF16A34A),
                    onClick = onNavigateTelemetry
                )
            }
            item {
                SafetyStatusCard(
                    title = "Khóa thiết bị khẩn cấp (Instant Lock)",
                    value = "Nhấn để quản lý khóa thiết bị từ xa",
                    icon = Icons.Default.Lock,
                    color = Color(0xFFDC2626),
                    onClick = onNavigateDevices
                )
            }
        }
    }
}

@Composable
fun SafetyStatusCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.fillMaxWidth() else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
