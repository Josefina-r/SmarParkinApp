package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel

// Definición de los métodos de pago
enum class PaymentMethodType(
    val id: String,
    val displayName: String,
    val emoji: String,
    val icon: ImageVector
) {
    TARJETA(
        id = "tarjeta",
        displayName = "Tarjeta de Crédito/Débito",
        emoji = "💳",
        icon = Icons.Default.CreditCard
    ),
    YAPE(
        id = "yape",
        displayName = "Yape",
        emoji = "📱",
        icon = Icons.Default.PhoneAndroid
    ),
    PLIN(
        id = "plin",
        displayName = "Plin",
        emoji = "📱",
        icon = Icons.Default.PhoneAndroid
    ),
    EFECTIVO(
        id = "efectivo",
        displayName = "Pago en Efectivo",
        emoji = "💰",
        icon = Icons.Default.Wallet
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavHostController,
    reservationId: Long?,
    viewModel: ReservationViewModel
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethodType?>(null) }
    val reservation by viewModel.createdReservation.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Mostrar errores
    LaunchedEffect(error) {
        error?.let {
            // Aquí puedes mostrar un snackbar o diálogo de error
            println("Error en pago: $it")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Método de Pago",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            if (selectedMethod != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shadowElevation = 8.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Button(
                        onClick = {
                            selectedMethod?.let { method ->
                                viewModel.createPayment(method.id) { payment ->
                                    // Navegar a la pantalla de ticket con el ID del pago
                                    navController.navigate("ticket/${payment.id}")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "Pagar S/ ${reservation?.costoEstimado ?: "0.00"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Información de la reserva
            ReservationSummaryCard(reservation)

            Spacer(modifier = Modifier.height(24.dp))

            // Métodos de pago
            Text(
                "Selecciona método de pago",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodsList(
                selectedMethod = selectedMethod,
                onMethodSelected = { method -> selectedMethod = method }
            )

            // Información adicional sobre métodos de pago
            PaymentInfoSection(selectedMethod)

            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el botón flotante
        }
    }
}

@Composable
private fun ReservationSummaryCard(reservation: com.example.smarparkinapp.ui.theme.data.model.ReservationResponse?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Resumen de Reserva",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Estacionamiento:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    reservation?.estacionamiento?.nombre ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dirección:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    reservation?.estacionamiento?.direccion ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Vehículo:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${reservation?.vehiculo?.marca ?: ""} ${reservation?.vehiculo?.modelo ?: ""} (${reservation?.vehiculo?.placa ?: "N/A"})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Duración:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${reservation?.duracionMinutos ?: 0} min",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Horario:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${formatTime(reservation?.horaEntrada) ?: "N/A"} - ${formatTime(reservation?.horaSalida) ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total a pagar:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "S/ ${reservation?.costoEstimado ?: "0.00"}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodsList(
    selectedMethod: PaymentMethodType?,
    onMethodSelected: (PaymentMethodType) -> Unit
) {
    val paymentMethods = listOf(
        PaymentMethodType.TARJETA,
        PaymentMethodType.YAPE,
        PaymentMethodType.PLIN,
        PaymentMethodType.EFECTIVO
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        paymentMethods.forEach { method ->
            PaymentMethodItem(
                method = method,
                isSelected = selectedMethod?.id == method.id,
                onSelected = { onMethodSelected(method) }
            )
        }
    }
}

@Composable
private fun PaymentMethodItem(
    method: PaymentMethodType,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = if (isSelected) {
            CardDefaults.cardElevation(0.dp) // Sin elevación cuando está seleccionado
        } else {
            CardDefaults.cardElevation(4.dp) // Con elevación cuando no está seleccionado
        },
        onClick = onSelected
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = method.icon,
                        contentDescription = method.displayName,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                Column {
                    Text(
                        method.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        getPaymentDescription(method.id),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelected,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun PaymentInfoSection(selectedMethod: PaymentMethodType?) {
    selectedMethod?.let { method ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "Información del pago",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    getPaymentDetails(method.id),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Funciones auxiliares
private fun getPaymentDescription(methodId: String): String {
    return when (methodId) {
        "tarjeta" -> "Pago seguro con tarjeta"
        "yape" -> "Pago rápido con Yape"
        "plin" -> "Pago rápido con Plin"
        "efectivo" -> "Paga al llegar al estacionamiento"
        else -> ""
    }
}

private fun getPaymentDetails(methodId: String): String {
    return when (methodId) {
        "tarjeta" -> "Tu pago será procesado de forma segura. Aceptamos Visa, MasterCard y otras tarjetas."
        "yape" -> "Serás redirigido a la app de Yape para completar el pago."
        "plin" -> "Serás redirigido a la app de Plin para completar el pago."
        "efectivo" -> "Paga directamente en el estacionamiento al momento de tu llegada."
        else -> ""
    }
}

private fun formatTime(dateTimeString: String?): String? {
    if (dateTimeString == null) return null
    return try {
        // Formatear de "yyyy-MM-dd HH:mm:ss" a "HH:mm"
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)
        date?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        dateTimeString
    }
}