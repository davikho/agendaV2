package com.example.agendav2.ui.theme

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agendav2.api.RetrofitClient
import com.example.agendav2.data.DoctorRepository
import com.example.agendav2.model.Doctor
import com.example.agendav2.viewModel.DoctorListState
import com.example.agendav2.viewModel.DoctorViewModel
import com.example.agendav2.viewModel.DoctorViewModelFactory

@Composable
fun DoctorListScreen() {
    val doctorRepository = DoctorRepository(RetrofitClient.doctorInstance)
    val factory = DoctorViewModelFactory(doctorRepository)
    val doctorViewModel: DoctorViewModel = viewModel(factory = factory)
    val doctorListState by doctorViewModel.doctorListState.collectAsState()

    var isCreatingDoctor by remember { mutableStateOf(false) }
    var newDoctorNombre by remember { mutableStateOf("") }
    var newDoctorEspecialidad by remember { mutableStateOf("") }
    var newDoctorClave by remember { mutableStateOf("") }

    when (doctorListState) {
        is DoctorListState.Loading -> CircularProgressIndicator()
        is DoctorListState.Success -> {
            val doctors = (doctorListState as DoctorListState.Success).doctors
            LazyColumn {
                item {
                    CreateDoctorCard(
                        isCreating = isCreatingDoctor,
                        onStartCreating = { isCreatingDoctor = true },
                        onSave = {
                            if (newDoctorNombre.isNotBlank() && newDoctorEspecialidad.isNotBlank() && newDoctorClave.isNotBlank()) {
                                val newDoctor = Doctor(
                                    nombre = newDoctorNombre,
                                    especialidad = newDoctorEspecialidad,
                                    //clave = newDoctorClave
                                )
                                doctorViewModel.createDoctor(newDoctor)
                                isCreatingDoctor = false
                                newDoctorNombre = ""
                                newDoctorEspecialidad = ""
                                newDoctorClave = ""
                            }
                        },
                        onNombreChange = { newDoctorNombre = it },
                        onEspecialidadChange = { newDoctorEspecialidad = it },
                        onClaveChange = { newDoctorClave = it },
                        nombre = newDoctorNombre,
                        especialidad = newDoctorEspecialidad,
                        clave = newDoctorClave
                    )
                }
                items(doctors) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        onDelete = { doctorViewModel.deleteDoctor(doctor) },
                        onUpdate = { updateDoctor ->
                            doctorViewModel.updateDoctor(updateDoctor)
                        }
                    )
                }
            }
        }
        is DoctorListState.Error -> {
            val errorMessage = (doctorListState as DoctorListState.Error).message
            Text(text = "Error: $errorMessage")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDoctorCard(
    isCreating: Boolean,
    onStartCreating: () -> Unit,
    onSave: () -> Unit,
    onNombreChange: (String) -> Unit,
    onEspecialidadChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    nombre: String,
    especialidad: String,
    clave: String
) {
    var isNombreValid by remember { mutableStateOf(true) }
    var isEspecialidadValid by remember { mutableStateOf(true) }
    var isClaveValid by remember { mutableStateOf(true) }

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
            if (isCreating) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        onNombreChange(it)
                        isNombreValid = it.isNotBlank()
                    },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isNombreValid
                )
                if (!isNombreValid) {
                    Text(text = "El nombre es requerido", color = Color.Red)
                }
                OutlinedTextField(
                    value = especialidad,
                    onValueChange = {
                        onEspecialidadChange(it)
                        isEspecialidadValid = it.isNotBlank()
                    },
                    label = { Text("Especialidad") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isEspecialidadValid
                )
                if (!isEspecialidadValid) {
                    Text(text = "La especialidad es requerida", color = Color.Red)
                }
                OutlinedTextField(
                    value = clave,
                    onValueChange = {
                        onClaveChange(it)
                        isClaveValid = it.isNotBlank()
                    },
                    label = { Text("Clave") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isClaveValid
                )
                if (!isClaveValid) {
                    Text(text = "La clave es requerida", color = Color.Red)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = onSave,
                    enabled = isNombreValid && isEspecialidadValid && isClaveValid
                ) {
                    Text("Guardar")
                }
            } else {
                Button(onClick = onStartCreating) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Crear Doctor")
                    Text("Crear Nuevo Doctor")
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorCard(doctor: Doctor, onDelete: () -> Unit, onUpdate: (Doctor) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNombre by remember { mutableStateOf(doctor.nombre ?: "") }
    var editedEspecialidad by remember { mutableStateOf(doctor.especialidad ?: "") }

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
            if (isEditing) {
                OutlinedTextField(
                    value = editedNombre,
                    onValueChange = { editedNombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedEspecialidad,
                    onValueChange = { editedEspecialidad = it },
                    label = { Text("Especialidad") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val updatedDoctor = doctor.copy(nombre = editedNombre, especialidad = editedEspecialidad)
                    onUpdate(updatedDoctor)
                    isEditing = false
                }) {
                    Text("Guardar")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Nombre: ${doctor.nombre}")
                        Text(text = "Especialidad: ${doctor.especialidad}")
                        Text(text = "Id: ${doctor.id}")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { isEditing = true }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }
    }
}