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
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    viewModel: ReservationViewModel,
    onSuccessNavigate: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Estados locales para manejar la UI
    var selectedVehicleId by remember { mutableStateOf<Int?>(null) }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var fechaReserva by remember { mutableStateOf("") }
    var tipoReserva by remember { mutableStateOf("hora") }
    var showAddVehicleDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // TimePickers
    var showTimePickerInicio by remember { mutableStateOf(false) }
    var showTimePickerFin by remember { mutableStateOf(false) }

    // Obtener vehículos del ViewModel de forma segura
    val vehicles = remember { viewModel.vehicles }.collectAsState().value


    if (showTimePickerInicio) {
        TimePickerDialogComposable(
            showDialog = showTimePickerInicio,
            onTimeSelected = { hour, minute ->
                horaInicio = "%02d:%02d".format(hour, minute)
                showTimePickerInicio = false
            },
            onDismiss = { showTimePickerInicio = false }
        )
    }

    if (showTimePickerFin) {
        TimePickerDialogComposable(
            showDialog = showTimePickerFin,
            onTimeSelected = { hour, minute ->
                horaFin = "%02d:%02d".format(hour, minute)
                showTimePickerFin = false
            },
            onDismiss = { showTimePickerFin = false }
        )
    }

    // Efecto para manejar la creación de reserva
    LaunchedEffect(key1 = isLoading) {
        if (isLoading) {
            try {
                // Simular llamada a API
                kotlinx.coroutines.delay(2000)

                // Si el ViewModel tiene la función createReservation, la llamamos
                viewModel.createReservation()

                Toast.makeText(context, "Reserva creada correctamente", Toast.LENGTH_SHORT).show()
                onSuccessNavigate()
            } catch (e: Exception) {
                errorMessage = "Error al crear reserva: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nueva Reserva") },
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del Parking
            ParkingInfoCard(viewModel)

            // Selección de Vehículo
            VehicleSelectionCard(
                vehicles = vehicles,
                selectedVehicleId = selectedVehicleId,
                onVehicleSelected = { vehicle ->
                    selectedVehicleId = vehicle.id
                    viewModel.navigateToReservationForm(vehicle)
                },
                onAddVehicle = {
                    showAddVehicleDialog = true
                    viewModel.showAddVehicleForm()
                }
            )

            // Fecha de Reserva
            DateSelectionCard(
                fechaReserva = fechaReserva,
                onDateSelected = { date ->
                    fechaReserva = date
                    viewModel.updateReservationDate(date)
                    Toast.makeText(context, "Fecha establecida: $date", Toast.LENGTH_SHORT).show()
                }
            )

            // Horarios
            TimeSelectionCard(
                horaInicio = horaInicio,
                horaFin = horaFin,
                onStartTimeClick = { showTimePickerInicio = true },
                onEndTimeClick = { showTimePickerFin = true }
            )

            // Tipo de Reserva
            ReservationTypeCard(
                tipoReserva = tipoReserva,
                onTypeSelected = { type ->
                    tipoReserva = type
                    viewModel.updateReservationType(type)
                }
            )

            // Botón Crear Reserva
            CreateReservationButton(
                isLoading = isLoading,
                isEnabled = selectedVehicleId != null &&
                        fechaReserva.isNotEmpty() &&
                        horaInicio.isNotEmpty() &&
                        horaFin.isNotEmpty() &&
                        !isLoading,
                onClick = {
                    if (selectedVehicleId == null) {
                        Toast.makeText(context, "Selecciona un vehículo", Toast.LENGTH_SHORT).show()
                        return@CreateReservationButton
                    }
                    if (fechaReserva.isEmpty()) {
                        Toast.makeText(context, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
                        return@CreateReservationButton
                    }
                    if (horaInicio.isEmpty() || horaFin.isEmpty()) {
                        Toast.makeText(context, "Selecciona horarios", Toast.LENGTH_SHORT).show()
                        return@CreateReservationButton
                    }
                    isLoading = true
                    errorMessage = null
                }
            )

            // Mostrar error
            ErrorCard(
                errorMessage = errorMessage,
                onDismiss = {
                    errorMessage = null
                    viewModel.clearError()
                }
            )
        }
    }

    // Dialog para agregar vehículo usando el componente personalizado
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
                // Recargar vehículos después de agregar uno nuevo
                viewModel.loadUserVehicles()
            }
        )
    }
}

// Componente para el TimePicker Dialog
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
            { _, hour, minute ->
                onTimeSelected(hour, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()

        // Efecto para manejar el dismiss
        LaunchedEffect(showDialog) {
            // El dialog se maneja automáticamente por Android
        }
    }
}

// Componente para la información del parking
@Composable
private fun ParkingInfoCard(viewModel: ReservationViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val selectedParking = viewModel.selectedParking
            if (selectedParking != null) {
                Text(
                    text = selectedParking.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedParking.direccion ?: "Dirección no disponible",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tarifa: $${selectedParking.tarifa_hora ?: 5.0}/hora",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Estacionamiento no seleccionado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Componente para selección de vehículo
@Composable
private fun VehicleSelectionCard(
    vehicles: List<com.example.smarparkinapp.data.model.Car>,
    selectedVehicleId: Int?,
    onVehicleSelected: (com.example.smarparkinapp.data.model.Car) -> Unit,
    onAddVehicle: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vehículo",
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column {
                    vehicles.forEach { vehicle ->
                        Card(
                            onClick = { onVehicleSelected(vehicle) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedVehicleId == vehicle.id)
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
                                    contentDescription = "Vehículo"
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
}

// Componente para selección de fecha
@Composable
private fun DateSelectionCard(
    fechaReserva: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Fecha de Reserva",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    // Usar la fecha actual
                    val today = java.time.LocalDate.now().toString()
                    onDateSelected(today)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (fechaReserva.isEmpty())
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Fecha")
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (fechaReserva.isEmpty()) "Seleccionar Fecha" else "Fecha: $fechaReserva")
            }
        }
    }
}

// Componente para selección de horarios
@Composable
private fun TimeSelectionCard(
    horaInicio: String,
    horaFin: String,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Horarios",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStartTimeClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (horaInicio.isEmpty())
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = "Inicio")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (horaInicio.isEmpty()) "Inicio" else horaInicio)
                }

                Button(
                    onClick = onEndTimeClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (horaFin.isEmpty())
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = "Fin")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (horaFin.isEmpty()) "Fin" else horaFin)
                }
            }
        }
    }
}

// Componente para tipo de reserva
@Composable
private fun ReservationTypeCard(
    tipoReserva: String,
    onTypeSelected: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
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
                    selected = tipoReserva == "hora",
                    onClick = { onTypeSelected("hora") },
                    label = { Text("Por Hora") }
                )
                FilterChip(
                    selected = tipoReserva == "dia",
                    onClick = { onTypeSelected("dia") },
                    label = { Text("Por Día") }
                )
            }
        }
    }
}

// Componente para el botón de crear reserva
@Composable
private fun CreateReservationButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Creando...")
        } else {
            Icon(Icons.Default.CarRental, contentDescription = "Crear")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Crear Reserva")
        }
    }
}

// Componente para mostrar errores
@Composable
private fun ErrorCard(
    errorMessage: String?,
    onDismiss: () -> Unit
) {
    errorMessage?.let { message ->
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Error, contentDescription = "Error")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        }
    }
}