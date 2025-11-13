package com.example.smarparkinapp.ui.theme.data.model

data class ParkingSpot(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val price: String,
    val availableSpots: Int,
    val ratingPromedio: Double = 0.0,  // ← Valor por defecto
    val totalResenas: Int = 0,
    val nivelSeguridad: Int = 3,  // ← Valor por defecto
    val tieneCamaras: Boolean = false,
    val tieneVigilancia24h: Boolean = false,
    val estaAbierto: Boolean = true,
    val description: String = "",  // ← Valor por defecto
    val horario: String = "24h"  // ← Valor por defecto
)