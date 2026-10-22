package com.example.childcare.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.childcare.data.local.TokenManager
import com.example.childcare.ui.screens.auth.AuthViewModel
import com.example.childcare.ui.screens.auth.LoginScreen
import com.example.childcare.ui.screens.auth.RegisterScreen
import com.example.childcare.ui.screens.auth.OtpScreen
import com.example.childcare.ui.screens.admin.AdminDashboardScreen
import com.example.childcare.ui.screens.parent.ParentDashboardScreen
import com.example.childcare.ui.screens.child.ChildDashboardScreen
import com.example.childcare.ui.screens.child.ChildListScreen
import com.example.childcare.ui.screens.devices.DeviceListScreen
import com.example.childcare.ui.screens.rules.RuleListScreen
import com.example.childcare.ui.screens.map.MapScreen
import com.example.childcare.ui.screens.qr.QrPairingScreen
import com.example.childcare.ui.screens.telemetry.TelemetryScreen
import com.example.childcare.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val startDestination = runBlocking {
        val token = tokenManager.tokenFlow.first()
        val role = tokenManager.roleFlow.first()?.uppercase() ?: ""
        if (!token.isNullOrEmpty()) {
            when (role) {
                "ADMIN" -> "admin_dashboard"
                "PARENT" -> "parent_dashboard"
                "CHILD" -> "child_dashboard"
                else -> "parent_dashboard"
            }
        } else {
            "login"
        }
    }

    val handleLogout: () -> Unit = {
        runBlocking {
            tokenManager.clearSession()
        }
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateRegister = { navController.navigate("register") },
                onNavigateOtp = { username -> navController.navigate("otp/$username") },
                onLoginSuccess = { role ->
                    when (role.uppercase()) {
                        "ADMIN" -> navController.navigate("admin_dashboard") { popUpTo("login") { inclusive = true } }
                        "CHILD" -> navController.navigate("child_dashboard") { popUpTo("login") { inclusive = true } }
                        else -> navController.navigate("parent_dashboard") { popUpTo("login") { inclusive = true } }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("otp/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            OtpScreen(
                username = username,
                authViewModel = authViewModel,
                onVerified = { role ->
                    when (role.uppercase()) {
                        "ADMIN" -> navController.navigate("admin_dashboard") { popUpTo("login") { inclusive = true } }
                        "CHILD" -> navController.navigate("child_dashboard") { popUpTo("login") { inclusive = true } }
                        else -> navController.navigate("parent_dashboard") { popUpTo("login") { inclusive = true } }
                    }
                }
            )
        }
        composable("admin_dashboard") {
            AdminDashboardScreen(onLogout = handleLogout)
        }
        composable("parent_dashboard") {
            ParentDashboardScreen(
                onNavigateDevices = { navController.navigate("devices") },
                onNavigateRules = { navController.navigate("rules") },
                onNavigateMap = { navController.navigate("map") },
                onNavigateQr = { navController.navigate("qr") },
                onNavigateTelemetry = { navController.navigate("telemetry") },
                onNavigateSettings = { navController.navigate("settings") },
                onLogout = handleLogout
            )
        }
        composable("child_dashboard") {
            ChildDashboardScreen(onLogout = handleLogout)
        }
        composable("child") { ChildListScreen() }
        composable("devices") { DeviceListScreen() }
        composable("rules") { RuleListScreen() }
        composable("map") { MapScreen() }
        composable("qr") { QrPairingScreen() }
        composable("telemetry") { TelemetryScreen() }
        composable("settings") { SettingsScreen() }
    }
}
