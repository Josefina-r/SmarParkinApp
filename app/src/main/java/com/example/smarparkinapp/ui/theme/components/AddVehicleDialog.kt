package com.example.smarparkinapp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.smarparkinapp.data.model.VehicleType
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel  // ✅ CORREGIDO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(
    viewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    // Obtener valores directamente del ViewModel
    val vehicleType = viewModel.vehicleType
    val vehicleBrand = viewModel.vehicleBrand
    val vehicleModel = viewModel.vehicleModel
    val vehicleColor = viewModel.vehicleColor
    val vehiclePlate = viewModel.vehiclePlate

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Agregar Vehículo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Tipo de Vehículo
                Text(
                    "Tipo de Vehículo",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    VehicleTypeOption(
                        type = VehicleType.AUTOMOVIL,
                        selectedType = vehicleType,
                        onTypeSelected = { viewModel.updateVehicleType(it) },
                        modifier = Modifier.weight(1f) // ✅ AGREGADO weight aquí
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    VehicleTypeOption(
                        type = VehicleType.MOTOCICLETA,
                        selectedType = vehicleType,
                        onTypeSelected = { viewModel.updateVehicleType(it) },
                        modifier = Modifier.weight(1f) // ✅ AGREGADO weight aquí
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Marca
                OutlinedTextField(
                    value = vehicleBrand,
                    onValueChange = { viewModel.updateVehicleBrand(it) },
                    label = { Text("Marca (ej: BMW, Toyota)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Modelo
                OutlinedTextField(
                    value = vehicleModel,
                    onValueChange = { viewModel.updateVehicleModel(it) },
                    label = { Text("Modelo (ej: Camioneta, Sedán)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color
                OutlinedTextField(
                    value = vehicleColor,
                    onValueChange = { viewModel.updateVehicleColor(it) },
                    label = { Text("Color") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Placa
                OutlinedTextField(
                    value = vehiclePlate,
                    onValueChange = { viewModel.updateVehiclePlate(it) },
                    label = { Text("Placa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSave,
                        enabled = vehicleBrand.isNotEmpty() &&
                                vehicleModel.isNotEmpty() &&
                                vehicleColor.isNotEmpty() &&
                                vehiclePlate.isNotEmpty()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleTypeOption(
    type: VehicleType,
    selectedType: VehicleType,
    onTypeSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier // ✅ AGREGADO modifier como parámetro
) {
    val isSelected = selectedType == type
    val displayName = when (type) {
        VehicleType.AUTOMOVIL -> "Automóvil"
        VehicleType.MOTOCICLETA -> "Motocicleta"
    }

    Card(
        modifier = modifier // ✅ USANDO el modifier pasado como parámetro
            .clickable { onTypeSelected(type) },
        elevation = if (isSelected) CardDefaults.cardElevation(8.dp) else CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (type == VehicleType.AUTOMOVIL) {
                    Icons.Default.DirectionsCar
                } else {
                    Icons.Default.TwoWheeler
                },
                contentDescription = displayName,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}