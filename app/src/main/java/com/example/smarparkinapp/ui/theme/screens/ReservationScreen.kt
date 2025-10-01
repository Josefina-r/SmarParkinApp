package com.example.smarparkinapp.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.ui.theme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    parkingName: String = "Estacionamiento Central",
    pricePerHour: Double = 5.0
) {
    var selectedDuration by remember { mutableStateOf(1) }
    var plate by remember { mutableStateOf("") }
    var selectedPayment by remember { mutableStateOf("Yape") }

    val totalPrice = selectedDuration * pricePerHour
    val context = LocalContext.current // ✅ Contexto válido dentro de Composable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservar", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal,
                    titleContentColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Nombre del estacionamiento
            Text(
                text = parkingName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal
                )
            )

            // Selección de duración
            DurationSelector(
                selected = selectedDuration,
                onSelected = { selectedDuration = it }
            )

            // Placa del vehículo
            VehicleInfoCard(
                plate = plate,
                onPlateChange = { plate = it }
            )

            // Método de pago
            PaymentMethodSelector(
                selected = selectedPayment,
                onSelected = { selectedPayment = it }
            )

            // Resumen
            SummaryCard(
                duration = selectedDuration,
                pricePerHour = pricePerHour,
                total = totalPrice
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón confirmar → abre TicketActivity con Intent explícito
            Button(
                onClick = {
                    val intent = Intent(context, TicketActivity::class.java).apply {
                        putExtra("parkingName", parkingName)
                        putExtra("plate", plate)
                        putExtra("duration", selectedDuration)
                        putExtra("totalPrice", totalPrice)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Text("Confirmar Reserva", fontWeight = FontWeight.Bold, color = Blanco)
            }
        }
    }
}

// COMPONENTES

@Composable
fun DurationSelector(selected: Int, onSelected: (Int) -> Unit) {
    Column {
        Text("Duración (horas)", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(1, 2, 3, 4).forEach { d ->
                Box(
                    modifier = Modifier
                        .background(
                            if (selected == d) AzulPrincipal else GrisClaro,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelected(d) }
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$d h",
                        color = if (selected == d) Blanco else Color.Black,
                        fontWeight = if (selected == d) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleInfoCard(plate: String, onPlateChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Vehículo", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = plate,
                onValueChange = { onPlateChange(it.uppercase()) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Placa del carro") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Ejemplo: ABC-123",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
fun PaymentMethodSelector(selected: String, onSelected: (String) -> Unit) {
    Column {
        Text("Método de pago", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        val methods = listOf("Yape", "Plin", "Tarjeta")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            methods.forEach { method ->
                Box(
                    modifier = Modifier
                        .background(
                            if (selected == method) VerdeSecundario else GrisClaro,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onSelected(method) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        method,
                        color = if (selected == method) Blanco else Color.Black,
                        fontWeight = if (selected == method) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(duration: Int, pricePerHour: Double, total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Resumen", fontWeight = FontWeight.SemiBold)
            Text("Duración: $duration h")
            Text("Precio por hora: S/ $pricePerHour")
            Text(
                "Total: S/ $total",
                fontWeight = FontWeight.Bold,
                color = VerdePrincipal,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
