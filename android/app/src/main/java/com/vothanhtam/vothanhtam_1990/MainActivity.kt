package com.vothanhtam.vothanhtam_1990

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vothanhtam.vothanhtam_1990.ui.auth.AuthViewModel
import com.vothanhtam.vothanhtam_1990.ui.auth.AuthState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onNavigateRegister = { navController.navigate("register") },
                            onNavigateOtp = { email -> navController.navigate("otp/$email") },
                            onLoginSuccess = { role ->
                                when (role) {
                                    "Admin" -> navController.navigate("admin_dashboard") { popUpTo("login") { inclusive = true } }
                                    "Employee" -> navController.navigate("employee_dashboard") { popUpTo("login") { inclusive = true } }
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
                    composable("otp/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        OtpScreen(
                            email = email,
                            authViewModel = authViewModel,
                            onVerified = { role ->
                                navController.navigate("parent_dashboard") { popUpTo("login") { inclusive = true } }
                            }
                        )
                    }
                    composable("admin_dashboard") {
                        AdminDashboardScreen(onLogout = { navController.navigate("login") { popUpTo(0) } })
                    }
                    composable("employee_dashboard") {
                        EmployeeDashboardScreen(onLogout = { navController.navigate("login") { popUpTo(0) } })
                    }
                    composable("parent_dashboard") {
                        ParentDashboardScreen(onLogout = { navController.navigate("login") { popUpTo(0) } })
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateRegister: () -> Unit,
    onNavigateOtp: (String) -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Parent Control & Monitoring", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onNavigateRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register New Account")
        }

        when (authState) {
            is AuthState.Loading -> {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text((authState as AuthState.Error).error, color = MaterialTheme.colorScheme.error)
            }
            is AuthState.OtpRequired -> {
                LaunchedEffect(Unit) {
                    onNavigateOtp((authState as AuthState.OtpRequired).email)
                }
            }
            is AuthState.LoggedIn -> {
                val role = (authState as AuthState.LoggedIn).role
                LaunchedEffect(role) {
                    onLoginSuccess(role)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Customer") }
    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { authViewModel.register(email, password, role) }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("Back to Login")
        }

        when (authState) {
            is AuthState.Success -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text((authState as AuthState.Success).message, color = MaterialTheme.colorScheme.primary)
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text((authState as AuthState.Error).error, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}

@Composable
fun OtpScreen(email: String, authViewModel: AuthViewModel, onVerified: (String) -> Unit) {
    var otp by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Verify OTP Sent to $email", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = otp, onValueChange = { otp = it }, label = { Text("OTP Code") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { authViewModel.verifyOtp(email, otp) }, modifier = Modifier.fillMaxWidth()) {
            Text("Verify OTP")
        }

        when (authState) {
            is AuthState.LoggedIn -> {
                LaunchedEffect(Unit) {
                    onVerified((authState as AuthState.LoggedIn).role)
                }
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text((authState as AuthState.Error).error, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}

@Composable
fun AdminDashboardScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Welcome Admin! Manage system users, devices & telemetry.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}

@Composable
fun EmployeeDashboardScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Employee Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Welcome Employee! Monitor safety alerts & telemetry.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}

@Composable
fun ParentDashboardScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Parent Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Manage child devices, rules, instant lock, QR pairing & noise/GPS monitoring.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) { Text("Logout") }
    }
}
