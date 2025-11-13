package com.example.smarparkinapp.ui.theme.data.model
import com.example.smarparkinapp.ui.theme.data.model.*
fun ParkingSpotResponse.toParkingSpot(): ParkingSpot {
    return ParkingSpot(
        id = this.id,
        name = this.name,
        address = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        price = this.hourlyRate,
        availableSpots = this.availableSpots,
        ratingPromedio = this.averageRating ?: 0.0,
        totalResenas = this.reviewCount,
        nivelSeguridad = mapSecurityLevel(this.securityLevel),  // ← Ahora maneja null
        tieneCamaras = this.amenidades?.contains("cámaras", ignoreCase = true) ?: false,  // ← Safe call
        tieneVigilancia24h = this.amenidades?.contains("24", ignoreCase = true) == true ||
                this.amenidades?.contains("vigilancia", ignoreCase = true) == true,
        estaAbierto = isCurrentlyOpen(this.openingTime, this.closingTime),
        description = this.description ?: "",  // ← Valor por defecto
        horario = "${this.openingTime ?: "24h"} - ${this.closingTime ?: "24h"}"  // ← Safe
    )
}


private fun mapSecurityLevel(level: String?): Int {
    return when (level?.toLowerCase()) {
        "alta" -> 5
        "media" -> 3
        "baja" -> 1
        else -> 3
    }
}

private fun isCurrentlyOpen(openingTime: String?, closingTime: String?): Boolean {

    if (openingTime == null || closingTime == null) return true

    return true
}