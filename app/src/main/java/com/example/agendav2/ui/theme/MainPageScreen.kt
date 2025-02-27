package com.example.agendav2.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.agendav2.model.MenuItem
import com.example.agendav2.model.UserType
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageScreen(
    userType: String,
    onLogout: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToCreateUser: () -> Unit,
    onNavigateToDoctors: () -> Unit,
    onNavigateToAppointments: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        MenuItem(
            id = "home",
            title = "Inicio",
            icon = Icons.Default.Home,
            userTypes = listOf(UserType.Admin.name, UserType.Paciente.name)
        ),
        MenuItem(
            id = "users",
            title = "Usuarios",
            icon = Icons.Default.List,
            userTypes = listOf(UserType.Admin.name)
        ),
        MenuItem(
            id = "doctors",
            title = "Médicos",
            icon = Icons.Default.Person,
            userTypes = listOf(UserType.Admin.name, UserType.Paciente.name)
        ),
        MenuItem(
            id = "appointments",
            title = "Citas",
            icon = Icons.Default.DateRange,
            userTypes = listOf(UserType.Admin.name, UserType.Paciente.name)
        ),
        MenuItem(
            id = "about",
            title = "Acerca de",
            icon = Icons.Default.Info,
            userTypes = listOf(UserType.Admin.name, UserType.Paciente.name)
        ),
        MenuItem(
            id = "logout",
            title = "Cerrar Sesión",
            icon = Icons.Default.Logout,
            userTypes = listOf(UserType.Admin.name, UserType.Paciente.name)
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody(
                    items = menuItems.filter { it.userTypes.contains(userType) },
                    onItemClick = {
                        Log.d("MainPageScreen", "Clicked on ${it.title}")
                        when (it.id) {
                            "logout" -> onLogout()
                            "users" -> onNavigateToUsers()
                            "doctors" -> onNavigateToDoctors()
                            "appointments" -> onNavigateToAppointments()
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Página Principal") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        onNavigateToCreateUser()
                    }) {
                        Icon(Icons.Filled.Add, "Agregar")
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Página Principal",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    when (userType) {
                        UserType.Admin.name -> {
                            Text(text = "Bienvenido Administrador")
                            Text(text = "Como administrador, puedes:")
                            Text(text = "- Gestionar usuarios")
                            Text(text = "- Ver estadísticas")
                            // ... más acciones de administrador
                        }
                        UserType.Paciente.name -> {
                            Text(text = "Bienvenido Paciente")
                            Text(text = "Como paciente, puedes:")
                            Text(text = "- Ver tus citas")
                            Text(text = "- Reservar citas")
                            // ... más acciones de paciente
                        }
                        else -> {
                            Text(text = "Tipo de usuario desconocido")
                        }
                    }
                }
            }
        }
    )
}