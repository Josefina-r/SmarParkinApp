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
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    parking: ParkingLot,
    onSuccessNavigate: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Inicializar ViewModel
    val reservationViewModel: ReservationViewModel = viewModel(
        factory = ReservationViewModelFactory(context)
    )

    // Estados locales para el formulario
    var selectedVehicleId by remember { mutableStateOf<Int?>(null) }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }

    // Estados locales para el formulario de vehículo
    var vehicleBrand by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleColor by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }
    var showAddVehicleDialog by remember { mutableStateOf(false) }

    // Estados del ViewModel - CORREGIDO
    val isLoading by reservationViewModel.isLoading.collectAsState()
    val error by reservationViewModel.error.collectAsState()
    val createdReservation by reservationViewModel.createdReservation.collectAsState()

    // Obtener vehículos como State
    val vehicles by reservationViewModel.vehicles.collectAsState()

    // TimePickers
    var showTimePickerInicio by remember { mutableStateOf(false) }
    var showTimePickerFin by remember { mutableStateOf(false) }

    // Navegar cuando se crea la reserva
    LaunchedEffect(createdReservation) {
        if (createdReservation != null) {
            Toast.makeText(context, "Reserva creada correctamente", Toast.LENGTH_SHORT).show()
            onSuccessNavigate()
        }
    }

    // Mostrar TimePickers
    if (showTimePickerInicio) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                horaInicio = "%02d:%02d".format(hour, minute)
                showTimePickerInicio = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    if (showTimePickerFin) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                horaFin = "%02d:%02d".format(hour, minute)
                showTimePickerFin = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = parking.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = parking.direccion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tarifa: $${parking.tarifa_hora}/hora",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Selección de Vehículo
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
                            onClick = { showAddVehicleDialog = true },
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
                                    onClick = { selectedVehicleId = vehicle.id },
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

            // Horarios
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
                            onClick = { showTimePickerInicio = true },
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
                            onClick = { showTimePickerFin = true },
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

            // Botón Crear Reserva
            Button(
                onClick = {
                    if (selectedVehicleId == null) {
                        Toast.makeText(context, "Selecciona un vehículo", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (horaInicio.isEmpty() || horaFin.isEmpty()) {
                        Toast.makeText(context, "Selecciona horarios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val fecha = LocalDate.now().toString()
                    reservationViewModel.createReservation(
                        parking = parking,
                        vehicleId = selectedVehicleId!!,
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        tipoReserva = "normal"
                    )
                },
                enabled = !isLoading && selectedVehicleId != null &&
                        horaInicio.isNotEmpty() && horaFin.isNotEmpty(),
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

            // Mostrar error
            error?.let { errorMessage ->
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
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // Dialog para agregar vehículo
    if (showAddVehicleDialog) {
        AlertDialog(
            onDismissRequest = { showAddVehicleDialog = false },
            title = { Text("Agregar Vehículo") },
            text = {
                Column {
                    OutlinedTextField(
                        value = vehicleBrand,
                        onValueChange = { vehicleBrand = it },
                        label = { Text("Marca *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = { vehicleModel = it },
                        label = { Text("Modelo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vehicleColor,
                        onValueChange = { vehicleColor = it },
                        label = { Text("Color *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vehiclePlate,
                        onValueChange = { vehiclePlate = it },
                        label = { Text("Placa *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Validar campos
                        if (vehicleBrand.isEmpty() || vehicleModel.isEmpty() ||
                            vehicleColor.isEmpty() || vehiclePlate.isEmpty()) {
                            Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Llamar al ViewModel para guardar el vehículo
                        reservationViewModel.updateVehicleBrand(vehicleBrand)
                        reservationViewModel.updateVehicleModel(vehicleModel)
                        reservationViewModel.updateVehicleColor(vehicleColor)
                        reservationViewModel.updateVehiclePlate(vehiclePlate)
                        reservationViewModel.saveNewVehicle()

                        // Limpiar formulario y cerrar diálogo
                        vehicleBrand = ""
                        vehicleModel = ""
                        vehicleColor = ""
                        vehiclePlate = ""
                        showAddVehicleDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddVehicleDialog = false
                        // Limpiar formulario al cancelar
                        vehicleBrand = ""
                        vehicleModel = ""
                        vehicleColor = ""
                        vehiclePlate = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}