package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import com.example.smarparkinapp.ui.theme.data.model.Payment
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.delay
import com.example.smarparkinapp.repository.StripeRepository
import com.example.smarparkinapp.model.StripePaymentResult
import com.example.smarparkinapp.model.CardDetails

// Definición de los métodos de pago - ACTUALIZADO para coincidir con Django
enum class PaymentMethodType(
    val id: String,
    val displayName: String,
    val icon: ImageVector,
    val phoneNumber: String = "",
    val qrDescription: String = "",
    val appPackage: String = "",
    val imageRes: Int? = null
) {
    YAPE(
        id = "yape",
        displayName = "Yape",
        icon = Icons.Default.PhoneAndroid,
        phoneNumber = "952695739",
        qrDescription = "Escanea con Yape o envía al número",
        appPackage = "com.bcp.yape",
        imageRes = com.example.smarparkinapp.R.drawable.logo_yape
    ),
    PLIN(
        id = "plin",
        displayName = "Plin",
        icon = Icons.Default.PhoneAndroid,
        phoneNumber = "952695739",
        qrDescription = "Escanea con Plin o envía al número",
        appPackage = "com.bcp.plin",
        imageRes = com.example.smarparkinapp.R.drawable.logo_plin
    ),
    TARJETA(
        id = "tarjeta",
        displayName = "Tarjeta Crédito/Débito",
        icon = Icons.Default.CreditCard,
        qrDescription = "Pago con tarjeta de crédito o débito",
        imageRes = com.example.smarparkinapp.R.drawable.logo_bcp
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
    var showProcessingDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showQRDialog by remember { mutableStateOf(false) }
    var showCreditCardDialog by remember { mutableStateOf(false) }
    var paymentProcessed by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val createdReservation by viewModel.createdReservation.collectAsState()
    val createdPayment by viewModel.createdPayment.collectAsState()
    val context = LocalContext.current

    // Observar cuando la reserva se crea exitosamente
    LaunchedEffect(createdReservation) {
        createdReservation?.let { reservation ->
            println("✅ Reserva creada exitosamente: ${reservation.id}")
            // Si ya tenemos un método de pago seleccionado, proceder con el pago
            selectedMethod?.let { method ->
                if (!paymentProcessed) {
                    processRealPayment(viewModel, method, reservation.id)
                    paymentProcessed = true
                }
            }
        }
    }

    // Observar cuando el pago se completa
    LaunchedEffect(createdPayment) {
        createdPayment?.let { payment ->
            println("✅ Pago creado exitosamente: ${payment.id}")
            showProcessingDialog = false
            showSuccessDialog = true
            paymentProcessed = false // Reset para futuras transacciones
        }
    }

    // Mostrar errores
    LaunchedEffect(error) {
        if (!error.isNullOrEmpty()) {
            kotlinx.coroutines.delay(100L)
            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            viewModel.clearError()
            showProcessingDialog = false
            paymentProcessed = false
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
                                when (method) {
                                    PaymentMethodType.YAPE, PaymentMethodType.PLIN -> {
                                        showQRDialog = true
                                    }
                                    PaymentMethodType.TARJETA -> {
                                        showCreditCardDialog = true
                                    }
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
                                when (selectedMethod) {
                                    PaymentMethodType.YAPE, PaymentMethodType.PLIN -> "Pagar con ${selectedMethod?.displayName}"
                                    PaymentMethodType.TARJETA -> "Pagar con Tarjeta"
                                    else -> "Confirmar y Pagar"
                                },
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
            ReservationSummaryCard(viewModel)

            Spacer(modifier = Modifier.height(24.dp))

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

            PaymentInfoSection(selectedMethod)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Diálogo de QR para Yape/Plin
    if (showQRDialog) {
        selectedMethod?.let { method ->
            QRPaymentDialog(
                paymentMethod = method,
                amount = calculateTotalCost(viewModel),
                onPaymentConfirmed = {
                    // Crear reserva primero, luego el pago se procesará automáticamente
                    createRealReservation(viewModel, method)
                    showQRDialog = false
                },
                onDismiss = {
                    showQRDialog = false
                }
            )
        }
    }

    // Diálogo de Tarjeta CON STRIPE
    if (showCreditCardDialog) {
        StripeCardPaymentDialog(
            amount = calculateTotalCost(viewModel),
            onPaymentConfirmed = { stripeResult ->
                // Crear reserva después del pago exitoso de Stripe
                createRealReservation(viewModel, PaymentMethodType.TARJETA)
                showCreditCardDialog = false
            },
            onDismiss = {
                showCreditCardDialog = false
            }
        )
    }

    // Diálogo de procesamiento
    if (showProcessingDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text("Procesando reserva y pago...")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Creando reserva y registrando pago...",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "La reserva se enviará al dashboard del estacionamiento",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = { }
        )
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            title = {
                Text("¡Reserva y Pago Exitosos! 🎉")
            },
            text = {
                Column {
                    Text("✅ Reserva creada y confirmada")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("✅ Pago registrado exitosamente")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📊 La reserva fue enviada al dashboard del owner")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Recibirás un correo de confirmación con los detalles",
                        style = MaterialTheme.typography.bodySmall
                    )

                    createdReservation?.let { reservation ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Código de reserva: ${reservation.codigoReserva ?: reservation.id}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("reservations") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Ver mis reservas")
                }
            }
        )
    }
}

@Composable
private fun StripeCardPaymentDialog(
    amount: Double,
    onPaymentConfirmed: (StripePaymentResult) -> Unit,
    onDismiss: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var paymentSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val stripeRepository = remember { StripeRepository() }
    val coroutineScope = rememberCoroutineScope()


    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                if (paymentSuccess) "✅ Pago Exitoso" else "Pago con Stripe",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💳", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Visa", style = MaterialTheme.typography.bodySmall)
                        Text("Mastercard", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(50.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Conectando con Stripe...", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Procesando tu pago de forma segura",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else if (paymentSuccess) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Éxito",
                            tint = Color(0xFF00D924), modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("¡Pago Completado!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("S/ ${"%.2f".format(amount)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    // Formulario de tarjeta
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Número de tarjeta
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = {
                                cardNumber = it.take(19).filter { it.isDigit() }
                                    .chunked(4).joinToString(" ")
                            },
                            label = { Text("Número de tarjeta") },
                            placeholder = { Text("4242 4242 4242 4242") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = errorMessage != null
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Fecha expiración
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = {
                                    expiryDate = it.take(5).filter { it.isDigit() }
                                        .chunked(2).joinToString("/")
                                },
                                label = { Text("MM/AA") },
                                placeholder = { Text("12/30") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = errorMessage != null
                            )

                            // CVC
                            OutlinedTextField(
                                value = cvc,
                                onValueChange = { cvc = it.take(3).filter { it.isDigit() } },
                                label = { Text("CVC") },
                                placeholder = { Text("123") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                isError = errorMessage != null
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Titular
                        OutlinedTextField(
                            value = cardHolder,
                            onValueChange = { cardHolder = it.uppercase() },
                            label = { Text("Titular de la tarjeta") },
                            placeholder = { Text("Parkea Ya") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = errorMessage != null
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Info de testing STRIPE
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🧪 Modo Testing - Stripe",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF635BFF))

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("Tarjetas de prueba:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium)
                                Text("• 4242 4242 4242 4242 - Visa (éxito)",
                                    style = MaterialTheme.typography.bodySmall)
                                Text("• 5555 5555 5555 4444 - Mastercard (éxito)",
                                    style = MaterialTheme.typography.bodySmall)
                                Text("• CVC: 123 | Fecha: 12/30",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        errorMessage?.let { message ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!paymentSuccess) {
                Button(
                    onClick = {
                        // Validaciones
                        val cleanCardNumber = cardNumber.replace(" ", "")
                        if (cleanCardNumber.length != 16) {
                            errorMessage = "Número de tarjeta debe tener 16 dígitos"
                            return@Button
                        }
                        if (expiryDate.length != 5) {
                            errorMessage = "Fecha de expiración inválida"
                            return@Button
                        }
                        if (cvc.length != 3) {
                            errorMessage = "CVC debe tener 3 dígitos"
                            return@Button
                        }
                        if (cardHolder.length < 3) {
                            errorMessage = "Nombre del titular requerido"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        // ✅ CORRECCIÓN: Usar coroutineScope.launch en lugar de LaunchedEffect
                        coroutineScope.launch {
                            try {
                                val expParts = expiryDate.split("/")
                                val cardDetails = CardDetails(
                                    number = cleanCardNumber,
                                    expMonth = expParts[0].toInt(),
                                    expYear = expParts[1].toInt() + 2000,
                                    cvc = cvc,
                                    cardholderName = cardHolder
                                )

                                val result = stripeRepository.processStripePayment(cardDetails, amount)

                                isLoading = false

                                if (result.success) {
                                    paymentSuccess = true
                                    delay(2000L)
                                    onPaymentConfirmed(result)
                                } else {
                                    errorMessage = result.error ?: "Error al procesar el pago"
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "Error: ${e.message}"
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF635BFF)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procesando...")
                    } else {
                        Icon(Icons.Default.Security, contentDescription = "Seguro")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pagar S/ ${"%.2f".format(amount)} con Stripe")
                    }
                }
            } else {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00D924)
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Continuar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continuar")
                }
            }
        },
        dismissButton = {
            if (!isLoading && !paymentSuccess) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )
}

@Composable
private fun QRPaymentDialog(
    paymentMethod: PaymentMethodType,
    amount: Double,
    onPaymentConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var countdown by remember { mutableStateOf(30) }
    var isPaymentSimulated by remember { mutableStateOf(false) }

    // Determinar qué imagen QR usar según el método de pago
    val qrImageResource = when (paymentMethod.id) {
        "yape" -> com.example.smarparkinapp.R.drawable.qr_yape
        "plin" -> com.example.smarparkinapp.R.drawable.qr_plin
        else -> com.example.smarparkinapp.R.drawable.qr_yape
    }

    // Función para abrir la app externa
    fun openExternalApp() {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(paymentMethod.appPackage)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                val playStoreIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${paymentMethod.appPackage}")
                    setPackage("com.android.vending")
                }
                context.startActivity(playStoreIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir ${paymentMethod.displayName}", Toast.LENGTH_SHORT).show()
        }
    }

    // Cuenta regresiva
    LaunchedEffect(Unit) {
        for (i in 30 downTo 1) {
            if (isPaymentSimulated) break
            delay(1000L)
            countdown = i
        }

        if (!isPaymentSimulated) {
            delay(2000L)
            isPaymentSimulated = true
            onPaymentConfirmed()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Pago con ${paymentMethod.displayName}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // IMAGEN REAL DEL QR
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = qrImageResource),
                        contentDescription = "Código QR para ${paymentMethod.displayName}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Número de teléfono para copiar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            " O envía al número:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                paymentMethod.phoneNumber,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(paymentMethod.phoneNumber))
                                        Toast.makeText(
                                            context,
                                            "Número copiado: ${paymentMethod.phoneNumber}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copiar", fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { openExternalApp() },
                                    modifier = Modifier.height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = "Abrir App", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Abrir", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            " Copia el número y pégalo en ${paymentMethod.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Tiempo restante: ${countdown}s",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (countdown < 10) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "El pago se confirmará automáticamente",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para simular pago manualmente
                if (!isPaymentSimulated) {
                    Button(
                        onClick = {
                            isPaymentSimulated = true
                            onPaymentConfirmed()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirmar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ya pagué con ${paymentMethod.displayName}")
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Procesando pago...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isPaymentSimulated) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Composable
private fun ReservationSummaryCard(viewModel: ReservationViewModel) {
    val selectedParking = viewModel.selectedParking
    val selectedVehicle = viewModel.selectedVehicle
    val totalCost = calculateTotalCost(viewModel)

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

            selectedParking?.let { parking ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estacionamiento:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        parking.nombre,
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
                        parking.direccion,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tarifa:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "S/ ${"%.2f".format(parking.tarifa_hora)} por hora",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            selectedVehicle?.let { vehicle ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vehículo:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${vehicle.brand} ${vehicle.model}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Placa: ${vehicle.plate}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Fecha:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    viewModel.reservationDate,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.reservationType == "hora") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Horario:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${viewModel.reservationStartTime} - ${viewModel.reservationEndTime}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Duración: ${calculateDuration(viewModel.reservationStartTime, viewModel.reservationEndTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tipo:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Reserva por día completo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "Hora de inicio: ${viewModel.reservationTime}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
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
                    "S/ ${"%.2f".format(totalCost)}",
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
        PaymentMethodType.YAPE,
        PaymentMethodType.PLIN,
        PaymentMethodType.TARJETA
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
            CardDefaults.cardElevation(0.dp)
        } else {
            CardDefaults.cardElevation(4.dp)
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
                    Image(
                        painter = painterResource(id = method.imageRes!!),
                        contentDescription = method.displayName,
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
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

                if (method.id == "yape" || method.id == "plin") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        " Número: ${method.phoneNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "💡 Escanea el QR o copia el número para pagar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Funciones auxiliares
private fun getPaymentDescription(methodId: String): String {
    return when (methodId) {
        "yape" -> "Pago rápido - Número: 952695739"
        "plin" -> "Pago rápido - Número: 952695739"
        "tarjeta" -> "Tarjeta de crédito o débito"
        else -> ""
    }
}

private fun getPaymentDetails(methodId: String): String {
    return when (methodId) {
        "yape" -> "Escanea el código QR con Yape o envía al número 952695739"
        "plin" -> "Escanea el código QR con Plin o envía al número 952695739"
        "tarjeta" -> "Pago seguro con tarjeta de crédito o débito"
        else -> ""
    }
}

private fun calculateTotalCost(viewModel: ReservationViewModel): Double {
    val parking = viewModel.selectedParking ?: return 0.0
    val startTime = viewModel.reservationStartTime
    val endTime = viewModel.reservationEndTime
    val reservationType = viewModel.reservationType

    return if (reservationType == "dia") {
        parking.tarifa_hora * 8
    } else {
        if (startTime.isEmpty() || endTime.isEmpty()) {
            parking.tarifa_hora
        } else {
            try {
                val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                val start = format.parse(startTime)
                val end = format.parse(endTime)
                if (start != null && end != null) {
                    val hours = ((end.time - start.time) / (1000 * 60 * 60)).toDouble()
                    parking.tarifa_hora * hours
                } else {
                    parking.tarifa_hora
                }
            } catch (e: Exception) {
                parking.tarifa_hora
            }
        }
    }
}

private fun calculateDuration(startTime: String, endTime: String): String {
    return try {
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val start = format.parse(startTime)
        val end = format.parse(endTime)

        if (start != null && end != null) {
            val diff = end.time - start.time
            val hours = diff / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)

            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        } else "0h"
    } catch (e: Exception) {
        "0h"
    }
}

// FUNCIONES PARA CONSUMIR APIS REALES
private fun createRealReservation(
    viewModel: ReservationViewModel,
    method: PaymentMethodType
) {
    println("🚀 Creando reserva REAL con método: ${method.displayName}")
    viewModel.createReservation { reservation ->
        println("✅ Reserva creada exitosamente, ID: ${reservation.id}")
        // El pago se procesará automáticamente a través del LaunchedEffect
    }
}

private fun processRealPayment(
    viewModel: ReservationViewModel,
    method: PaymentMethodType,
    reservationId: Long
) {
    println("💰 Procesando pago REAL para reserva: $reservationId, método: ${method.id}")

    viewModel.createPayment(method.id) { resultString ->
        println("✅ Pago procesado - Respuesta: $resultString")
    }
}
