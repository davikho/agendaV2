package com.example.agendav2.api

import androidx.compose.foundation.layout.add
import com.example.agendav2.model.User

object ApiSimulator {
    private val baseUrl = "http://127.0.0.1:5000" // URL base de la API simulada
    private val users = mutableListOf<User>()

    fun createUser(user: User): Boolean {
        // Simula la ruta POST /usuarios
        println("API Simulada: POST $baseUrl/usuarios")
        if (users.any { it.nombre == user.nombre }) {
            println("API Simulada: Usuario ya existe")
            return false // Usuario ya existe
        }
        users.add(user)
        println("API Simulada: Usuario creado con éxito")
        return true // Usuario creado con éxito
    }

    fun login(name: String, clave: String): Boolean {
        // Simula la ruta POST /login
        println("API Simulada: POST $baseUrl/login")
        val userExists = users.any { it.nombre == name && it.clave == clave }
        if (userExists){
            println("API Simulada: Login exitoso")
        }else{
            println("API Simulada: Login fallido")
        }
        return userExists
    }
}