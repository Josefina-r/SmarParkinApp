// ui/theme/screens/ReservationScreen.kt
package com.example.smarparkinapp.ui.theme.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.components.AddVehicleDialog
import com.example.smarparkinapp.ui.theme.components.DateSelectorDialog
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    viewModel: ReservationViewModel = viewModel(),
    selectedParking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    onSuccessNavigate: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Estados locales
    var currentStep by remember { mutableStateOf(0) } // 0: Vehículo, 1: Fecha/Hora, 2: Pago
    var showAddVehicleDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePickerInicio by remember { mutableStateOf(false) }
    var showTimePickerFin by remember { mutableStateOf(false) }

    // Observar estados del ViewModel
    val vehicles = viewModel.vehicles.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val createdReservation = viewModel.createdReservation.collectAsState().value

    // Inicializar el estacionamiento seleccionado - CORREGIDO
    LaunchedEffect(selectedParking) {
        selectedParking?.let { parking ->
            // CAMBIO: Asignar directamente en lugar de usar setSelectedParking
            viewModel.selectedParking = parking
        }
    }

    // Navegar al éxito cuando se complete el pago
    LaunchedEffect(key1 = createdReservation) {
        if (createdReservation != null && currentStep == 2) {
            onSuccessNavigate()
        }
    }

    // Mostrar errores
    LaunchedEffect(key1 = error) {
        if (!error.isNullOrEmpty()) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // TimePickers
    if (showTimePickerInicio) {
        TimePickerDialogComposable(
            showDialog = showTimePickerInicio,
            onTimeSelected = { hour, minute ->
                viewModel.updateReservationStartTime("%02d:%02d".format(hour, minute))
                showTimePickerInicio = false
            },
            onDismiss = { showTimePickerInicio = false }
        )
    }

    if (showTimePickerFin) {
        TimePickerDialogComposable(
            showDialog = showTimePickerFin,
            onTimeSelected = { hour, minute ->
                viewModel.updateReservationEndTime("%02d:%02d".format(hour, minute))
                showTimePickerFin = false
            },
            onDismiss = { showTimePickerFin = false }
        )
    }

    // DatePicker
    if (showDatePicker) {
        DateSelectorDialog(
            selectedDate = viewModel.reservationDate,
            onDateSelected = { selectedDate ->
                viewModel.updateReservationDate(selectedDate)
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Dialog para agregar vehículo
    if (showAddVehicleDialog) {
        AddVehicleDialog(
            viewModel = viewModel,
            onDismiss = {
                showAddVehicleDialog = false
                viewModel.hideAddVehicleForm()
            },
            onSave = {
                showAddVehicleDialog = false
                viewModel.saveNewVehicleAndNavigate()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (currentStep) {
                            0 -> "Seleccionar Vehículo"
                            1 -> "Fecha y Horario"
                            2 -> "Método de Pago"
                            else -> "Nueva Reserva"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Stepper
            StepperIndicator(currentStep = currentStep, totalSteps = 3)

            Spacer(modifier = Modifier.height(24.dp))

            when (currentStep) {
                0 -> VehicleSelectionStep(
                    vehicles = vehicles,
                    selectedVehicle = viewModel.selectedVehicle,
                    selectedParking = viewModel.selectedParking,
                    onVehicleSelected = { vehicle ->
                        viewModel.selectVehicle(vehicle)
                    },
                    onAddVehicle = {
                        showAddVehicleDialog = true
                    },
                    onContinue = {
                        if (viewModel.selectedVehicle != null) {
                            currentStep = 1
                        } else {
                            Toast.makeText(context, "Selecciona un vehículo", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                1 -> DateTimeSelectionStep(
                    viewModel = viewModel,
                    onDateClick = { showDatePicker = true },
                    onStartTimeClick = { showTimePickerInicio = true },
                    onEndTimeClick = { showTimePickerFin = true },
                    onContinue = {
                        if (viewModel.validateReservationForm()) {
                            currentStep = 2
                        } else {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onBack = { currentStep = 0 }
                )

                2 -> PaymentStep(
                    viewModel = viewModel,
                    isLoading = isLoading,
                    onPay = {
                        viewModel.processPayment()
                    },
                    onBack = { currentStep = 1 }
                )
            }
        }
    }
}

@Composable
private fun StepperIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { step ->
            val isActive = step == currentStep
            val isCompleted = step < currentStep

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp),
                contentAlignment = Alignment.Center
            ) {
                LinearProgressIndicator(
                    progress = if (isCompleted) 1f else if (isActive) 0.5f else 0f,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isCompleted || isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            }

            if (step < totalSteps - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
private fun VehicleSelectionStep(
    vehicles: List<com.example.smarparkinapp.data.model.Car>,
    selectedVehicle: com.example.smarparkinapp.data.model.Car?,
    selectedParking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    onVehicleSelected: (com.example.smarparkinapp.data.model.Car) -> Unit,
    onAddVehicle: () -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información del Parking con nombre real
        ParkingInfoCard(selectedParking = selectedParking)

        // Selección de Vehículo
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selecciona tu vehículo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = onAddVehicle,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (vehicles.isEmpty()) {
                    Text(
                        text = "No hay vehículos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        vehicles.forEach { vehicle ->
                            Card(
                                onClick = { onVehicleSelected(vehicle) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedVehicle?.id == vehicle.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = "Vehículo",
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${vehicle.brand} ${vehicle.model}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "${vehicle.plate} • ${vehicle.color}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón continuar
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedVehicle != null
        ) {
            Text("Continuar a Fecha y Horario")
        }
    }
}

@Composable
private fun DateTimeSelectionStep(
    viewModel: ReservationViewModel,
    onDateClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información del estacionamiento con nombre real
        ParkingInfoCard(selectedParking = viewModel.selectedParking)

        // Información del vehículo seleccionado
        viewModel.selectedVehicle?.let { vehicle ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = "Vehículo")
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("${vehicle.brand} ${vehicle.model}")
                        Text(
                            "${vehicle.plate} • ${vehicle.color}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Selector de fecha
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDateClick
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Fecha")
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fecha de Reserva")
                    Text(
                        text = if (viewModel.reservationDate.isNotEmpty()) viewModel.reservationDate else "Seleccionar fecha",
                        color = if (viewModel.reservationDate.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Seleccionar")
            }
        }

        // Selectores de hora
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.weight(1f), onClick = onStartTimeClick) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = "Hora inicio")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hora Inicio")
                    Text(
                        text = if (viewModel.reservationStartTime.isNotEmpty()) viewModel.reservationStartTime else "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Card(modifier = Modifier.weight(1f), onClick = onEndTimeClick) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = "Hora fin")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hora Fin")
                    Text(
                        text = if (viewModel.reservationEndTime.isNotEmpty()) viewModel.reservationEndTime else "--:--",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // Tipo de Reserva
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tipo de Reserva", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Precio Estimado")
                Text(
                    text = "S/ %.2f".format(viewModel.getReservationPrice()),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Atrás")
            }

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = viewModel.validateReservationForm()
            ) {
                Text("Continuar a Pago")
            }
        }
    }
}

@Composable
private fun PaymentStep(
    viewModel: ReservationViewModel,
    isLoading: Boolean,
    onPay: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Resumen de la reserva con información real
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Resumen de Reserva", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                viewModel.selectedParking?.let { parking ->
                    InfoRow("Estacionamiento:", parking.nombre ?: "No disponible")
                    InfoRow("Dirección:", parking.direccion ?: "No disponible")
                    InfoRow("Tarifa:", "S/ ${parking.tarifa_hora ?: 0.0} por hora")
                    InfoRow("Horario:", "${parking.horario_apertura ?: "07:00"} - ${parking.horario_cierre ?: "23:00"}")
                }

                viewModel.selectedVehicle?.let { vehicle ->
                    InfoRow("Vehículo:", "${vehicle.brand} ${vehicle.model}")
                    InfoRow("Placa:", vehicle.plate)
                }

                InfoRow("Fecha:", viewModel.reservationDate)
                InfoRow("Horario:", "${viewModel.reservationStartTime} - ${viewModel.reservationEndTime}")
                InfoRow("Tipo:", if (viewModel.reservationType == "hora") "Por Hora" else "Por Día")

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                InfoRow("Total:", "S/ %.2f".format(viewModel.getReservationPrice()))
            }
        }

        // Métodos de pago
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Selecciona método de pago", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                viewModel.availablePaymentMethods.forEach { method ->
                    PaymentMethodItem(
                        method = method,
                        isSelected = viewModel.selectedPaymentMethod == method,
                        onSelected = { viewModel.selectPaymentMethod(method) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botones
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Atrás")
            }

            Button(
                onClick = onPay,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = viewModel.selectedPaymentMethod != null && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesando...")
                } else {
                    Text("Pagar S/ %.2f".format(viewModel.getReservationPrice()))
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onSelected,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono diferente según el método de pago
            val icon = when (method) {
                "Tarjeta de Crédito" -> Icons.Default.CreditCard
                "Tarjeta de Débito" -> Icons.Default.CreditCard
                "Yape" -> Icons.Default.AccountBalanceWallet
                "Plin" -> Icons.Default.AccountBalanceWallet
                "Efectivo" -> Icons.Default.AttachMoney
                else -> Icons.Default.Payment
            }

            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(method, modifier = Modifier.weight(1f))
            RadioButton(selected = isSelected, onClick = null)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

@Composable
private fun ParkingInfoCard(selectedParking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Estacionamiento Seleccionado", style = MaterialTheme.typography.titleMedium)
            Text(
                selectedParking?.nombre ?: "No seleccionado",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                selectedParking?.direccion ?: "Dirección no disponible",
                style = MaterialTheme.typography.bodySmall
            )
            selectedParking?.let { parking ->
                Text(
                    "Tarifa: S/ ${parking.tarifa_hora ?: 0.0} por hora",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Horario: ${parking.horario_apertura ?: "07:00"} - ${parking.horario_cierre ?: "23:00"}",
                    style = MaterialTheme.typography.bodySmall
                )
                if ((parking.plazas_disponibles ?: 0) > 0) {
                    Text(
                        "Espacios disponibles: ${parking.plazas_disponibles}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// TimePicker (mantener el mismo que tenías)
@Composable
private fun TimePickerDialogComposable(
    showDialog: Boolean,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    if (showDialog) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute -> onTimeSelected(hour, minute) },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}