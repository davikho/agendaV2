package com.example.agendav2.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendav2.api.RetrofitClient
import com.example.agendav2.data.AppointmentRepository
import com.example.agendav2.model.Appointment
import com.example.agendav2.model.Fecha
import com.example.agendav2.model.Hora
import com.example.agendav2.viewModel.AppointmentListState
import com.example.agendav2.viewModel.AppointmentViewModel
import com.example.agendav2.viewModel.AppointmentViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentListScreen() {
    val appointmentRepository = AppointmentRepository(RetrofitClient.AppointmentInstance)
    val factory = AppointmentViewModelFactory(appointmentRepository)
    val appointmentViewModel: AppointmentViewModel = viewModel(factory = factory)
    val appointmentListState by appointmentViewModel.appointmentListState.collectAsState()

    var isCreatingAppointment by remember { mutableStateOf(false) }
    var newAppointmentUsuarioId by remember { mutableStateOf("") }
    var newAppointmentMedicoId by remember { mutableStateOf("") }
    var newAppointmentFecha by remember { mutableStateOf(LocalDate.now()) }
    var newAppointmentHora by remember { mutableStateOf(LocalTime.now()) }
    var newAppointmentEstado by remember { mutableStateOf("Confirmada") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedView by remember { mutableStateOf("Lista") }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var appointmentToDelete: Appointment? by remember { mutableStateOf(null) }
    var appointmentToEdit: Appointment? by remember { mutableStateOf(null) }
    var isEditingAppointment by remember { mutableStateOf(false) }

    // Función para reiniciar los valores de la nueva cita
    fun resetNewAppointment() {
        newAppointmentUsuarioId = ""
        newAppointmentMedicoId = ""
        newAppointmentFecha = LocalDate.now()
        newAppointmentHora = LocalTime.now()
        newAppointmentEstado = "Confirmada"
    }

    when (appointmentListState) {
        is AppointmentListState.Loading -> CircularProgressIndicator()
        is AppointmentListState.Success -> {
            val appointments = (appointmentListState as AppointmentListState.Success).appointments
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedView == "Lista",
                        onClick = { selectedView = "Lista" }
                    )
                    Text(text = "Vista de Lista")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = selectedView == "Calendario",
                        onClick = { selectedView = "Calendario" }
                    )
                    Text(text = "Vista de Calendario")
                }
                if (selectedView == "Lista") {
                    LazyColumn {
                        item {
                            if (!isEditingAppointment) {
                                CreateAppointmentCard(
                                    isCreating = isCreatingAppointment,
                                    onStartCreating = { isCreatingAppointment = true },
                                    onSave = {
                                        // Manejo de errores con try-catch
                                        try {
                                            if (newAppointmentUsuarioId.isNotBlank() && newAppointmentMedicoId.isNotBlank()) {
                                                val newAppointment = Appointment(
                                                    usuario_id = newAppointmentUsuarioId.toInt(),
                                                    medico_id = newAppointmentMedicoId.toInt(),
                                                    fecha = Fecha(newAppointmentFecha.dayOfMonth, newAppointmentFecha.monthValue, newAppointmentFecha.year),
                                                    hora = Hora(newAppointmentHora.hour, newAppointmentHora.minute),
                                                    estado = newAppointmentEstado
                                                )
                                                appointmentViewModel.createAppointment(newAppointment)
                                                isCreatingAppointment = false
                                                resetNewAppointment() // Reiniciar los valores
                                            }
                                        } catch (e: NumberFormatException) {
                                            // Mostrar un mensaje de error al usuario
                                            println("Error: ID de usuario o médico no válido")
                                        }
                                    },
                                    onUsuarioIdChange = { newAppointmentUsuarioId = it },
                                    onMedicoIdChange = { newAppointmentMedicoId = it },
                                    onFechaChange = { newAppointmentFecha = it },
                                    onHoraChange = { newAppointmentHora = it },
                                    onEstadoChange = { newAppointmentEstado = it },
                                    usuarioId = newAppointmentUsuarioId,
                                    medicoId = newAppointmentMedicoId,
                                    fecha = newAppointmentFecha,
                                    hora = newAppointmentHora,
                                    estado = newAppointmentEstado,
                                    onShowDatePicker = { showDatePicker = true },
                                    onShowTimePicker = { showTimePicker = true }
                                )
                            } else {
                                appointmentToEdit?.let { appointment ->
                                    EditAppointmentCard(
                                        appointment = appointment,
                                        onSave = { updatedAppointment ->
                                            appointmentViewModel.updateAppointment(updatedAppointment)
                                            isEditingAppointment = false
                                            appointmentToEdit = null
                                        },
                                        onCancel = {
                                            isEditingAppointment = false
                                            appointmentToEdit = null
                                        },
                                        onUsuarioIdChange = { newAppointmentUsuarioId = it },
                                        onMedicoIdChange = { newAppointmentMedicoId = it },
                                        onFechaChange = { newAppointmentFecha = it },
                                        onHoraChange = { newAppointmentHora = it },
                                        onEstadoChange = { newAppointmentEstado = it },
                                        usuarioId = newAppointmentUsuarioId,
                                        medicoId = newAppointmentMedicoId,
                                        fecha = newAppointmentFecha,
                                        hora = newAppointmentHora,
                                        estado = newAppointmentEstado,
                                        onShowDatePicker = { showDatePicker = true },
                                        onShowTimePicker = { showTimePicker = true }
                                    )
                                }
                            }
                        }
                        items(appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                onDelete = {
                                    appointmentToDelete = it
                                    showDeleteConfirmationDialog = true
                                },
                                onUpdate = {
                                    appointmentToEdit = it
                                    isEditingAppointment = true
                                    newAppointmentUsuarioId = it.usuario_id.toString()
                                    newAppointmentMedicoId = it.medico_id.toString()
                                    newAppointmentFecha = LocalDate.of(it.fecha.year, it.fecha.month, it.fecha.day)
                                }
                            )
                        }
                    }
                } else {
                    // Aquí iría la vista de calendario
                    Text(text = "Vista de Calendario")
                }
            }
            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                newAppointmentFecha = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                            }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDatePicker = false }) {
                            Text("Cancelar")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            if (showTimePicker) {
                val timePickerState = rememberTimePickerState()
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    confirmButton = {
                        Button(onClick = {
                            newAppointmentHora = LocalTime.of(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showTimePicker = false }) {
                            Text("Cancelar")
                        }
                    },
                    title = { Text("Seleccionar Hora") },
                    text = { TimePicker(state = timePickerState) }
                )
            }
            if (showDeleteConfirmationDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmationDialog = false },
                    title = { Text("Confirmar Eliminación") },
                    text = { Text("¿Estás seguro de que quieres eliminar esta cita?") },
                    confirmButton = {
                        TextButton(onClick = {
                            appointmentToDelete?.let {
                                appointmentViewModel.deleteAppointment(it)
                            }
                            showDeleteConfirmationDialog = false
                        }) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }

        is AppointmentListState.Error -> {
            val errorMessage = (appointmentListState as AppointmentListState.Error).message
            Text(text = "Error: $errorMessage")
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAppointmentCard(
    isCreating: Boolean,
    onStartCreating: () -> Unit,
    onSave: () -> Unit,
    onUsuarioIdChange: (String) -> Unit,
    onMedicoIdChange: (String) -> Unit,
    onFechaChange: (LocalDate) -> Unit,
    onHoraChange: (LocalTime) -> Unit,
    onEstadoChange: (String) -> Unit,
    usuarioId: String,
    medicoId: String,
    fecha: LocalDate,
    hora: LocalTime,
    estado: String,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit
) {
    var isUsuarioIdValid by remember { mutableStateOf(true) }
    var isMedicoIdValid by remember { mutableStateOf(true) }
    var isEstadoExpanded by remember { mutableStateOf(false) }
    var usuarioIdError by remember { mutableStateOf<String?>(null) }
    var medicoIdError by remember { mutableStateOf<String?>(null) }
    val estados = listOf("Confirmada", "Cancelada")

    fun validateUsuarioId(id: String) {
        isUsuarioIdValid = id.isNotBlank() && id.all { it.isDigit() }
        usuarioIdError = when {
            id.isBlank() -> "El ID de usuario no puede estar vacío"
            !id.all { it.isDigit() } -> "El ID de usuario debe ser un número"
            else -> null
        }
    }

    fun validateMedicoId(id: String) {
        isMedicoIdValid = id.isNotBlank() && id.all { it.isDigit() }
        medicoIdError = when {
            id.isBlank() -> "El ID de médico no puede estar vacío"
            !id.all { it.isDigit() } -> "El ID de médico debe ser un número"
            else -> null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (!isCreating) {
                Button(
                    onClick = onStartCreating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Crear Cita")
                    Text("Crear Cita")
                }
            } else {
                OutlinedTextField(
                    value = usuarioId,
                    onValueChange = {
                        onUsuarioIdChange(it)
                        validateUsuarioId(it)
                    },
                    label = { Text("Usuario ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isUsuarioIdValid,
                    supportingText = {
                        if (usuarioIdError != null) {
                            Text(text = usuarioIdError!!)
                        }
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = medicoId,
                    onValueChange = {
                        onMedicoIdChange(it)
                        validateMedicoId(it)
                    },
                    label = { Text("Medico ID") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isMedicoIdValid,
                    supportingText = {
                        if (medicoIdError != null) {
                            Text(text = medicoIdError!!)
                        }
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fecha: ${fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = onShowDatePicker) {
                        Text("Seleccionar Fecha")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hora: ${hora.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}",
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = onShowTimePicker) {
                        Text("Seleccionar Hora")
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = isEstadoExpanded,
                    onExpandedChange = { isEstadoExpanded = !isEstadoExpanded }
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEstadoExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isEstadoExpanded,
                        onDismissRequest = { isEstadoExpanded = false }
                    ) {
                        estados.forEach { selectedEstado ->
                            DropdownMenuItem(
                                text = { Text(text = selectedEstado) },
                                onClick = {
                                    onEstadoChange(selectedEstado)
                                    isEstadoExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isUsuarioIdValid && isMedicoIdValid
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAppointmentCard(
    appointment: Appointment,
    onSave: (Appointment) -> Unit,
    onCancel: () -> Unit,
    onUsuarioIdChange: (String) -> Unit,
    onMedicoIdChange: (String) -> Unit,
    onFechaChange: (LocalDate) -> Unit,
    onHoraChange: (LocalTime) -> Unit,
    onEstadoChange: (String) -> Unit,
    usuarioId: String,
    medicoId: String,
    fecha: LocalDate,
    hora: LocalTime,
    estado: String,
    onShowDatePicker: () -> Unit,
    onShowTimePicker: () -> Unit
) {
    var isUsuarioIdValid by remember { mutableStateOf(true) }
    var isMedicoIdValid by remember { mutableStateOf(true) }
    var isEstadoExpanded by remember { mutableStateOf(false) }
    var usuarioIdError by remember { mutableStateOf<String?>(null) }
    var medicoIdError by remember { mutableStateOf<String?>(null) }
    val estados = listOf("Confirmada", "Cancelada")

    fun validateUsuarioId(id: String) {
        isUsuarioIdValid = id.isNotBlank() && id.all { it.isDigit() }
        usuarioIdError = when {
            id.isBlank() -> "El ID de usuario no puede estar vacío"
            !id.all { it.isDigit() } -> "El ID de usuario debe ser un número"
            else -> null
        }
    }

    fun validateMedicoId(id: String) {
        isMedicoIdValid = id.isNotBlank() && id.all { it.isDigit() }
        medicoIdError = when {
            id.isBlank() -> "El ID de médico no puede estar vacío"
            !id.all { it.isDigit() } -> "El ID de médico debe ser un número"
            else -> null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = usuarioId,
                onValueChange = {
                    onUsuarioIdChange(it)
                    validateUsuarioId(it)
                },
                label = { Text("Usuario ID") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isUsuarioIdValid,
                supportingText = {
                    if (usuarioIdError != null) {
                        Text(text = usuarioIdError!!)
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = medicoId,
                onValueChange = {
                    onMedicoIdChange(it)
                    validateMedicoId(it)
                },
                label = { Text("Medico ID") },
                modifier = Modifier.fillMaxWidth(),
                isError = !isMedicoIdValid,
                supportingText = {
                    if (medicoIdError != null) {
                        Text(text = medicoIdError!!)
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fecha: ${fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onShowDatePicker) {
                    Text("Seleccionar Fecha")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hora: ${hora.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onShowTimePicker) {
                    Text("Seleccionar Hora")
                }
            }
            ExposedDropdownMenuBox(
                expanded = isEstadoExpanded,
                onExpandedChange = { isEstadoExpanded = !isEstadoExpanded }
            ) {
                OutlinedTextField(
                    value = estado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isEstadoExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isEstadoExpanded,
                    onDismissRequest = { isEstadoExpanded = false }
                ) {
                    estados.forEach { selectedEstado ->
                        DropdownMenuItem(
                            text = { Text(text = selectedEstado) },
                            onClick = {
                                onEstadoChange(selectedEstado)
                                isEstadoExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onSave(
                            appointment.copy(
                                usuario_id = usuarioId.toInt(),
                                medico_id = medicoId.toInt(),
                                fecha = Fecha(fecha.dayOfMonth, fecha.monthValue, fecha.year),
                                hora = Hora(hora.hour, hora.minute),
                                estado = estado
                            )
                        )
                    },
                    enabled = isUsuarioIdValid && isMedicoIdValid
                ) {
                    Text("Guardar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onCancel) {
                    Text("Cancelar")
                }
            }
        }
    }
}
@Composable
fun AppointmentCard(
    appointment: Appointment,
    onDelete: (Appointment) -> Unit,
    onUpdate: (Appointment) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Usuario ID: ${appointment.usuario_id}")
            Text(text = "Medico ID: ${appointment.medico_id}")
            Text(text = "Fecha: ${appointment.fecha.day}/${appointment.fecha.month}/${appointment.fecha.year}")
            Text(text = "Hora: ${appointment.hora.hour}:${appointment.hora.minute}")
            Text(text = "Estado: ${appointment.estado}")
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = { onDelete(appointment) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
                IconButton(onClick = { onUpdate(appointment) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
            }
        }
    }
}
