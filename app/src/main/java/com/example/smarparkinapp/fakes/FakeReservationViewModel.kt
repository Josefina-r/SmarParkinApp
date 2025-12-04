package com.example.smarparkinapp.fakes

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

class FakeReservationViewModel : ViewModel() {

    // Lista de vehículos simulada
    private val _vehicles = mutableStateListOf<String>()
    val vehicles: SnapshotStateList<String> get() = _vehicles

    init {
        // Vehículos iniciales falsos
        _vehicles.addAll(
            listOf(
                "Toyota Corolla",
                "Nissan Versa",
                "Kia Rio"
            )
        )
    }

    // Función falsa que simula agregar un vehículo
    fun addVehicle(vehicleName: String) {
        _vehicles.add(vehicleName)
    }
}