package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

// Paso 1: Define un modelo de datos para el estacionamiento.
// Esto representa la información que quieres mostrar.
data class ParkingInfo(
    val id: String,
    val name: String,
    val address: String,
    val pricePerHour: Double,
    val availableSpots: Int,
    val totalSpots: Int,
    val imageUrl: String
)

// Datos de ejemplo para la vista previa y pruebas.
val sampleParking = ParkingInfo(
    id = "park_123",
    name = "Estacionamiento Central",
    address = "Av. Siempre Viva 742",
    pricePerHour = 2.50,
    availableSpots = 15,
    totalSpots = 50,
    imageUrl = "https://via.placeholder.com/400x200.png?text=Parking+Image" // URL de imagen de ejemplo
)

// Paso 2: Crea la pantalla Composable para los detalles del estacionamiento.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingDetailScreen(
    parkingId: String, // Recibes el ID desde el navegador.
    onBackClicked: () -> Unit // Función para volver atrás.
) {
    // En una app real, usarías el parkingId para obtener los datos de una base de datos o API.
    // Por ahora, usamos los datos de ejemplo.
    val parking = sampleParking

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalles del Estacionamiento") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Para que la pantalla sea deslizable si el contenido no cabe
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nombre del estacionamiento
            Text(
                text = parking.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del estacionamiento
            Image(
                painter = rememberAsyncImagePainter(parking.imageUrl),
                contentDescription = "Imagen de ${parking.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta con información detallada
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "Dirección",
                        value = parking.address
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(
                        icon = Icons.Default.Money,
                        label = "Precio por hora",
                        value = "$${String.format("%.2f", parking.pricePerHour)}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información de disponibilidad
            AvailabilityInfo(
                available = parking.availableSpots,
                total = parking.totalSpots
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de acción
            Button(
                onClick = { /* Lógica para reservar o navegar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Reservar Lugar", fontSize = 18.sp)
            }
        }
    }
}

// Composable auxiliar para mostrar una fila de información con ícono.
@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

// Composable auxiliar para mostrar la disponibilidad.
@Composable
fun AvailabilityInfo(available: Int, total: Int) {
    val availabilityPercentage = if (total > 0) available.toFloat() / total else 0f
    val backgroundColor = when {
        availabilityPercentage > 0.5f -> Color(0xFFC8E6C9) // Verde claro
        availabilityPercentage > 0.1f -> Color(0xFFFFECB3) // Amarillo claro
        else -> Color(0xFFFFCDD2) // Rojo claro
    }
    val contentColor = when {
        availabilityPercentage > 0.5f -> Color(0xFF2E7D32) // Verde oscuro
        availabilityPercentage > 0.1f -> Color(0xFFFFA000) // Amarillo oscuro
        else -> Color(0xFFC62828) // Rojo oscuro
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Disponibles: $available / $total",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

