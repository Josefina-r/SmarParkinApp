package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.smarparkinapp.fakes.FakeReservationViewModel

@Composable
fun VehicleSelectionScreenFake(viewModel: FakeReservationViewModel) {

    Column {

        Text("Selecciona un vehículo")

        viewModel.vehicles.forEach { vehicle ->
            Text(vehicle)
        }

        Button(onClick = {
            viewModel.addVehicle("Nuevo Auto")
        }) {
            Text("Agregar Vehículo")
        }
    }
}
