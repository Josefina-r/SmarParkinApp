// VehicleSelectionScreen.kt - VERSIÓN COMPLETA ACTUALIZADA
package com.example.smarparkinapp.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.components.AddVehicleDialog
import com.example.smarparkinapp.data.model.Car
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSelectionScreen(
    navController: NavHostController,
    parkingId: Int?,
    viewModel: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(LocalContext.current))
) {
    // Estado para el vehículo seleccionado
    var selectedVehicle by remember { mutableStateOf<Car?>(null) }
    var showAddVehicleDialog by remember { mutableStateOf(false) } // ✅ ESTADO PARA CONTROLAR EL DIÁLOGO

    // Observar los vehículos del ViewModel
    val vehicles by viewModel.vehicles.collectAsState()

    // ✅ DEBUG: Ver qué datos tenemos al iniciar
    LaunchedEffect(Unit) {
        println("🔍 [VehicleSelection] ===== INICIANDO PANTALLA =====")
        println("🔍 [VehicleSelection] parkingId recibido: $parkingId")
        println("🔍 [VehicleSelection] Número de vehículos: ${vehicles.size}")
        vehicles.forEachIndexed { index, car ->
            println("🔍 [VehicleSelection] Vehículo $index: ${car.brand} ${car.model} - ${car.plate} - ID: ${car.id}")
        }
    }

    // ✅ DEBUG: Ver cuando cambia la selección
    LaunchedEffect(selectedVehicle) {
        println("🔍 [VehicleSelection] selectedVehicle CAMBIÓ: ${selectedVehicle?.plate ?: "NULO"}")
        println("🔍 [VehicleSelection] FAB debería estar: ${if (selectedVehicle != null) "VISIBLE" else "OCULTO"}")
    }

    // ✅ DEBUG: Ver cuando cambian los vehículos
    LaunchedEffect(vehicles) {
        println("🔍 [VehicleSelection] Vehículos actualizados: ${vehicles.size}")
        if (vehicles.isNotEmpty()) {
            println("🔍 [VehicleSelection] ✅ Ahora hay vehículos disponibles")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Vehículo") },
                navigationIcon = {
                    IconButton(onClick = {
                        println("🔍 [VehicleSelection] 🔙 Botón ATRÁS presionado")
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // ✅ DEBUG: Log para saber si el FAB se renderiza
            println("🔍 [VehicleSelection] 🔄 Renderizando FAB - selectedVehicle: ${selectedVehicle?.plate ?: "NULO"}")

            if (selectedVehicle != null) {
                FloatingActionButton(
                    onClick = {
                        println("🔍 [VehicleSelection] ✅✅✅ FAB PRESIONADO ✅✅✅")
                        println("🔍 [VehicleSelection] Vehículo seleccionado: ${selectedVehicle?.plate}")
                        println("🔍 [VehicleSelection] Parking ID: $parkingId")

                        selectedVehicle?.let { vehicle ->
                            parkingId?.let { id ->
                                println("🔍 [VehicleSelection] 🚗 Configurando ViewModel con vehículo...")

                                // Configurar el vehículo seleccionado en el ViewModel
                                viewModel.selectVehicle(vehicle)
                                println("🔍 [VehicleSelection] ✅ ViewModel configurado con: ${vehicle.plate}")

                                // Navegar a la pantalla de reserva
                                val route = NavRoutes.Reservation.createRoute(id)
                                println("🔍 [VehicleSelection] 🚀 Navegando a: $route")

                                navController.navigate(route)
                                println("🔍 [VehicleSelection] ✅ Comando de navegación ejecutado")

                            } ?: run {
                                println("❌ [VehicleSelection] ERROR: parkingId es null - NO SE PUEDE NAVEGAR")
                            }
                        } ?: run {
                            println("❌ [VehicleSelection] ERROR: selectedVehicle es null - NO SE PUEDE NAVEGAR")
                        }
                    },
                    containerColor = Color(0xFF5555FF)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Confirmar reserva")
                    println("🔍 [VehicleSelection] 🎨 FAB renderizado con icono Check")
                }
            } else {
                println("🔍 [VehicleSelection] ❌ FAB OCULTO - No hay vehículo seleccionado")
            }
        }
    ) { padding ->
        if (vehicles.isEmpty()) {
            println("🔍 [VehicleSelection] 📭 Mostrando estado VACÍO (sin vehículos)")
            EmptyVehiclesState(
                onAddVehicle = {
                    println("🔍 [VehicleSelection] ➕ MOSTRANDO DIÁLOGO DE AGREGAR VEHÍCULO")
                    showAddVehicleDialog = true // ✅ MOSTRAR DIÁLOGO DIRECTAMENTE
                }
            )
        } else {
            println("🔍 [VehicleSelection] 📋 Mostrando lista con ${vehicles.size} vehículos")
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
                            println("🔍 [VehicleSelection] 👆 Clic en vehículo: ${car.plate}")
                            println("🔍 [VehicleSelection] ID del vehículo: ${car.id}")
                            println("🔍 [VehicleSelection] selectedVehicle antes: ${selectedVehicle?.plate ?: "NULO"}")
                            selectedVehicle = car
                            println("🔍 [VehicleSelection] selectedVehicle después: ${selectedVehicle?.plate}")
                            println("🔍 [VehicleSelection] ¿Es el mismo vehículo? ${selectedVehicle?.id == car.id}")
                        }
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                println("🔍 [VehicleSelection] ➕ Clic en 'Agregar vehículo'")
                                showAddVehicleDialog = true // ✅ MOSTRAR DIÁLOGO DIRECTAMENTE
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.LightGray.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar vehículo", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    // ✅ MOSTRAR EL DIÁLOGO CUANDO SEA NECESARIO
    if (showAddVehicleDialog) {
        println("🔍 [VehicleSelection] 🗨️ MOSTRANDO AddVehicleDialog")
        AddVehicleDialog(
            viewModel = viewModel,
            onDismiss = {
                println("🔍 [VehicleSelection] ❌ AddVehicleDialog descartado")
                showAddVehicleDialog = false
            },
            onSave = {
                println("🔍 [VehicleSelection] 💾 Vehículo guardado, cerrando diálogo")
                showAddVehicleDialog = false
                // Recargar vehículos después de guardar
                println("🔍 [VehicleSelection] 🔄 Recargando lista de vehículos...")
                viewModel.loadUserVehicles()
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
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No tienes vehículos registrados",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Agrega tu primer vehículo para realizar reservas",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddVehicle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar vehículo")
        }
    }
}

// Componente VehicleItem CORREGIDO
@Composable
fun VehicleItem(
    car: Car,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                println("🔍 [VehicleItem] ✅ CLIC EN VEHÍCULO: ${car.plate}")
                println("🔍 [VehicleItem] isSelected antes del clic: $isSelected")
                onClick()
                println("🔍 [VehicleItem] onClick ejecutado")
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF5555FF).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = if (isSelected) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(2.dp)
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
                tint = if (isSelected) Color(0xFF5555FF) else Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                )
                Text(
                    text = car.plate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = car.color,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = Color(0xFF5555FF)
                )
                println("🔍 [VehicleItem] ✅ Checkmark visible para: ${car.plate}")
            }
        }
    }
}