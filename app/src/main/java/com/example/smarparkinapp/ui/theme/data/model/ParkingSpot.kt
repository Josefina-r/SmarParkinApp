package com.example.smarparkinapp.ui.theme.data.model

data class ParkingSpot(

    val id: Int,
    val name: String,
    val address: String,
    val price: String,
    val availableSpots: Int,
    val latitude: Double,
    val longitude: Double,
    val nivelSeguridad: Int = 1,
    val ratingPromedio: Double = 0.0,
    val totalResenas: Int = 0,
    val estaAbierto: Boolean = true,
    val tieneCamaras: Boolean = false,
    val tieneVigilancia24h: Boolean = false,
    val distanciaKm: Double? = null,
    val imagenUrl: String = ""
)