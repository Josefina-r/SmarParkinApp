package com.example.smarparkinapp.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.R
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.data.model.*

@Composable
fun EstacionamientoDetalleScreen(
    estacionamiento: Estacionamiento = Estacionamiento(
        nombre = "Parking Central",
        direccion = "Av. Los Pinos 123, Trujillo",
        precioHora = 5.50,
        horario = "24 horas",
        amenidades = listOf("Seguridad 24/7", "Cámaras", "Techado", "Acceso rápido"),
        telefono = "926065973"
    ),
    onReservarClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Imagen destacada con overlay y degradado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.estacionamiento),
                contentDescription = "Foto del estacionamiento",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                            startY = 200f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = estacionamiento.nombre,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Blanco,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "S/.${estacionamiento.precioHora} / hora",
                    style = MaterialTheme.typography.titleMedium.copy(color = Blanco)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dirección
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AzulPrincipal)
            Spacer(Modifier.width(8.dp))
            Text(estacionamiento.direccion, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horario
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = VerdePrincipal)
            Spacer(Modifier.width(8.dp))
            Text("Horario: ${estacionamiento.horario}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Teléfono con botón para llamar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Call, contentDescription = null, tint = Color.Green)
            Spacer(Modifier.width(8.dp))
            Text("Tel: ${estacionamiento.telefono}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${estacionamiento.telefono}")
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Llamar", color = Blanco)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Amenidades en Chips
        Text(
            "Amenidades",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AzulPrincipal
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            estacionamiento.amenidades.forEach { amenidad ->
                AssistChip(
                    onClick = {},
                    label = { Text(amenidad, color = Blanco) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = AzulSecundario
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón Reservar ahora destacado
        Button(
            onClick = onReservarClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
        ) {
            Text(
                "Reservar ahora",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Blanco,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EstacionamientoDetalleScreenPreview() {
    SmarParkinAppTheme {
        EstacionamientoDetalleScreen()
    }
}
