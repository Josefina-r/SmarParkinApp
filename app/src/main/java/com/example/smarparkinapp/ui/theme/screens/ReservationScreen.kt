package com.example.smarparkinapp.ui.theme.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.viewmodel.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    parkingId: Int,
    reservationViewModel: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(LocalContext.current)),
    onSuccessNavigate: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var carId by remember { mutableStateOf("") }
    var tipoReserva by remember { mutableStateOf("normal") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("pendiente") }
    var montoTotal by remember { mutableStateOf("") }

    val isLoading by reservationViewModel.isLoading.collectAsState()
    val error by reservationViewModel.error.collectAsState()
    val createdReservation by reservationViewModel.createdReservation.collectAsState()

    val tipoReservaOptions = listOf("normal", "premium", "vip")
    val estadoOptions = listOf("pendiente", "confirmada", "activa")

    // SI LA RESERVA SE CREÓ — NAVEGA
    LaunchedEffect(createdReservation) {
        if (createdReservation != null) {
            Toast.makeText(context, "Reserva creada correctamente", Toast.LENGTH_SHORT).show()
            onSuccessNavigate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CarRental,
                contentDescription = "Reserva",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Crear Nueva Reserva",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Información del Parking
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Parking",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Parking ID: $parkingId",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ------------------------- CAR ID -------------------------
                OutlinedTextField(
                    value = carId,
                    onValueChange = { carId = it },
                    label = { Text("ID del Vehículo *") },
                    placeholder = { Text("Ingresa el ID del vehículo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.CarRental, contentDescription = "Vehículo")
                    },
                    singleLine = true,
                    isError = carId.isEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------------- TIPO RESERVA -------------------------
                Column {
                    Text(
                        text = "Tipo de Reserva *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tipoReservaOptions.forEach { option ->
                            FilterChip(
                                selected = tipoReserva == option,
                                onClick = { tipoReserva = option },
                                label = { Text(option.replaceFirstChar { it.uppercase() }) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------------- ESTADO -------------------------
                Column {
                    Text(
                        text = "Estado de Reserva",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        estadoOptions.forEach { option ->
                            FilterChip(
                                selected = estado == option,
                                onClick = { estado = option },
                                label = { Text(option.replaceFirstChar { it.uppercase() }) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------------- HORARIOS -------------------------
                Column {
                    Text(
                        text = "Horarios *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // HORA INICIO
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        horaInicio = "%02d:%02d".format(hour, minute)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (horaInicio.isEmpty()) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Hora inicio",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (horaInicio.isEmpty()) "Inicio" else horaInicio,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        // HORA FIN
                        Button(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        horaFin = "%02d:%02d".format(hour, minute)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (horaFin.isEmpty()) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Hora fin",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (horaFin.isEmpty()) "Fin" else horaFin,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ------------------------- MONTO TOTAL -------------------------
                OutlinedTextField(
                    value = montoTotal,
                    onValueChange = {
                        // Validar que solo sean números y punto decimal
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            montoTotal = it
                        }
                    },
                    label = { Text("Monto Total") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Payments, contentDescription = "Monto")
                    },
                    suffix = { Text("USD") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Opcional - se calculará automáticamente si se deja vacío",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ------------------------- CREAR RESERVA -------------------------
        Button(
            onClick = {
                if (carId.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
                    Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val hoy = "2025-11-15" // cambiar por LocalDate.now().toString()

                reservationViewModel.createReservation(
                    parkingId = parkingId,
                    carId = carId.toInt(),
                    horaInicio = "${hoy}T${horaInicio}:00",
                    horaFin = "${hoy}T${horaFin}:00",
                    tipoReserva = tipoReserva,
                    estado = estado,
                    montoTotal = if (montoTotal.isNotEmpty()) montoTotal.toDouble() else null
                )
            },
            enabled = !isLoading && carId.isNotEmpty() && horaInicio.isNotEmpty() && horaFin.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Creando Reserva...")
            } else {
                Icon(
                    imageVector = Icons.Default.CarRental,
                    contentDescription = "Crear",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Reserva")
            }
        }

        // ------------------------- ERROR -------------------------
        error?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Error",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Información adicional
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Los campos marcados con * son obligatorios",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}