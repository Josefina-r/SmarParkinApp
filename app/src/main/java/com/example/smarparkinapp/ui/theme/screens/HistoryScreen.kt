package com.example.smarparkinapp.ui.theme.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.ui.theme.theme.SmarParkinAppTheme
import com.example.smarparkinapp.ui.theme.theme.*




data class ReservaHistorial(
    val id: String,
    val estacionamiento: String,
    val fecha: String,
    val precio: Double,
    val estado: String
)

@Composable
fun HistoryScreen(
    reservas: List<ReservaHistorial> = listOf(
        ReservaHistorial("1", "Parking Central", "12/09/2025", 8.50, "Completada"),
        ReservaHistorial("2", "Plaza Norte", "10/09/2025", 6.00, "Cancelada"),
        ReservaHistorial("3", "Parking Express", "05/09/2025", 10.00, "Completada")
    )
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Reservas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(reservas) { reserva ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Aquí abrimos la actividad con un Intent explícito
                            val intent = Intent(context, ParkingDetailActivity::class.java)
                            intent.putExtra("parkingName", reserva.estacionamiento)
                            intent.putExtra("parkingAddress", "Av. Los Pinos 123, Trujillo") // ejemplo
                            intent.putExtra("parkingPrice", reserva.precio)
                            intent.putExtra("parkingPhone", "926065973") // ejemplo
                            context.startActivity(intent)
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Título (Estacionamiento y Precio a la derecha)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reserva.estacionamiento,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "S/.${reserva.precio}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fecha
                        Text(
                            text = "Fecha: ${reserva.fecha}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val estadoColor = when (reserva.estado) {
                            "Completada" -> VerdePrincipal
                            "Cancelada" -> AzulPrincipal
                            "Pendiente" -> VerdeSecundario
                            else -> GrisClaro
                        }

                        Box(
                            modifier = Modifier
                                .background(estadoColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = reserva.estado,
                                color = estadoColor,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    SmarParkinAppTheme {
        HistoryScreen()
    }
}
