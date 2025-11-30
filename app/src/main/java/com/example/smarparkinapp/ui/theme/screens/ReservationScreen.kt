package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import com.example.smarparkinapp.ui.theme.NavRoutes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    navController: NavHostController,
    viewModel: ReservationViewModel
) {
    val selectedParking = viewModel.selectedParking
    val selectedVehicle = viewModel.selectedVehicle
    val isLoading by viewModel.isLoading.collectAsState()
    val createdReservation by viewModel.createdReservation.collectAsState()

    // Estados para el calendario y horas
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var showReservationTimePicker by remember { mutableStateOf(false) }

    val isFormComplete = selectedParking != null &&
            selectedVehicle != null &&
            viewModel.reservationDate.isNotEmpty() &&
            ((viewModel.reservationType == "hora" && viewModel.reservationStartTime.isNotEmpty() && viewModel.reservationEndTime.isNotEmpty()) ||
                    (viewModel.reservationType == "dia" && viewModel.reservationTime.isNotEmpty())) &&
            !isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reserva", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    navController.navigate(NavRoutes.Payment.route) {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                enabled = isFormComplete
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Continuar al Pago")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Información del estacionamiento
            selectedParking?.let { parking ->
                ParkingDetailsCard(parking)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información del vehículo
            selectedVehicle?.let { vehicle ->
                VehicleInfoCard(vehicle, navController)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tipo de reserva
            ReservationTypeSection(
                reservationType = viewModel.reservationType,
                onTypeSelected = { type -> viewModel.setReservationType(type) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selección de fecha
            DateSelectionSection(
                selectedDate = viewModel.reservationDate,
                onDateClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Horarios según el tipo de reserva
            when (viewModel.reservationType) {
                "hora" -> {
                    TimeRangeSelectionSection(
                        startTime = viewModel.reservationStartTime,
                        endTime = viewModel.reservationEndTime,
                        onStartTimeClick = { showStartTimePicker = true },
                        onEndTimeClick = { showEndTimePicker = true }
                    )
                }
                "dia" -> {
                    ReservationTimeSection(
                        reservationTime = viewModel.reservationTime,
                        onTimeClick = { showReservationTimePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen de costo
            CostSummarySection(
                parking = selectedParking,
                startTime = viewModel.reservationStartTime,
                endTime = viewModel.reservationEndTime,
                reservationTime = viewModel.reservationTime,
                reservationType = viewModel.reservationType
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(Date(millis))
                            viewModel.setReservationDate(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Pickers para las 24 horas
    if (showStartTimePicker) {
        Time24HoursDialog(
            title = "Hora de entrada",
            onTimeSelected = { time ->
                viewModel.setReservationStartTime(time)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        Time24HoursDialog(
            title = "Hora de salida",
            onTimeSelected = { time ->
                viewModel.setReservationEndTime(time)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }

    if (showReservationTimePicker) {
        Time24HoursDialog(
            title = "Hora de reserva",
            onTimeSelected = { time ->
                viewModel.updateReservationTime(time)
                showReservationTimePicker = false
            },
            onDismiss = { showReservationTimePicker = false }
        )
    }
}

@Composable
private fun DateSelectionSection(
    selectedDate: String,
    onDateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Fecha de reserva",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDateClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (selectedDate.isNotEmpty()) {
                        formatDateForDisplay(selectedDate)
                    } else {
                        "Seleccionar fecha"
                    }
                )
            }

            if (selectedDate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "📅 ${formatDateForDisplay(selectedDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TimeRangeSelectionSection(
    startTime: String,
    endTime: String,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Horarios de reserva",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TimeSelectionButton(
                    time = startTime,
                    label = "Hora de entrada",
                    onClick = onStartTimeClick,
                    modifier = Modifier.weight(1f)
                )

                TimeSelectionButton(
                    time = endTime,
                    label = "Hora de salida",
                    onClick = onEndTimeClick,
                    modifier = Modifier.weight(1f)
                )
            }

            if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⏰ $startTime - $endTime (${calculateDuration(startTime, endTime)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReservationTimeSection(
    reservationTime: String,
    onTimeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Hora de reserva",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            TimeSelectionButton(
                time = reservationTime,
                label = "Hora de inicio",
                onClick = onTimeClick,
                modifier = Modifier.fillMaxWidth()
            )

            if (reservationTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⏰ Reserva a las $reservationTime",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TimeSelectionButton(
    time: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (time.isNotEmpty()) time else "--:--")
        }
    }
}

@Composable
private fun Time24HoursDialog(
    title: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                // Generar todas las horas de 00:00 a 23:00
                val timeSlots = (0..23).flatMap { hour ->
                    listOf(
                        String.format("%02d:00", hour),
                        String.format("%02d:30", hour)
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(timeSlots.size) { index ->
                        val time = timeSlots[index]
                        OutlinedButton(
                            onClick = { onTimeSelected(time) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(time)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ParkingDetailsCard(parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(parking.nombre, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(parking.direccion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Tarifa: S/ ${"%.2f".format(parking.tarifa_hora)}", fontWeight = FontWeight.Medium)
                Text("Disponibles: ${parking.plazas_disponibles}", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun VehicleInfoCard(
    vehicle: com.example.smarparkinapp.ui.theme.data.model.Car,
    navController: NavHostController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Vehículo seleccionado", style = MaterialTheme.typography.titleSmall)
                Text("${vehicle.brand} ${vehicle.model}", style = MaterialTheme.typography.bodyMedium)
                Text(vehicle.plate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Cambiar")
            }
        }
    }
}

@Composable
private fun ReservationTypeSection(
    reservationType: String,
    onTypeSelected: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tipo de reserva", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ReservationTypeOption(
                title = "Por hora",
                isSelected = reservationType == "hora",
                onClick = { onTypeSelected("hora") },
                modifier = Modifier.weight(1f)
            )
            ReservationTypeOption(
                title = "Por día",
                isSelected = reservationType == "dia",
                onClick = { onTypeSelected("dia") },
                modifier = Modifier.weight(1f)
            )
        }

        // Información adicional según el tipo
        if (reservationType.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (reservationType) {
                    "hora" -> "• Pagas solo por las horas que uses\n• Selecciona hora de entrada y salida"
                    "dia" -> "• Tarifa plana por día completo\n• Selecciona hora de inicio"
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReservationTypeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceVariant

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor)
    ) {
        Text(title)
    }
}

@Composable
private fun CostSummarySection(
    parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    startTime: String,
    endTime: String,
    reservationTime: String,
    reservationType: String
) {
    val costoEstimado = calculateEstimatedCost(parking, startTime, endTime, reservationType)

    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de costo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total estimado:", style = MaterialTheme.typography.bodyMedium)
                Text("S/ ${"%.2f".format(costoEstimado)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }

            // Mostrar detalles según el tipo
            if (reservationType == "hora" && startTime.isNotEmpty() && endTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⏰ $startTime - $endTime (${calculateDuration(startTime, endTime)})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (reservationType == "dia" && reservationTime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "⏰ Inicio a las $reservationTime (Todo el día)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Funciones auxiliares
private fun calculateDuration(startTime: String, endTime: String): String {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = format.parse(startTime)
        val end = format.parse(endTime)

        if (start != null && end != null) {
            val diff = end.time - start.time
            val hours = diff / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)

            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        } else "0h"
    } catch (e: Exception) {
        "0h"
    }
}

private fun calculateEstimatedCost(
    parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    startTime: String,
    endTime: String,
    reservationType: String
): Double {
    if (parking == null) return 0.0

    return try {
        if (reservationType == "dia") {
            // Tarifa por día = 8 horas de tarifa por hora
            parking.tarifa_hora * 8
        } else {
            if (startTime.isEmpty() || endTime.isEmpty()) {
                parking.tarifa_hora
            } else {
                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                val start = format.parse(startTime)
                val end = format.parse(endTime)

                if (start != null && end != null) {
                    val hours = ((end.time - start.time) / (1000 * 60 * 60)).toDouble()
                    parking.tarifa_hora * hours
                } else {
                    parking.tarifa_hora
                }
            }
        }
    } catch (e: Exception) {
        parking.tarifa_hora
    }
}

private fun formatDateForDisplay(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}