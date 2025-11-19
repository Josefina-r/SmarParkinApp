package com.example.smarparkinapp.data.model

// Car.kt


data class Car(
    val id: Int = 0,  // Cambia de Int? a Int con valor por defecto
    val plate: String,
    val model: String,
    val brand: String,
    val color: String,
    val type: VehicleType = VehicleType.AUTOMOVIL
)

enum class VehicleType {
    AUTOMOVIL, MOTOCICLETA
}