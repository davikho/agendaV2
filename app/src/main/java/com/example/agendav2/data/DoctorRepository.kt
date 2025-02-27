package com.example.agendav2.data

import android.util.Log
import com.example.agendav2.api.DoctorApiService
import com.example.agendav2.model.Doctor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class DoctorRepository(private val apiService: DoctorApiService) {

    suspend fun getDoctors(): List<Doctor> = withContext(Dispatchers.IO) {
        Log.d("DoctorRepository", "Iniciando getDoctors()")
        try {
            val response: Response<List<Doctor>> = apiService.getDoctors()
            if (response.isSuccessful) {
                val doctors = response.body() ?: emptyList()
                Log.d("DoctorRepository", "Doctores obtenidos: ${doctors.size}")
                doctors
            } else {
                Log.e("DoctorRepository", "Error en getDoctors(): ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("DoctorRepository", "Error Body: $errorBody")
                throw Exception("Error en la respuesta de la API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error en getDoctors(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun getDoctorById(id: Int): Doctor = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDoctorById(id)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error al obtener el doctor: Cuerpo de respuesta vacío")
            } else {
                throw Exception("Error al obtener el doctor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error al obtener el doctor: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun updateDoctor(doctor: Doctor): Doctor = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateDoctor(doctor.id!!,doctor)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error al actualizar el doctor: Cuerpo de respuesta vacío")
            } else {
                throw Exception("Error al actualizar el doctor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error al actualizar el doctor: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun deleteDoctor(id: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteDoctor(id)
            if (response.isSuccessful) {
                Log.d("DoctorRepository", "Doctor eliminado correctamente")
            } else {
                Log.e("DoctorRepository", "Error al eliminar el doctor: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("DoctorRepository", "Error Body: $errorBody")
                throw Exception("Error en la respuesta de la API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error al eliminar el doctor: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun createDoctor(doctor: Doctor) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createDoctor(doctor)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error al crear el doctor: Cuerpo de respuesta vacío")
            } else {
                throw Exception("Error al crear el doctor: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("DoctorRepository", "Error al crear el doctor: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }
}