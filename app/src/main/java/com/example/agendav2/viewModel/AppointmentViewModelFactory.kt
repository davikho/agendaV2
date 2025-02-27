package com.example.agendav2.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agendav2.data.AppointmentRepository

class AppointmentViewModelFactory(private val appointmentRepository: AppointmentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(appointmentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}