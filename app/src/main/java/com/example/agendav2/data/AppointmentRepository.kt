package com.example.agendav2.data

import android.util.Log
import com.example.agendav2.model.Appointment
import com.example.agendav2.api.AppointmentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class AppointmentRepository(private val apiService: AppointmentApiService) {

    suspend fun getAppointment(): List<Appointment> = withContext(Dispatchers.IO) {
        Log.d("AppointmentRepository", "Iniciando getAppointment()")
        try {
            val response: Response<List<Appointment>> = apiService.getAppointment()
            if (response.isSuccessful) {
                val appointments = response.body() ?: emptyList()
                Log.d("AppointmentRepository", "citas obtenidos: ${appointments.size}")
                appointments
            } else {
                Log.e("AppointmentRepository", "Error en getappointments(): ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("AppointmentRepository", "Error Body: $errorBody")
                throw Exception("Error en la respuesta de la API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error en getappointments(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun getAppointmentById(id: Int): Appointment = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAppointmentById(id)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Error al obtener el cita: Cuerpo de respuesta vacío")
            } else {
                throw Exception("Error al obtener cita: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error al obtener cita: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun deleteAppointment(id: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteAppointment(id)
            if (response.isSuccessful) {
                Log.d("AppointmentRepository", "cita eliminado correctamente")
            } else {
                Log.e("AppointmentRepository", "Error al eliminar el cita: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("AppointmentRepository", "Error Body: $errorBody")
                throw Exception("Error en la respuesta de la API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error al eliminar el cita: ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun updateAppointment(appointment: Appointment): Appointment = withContext(Dispatchers.IO) {
        Log.d("AppointmentRepository", "Iniciando updatePatient()")
        try {
            val response = apiService.updateAppointment(appointment.id!!, appointment)
            if (response.isSuccessful) {
                Log.d("AppointmentRepository", "cita actualizado correctamente")
                response.body() ?: throw Exception("Cuerpo de respuesta nulo")
            } else {
                Log.e("AppointmentRepository", "Error al actualizar cita: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("AppointmentRepository", "Error Body: $errorBody")
                throw Exception("Error al actualizar cita: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error en updatePatient(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }

    suspend fun createAppointment(appointment: Appointment) = withContext(Dispatchers.IO) {
        Log.d("AppointmentRepository", "Iniciando createAppointment()")
        try {
            val response = apiService.createAppointment(appointment)
            if (response.isSuccessful) {
                Log.d("AppointmentRepository", "cita creado correctamente")
            } else {
                Log.e("AppointmentRepository", "Error al crear cita: ${response.code()}")
                val errorBody = response.errorBody()?.string() ?: "Error body is null"
                Log.e("AppointmentRepository", "Error Body: $errorBody")
                throw Exception("Error al crear cita: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AppointmentRepository", "Error en createAppointment(): ${e.message}")
            throw Exception("Error de conexión", e)
        }
    }
}