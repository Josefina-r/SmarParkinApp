package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.theme.*
import androidx.compose.foundation.interaction.MutableInteractionSource

data class ReservaHistorial(
    val id: String,
    val estacionamiento: String,
    val fecha: String,
    val precio: Double,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController? = null,
    reservas: List<ReservaHistorial> = listOf(
        ReservaHistorial("1", "Parking Central", "12/09/2025", 8.50, "Completada"),
        ReservaHistorial("2", "Plaza Norte", "10/09/2025", 6.00, "Cancelada"),
        ReservaHistorial("3", "Parking Express", "05/09/2025", 10.00, "Completada")
    )
) {
    var showOptionsMenu by remember { mutableStateOf(false) }

    val navigateToParkingDetail = { reserva: ReservaHistorial ->
        navController?.navigate(NavRoutes.ParkingDetail.createRoute(reserva.id.toLong())) {
            launchSingleTop = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Reservas", color = Blanco) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Blanco)
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menú", tint = Blanco)
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Actualizar") },
                            onClick = { showOptionsMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar todo") },
                            onClick = { showOptionsMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar") },
                            onClick = { showOptionsMenu = false }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal
                )
            )
        },
        containerColor = Blanco
    )
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reservas) { reserva ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                navigateToParkingDetail(reserva)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { }
                        ) {

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
                                        color = AzulPrincipal
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
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
                                    .background(
                                        estadoColor.copy(alpha = 0.15f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = reserva.estado,
                                    color = estadoColor,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
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
