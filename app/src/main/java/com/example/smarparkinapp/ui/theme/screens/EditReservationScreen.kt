package com.example.smarparkinapp.ui.theme.screens
/**
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.R
import com.example.smarparkinapp.presentation.viewmodel.ReservationViewModel
import com.example.smarparkinapp.ui.theme.data.*

@Composable
fun EditReservationScreen(
    reservationId: Int,
    viewModel: ReservationViewModel = viewModel(),
    onUpdated: () -> Unit
) {
    // Cargar reservas desde la API
    LaunchedEffect(Unit) {
        viewModel.getReservations()   // 🔥 Llama a tu API REAL
    }

    val reservations by viewModel.reservations.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    // Obtener reserva seleccionada
    val reservation = reservations.find { it.id == reservationId }

    var horaInicio by remember { mutableStateOf(reservation?.hora_inicio ?: "") }
    var horaFin by remember { mutableStateOf(reservation?.hora_fin ?: "") }
    var tipoReserva by remember { mutableStateOf(reservation?.tipo_reserva ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Editar Reserva", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = horaInicio,
            onValueChange = { horaInicio = it },
            label = { Text("Hora Inicio (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = horaFin,
            onValueChange = { horaFin = it },
            label = { Text("Hora Fin (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tipoReserva,
            onValueChange = { tipoReserva = it },
            label = { Text("Tipo de Reserva") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.updateReservation(
                    id = reservationId,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    tipoReserva = tipoReserva
                )
                onUpdated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar Reserva")
        }

        if (isLoading) {
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }

        // Mostrar errores del servidor
        errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Error: $it",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}*/
