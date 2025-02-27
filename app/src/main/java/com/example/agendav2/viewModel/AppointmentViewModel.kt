package com.example.agendav2.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendav2.data.AppointmentRepository
import com.example.agendav2.model.Appointment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class AppointmentListState {
    data object Loading : AppointmentListState()
    data class Success(val appointments: List<Appointment>) : AppointmentListState()
    data class Error(val message: String) : AppointmentListState()
}

class AppointmentViewModel(private val appointmentRepository: AppointmentRepository) : ViewModel() {

    private val _appointmentListState = MutableStateFlow<AppointmentListState>(AppointmentListState.Loading)
    val appointmentListState: StateFlow<AppointmentListState> = _appointmentListState.asStateFlow()

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            _appointmentListState.value = AppointmentListState.Loading
            try {
                val appointments = appointmentRepository.getAppointment()
                _appointmentListState.value = AppointmentListState.Success(appointments)
            } catch (e: Exception) {
                _appointmentListState.value = AppointmentListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getAppointmentById(id: Int) {
        viewModelScope.launch {
            _appointmentListState.value = AppointmentListState.Loading
            try {
                val appointment = appointmentRepository.getAppointmentById(id)
                _appointmentListState.value = AppointmentListState.Success(listOf(appointment))
            } catch (e: Exception) {
                _appointmentListState.value = AppointmentListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                appointmentRepository.deleteAppointment(appointment.id!!)
                loadAppointments()
            } catch (e: Exception) {
                _appointmentListState.value = AppointmentListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                val response = appointmentRepository.updateAppointment(appointment)
                Log.d("DoctorViewModel", "Respuesta de la API: $response")
                loadAppointments()
            } catch (e: Exception) {
                _appointmentListState.value = AppointmentListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                appointmentRepository.createAppointment(appointment)
                loadAppointments()
            } catch (e: Exception) {
                _appointmentListState.value = AppointmentListState.Error(e.message ?: "Unknown error")
            }
        }
    }
}