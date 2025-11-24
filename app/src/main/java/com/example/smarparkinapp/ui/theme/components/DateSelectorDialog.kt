// ui/theme/components/DateSelectorDialog.kt
package com.example.smarparkinapp.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectorDialog(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar Fecha",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mensaje informativo
                Text(
                    text = "Selecciona una fecha para tu reserva",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Opciones de fecha rápidas
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val today = LocalDate.now()
                    val tomorrow = today.plusDays(1)
                    val dayAfterTomorrow = today.plusDays(2)

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                    DateOptionButton(
                        date = today,
                        label = "Hoy",
                        formatter = formatter,
                        onDateSelected = onDateSelected,
                        onDismiss = onDismiss
                    )

                    DateOptionButton(
                        date = tomorrow,
                        label = "Mañana",
                        formatter = formatter,
                        onDateSelected = onDateSelected,
                        onDismiss = onDismiss
                    )

                    DateOptionButton(
                        date = dayAfterTomorrow,
                        label = "Pasado mañana",
                        formatter = formatter,
                        onDateSelected = onDateSelected,
                        onDismiss = onDismiss
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para otras fechas (podrías integrar un DatePicker real aquí)
                OutlinedButton(
                    onClick = {
                        // Para una implementación más completa, podrías usar:
                        // - DatePicker de Android nativo
                        // - O una librería de calendario
                        val nextWeek = LocalDate.now().plusDays(7)
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        onDateSelected(nextWeek.format(formatter))
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Otra fecha")
                }
            }
        }
    }
}

@Composable
private fun DateOptionButton(
    date: LocalDate,
    label: String,
    formatter: java.time.format.DateTimeFormatter,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val formattedDate = date.format(formatter)
    val displayDate = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("es", "ES")))

    Card(
        onClick = {
            onDateSelected(formattedDate)
            onDismiss()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}