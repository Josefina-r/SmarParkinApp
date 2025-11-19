package com.example.smarparkinapp.ui.theme.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel  // ✅ CORREGIDO
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    parkingId: Int,
    onSuccessNavigate: () -> Unit
) {
    val context = LocalContext.current
    val reservationViewModel: ReservationViewModel = viewModel()

    var carId by remember { mutableStateOf("") }
    var tipoReserva by remember { mutableStateOf("normal") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fecha actual por defecto
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        fecha = sdf.format(Date())
    }

    // Función para crear reserva
    fun createReservation() {
        if (carId.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (carId.toIntOrNull() == null) {
            Toast.makeText(context, "ID del carro debe ser un número", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        errorMessage = null

        try {
            // Formato de fecha y hora para tu API Django
            val horaInicioCompleta = "${fecha}T${horaInicio}:00"
            val horaFinCompleta = "${fecha}T${horaFin}:00"

            // TODO: Aquí llamarías a tu API Django
            // reservationViewModel.createReservation(
            //     parkingId = parkingId,
            //     carId = carId.toInt(),
            //     horaInicio = horaInicioCompleta,
            //     horaFin = horaFinCompleta,
            //     tipoReserva = tipoReserva
            // )

            // Simulación temporal
            android.os.Handler(context.mainLooper).postDelayed({
                isLoading = false
                Toast.makeText(context, "Reserva creada exitosamente", Toast.LENGTH_SHORT).show()
                onSuccessNavigate()
            }, 2000)

        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Error: ${e.message}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Crear Reserva - Parking #$parkingId",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Fecha actual (solo lectura)
        OutlinedTextField(
            value = "Fecha: $fecha",
            onValueChange = { },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Fecha de reserva") }
        )

        Spacer(modifier = Modifier.height(15.dp))

        // ID del Carro
        OutlinedTextField(
            value = carId,
            onValueChange = { carId = it },
            label = { Text("ID del Carro") },
            placeholder = { Text("Ej: 1, 2, 3...") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Tipo de Reserva (según tu API)
        var expanded by remember { mutableStateOf(false) }
        val tiposReserva = listOf("normal", "recurrente", "premium")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = tipoReserva,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Tipo de Reserva") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tiposReserva.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            tipoReserva = tipo
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Hora Inicio
        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        horaInicio = "%02d:%02d".format(hour, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hora Inicio: ${if (horaInicio.isEmpty()) "--:--" else horaInicio}")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hora Fin
        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        horaFin = "%02d:%02d".format(hour, minute)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hora Fin: ${if (horaFin.isEmpty()) "--:--" else horaFin}")
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Resumen de la reserva
        if (horaInicio.isNotEmpty() && horaFin.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Resumen de Reserva",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fecha: $fecha")
                    Text("Hora: $horaInicio - $horaFin")
                    Text("Tipo: ${tipoReserva.uppercase()}")
                    Text("Car ID: ${if (carId.isEmpty()) "No seleccionado" else carId}")
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }

        // Botón Crear Reserva
        Button(
            onClick = { createReservation() },
            enabled = !isLoading && carId.isNotEmpty() && horaInicio.isNotEmpty() && horaFin.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Crear Reserva")
            }
        }

        // Mostrar errores
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}