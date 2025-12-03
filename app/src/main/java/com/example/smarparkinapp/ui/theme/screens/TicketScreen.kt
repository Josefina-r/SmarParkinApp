package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    navController: NavHostController,
    ticketId: String? = null,
    reservationId: Long? = null,
    viewModel: ReservationViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados REALES de tickets
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentTicket by viewModel.currentTicket.collectAsState()
    val userTickets by viewModel.userTickets.collectAsState()
    val createdReservation by viewModel.createdReservation.collectAsState()
    val selectedParking by remember { derivedStateOf { viewModel.selectedParking } }
    val selectedVehicle by remember { derivedStateOf { viewModel.selectedVehicle } }

    // Cargar tickets según parámetro
    LaunchedEffect(ticketId, reservationId) {
        if (ticketId != null) {
            // Cargar ticket específico
            viewModel.loadTicketById(ticketId)
        } else if (reservationId != null) {
            // Buscar ticket por ID de reserva
            val ticket = viewModel.findTicketByReservationId(reservationId)
            if (ticket != null) {
                viewModel.loadTicketById(ticket.id)
            } else {
                // Si no hay ticket, cargar todos
                viewModel.loadUserTickets()
            }
        } else {
            // Cargar todos los tickets del usuario
            viewModel.loadUserTickets()
        }
    }

    // Auto-refrescar cada 30 segundos
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(30000L)
        if (ticketId != null) {
            viewModel.loadTicketById(ticketId)
        } else {
            viewModel.loadUserTickets()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Tickets",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            if (ticketId != null) {
                                viewModel.loadTicketById(ticketId)
                            } else {
                                viewModel.loadUserTickets()
                            }
                        }
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                ticketId != null && currentTicket != null -> {
                    // Vista de ticket específico
                    SingleTicketView(
                        ticket = currentTicket!!,
                        parking = selectedParking,
                        vehicle = selectedVehicle,
                        reservation = createdReservation,
                        onBack = { navController.popBackStack() },
                        onCancel = {
                            scope.launch {
                                viewModel.cancelUserTicket(ticketId)
                            }
                        },
                        canCancel = viewModel.canUserCancelTicket(currentTicket!!)
                    )
                }

                ticketId != null && currentTicket == null -> {
                    // Ticket no encontrado
                    EmptyStateView(
                        icon = Icons.Filled.ErrorOutline,
                        title = "Ticket no encontrado",
                        message = "El ticket que buscas no existe",
                        actionText = "Volver",
                        onAction = { navController.popBackStack() }
                    )
                }

                userTickets.isEmpty() -> {
                    // No hay tickets
                    EmptyStateView(
                        icon = Icons.Filled.Receipt,
                        title = "No tienes tickets",
                        message = "Realiza una reserva para obtener tickets",
                        actionText = "Hacer Reserva",
                        onAction = { navController.navigate("home") }
                    )
                }

                else -> {
                    // Lista de todos los tickets
                    TicketsListView(
                        tickets = userTickets,
                        onTicketClick = { ticket ->
                            navController.navigate("ticket/${ticket.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleTicketView(
    ticket: com.example.smarparkinapp.ui.theme.data.model.TicketResponse,
    parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    vehicle: com.example.smarparkinapp.ui.theme.data.model.Car?,
    reservation: com.example.smarparkinapp.ui.theme.data.model.ReservationResponse?,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    canCancel: Boolean
) {
    val statusColor = getTicketStatusColor(ticket.estado)
    val statusText = getTicketStatusText(ticket.estado)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header con estado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(statusColor.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = getStatusIcon(ticket.estado),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = statusColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    statusText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Ticket #${ticket.codigoTicket}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code (solo si está válido)
        if (ticket.estado == "valido" && ticket.qrData != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "CÓDIGO DE ACCESO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Code placeholder
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.QrCode,
                                contentDescription = "QR Code",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                ticket.codigoTicket.take(12),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Muestra este código al ingresar al estacionamiento",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Detalles del ticket
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "INFORMACIÓN DEL TICKET",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow("Código:", ticket.codigoTicket)
                InfoRow("Estado:", ticket.estado)
                InfoRow("Fecha Emisión:", formatTicketDate(ticket.fechaEmision))
                ticket.fechaValidezDesde?.let { InfoRow("Válido Desde:", formatTicketDate(it)) }
                ticket.fechaValidezHasta?.let { InfoRow("Válido Hasta:", formatTicketDate(it)) }
                ticket.fechaValidacion?.let { InfoRow("Validado:", formatTicketDate(it)) }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Información de la reserva
                Text(
                    "INFORMACIÓN DE LA RESERVA",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                reservation?.let {
                    InfoRow("Código Reserva:", it.codigoReserva ?: "N/A")
                    InfoRow("Check-in:", formatDateTime(it.horaEntrada ?: ""))
                    InfoRow("Check-out:", formatDateTime(it.horaSalida ?: "No definido"))
                    InfoRow("Duración:", "${it.duracionMinutos ?: 0} minutos")
                    it.costoEstimado?.let { costo ->
                        InfoRow("Costo:", "S/ ${"%.2f".format(costo)}")
                    }
                }

                // Información del parking
                parking?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "INFORMACIÓN DEL ESTACIONAMIENTO",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Nombre:", it.nombre)
                    InfoRow("Dirección:", it.direccion)
                    InfoRow("Tarifa:", "S/ ${"%.2f".format(it.tarifa_hora)} por hora")
                }

                // Información del vehículo
                vehicle?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "INFORMACIÓN DEL VEHÍCULO",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoRow("Vehículo:", "${it.brand} ${it.model}")
                    InfoRow("Placa:", it.plate)
                    InfoRow("Color:", it.color)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Acciones según estado
        when (ticket.estado) {
            "pendiente" -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Yellow.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "⏳ Esperando Confirmación",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "El estacionamiento debe aceptar tu reserva",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (canCancel) {
                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(Icons.Filled.Cancel, contentDescription = "Cancelar")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cancelar Reserva")
                            }
                        }
                    }
                }
            }

            "valido" -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Green.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "✅ Reserva Confirmada",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Presenta el código QR al ingresar al estacionamiento",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onCancel,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            enabled = canCancel
                        ) {
                            Icon(Icons.Filled.Cancel, contentDescription = "Cancelar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar Reserva")
                        }
                    }
                }
            }

            "cancelado" -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "❌ Reserva Cancelada",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Esta reserva ha sido cancelada",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            "usado" -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Blue.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            " Reserva Utilizada",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ya has utilizado esta reserva",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TicketsListView(
    tickets: List<com.example.smarparkinapp.ui.theme.data.model.TicketResponse>,
    onTicketClick: (com.example.smarparkinapp.ui.theme.data.model.TicketResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(tickets) { ticket ->
            TicketCard(
                ticket = ticket,
                onClick = { onTicketClick(ticket) }
            )
        }
    }
}

@Composable
private fun TicketCard(
    ticket: com.example.smarparkinapp.ui.theme.data.model.TicketResponse,
    onClick: () -> Unit
) {
    val statusColor = getTicketStatusColor(ticket.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de estado
            Icon(
                imageVector = getStatusIcon(ticket.estado),
                contentDescription = "Estado",
                modifier = Modifier.size(32.dp),
                tint = statusColor
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "Ticket #${ticket.codigoTicket.take(8)}...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    getTicketStatusText(ticket.estado),
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )

                Text(
                    "Código Reserva: ${ticket.codigoReserva ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Fecha: ${formatTicketDate(ticket.fechaEmision)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Flecha
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onAction) {
                Text(actionText)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// Funciones auxiliares
private fun getTicketStatusColor(estado: String): Color {
    return when (estado.lowercase()) {
        "valido", "validado" -> Color(0xFF4CAF50)
        "pendiente" -> Color(0xFFFFC107)
        "cancelado", "expirado" -> Color(0xFFF44336)
        "usado" -> Color(0xFF2196F3)
        else -> Color.Gray
    }
}

private fun getTicketStatusText(estado: String): String {
    return when (estado.lowercase()) {
        "valido", "validado" -> "Confirmado"
        "pendiente" -> "Pendiente"
        "cancelado" -> "Cancelado"
        "expirado" -> "Expirado"
        "usado" -> "Usado"
        else -> estado
    }
}

private fun getStatusIcon(estado: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (estado.lowercase()) {
        "valido", "validado" -> Icons.Filled.CheckCircle
        "pendiente" -> Icons.Filled.Schedule
        "cancelado", "expirado" -> Icons.Filled.Cancel
        "usado" -> Icons.Filled.DoneAll
        else -> Icons.Filled.Receipt
    }
}

private fun formatTicketDate(fecha: String): String {
    if (fecha.isNullOrEmpty()) return "No disponible"

    return try {
        if (fecha.contains("T")) {
            val parts = fecha.split("T")
            val datePart = parts[0]
            val timePart = parts[1].substring(0, 5)
            "$datePart $timePart"
        } else {
            fecha
        }
    } catch (e: Exception) {
        fecha
    }
}

private fun formatDateTime(dateTime: String): String {
    return try {
        if (dateTime.contains("T")) {
            val parts = dateTime.split("T")
            val datePart = parts[0]
            val timePart = parts[1].substring(0, 5)
            "$datePart $timePart"
        } else if (dateTime.length > 16) {
            dateTime.substring(0, 16).replace("T", " ")
        } else {
            dateTime
        }
    } catch (e: Exception) {
        dateTime
    }
}