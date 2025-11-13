package com.example.smarparkinapp.ui.screens

/* screens/ReservationScreen.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReservationScreen(
    viewModel: ReservationViewModel = hiltViewModel(),
    onNavigateToCreateReservation: () -> Unit,
    onNavigateToReservationDetail: (Int) -> Unit
) {
    val reservations by viewModel.reservations.collectAsState()
    val activeReservations by viewModel.activeReservations.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadReservations()
        viewModel.loadActiveReservations()
        viewModel.loadReservationTypes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas") },
                actions = {
                    IconButton(onClick = {
                        viewModel.loadReservations()
                        viewModel.loadActiveReservations()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateReservation) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Reserva")
            }
        }
    ) { padding ->
        when (uiState) {
            is ReservationUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ReservationUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${(uiState as ReservationUiState.Error).message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.loadReservations()
                            viewModel.loadActiveReservations()
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Reservas Activas
                    if (activeReservations.isNotEmpty()) {
                        item {
                            Text(
                                text = "Reservas Activas",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        items(activeReservations) { reservation ->
                            ReservationCard(
                                reservation = reservation,
                                onClick = { onNavigateToReservationDetail(reservation.id) }
                            )
                        }
                    }

                    // Todas las Reservas
                    item {
                        Text(
                            text = "Historial de Reservas",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    items(reservations) { reservation ->
                        ReservationCard(
                            reservation = reservation,
                            onClick = { onNavigateToReservationDetail(reservation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationCard(reservation: Reservation, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reservation.estacionamiento.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Vehículo: ${reservation.vehiculo.marca} ${reservation.vehiculo.modelo}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Entrada: ${reservation.horaEntrada.formatForDisplay()}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Estado: ${reservation.estado.name}",
                style = MaterialTheme.typography.bodySmall,
                color = when (reservation.estado) {
                    ReservationState.ACTIVA -> MaterialTheme.colorScheme.primary
                    ReservationState.FINALIZADA -> MaterialTheme.colorScheme.onSurface
                    ReservationState.CANCELADA -> MaterialTheme.colorScheme.error
                }
            )
            Text(
                text = "Tipo: ${reservation.tipoReserva.name} - $${reservation.costoEstimado}",
                style = MaterialTheme.typography.bodySmall
            )

            reservation.tiempoRestante?.let { tiempoRestante ->
                if (tiempoRestante > 0) {
                    Text(
                        text = "Tiempo restante: ${tiempoRestante}min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Extension para formatear fecha
fun LocalDateTime.formatForDisplay(): String {
    // Implementar formateo de fecha según necesidades
    return toString()
}*/