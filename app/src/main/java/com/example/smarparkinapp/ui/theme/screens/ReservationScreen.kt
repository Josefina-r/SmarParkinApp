package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    // CORREGIDO: Usar las propiedades directamente del ViewModel
    val selectedParking = viewModel.selectedParking
    val selectedVehicle = viewModel.selectedVehicle
    val isLoading by viewModel.isLoading.collectAsState()
    val createdReservation by viewModel.createdReservation.collectAsState()

    // Estados para pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Navegar a payment cuando se crea la reserva
    LaunchedEffect(createdReservation) {
        createdReservation?.let { reservation ->
            navController.navigate("payment/${reservation.id}")
        }
    }
    LaunchedEffect(Unit) {
        println("=== 🔍 RESERVATION SCREEN DEBUG ===")
        println("🏢 Selected Parking: $selectedParking")
        println("🚗 Selected Vehicle: $selectedVehicle")
        println("📱 ViewModel: $viewModel")
        println("=== FIN DEBUG ===")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reserva",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.createReservation()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = selectedParking != null &&
                            selectedVehicle != null &&
                            viewModel.reservationDate.isNotEmpty() &&
                            viewModel.reservationStartTime.isNotEmpty() &&
                            viewModel.reservationEndTime.isNotEmpty() &&
                            !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        val costoEstimado = calculateEstimatedCost(
                            parking = selectedParking,
                            startTime = viewModel.reservationStartTime,
                            endTime = viewModel.reservationEndTime,
                            reservationType = viewModel.reservationType
                        )
                        Text(
                            "Pagar S/ ${"%.2f".format(costoEstimado)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 16.sp
                        )
                    }
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
            // Información del estacionamiento seleccionado
            selectedParking?.let { parking ->
                ParkingDetailsCard(parking)
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Información del vehículo seleccionado
            selectedVehicle?.let { vehicle ->
                VehicleInfoCard(vehicle, navController)
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }

            // Tipo de reserva
            ReservationTypeSection(
                reservationType = viewModel.reservationType,
                onTypeSelected = { type -> viewModel.setReservationType(type) }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            @Composable
            fun DateSelectionSection(
                selectedDate: String,
                onDateSelected: (String) -> Unit
            ) {
                var showDatePicker by remember { mutableStateOf(false) }

                Column {
                    Text(
                        text = "Fecha de reserva",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Text(
                            text = selectedDate.ifEmpty { "Seleccionar fecha" }
                        )
                    }

                    if (showDatePicker) {
                        // Aquí puedes implementar un DatePicker
                        // Por ejemplo, usando DatePickerDialog nativo
                    }
                }
            }

            // Date Picker Dialog
            if (showDatePicker) {
                LaunchedEffect(showDatePicker) {
                    if (viewModel.reservationDate.isEmpty()) {
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date())
                        viewModel.setReservationDate(today)
                    }
                    showDatePicker = false
                }
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Horarios
            TimeSelectionSection(
                startTime = viewModel.reservationStartTime,
                endTime = viewModel.reservationEndTime,
                onStartTimeSelected = { showStartTimePicker = true },
                onEndTimeSelected = { showEndTimePicker = true }
            )

            // Time Picker Dialogs
            if (showStartTimePicker) {
                LaunchedEffect(showStartTimePicker) {
                    if (viewModel.reservationStartTime.isEmpty()) {
                        viewModel.setReservationStartTime("08:00")
                    }
                    showStartTimePicker = false
                }
            }

            if (showEndTimePicker) {
                LaunchedEffect(showEndTimePicker) {
                    if (viewModel.reservationEndTime.isEmpty()) {
                        viewModel.setReservationEndTime("09:00")
                    }
                    showEndTimePicker = false
                }
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Resumen de costo estimado
            CostSummarySection(
                parking = selectedParking,
                startTime = viewModel.reservationStartTime,
                endTime = viewModel.reservationEndTime,
                reservationType = viewModel.reservationType
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ParkingDetailsCard(parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                parking.nombre,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                parking.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rating y seguridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "%.1f".format(parking.rating_promedio ?: 0.0),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        " (${parking.total_resenas ?: 0})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Security,
                        contentDescription = "Seguridad",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Nivel ${parking.nivel_seguridad ?: "1"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de tarifas y disponibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Tarifa por hora:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "S/ ${"%.2f".format(parking.tarifa_hora)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        "Espacios disponibles:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${parking.plazas_disponibles}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Horario
            parking.horario_apertura?.let { apertura ->
                parking.horario_cierre?.let { cierre ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Watch,
                            contentDescription = "Horario",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "$apertura - $cierre",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Vehículo seleccionado",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    vehicle.plate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = {

                    navController.popBackStack()
                }
            ) {
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
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "⏱️ Tipo de reserva",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium
                )
            )

            // Indicador de selección
            if (reservationType.isNotEmpty()) {
                Text(
                    "Seleccionado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón "Por hora" - Tarjeta interactiva
            ReservationTypeCard(
                title = "🕐 Por hora",
                description = "Flexible - paga por horas",
                isSelected = reservationType == "hora",
                onClick = { onTypeSelected("hora") },
                modifier = Modifier.weight(1f)
            )

            // Botón "Por día" - Tarjeta interactiva
            ReservationTypeCard(
                title = "📅 Por día",
                description = "Económico - tarifa completa",
                isSelected = reservationType == "dia",
                onClick = { onTypeSelected("dia") },
                modifier = Modifier.weight(1f)
            )
        }

        // Información adicional
        if (reservationType.isNotEmpty()) {
            Text(
                text = when (reservationType) {
                    "hora" -> "• Pagas solo por las horas que uses\n• Ideal para visitas cortas"
                    "dia" -> "• Tarifa plana por día completo\n• Perfecto para todo el día"
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Text(
                "Seleccione el tipo de reserva que prefiera",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ReservationTypeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder()
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Título
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Descripción
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Indicador de selección
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSelected) "Seleccionado" else "Seleccionar",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimeSelectionSection(
    startTime: String,
    endTime: String,
    onStartTimeSelected: () -> Unit,
    onEndTimeSelected: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                " Horarios de reserva",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium
                )
            )

            // Indicador de completado
            if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                Text(
                    "Completado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Check-in - Tarjeta interactiva
            TimeSelectionCard(
                title = "🟢 Check-in",
                subtitle = "Hora de entrada",
                selectedTime = startTime,
                defaultTime = "08:00",
                onClick = onStartTimeSelected,
                modifier = Modifier.weight(1f)
            )

            // Check-out - Tarjeta interactiva
            TimeSelectionCard(
                title = " Check-out",
                subtitle = "Hora de salida",
                selectedTime = endTime,
                defaultTime = "18:00",
                onClick = onEndTimeSelected,
                modifier = Modifier.weight(1f)
            )
        }

        // Validación y información de horarios
        if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
            val duration = calculateDuration(startTime, endTime)
            val isValid = isTimeRangeValid(startTime, endTime)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                // Duración calculada
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "⏳ Duración estimada:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        duration,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (isValid) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                        )
                    )
                }

                // Mensaje de validación
                if (!isValid) {
                    Text(
                        "⚠️ La hora de salida debe ser posterior a la de entrada",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            Text(
                "Seleccione tanto la hora de entrada como de salida",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun TimeSelectionCard(
    title: String,
    subtitle: String,
    selectedTime: String,
    defaultTime: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Título y subtítulo
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hora seleccionada
            Text(
                text = if (selectedTime.isNotEmpty()) selectedTime else defaultTime,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTime.isNotEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Indicador de acción
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Seleccionar hora",
                    tint = if (selectedTime.isNotEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (selectedTime.isNotEmpty()) "Cambiar" else "Seleccionar",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedTime.isNotEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun calculateDuration(startTime: String, endTime: String): String {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = format.parse(startTime)
        val end = format.parse(endTime)

        if (start != null && end != null) {
            val diff = end.time - start.time
            val hours = diff / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)

            if (hours > 0) {
                if (minutes > 0) {
                    "${hours}h ${minutes}m"
                } else {
                    "${hours}h"
                }
            } else {
                "${minutes}m"
            }
        } else {
            "0h"
        }
    } catch (e: Exception) {
        "0h"
    }
}

// Función auxiliar para validar rango de tiempo
private fun isTimeRangeValid(startTime: String, endTime: String): Boolean {
    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = format.parse(startTime)
        val end = format.parse(endTime)
        start != null && end != null && end.after(start)
    } catch (e: Exception) {
        false
    }
}

@Composable
private fun CostSummarySection(
    parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    startTime: String,
    endTime: String,
    reservationType: String
) {
    val costoEstimado = calculateEstimatedCost(parking, startTime, endTime, reservationType)

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            "Resumen de costo",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            PriceRow(
                description = "Tarifa base",
                amount = "S/ ${"%.2f".format(costoEstimado)}"
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            PriceRow(
                description = "Total estimado",
                amount = "S/ ${"%.2f".format(costoEstimado)}",
                isTotal = true
            )
        }
    }
}

@Composable
private fun PriceRow(
    description: String,
    amount: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = description,
            style = if (isTotal) {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (isTotal) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        Text(
            text = amount,
            style = if (isTotal) {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (isTotal) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

// Funciones auxiliares
private fun calculateEstimatedCost(
    parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    startTime: String,
    endTime: String,
    reservationType: String
): Double {
    if (parking == null || startTime.isEmpty() || endTime.isEmpty()) return 0.0

    return try {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = format.parse(startTime)
        val end = format.parse(endTime)

        if (start != null && end != null) {
            val hours = ((end.time - start.time) / (1000 * 60 * 60)).toDouble()
            if (reservationType == "dia") {
                // Asumir tarifa por día = 8 horas de tarifa por hora
                parking.tarifa_hora * 8
            } else {
                parking.tarifa_hora * hours
            }
        } else {
            parking.tarifa_hora
        }
    } catch (e: Exception) {
        parking.tarifa_hora
    }
}

private fun formatDateToString(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}