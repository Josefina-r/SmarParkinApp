package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.components.AddVehicleDialog
import com.example.smarparkinapp.ui.theme.data.model.Car
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSelectionScreen(
    navController: NavHostController,
    parkingId: Long?,
    viewModel: ReservationViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(parkingId) {
        println("🔄 [VehicleSelection] Parking ID: $parkingId")

        parkingId?.let { id ->
            viewModel.loadParkingById(id)
        }
        viewModel.loadUserVehicles()
    }

    // Estado para el vehículo seleccionado
    var selectedVehicle by remember { mutableStateOf<Car?>(null) }
    var showAddVehicleDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var vehicleToDelete by remember { mutableStateOf<Car?>(null) }

    val vehicles by viewModel.vehicles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(vehicles) {
        println("✅ [VehicleSelection] Vehículos cargados: ${vehicles.size}")
    }

    // Mostrar errores
    LaunchedEffect(error) {
        if (!error.isNullOrEmpty()) {
            android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Función para manejar la eliminación
    val onDeleteVehicle = { car: Car ->
        vehicleToDelete = car
        showDeleteConfirmation = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Vehículo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedVehicle != null) {
                FloatingActionButton(
                    onClick = {
                        selectedVehicle?.let { vehicle ->
                            parkingId?.let { id ->
                                // ✅  Establecer el vehículo seleccionado en el ViewModel
                                viewModel.setSelectedVehicle(vehicle)
                                println("🚗 [VehicleSelection] Vehículo seleccionado: ${vehicle.plate}")

                                // ✅ Navegar a ReservationScreen
                                navController.navigate("reservation/$id") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    },
                    containerColor = Color(0xFF5555FF)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirmar reserva")
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Cargando vehículos...")
            }
        } else if (vehicles.isEmpty()) {
            EmptyVehiclesState { showAddVehicleDialog = true }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(vehicles) { car ->
                    VehicleItem(
                        car = car,
                        isSelected = selectedVehicle?.id == car.id,
                        onClick = {
                            selectedVehicle = if (selectedVehicle?.id == car.id) null else car
                            println("🚗 [VehicleSelection] Vehículo clickeado: ${car.plate}")
                        },
                        onDelete = { onDeleteVehicle(car) }
                    )
                }

                item {
                    Card(
                        onClick = { showAddVehicleDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Agregar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar vehículo", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    if (showAddVehicleDialog) {
        AddVehicleDialog(
            viewModel = viewModel,
            onDismiss = { showAddVehicleDialog = false },
            onSave = {
                showAddVehicleDialog = false
                viewModel.loadUserVehicles()
            }
        )
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                vehicleToDelete = null
            },
            title = { Text("Eliminar vehículo") },
            text = {
                Text("¿Estás seguro de que quieres eliminar el vehículo ${vehicleToDelete?.plate}? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vehicleToDelete?.let { car ->
                            // Convertir Int a Long
                            viewModel.deleteVehicle(car.id.toLong())
                            // Si el vehículo eliminado era el seleccionado, limpiar la selección
                            if (selectedVehicle?.id == car.id) {
                                selectedVehicle = null
                            }
                        }
                        showDeleteConfirmation = false
                        vehicleToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        vehicleToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun EmptyVehiclesState(onAddVehicle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.DirectionsCar,
            contentDescription = "No vehicles",
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No tienes vehículos registrados", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Agrega tu primer vehículo para realizar reservas",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddVehicle, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar vehículo")
        }
    }
}

@Composable
fun VehicleItem(
    car: Car,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.DirectionsCar,
                contentDescription = "Vehículo",
                modifier = Modifier.size(40.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${car.brand} ${car.model}", style = MaterialTheme.typography.titleMedium)
                Text(car.plate, style = MaterialTheme.typography.bodyMedium)
                Text(car.color, style = MaterialTheme.typography.bodySmall)
            }

            // Icono de selección o menú de opciones
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Box {
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Eliminar") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}