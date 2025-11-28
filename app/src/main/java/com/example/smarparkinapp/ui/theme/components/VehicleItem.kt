package com.example.smarparkinapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.ui.theme.data.model.Car

@Composable
fun VehicleItem(
    car: Car,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                println("🔍 [VehicleItem] Clic en vehículo: ${car.plate}, seleccionado: $isSelected")
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF5555FF).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = if (isSelected) CardDefaults.cardElevation(4.dp) else CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de vehículo
            Icon(
                imageVector = Icons.Outlined.DirectionsCar,
                contentDescription = "Automóvil",
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) Color(0xFF5555FF) else Color.Gray
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del vehículo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Marca en negrita
                Text(
                    text = car.brand,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF5555FF) else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Placa
                Text(
                    text = car.plate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color(0xFF5555FF) else Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Modelo y Color
                Text(
                    text = "${car.model} • ${car.color}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Gray // ✅ CORREGIDO
                )
            }

            // Indicador de selección
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Seleccionado",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF5555FF)
                )
            }
        }
    }
}