// components/AddVehicleDialog.kt
package com.example.smarparkinapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(
    viewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agregar Vehículo",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Campos del formulario
                OutlinedTextField(
                    value = viewModel.vehicleBrand,
                    onValueChange = { viewModel.vehicleBrand = it },
                    label = { Text("Marca *") },
                    placeholder = { Text("Ej: Toyota, Honda, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.vehicleModel,
                    onValueChange = { viewModel.vehicleModel = it },
                    label = { Text("Modelo *") },
                    placeholder = { Text("Ej: Corolla, Civic, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.vehicleColor,
                    onValueChange = { viewModel.vehicleColor = it },
                    label = { Text("Color *") },
                    placeholder = { Text("Ej: Blanco, Negro, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.vehiclePlate,
                    onValueChange = { viewModel.vehiclePlate = it.uppercase() },
                    label = { Text("Placa *") },
                    placeholder = { Text("Ej: ABC123") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.vehiclePlate.isNotEmpty() && !isValidPlate(viewModel.vehiclePlate)
                )

                if (viewModel.vehiclePlate.isNotEmpty() && !isValidPlate(viewModel.vehiclePlate)) {
                    Text(
                        text = "Formato: 3 letras + 3 números (Ej: ABC123)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isFormValid(viewModel)) {
                            viewModel.saveNewVehicleAndNavigate()
                            onSave()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid(viewModel)
                ) {
                    Text("Guardar Vehículo")
                }
            }
        }
    }
}

// Función para validar placa
private fun isValidPlate(plate: String): Boolean {
    val standardPlateRegex = Regex("^[A-Z]{3}[0-9]{3}\$")
    return plate.matches(standardPlateRegex)
}

// Función para validar el formulario completo
private fun isFormValid(viewModel: ReservationViewModel): Boolean {
    return viewModel.vehicleBrand.isNotEmpty() &&
            viewModel.vehicleModel.isNotEmpty() &&
            viewModel.vehicleColor.isNotEmpty() &&
            viewModel.vehiclePlate.isNotEmpty() &&
            isValidPlate(viewModel.vehiclePlate)
}