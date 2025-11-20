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
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.components.VehicleItem
import com.example.smarparkinapp.data.model.Car
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSelectionScreen(
    onBack: () -> Unit,
    onVehicleSelected: (Car) -> Unit,
    onAddVehicle: () -> Unit,
    viewModel: ReservationViewModel
) {
    // CORREGIDO: Usar collectAsState() para observar el StateFlow
    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Vehículo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (vehicles.isEmpty()) {
            EmptyVehiclesState(onAddVehicle = onAddVehicle)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(vehicles) { car ->
                    VehicleItem(
                        car = car,
                        isSelected = false,
                        onClick = { onVehicleSelected(car) }
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { onAddVehicle() },
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
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Agrega tu primer vehículo para realizar reservas",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
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