package com.example.agendav2.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendav2.data.DoctorRepository
import com.example.agendav2.model.Doctor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class DoctorListState {
    data object Loading : DoctorListState()
    data class Success(val doctors: List<Doctor>) : DoctorListState()
    data class Error(val message: String) : DoctorListState()
}

class DoctorViewModel(private val doctorRepository: DoctorRepository) : ViewModel() {

    private val _doctorListState = MutableStateFlow<DoctorListState>(DoctorListState.Loading)
    val doctorListState: StateFlow<DoctorListState> = _doctorListState.asStateFlow()

    init {
        loadDoctors()
    }

    private fun loadDoctors() {
        viewModelScope.launch {
            _doctorListState.value = DoctorListState.Loading
            try {
                val doctors = doctorRepository.getDoctors()
                _doctorListState.value = DoctorListState.Success(doctors)
            } catch (e: Exception) {
                _doctorListState.value = DoctorListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getDoctorById(id: Int) {
        viewModelScope.launch {
            _doctorListState.value = DoctorListState.Loading
            try {
                val doctor = doctorRepository.getDoctorById(id)
                _doctorListState.value = DoctorListState.Success(listOf(doctor))
            } catch (e: Exception) {
                _doctorListState.value = DoctorListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                doctorRepository.deleteDoctor(doctor.id!!)
                loadDoctors()
            } catch (e: Exception) {
                _doctorListState.value = DoctorListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                val response = doctorRepository.updateDoctor(doctor)
                Log.d("DoctorViewModel", "Respuesta de la API: $response")
                loadDoctors()
            } catch (e: Exception) {
                _doctorListState.value = DoctorListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createDoctor(doctor: Doctor) {
        viewModelScope.launch {
            try {
                doctorRepository.createDoctor(doctor)
                loadDoctors()
            } catch (e: Exception) {
                _doctorListState.value = DoctorListState.Error(e.message ?: "Unknown error")
            }
        }
    }
}