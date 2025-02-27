package com.example.agendav2.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendav2.data.DoctorRepository

class DoctorViewModelFactory(private val doctorRepository: DoctorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorViewModel(doctorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}