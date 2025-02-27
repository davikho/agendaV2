package com.example.agendav2.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agendav2.data.PatientRepository
import com.example.agendav2.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PatientListState {
    data object Loading : PatientListState()
    data class Success(val patients: List<User>) : PatientListState()
    data class Error(val message: String) : PatientListState()
}

class PatientViewModel(private val patientRepository: PatientRepository) : ViewModel() {

    private val _patientListState = MutableStateFlow<PatientListState>(PatientListState.Loading)
    val patientListState: StateFlow<PatientListState> = _patientListState.asStateFlow()

    init {
        loadPatients()
    }

    private fun loadPatients() {
        viewModelScope.launch {
            _patientListState.value = PatientListState.Loading
            try {
                val patients = patientRepository.getPatients()
                _patientListState.value = PatientListState.Success(patients)
            } catch (e: Exception) {
                _patientListState.value = PatientListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            _patientListState.value = PatientListState.Loading
            try {
                val patient = patientRepository.getUserById(id)
                _patientListState.value = PatientListState.Success(listOf(patient))
            } catch (e: Exception) {
                _patientListState.value = PatientListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deletePatient(patient: User) {
        viewModelScope.launch {
            try {
                patientRepository.deletePatient(patient)
                loadPatients()
            } catch (e: Exception) {
                _patientListState.value = PatientListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updatePatient(patient: User) {
        viewModelScope.launch {
            try {
                val response = patientRepository.updatePatient(patient)
                Log.d("PatientViewModel", "Respuesta de la API: $response")
                loadPatients()
            } catch (e: Exception) {
                _patientListState.value = PatientListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createPatient(patient: User) {
        viewModelScope.launch {
            try {
                patientRepository.createPatient(patient)
                loadPatients()
            } catch (e: Exception) {
                _patientListState.value = PatientListState.Error(e.message ?: "Unknown error")
            }
        }
    }
}