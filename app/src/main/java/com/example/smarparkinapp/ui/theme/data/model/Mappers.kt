// Mappers.kt - CORREGIDO
package com.example.smarparkinapp.ui.theme.data.model

fun ParkingLot.toParkingSpot(): ParkingSpot {
    val (latitude, longitude) = parseCoordenadas(this.coordenadas)
    val imagenUrl = buildImageUrl(this.imagen_principal)

    // ✅ CORREGIR: Cambiar localhost por 10.0.2.2
    val fixedImagenUrl = if (imagenUrl.contains("localhost")) {
        imagenUrl.replace("http://localhost:8000", "http://10.0.2.2:8000")
    } else {
        imagenUrl
    }

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
        distanciaKm = null,
        imagenUrl = fixedImagenUrl, // ✅ Usar URL corregida
        telefono = this.telefono
    )
}

// Función auxiliar para construir URL completa si es necesario
private fun buildImageUrl(imagenPath: String?): String {
    return when {
        imagenPath.isNullOrEmpty() -> getDefaultParkingImage()
        imagenPath.startsWith("http") -> imagenPath
        imagenPath.startsWith("/") -> "http://localhost:8000$imagenPath"
        else -> "http://localhost:8000/media/$imagenPath"
    }
}

private fun getDefaultParkingImage(): String {
    return "http://10.0.2.2:8000/media/parking_images/default.jpg"
}

// Resto del código igual...
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

private fun parseNivelSeguridad(nivelSeguridad: String?): Int {
    return when (nivelSeguridad) {
        "ALTO" -> 3
        "MEDIO" -> 2
        "BAJO" -> 1
        else -> 1
    }
}

private fun hasCamaras(nivelSeguridad: String?): Boolean {
    return nivelSeguridad == "ALTO" || nivelSeguridad == "MEDIO"
}

private fun hasVigilancia24h(nivelSeguridad: String?): Boolean {
    return nivelSeguridad == "ALTO"
}