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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.viewmodel.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(
    parkingId: Int,
    reservationViewModel: ReservationViewModel = viewModel(factory = ReservationViewModelFactory(LocalContext.current)),
    onSuccessNavigate: () -> Unit
) {
    val context = LocalContext.current

    var carId by remember { mutableStateOf("") }
    var tipoReserva by remember { mutableStateOf("normal") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFin by remember { mutableStateOf("") }

    val isLoading by reservationViewModel.isLoading.collectAsState()
    val error by reservationViewModel.error.collectAsState()
    val createdReservation by reservationViewModel.createdReservation.collectAsState()


    // SI LA RESERVA SE CREÓ — NAVEGA
    LaunchedEffect(createdReservation) {
        if (createdReservation != null) {
            Toast.makeText(context, "Reserva creada correctamente", Toast.LENGTH_SHORT).show()
            onSuccessNavigate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Crear Reserva",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ------------------------- CAR ID -------------------------
        OutlinedTextField(
            value = carId,
            onValueChange = { carId = it },
            label = { Text("ID del Carro") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        // ------------------------- TIPO RESERVA -------------------------
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = {}
        ) {
            OutlinedTextField(
                value = tipoReserva,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tipo de Reserva") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(false) }
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // ------------------------- HORA INICIO -------------------------
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
            Text("Seleccionar Hora Inicio: ${if (horaInicio.isEmpty()) "--:--" else horaInicio}")
        }

        Spacer(modifier = Modifier.height(15.dp))

        // ------------------------- HORA FIN -------------------------
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
            Text("Seleccionar Hora Fin: ${if (horaFin.isEmpty()) "--:--" else horaFin}")
        }

        Spacer(modifier = Modifier.height(25.dp))


        // ------------------------- CREAR RESERVA -------------------------
        Button(
            onClick = {
                if (carId.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val hoy = "2025-11-15" // cambiar por LocalDate.now().toString()

                reservationViewModel.createReservation(
                    parkingId = parkingId,
                    carId = carId.toInt(),
                    horaInicio = "${hoy}T${horaInicio}:00",
                    horaFin = "${hoy}T${horaFin}:00",
                    tipoReserva = tipoReserva
                )
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Creando..." else "Crear Reserva")
        }

        // ------------------------- ERROR -------------------------
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}
