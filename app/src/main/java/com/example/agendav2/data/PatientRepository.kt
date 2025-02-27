package com.example.agendav2.data

import android.util.Log
import com.example.agendav2.model.User
import com.example.agendav2.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PatientRepository(private val apiService: ApiService) {

    suspend fun getPatients(): List<User> = withContext(Dispatchers.IO) {
        Log.d("PatientRepository", "Iniciando getPatients()")
        try {
            val response: Response<List<User>> = apiService.getUsers()
            if (response.isSuccessful) {
                val patients = response.body() ?: emptyList()
                Log.d("PatientRepository", "Pacientes obtenidos: ${patients.size}")
                patients
            } else {
                Log.e("PatientRepository", "Error en getPatients(): ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("PatientRepository", "Error Body: $errorBody")
                throw Exception("Error en la respuesta de la API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PatientRepository", "Error en getPatients(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun getUserById(id: Int): User = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsuariosById(id)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error al obtener el usuario: Cuerpo de respuesta vacío")
            } else {
                throw Exception("Error al obtener el usuario: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error al obtener el usuario: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun deletePatient(patient: User) = withContext(Dispatchers.IO) {
        Log.d("PatientRepository", "Iniciando deletePatient()")
        try {
            val response = apiService.deleteUser(patient.id!!)
            if (response.isSuccessful) {
                Log.d("PatientRepository", "Paciente eliminado correctamente")
            } else {
                Log.e("PatientRepository", "Error al eliminar paciente: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("PatientRepository", "Error Body: $errorBody")
                throw Exception("Error al eliminar paciente: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PatientRepository", "Error en deletePatient(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun updatePatient(patient: User): User = withContext(Dispatchers.IO) {
        Log.d("PatientRepository", "Iniciando updatePatient()")
        try {
            val response = apiService.updateUser(patient.id!!, patient)
            if (response.isSuccessful) {
                Log.d("PatientRepository", "Paciente actualizado correctamente")
                response.body() ?: throw Exception("Cuerpo de respuesta nulo")
            } else {
                Log.e("PatientRepository", "Error al actualizar paciente: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("PatientRepository", "Error Body: $errorBody")
                throw Exception("Error al actualizar paciente: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PatientRepository", "Error en updatePatient(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun createPatient(patient: User) = withContext(Dispatchers.IO) {
        Log.d("PatientRepository", "Iniciando createPatient()")
        try {
            val response = apiService.createUser(patient)
            if (response.isSuccessful) {
                Log.d("PatientRepository", "Paciente creado correctamente")
            } else {
                Log.e("PatientRepository", "Error al crear paciente: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("PatientRepository", "Error Body: $errorBody")
                throw Exception("Error al crear paciente: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("PatientRepository", "Error en createPatient(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }
}