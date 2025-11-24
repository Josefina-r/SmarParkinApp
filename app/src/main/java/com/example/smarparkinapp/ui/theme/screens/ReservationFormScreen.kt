// ui/theme/components/ReservationFormScreen.kt
/*package com.example.smarparkinapp.ui.theme.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationFormScreen(
    viewModel: ReservationViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Observar estados del ViewModel - CORREGIDO
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val createdReservation = viewModel.createdReservation.collectAsState().value

    // Navegar automáticamente cuando se cree la reserva exitosamente
    LaunchedEffect(key1 = createdReservation) {
        if (createdReservation != null) {
            onContinue() // Navegar a la pantalla de pago
        }
    }

    // Mostrar errores
    LaunchedEffect(key1 = error) {
        if (!error.isNullOrEmpty()) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Mostrar el DatePicker si es necesario
    if (showDatePicker) {
        DateSelectorDialog(
            selectedDate = viewModel.reservationDate,
            onDateSelected = { selectedDate ->
                viewModel.updateReservationDate(selectedDate)
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalles de Reserva") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Información del estacionamiento
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Estacionamiento Seleccionado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    viewModel.selectedParking?.let { parking ->
                        Text("Nombre: ${parking.nombre}")
                        Text("Dirección: ${parking.direccion}")
                        Text("Tarifa: S/ ${parking.tarifa_hora} por hora")
                    } ?: run {
                        Text(
                            "No se ha seleccionado estacionamiento",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Información del vehículo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Vehículo Seleccionado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    viewModel.selectedVehicle?.let { vehicle ->
                        Text("Marca: ${vehicle.brand}")
                        Text("Modelo: ${vehicle.model}")
                        Text("Placa: ${vehicle.plate}")
                        Text("Color: ${vehicle.color}")
                    } ?: run {
                        Text(
                            "No se ha seleccionado vehículo",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Selector de fecha
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Fecha de Reserva",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (viewModel.reservationDate.isNotEmpty()) {
                                viewModel.reservationDate
                            } else {
                                "Seleccionar fecha"
                            },
                            color = if (viewModel.reservationDate.isEmpty()) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    // CORREGIDO: Usar KeyboardArrowRight en lugar de ArrowForwardIos
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Seleccionar",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Selectores de hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hora de inicio
                Card(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // Aquí podrías abrir un TimePickerDialog
                        // Por ahora, establecemos valores por defecto
                        viewModel.updateReservationStartTime("08:00")
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = "Hora inicio",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Hora Inicio",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (viewModel.reservationStartTime.isNotEmpty()) {
                                viewModel.reservationStartTime
                            } else {
                                "--:--"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Hora de fin
                Card(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // Aquí podrías abrir un TimePickerDialog
                        viewModel.updateReservationEndTime("10:00")
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = "Hora fin",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Hora Fin",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (viewModel.reservationEndTime.isNotEmpty()) {
                                viewModel.reservationEndTime
                            } else {
                                "--:--"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Tipo de Reserva
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tipo de Reserva",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = viewModel.reservationType == "hora",
                            onClick = { viewModel.updateReservationType("hora") },
                            label = { Text("Por Hora") }
                        )
                        FilterChip(
                            selected = viewModel.reservationType == "dia",
                            onClick = { viewModel.updateReservationType("dia") },
                            label = { Text("Por Día") }
                        )
                    }
                }
            }

            // Precio estimado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Precio Estimado",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "S/ ${viewModel.getReservationPrice()}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón de continuar
            Button(
                onClick = {
                    if (viewModel.validateReservationForm()) {
                        coroutineScope.launch {
                            viewModel.createReservation()
                            // NO llamar onContinue() aquí - se maneja automáticamente con LaunchedEffect
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Completa todos los campos de la reserva",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.validateReservationForm() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creando reserva...")
                } else {
                    Text(
                        text = "Continuar al Pago - S/ ${viewModel.getReservationPrice()}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}*/