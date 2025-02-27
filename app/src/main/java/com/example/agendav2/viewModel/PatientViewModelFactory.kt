package com.example.agendav2.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendav2.data.PatientRepository

class PatientViewModelFactory(private val patientRepository: PatientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientViewModel(patientRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
