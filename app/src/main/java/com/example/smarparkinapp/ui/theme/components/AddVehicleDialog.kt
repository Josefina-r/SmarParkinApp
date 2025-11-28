package com.example.smarparkinapp.ui.theme.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
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
    // Estados locales para el formulario
    var vehicleBrand by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleColor by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar errores
                if (errorMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Campos del formulario
                OutlinedTextField(
                    value = vehicleBrand,
                    onValueChange = {
                        vehicleBrand = it
                        errorMessage = null
                    },
                    label = { Text("Marca *") },
                    placeholder = { Text("Ej: Toyota, Honda, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = vehicleModel,
                    onValueChange = {
                        vehicleModel = it
                        errorMessage = null
                    },
                    label = { Text("Modelo *") },
                    placeholder = { Text("Ej: Corolla, Civic, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = vehicleColor,
                    onValueChange = {
                        vehicleColor = it
                        errorMessage = null
                    },
                    label = { Text("Color *") },
                    placeholder = { Text("Ej: Blanco, Negro, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = vehiclePlate,
                    onValueChange = { vehiclePlate = it.uppercase() },
                    label = { Text("Placa *") },
                    placeholder = { Text("Ej: ABC123") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = vehiclePlate.isNotEmpty() && !isValidPlate(vehiclePlate),
                    enabled = !isLoading
                )

                if (vehiclePlate.isNotEmpty() && !isValidPlate(vehiclePlate)) {
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
                        if (isFormValid(vehicleBrand, vehicleModel, vehicleColor, vehiclePlate)) {
                            isLoading = true
                            errorMessage = null

                            println("🔄 [Dialog] Guardando vehículo: $vehiclePlate")

                            // ✅ CORREGIDO: Llamar al ViewModel para guardar
                            viewModel.addVehicle(
                                plate = vehiclePlate,
                                brand = vehicleBrand,
                                model = vehicleModel,
                                color = vehicleColor,
                                onSuccess = { car ->
                                    println("✅ [Dialog] Vehículo guardado exitosamente: ${car.plate}")
                                    isLoading = false
                                    onSave() // Recargar lista
                                    onDismiss() // Cerrar diálogo
                                },
                                onError = { error ->
                                    println("❌ [Dialog] Error guardando vehículo: $error")
                                    isLoading = false
                                    errorMessage = error
                                }
                            )
                        } else {
                            errorMessage = "Por favor completa todos los campos correctamente"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && isFormValid(vehicleBrand, vehicleModel, vehicleColor, vehiclePlate)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar Vehículo")
                    }
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
private fun isFormValid(
    brand: String,
    model: String,
    color: String,
    plate: String
): Boolean {
    return brand.isNotEmpty() &&
            model.isNotEmpty() &&
            color.isNotEmpty() &&
            plate.isNotEmpty() &&
            isValidPlate(plate)
}