package com.example.agendav2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agendav2.ui.theme.AgendaV2Theme
//import com.example.agendav2.ui.theme.CreateUserScreen
import com.example.agendav2.ui.theme.LoaderScreen
import com.example.agendav2.ui.theme.LoginScreen
import com.example.agendav2.ui.theme.MainPageScreen
import com.example.agendav2.ui.theme.PatientListScreen
import com.example.agendav2.ui.theme.RegisterScreen
import com.example.agendav2.ui.theme.DoctorListScreen
import com.example.agendav2.ui.theme.AppointmentListScreen
import com.example.agendav2.api.DoctorApiService
import com.example.agendav2.api.AppointmentApiService
import com.example.agendav2.api.RetrofitClient.createService
import com.example.agendav2.data.DoctorRepository
import com.example.agendav2.data.AppointmentRepository
import com.example.agendav2.viewModel.AppointmentViewModel
import com.example.agendav2.viewModel.AppointmentViewModelFactory
import com.example.agendav2.viewModel.DoctorViewModel
import com.example.agendav2.viewModel.DoctorViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgendaV2Theme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showLoader by remember { mutableStateOf(true) }
    var showLogin by remember { mutableStateOf(false) }
    var showRegister by remember { mutableStateOf(false) }
    var userType by remember { mutableStateOf("") }

    // Función para manejar el logout
    fun logout() {
        userType = ""
    }
    val apiService = createService(DoctorApiService::class.java)
    val appointmentApiService = createService(AppointmentApiService::class.java)
    val doctorRepository = DoctorRepository(apiService)
    val appointmentRepository = AppointmentRepository(appointmentApiService)
    val appointmentViewModelFactory = AppointmentViewModelFactory(appointmentRepository)
    val doctorViewModelFactory = DoctorViewModelFactory(doctorRepository)

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (showLoader) "loader" else if (showLogin) "login" else "mainPage",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("loader") {
                LoaderScreen {
                    showLoader = false
                    showLogin = true
                    navController.navigate("login")
                }
            }
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { type ->
                        userType = type
                        navController.navigate("mainPage")
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login")
                    }
                )
            }
            composable("mainPage") {
                MainPageScreen(
                    userType = userType,
                    onLogout = {
                        logout()
                        navController.navigate("login")
                    },
                    onNavigateToUsers = {
                        navController.navigate("patientList")
                    },
                    onNavigateToCreateUser = {
                        navController.navigate("createUser")
                    },
                    onNavigateToDoctors = {
                        navController.navigate("doctorList")
                    },
                    onNavigateToAppointments = {
                        navController.navigate("appointmentList")
                    }
                )
            }
            composable("patientList") {
                PatientListScreen()
            }

            //composable("createUser") {
              //  CreateUserScreen()
            //}
            composable("doctorList") {

                DoctorListScreen()
            }
            composable("appointmentList") {
                AppointmentListScreen()
            }
        }
    }
}