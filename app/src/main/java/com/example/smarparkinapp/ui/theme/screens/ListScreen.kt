package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.smarparkinapp.ui.theme.theme.* // ← Importa tus colores personalizados

// Modelo simple para una reserva
data class Reserva(
    val id: Int,
    val nombre: String,
    val placa: String,
    val horaEntrada: String,
    val horaSalida: String,
    val estado: String
)

@Composable
fun ListScreen(
    reservas: List<Reserva>,
    onReservaClick: (Reserva) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CarRental,
                contentDescription = "Reservas",
                tint = AzulPrincipal,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mis Reservas",
                style = MaterialTheme.typography.headlineSmall,
                color = AzulPrincipal
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (reservas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes reservas registradas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GrisClaro
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reservas) { reserva ->
                    ReservaCard(reserva, onClick = { onReservaClick(reserva) })
                }
            }
        }
    }
}

@Composable
fun ReservaCard(reserva: Reserva, onClick: () -> Unit) {
    // Color del estado
    val estadoColor = when (reserva.estado) {
        "Activa" -> VerdePrincipal
        "Completada" -> VerdeSecundario
        "Cancelada" -> AzulPrincipal
        else -> GrisClaro
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nombre + Estado
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = reserva.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulPrincipal
                    )
                    Text(
                        text = "Placa: ${reserva.placa}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AzulSecundario
                    )
                }

                // Chip de estado
                AssistChip(
                    onClick = {},
                    label = { Text(reserva.estado) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = estadoColor,
                        labelColor = Blanco
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horarios
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = VerdePrincipal)
                    Spacer(Modifier.width(4.dp))
                    Text("Entrada: ${reserva.horaEntrada}", style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = AzulPrincipal)
                    Spacer(Modifier.width(4.dp))
                    Text("Salida: ${reserva.horaSalida}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservationsScreenPreview() {
    val demoReservas = listOf(
        Reserva(1, "Ana López", "ABC-123", "10:00 AM", "12:00 PM", "Activa"),
        Reserva(2, "Carlos Ruiz", "XYZ-789", "11:00 AM", "01:30 PM", "Completada"),
        Reserva(3, "Luis Torres", "LMN-456", "09:30 AM", "11:00 AM", "Cancelada")
    )
    ListScreen(reservas = demoReservas)
}
