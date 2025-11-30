package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.data.model.Payment
import com.example.smarparkinapp.ui.theme.data.model.ReservationResponse
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    navController: NavHostController,
    paymentId: String?,
    viewModel: ReservationViewModel = viewModel()
) {
    // ✅ CORREGIDO: Solo los Flows usan collectAsState()
    val payment: Payment? by viewModel.createdPayment.collectAsState()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()

    // ✅ CORREGIDO: selectedParking y selectedVehicle son propiedades directas
    val reservation = payment?.reserva

    // Observar cambios en las propiedades del ViewModel
    val selectedParking by remember { derivedStateOf { viewModel.selectedParking } }
    val selectedVehicle by remember { derivedStateOf { viewModel.selectedVehicle } }

    // Cargar detalles cuando la reserva esté disponible
    LaunchedEffect(reservation) {
        if (reservation != null) {
            viewModel.loadTicketDetails(reservation)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tu Ticket",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                TicketHeader(reservation)

                Spacer(modifier = Modifier.height(24.dp))

                QrCodeSection(payment)

                Spacer(modifier = Modifier.height(24.dp))

                TicketDetails(
                    reservation = reservation,
                    payment = payment,
                    parking = selectedParking,
                    vehicle = selectedVehicle
                )

                Spacer(modifier = Modifier.height(32.dp))

                InstructionsSection()
            }
        }
    }
}

// El resto del código permanece igual...
@Composable
private fun TicketHeader(reservation: ReservationResponse?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Reserva Confirmada",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "¡Reserva Confirmada!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "Tu espacio está reservado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                reservation?.codigoReserva ?: "N/A",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QrCodeSection(payment: Payment?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Código QR de Acceso",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (payment?.id != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "QR CODE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ref: ${payment.id.take(8)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Muestra este código al ingresar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TicketDetails(
    reservation: ReservationResponse?,
    payment: Payment?,
    parking: com.example.smarparkinapp.ui.theme.data.model.ParkingLot?,
    vehicle: com.example.smarparkinapp.ui.theme.data.model.Car?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                "Detalles del Ticket",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Información del estacionamiento
            DetailRow(
                icon = Icons.Filled.CheckCircle,
                title = "Estacionamiento",
                value = parking?.nombre ?: "Cargando..."
            )

            DetailRow(
                icon = Icons.Filled.Schedule,
                title = "Dirección",
                value = parking?.direccion ?: "No disponible"
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()

            // Información de la reserva
            DetailRow(
                icon = Icons.Filled.Schedule,
                title = "Check-in",
                value = formatDateTime(reservation?.horaEntrada ?: "")
            )

            DetailRow(
                icon = Icons.Filled.Schedule,
                title = "Check-out",
                value = formatDateTime(reservation?.horaSalida ?: "No definido")
            )

            DetailRow(
                icon = Icons.Filled.Schedule,
                title = "Duración",
                value = "${reservation?.duracionMinutos ?: 0} minutos"
            )

            // Información del vehículo
            DetailRow(
                icon = Icons.Filled.Schedule,
                title = "Vehículo",
                value = buildVehicleInfo(vehicle)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()

            // Información del pago
            DetailRow(
                icon = Icons.Filled.CheckCircle,
                title = "Método de pago",
                value = getPaymentMethodName(payment?.metodo ?: "")
            )

            DetailRow(
                icon = Icons.Filled.CheckCircle,
                title = "Estado de pago",
                value = getPaymentStatus(payment?.estado ?: ""),
                valueColor = getPaymentStatusColor(payment?.estado ?: "")
            )

            DetailRow(
                icon = Icons.Filled.CheckCircle,
                title = "Referencia",
                value = payment?.referenciaPago ?: "N/A"
            )

            // Mostrar monto si está disponible
            payment?.monto?.let { monto ->
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                DetailRow(
                    icon = Icons.Filled.CheckCircle,
                    title = "Total pagado",
                    value = "S/ ${"%.2f".format(monto)}",
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }

            // Mostrar costo estimado de la reserva
            reservation?.costoEstimado?.let { costo ->
                DetailRow(
                    icon = Icons.Filled.CheckCircle,
                    title = "Costo estimado",
                    value = "S/ ${"%.2f".format(costo)}",
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun InstructionsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    "Instrucciones importantes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            InstructionItem("1. Presenta el código QR al ingresar al estacionamiento")
            InstructionItem("2. Tu reserva es válida solo en el horario indicado")
            InstructionItem("3. En caso de problemas, muestra el código de reserva")
            InstructionItem("4. Paga en efectivo si elegiste ese método")
        }
    }
}

@Composable
private fun InstructionItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

// Función auxiliar para construir la información del vehículo
private fun buildVehicleInfo(vehicle: com.example.smarparkinapp.ui.theme.data.model.Car?): String {
    return if (vehicle != null) {
        "${vehicle.brand} ${vehicle.model} - ${vehicle.plate}"
    } else {
        "Cargando..."
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

private fun getPaymentMethodName(method: String): String {
    return when (method) {
        "tarjeta" -> "Tarjeta"
        "yape" -> "Yape"
        "plin" -> "Plin"
        "efectivo" -> "Efectivo"
        else -> method
    }
}

private fun getPaymentStatus(status: String): String {
    return when (status) {
        "pagado" -> "Pagado"
        "pendiente" -> "Pendiente"
        "procesando" -> "Procesando"
        "fallido" -> "Fallido"
        "reembolsado" -> "Reembolsado"
        "cancelado" -> "Cancelado"
        else -> status
    }
}

@Composable
private fun getPaymentStatusColor(status: String): Color {
    return when (status) {
        "pagado" -> Color(0xFF4CAF50)
        "pendiente" -> Color(0xFFFFC107)
        "procesando" -> Color(0xFF03A9F4)
        "fallido" -> Color(0xFFF44336)
        "reembolsado" -> Color(0xFF9C27B0)
        "cancelado" -> Color(0xFF607D8B)
        else -> MaterialTheme.colorScheme.onSurface
    }
}