package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.ui.theme.theme.*
import java.text.SimpleDateFormat
import java.util.*


data class Reserva(
    val id: Int,
    val nombre: String,
    val placa: String,
    val horaEntrada: String,
    val horaSalida: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    reservas: List<Reserva>,
    onReservaClick: (Reserva) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val fechaActual = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Reservas",
                        color = Blanco
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Blanco
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: mostrar menú o acciones */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Fecha actual alineada a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = fechaActual,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AzulSecundario
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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
}

@Composable
fun ReservaCard(reserva: Reserva, onClick: () -> Unit) {
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
