package com.example.smarparkinapp.ui.theme.data.model

data class Parking(
    val id: String,
    val nombre: String, // Usamos nombre en español
    val direccion: String,
    val precioHora: Double,
    val horario: String,
    val amenidades: List<String>,
    val telefono: String,
    // Campos para estacionamientos reales
    val latitude: Double,
    val longitude: Double,
    val availableSpots: Int = -1,
    val totalSpots: Int = -1,
    val isAvailable: Boolean = true,
    val isRealParking: Boolean = false,
    val rating: Float? = null,
    val userRatingsTotal: Int? = null,
    val photos: List<ParkingPhoto> = emptyList(),
    val placeId: String? = null
)

data class ParkingPhoto(
    val photoReference: String,
    val width: Int,
    val height: Int,
    val imageUrl: String? = null
)
