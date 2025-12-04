package com.example.smarparkinapp.ui.theme.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyReservationsScreen(
    navController: NavHostController,
    viewModel: ReservationViewModel = viewModel()
) {
    val context = LocalContext.current

    // CONECTA CON LOS DATOS REALES
    val reservations by viewModel.userReservations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedFilter by remember { mutableStateOf("Todos") }
    var isRefreshing by remember { mutableStateOf(false) }

    // Cargar reservas cuando entra a la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadUserReservations()
    }

    // Mostrar errores
    LaunchedEffect(error) {
        error?.let {
            if (it.isNotEmpty()) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Reservas",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    // Botón para refrescar manualmente
                    IconButton(
                        onClick = {
                            isRefreshing = true
                            viewModel.loadUserReservations()
                            isRefreshing = false
                        },
                        enabled = !isLoading
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Actualizar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todos", "Activas", "Finalizadas", "Canceladas").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CONTENIDO REAL CON LAS RESERVAS
            when {
                isLoading && reservations.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                reservations.isEmpty() -> {
                    EmptyReservationsView(
                        onRefresh = { viewModel.loadUserReservations() }
                    )
                }

                else -> {
                    // Filtrar reservas según selección
                    val filteredReservations = when (selectedFilter) {
                        "Activas" -> reservations.filter { it.estado.lowercase() == "activa" }
                        "Finalizadas" -> reservations.filter { it.estado.lowercase() == "finalizada" }
                        "Canceladas" -> reservations.filter { it.estado.lowercase() == "cancelada" }
                        else -> reservations
                    }

                    if (filteredReservations.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay reservas $selectedFilter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        ReservationsList(
                            reservations = filteredReservations,
                            onReservationClick = { reservation ->
                                // Navegar al ticket de la reserva
                                navController.navigate("ticket/reservation/${reservation.id}")
                            },
                            onCancelReservation = { reservation ->
                                scope.launch {
                                    // TODO: Implementar cancelación real
                                    // Por ahora solo mensaje
                                    Toast.makeText(
                                        context,
                                        "Función de cancelar próximamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// COMPONENTE PARA MOSTRAR LISTA DE RESERVAS
@Composable
fun ReservationsList(
    reservations: List<com.example.smarparkinapp.ui.theme.data.model.ReservationResponse>,
    onReservationClick: (com.example.smarparkinapp.ui.theme.data.model.ReservationResponse) -> Unit,
    onCancelReservation: (com.example.smarparkinapp.ui.theme.data.model.ReservationResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(reservations) { reservation ->
            ReservationCard(
                reservation = reservation,
                onClick = { onReservationClick(reservation) },
                onCancel = { onCancelReservation(reservation) }
            )
        }
    }
}

// COMPONENTE PARA CADA RESERVA - USANDO TU MODELO CORRECTO
@Composable
fun ReservationCard(
    reservation: com.example.smarparkinapp.ui.theme.data.model.ReservationResponse,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con código y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Reserva #${reservation.codigoReserva}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        formatDateTime(reservation.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Badge de estado
                ReservationStatusBadge(reservation.estado)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del usuario que hizo la reserva
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Usuario: ${reservation.usuarioNombre}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información de horario
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = "Horario",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Column {
                    Text(
                        "Entrada: ${formatTime(reservation.horaEntrada)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (reservation.horaSalida?.isNotEmpty() == true) {
                        Text(
                            "Salida: ${formatTime(reservation.horaSalida)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Duración y tipo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Duración:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${reservation.duracionMinutos ?: 0} minutos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "Tipo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        reservation.tipoReserva.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer con costo y acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Costo estimado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "S/ ${String.format("%.2f", reservation.costoEstimado ?: 0.0)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Botones de acción
                Row {
                    // Botón de ver ticket
                    Button(
                        onClick = onClick,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Filled.Receipt,
                            contentDescription = "Ver Ticket",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ticket", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón de cancelar (solo si está activa y se puede)
                    if (reservation.estado.lowercase() == "activa" && reservation.puedeCancelar) {
                        Button(
                            onClick = onCancel,
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336)
                            )
                        ) {
                            Icon(
                                Icons.Filled.Cancel,
                                contentDescription = "Cancelar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancelar", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Tiempo restante (si aplica)
            reservation.tiempoRestante?.let { tiempo ->
                if (tiempo > 0 && reservation.estado.lowercase() == "activa") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "⏳ Tiempo restante: ${tiempo} minutos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }
    }
}

// COMPONENTE PARA BADGE DE ESTADO
@Composable
fun ReservationStatusBadge(estado: String) {
    val (color, text) = when (estado.lowercase()) {
        "activa" -> Pair(Color(0xFF4CAF50), "Activa")
        "pendiente" -> Pair(Color(0xFFFF9800), "Pendiente")
        "cancelada" -> Pair(Color(0xFFF44336), "Cancelada")
        "finalizada" -> Pair(Color(0xFF2196F3), "Finalizada")
        else -> Pair(Color.Gray, estado)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            ),
            fontSize = 10.sp
        )
    }
}

// COMPONENTE PARA CUANDO NO HAY RESERVAS
@Composable
fun EmptyReservationsView(
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Receipt,
            contentDescription = "Sin reservas",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "No tienes reservas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Cuando hagas una reserva, aparecerá aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRefresh,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Actualizar")
        }
    }
}

// FUNCIONES DE AYUDA
private fun formatDateTime(dateTime: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateTime
    }
}

private fun formatTime(time: String?): String {
    if (time.isNullOrEmpty()) return "No definida"

    return try {
        if (time.length >= 5) {
            time.substring(0, 5)
        } else {
            time
        }
    } catch (e: Exception) {
        time ?: ""
    }
}