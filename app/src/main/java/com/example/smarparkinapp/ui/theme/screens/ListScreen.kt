package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ParkingViewModel
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onParkingClick: (ParkingLot) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val viewModel: ParkingViewModel = viewModel()
    val parkingLots by viewModel.parkingLots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // 🔥 CARGA DATOS REALES AL INICIAR
    LaunchedEffect(Unit) {
        viewModel.loadAllParkingLots() // Esto llama a tu API real de Django
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estacionamientos Aprobados") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadAllParkingLots() // 🔄 Actualizar datos reales
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // ESTADO DE CARGA
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando estacionamientos...")
                    }
                }
            }
            // ERROR
            else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Error de conexión", fontWeight = FontWeight.Bold)
                        Text(error!!)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAllParkingLots() }) {
                            Text("Reintentar conexión")
                        }
                    }
                }
            }
            // DATOS REALES
            else if (parkingLots.isNotEmpty()) {
                Text(
                    text = "${parkingLots.size} estacionamientos disponibles",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(parkingLots) { parking ->
                        RealParkingItem(
                            parking = parking,
                            onClick = { onParkingClick(parking) }
                        )
                    }
                }
            }
            // SIN DATOS
            else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LocalParking,
                            contentDescription = "Sin estacionamientos",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hay estacionamientos aprobados")
                        Text("Todos los estacionamientos deben ser aprobados en el panel general")
                    }
                }
            }
        }
    }
}

@Composable
fun RealParkingItem(parking: ParkingLot, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // NOMBRE Y DIRECCIÓN
            Text(
                text = parking.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = parking.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // INFORMACIÓN PRINCIPAL
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // PRECIO
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = "Precio",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${parking.tarifa_hora}/h")
                }

                // DISPONIBILIDAD
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalParking,
                        contentDescription = "Espacios",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${parking.plazas_disponibles}/${parking.total_plazas}")
                }
            }

            // RATING Y SEGURIDAD (si existen)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                parking.rating_promedio?.let { rating ->
                    if (rating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFA000)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("%.1f".format(rating))
                        }
                    }
                }

                parking.nivel_seguridad?.let { seguridad ->
                    if (seguridad.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = "Seguridad",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Green
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(seguridad)
                        }
                    }
                }
            }
        }
    }
}