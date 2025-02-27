package com.example.agendav2.model

import java.time.LocalDate
import java.time.LocalTime

data class Appointment(
    val id: Int? = null, // Puede ser nulo si es una nueva cita que aún no tiene ID
    val usuario_id: Int, // No nulo, ya que es una clave foránea requerida
    val medico_id: Int, // No nulo, ya que es una clave foránea requerida
    val fecha: Fecha, // Usamos LocalDate para representar la fecha
    val hora: Hora, // Usamos LocalTime para representar la hora
    val estado: String = "Confirmada" // Valor por defecto "Confirmada"
)

data class Fecha(
    val day: Int,
    val month: Int,
    val year: Int
)

data class Hora(
    val hour: Int,
    val minute: Int
)