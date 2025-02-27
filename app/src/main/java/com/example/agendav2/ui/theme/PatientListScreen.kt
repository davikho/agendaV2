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
import com.example.agendav2.model.User
import com.example.agendav2.viewModel.PatientViewModel
import com.example.agendav2.viewModel.PatientListState
import com.example.agendav2.api.RetrofitClient
import com.example.agendav2.data.PatientRepository
import com.example.agendav2.model.Doctor
import com.example.agendav2.model.Patient
import com.example.agendav2.viewModel.PatientViewModelFactory

@Composable
fun PatientListScreen() {
    val patientRepository = PatientRepository(RetrofitClient.instance)
    val factory = PatientViewModelFactory(patientRepository)
    val patientViewModel: PatientViewModel = viewModel(factory = factory)
    val patientListState by patientViewModel.patientListState.collectAsState()

    var isCreatingPatient by remember { mutableStateOf(false) }
    var newPatientNombre by remember { mutableStateOf("") }
    var newPatientTipo by remember { mutableStateOf("") }
    var newPatientClave by remember { mutableStateOf("") }

    when (patientListState) {
        is PatientListState.Loading -> CircularProgressIndicator()
        is PatientListState.Success -> {
            val patients = (patientListState as PatientListState.Success).patients
            LazyColumn {
                item {
                    CreatePatientCard(
                        isCreating = isCreatingPatient,
                        onStartCreating = { isCreatingPatient = true },
                        onSave = {
                            if (newPatientNombre.isNotBlank() && newPatientTipo.isNotBlank() && newPatientClave.isNotBlank()) {
                                val newPatient = User(
                                    nombre = newPatientNombre,
                                    tipo = newPatientTipo,
                                    clave = newPatientClave
                                )
                                patientViewModel.createPatient(newPatient)
                                isCreatingPatient = false
                                newPatientNombre = ""
                                newPatientTipo = ""
                                newPatientClave = ""
                            }
                        },
                        onNombreChange = { newPatientNombre = it },
                        onTipoChange = { newPatientTipo = it },
                        onClaveChange = { newPatientClave = it },
                        nombre = newPatientNombre,
                        tipo = newPatientTipo,
                        clave = newPatientClave
                    )
                }
                items(patients) { patient ->
                    PatientCard(
                        patient = patient,
                        onDelete = { patientViewModel.deletePatient(patient) },
                        onUpdate = { updatedPatient ->
                            patientViewModel.updatePatient(updatedPatient)
                        }
                    )
                }
            }
        }
        is PatientListState.Error -> {
            val errorMessage = (patientListState as PatientListState.Error).message
            Text(text = "Error: $errorMessage")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePatientCard(
    isCreating: Boolean,
    onStartCreating: () -> Unit,
    onSave: () -> Unit,
    onNombreChange: (String) -> Unit,
    onTipoChange: (String) -> Unit,
    onClaveChange: (String) -> Unit,
    nombre: String,
    tipo: String,
    clave: String
) {
    var isNombreValid by remember { mutableStateOf(true) }
    var isTipoValid by remember { mutableStateOf(true) }
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
                    value = tipo,
                    onValueChange = {
                        onTipoChange(it)
                        isTipoValid = it.isNotBlank()
                    },
                    label = { Text("tipo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isTipoValid
                )
                if (!isTipoValid) {
                    Text(text = "El tipo es requerido", color = Color.Red)
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
                    enabled = isNombreValid && isTipoValid && isClaveValid
                ) {
                    Text("Guardar")
                }
            } else {
                Button(onClick = onStartCreating) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Crear usuario")
                    Text("Crear Nuevo usuario")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCard(patient: User, onDelete: () -> Unit, onUpdate: (User) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNombre by remember { mutableStateOf(patient.nombre ?: "") }
    var editedTipo by remember { mutableStateOf(patient.tipo ?: "") }
    var editedClave by remember { mutableStateOf(patient.clave ?: "") }

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
                    value = editedTipo,
                    onValueChange = { editedTipo = it },
                    label = { Text("Tipo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = editedClave,
                    onValueChange = { editedClave = it },
                    label = { Text("Clave") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    val updatedPatient = patient.copy(nombre = editedNombre, tipo = editedTipo, clave = editedClave)
                    onUpdate(updatedPatient)
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
                        Text(text = "Nombre: ${patient.nombre}")
                        Text(text = "Tipo: ${patient.tipo}")
                        Text(text = "Id: ${patient.id}")
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