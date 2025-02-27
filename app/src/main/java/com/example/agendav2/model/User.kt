package com.example.agendav2.model
import androidx.compose.ui.graphics.vector.ImageVector

data class User(
    val id: Int? = null,
    val nombre: String,
    val tipo: String,
    val clave: String
)

data class LoginRequest(
    val nombre: String,
    val clave: String
)

data class LoginResponse(
    val mensaje: String,
    val usuario: User
)
data class MenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val userTypes: List<String> // List of user types that can see this item
)

enum class UserType {
    Admin, Paciente
}
data class Patient(
    val id: Int? = null,
    val name: String,
    val lastName: String,
    val age: Int,
    val email: String
)

data class CreateUserRequest(
    val id: Int? = null,
    val nombre: String,
    val tipo: String,
    val clave: String
)