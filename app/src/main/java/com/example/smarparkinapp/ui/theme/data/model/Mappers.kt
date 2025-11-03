package com.example.smarparkinapp.ui.theme.data.model

// Función para convertir ParkingSpotResponse a ParkingSpot
fun ParkingSpotResponse.toParkingSpot(): ParkingSpot {
    return ParkingSpot(
        id = this.id,
        name = this.nombre,
        address = this.direccion,
        price = this.precioHora,
        availableSpots = this.plazasDisponibles,
        latitude = this.latitud,
        longitude = this.longitud,
        nivelSeguridad = this.nivelSeguridad ?: 1,
        ratingPromedio = this.ratingPromedio ?: 0.0,
        totalResenas = this.totalResenas ?: 0,
        estaAbierto = this.estaAbierto ?: true,
        tieneCamaras = this.tieneCamaras ?: false,
        tieneVigilancia24h = this.tieneVigilancia24h ?: false,
        distanciaKm = this.distanciaKm
    )
}

// Función para convertir ParkingLot a ParkingSpot
fun ParkingLot.toParkingSpot(): ParkingSpot {
    // Parsear coordenadas (formato: "latitud,longitud")
    val (latitude, longitude) = parseCoordenadas(this.coordenadas)

    return ParkingSpot(
        id = this.id.toInt(),
        name = this.nombre,
        address = this.direccion,
        price = "S/ ${this.tarifa_hora}",
        availableSpots = this.plazas_disponibles,
        latitude = latitude,
        longitude = longitude,
        nivelSeguridad = parseNivelSeguridad(this.nivel_seguridad),
        ratingPromedio = this.rating_promedio ?: 0.0,
        totalResenas = this.total_resenas ?: 0,
        estaAbierto = this.esta_abierto ?: true,
        tieneCamaras = hasCamaras(this.nivel_seguridad),
        tieneVigilancia24h = hasVigilancia24h(this.nivel_seguridad),
        distanciaKm = null
    )
}

// Función auxiliar para parsear coordenadas
private fun parseCoordenadas(coordenadas: String?): Pair<Double, Double> {
    return if (coordenadas != null && coordenadas.contains(",")) {
        val parts = coordenadas.split(",")
        if (parts.size == 2) {
            val lat = parts[0].trim().toDoubleOrNull() ?: 0.0
            val lng = parts[1].trim().toDoubleOrNull() ?: 0.0
            lat to lng
        } else {
            0.0 to 0.0
        }
    } else {
        0.0 to 0.0
    }
}

// Función auxiliar para parsear nivel de seguridad
private fun parseNivelSeguridad(nivelSeguridad: String?): Int {
    return when (nivelSeguridad) {
        "ALTO" -> 3
        "MEDIO" -> 2
        "BAJO" -> 1
        else -> 1
    }
}

// Función auxiliar para determinar si tiene cámaras
private fun hasCamaras(nivelSeguridad: String?): Boolean {
    return nivelSeguridad == "ALTO" || nivelSeguridad == "MEDIO"
}

// Función auxiliar para determinar si tiene vigilancia 24h
private fun hasVigilancia24h(nivelSeguridad: String?): Boolean {
    return nivelSeguridad == "ALTO"
}