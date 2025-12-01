// ParkingSpotResponse.kt - CORREGIDO PARA EMULADOR
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ParkingSpotResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,

    // Django devuelve 'tarifa_hora' como Double
    @SerializedName("tarifa_hora") val tarifaHora: Double?,

    @SerializedName("plazas_disponibles") val plazasDisponibles: Int,
    @SerializedName("total_plazas") val totalPlazas: Int? = 0,

    // Coordenadas
    @SerializedName("coordenadas") val coordenadas: String?,

    // Django devuelve 'nivel_seguridad' como String
    @SerializedName("nivel_seguridad") val nivelSeguridad: String?,

    @SerializedName("rating_promedio") val ratingPromedio: Double? = 0.0,
    @SerializedName("total_reseñas") val totalResenas: Int? = 0,

    // Estados
    @SerializedName("esta_abierto") val estaAbierto: Boolean? = true,
    @SerializedName("aprobado") val aprobado: Boolean? = false,
    @SerializedName("activo") val activo: Boolean? = false,

    // IMÁGENES
    @SerializedName("imagen_principal") val imagenPrincipal: String? = null,
    @SerializedName("imagenes") val imagenes: List<ParkingImageResponse>? = emptyList(),

    // Detalles adicionales
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("horario_apertura") val horarioApertura: String? = null,
    @SerializedName("horario_cierre") val horarioCierre: String? = null,
    @SerializedName("servicios") val servicios: List<String>? = emptyList()
) {
    fun toParkingSpot(): ParkingSpot {
        val (lat, lng) = parseCoordinates(coordenadas)

        return ParkingSpot(
            id = id,
            name = nombre,
            address = direccion,
            price = formatPrice(tarifaHora),
            availableSpots = plazasDisponibles,
            latitude = lat,
            longitude = lng,
            nivelSeguridad = parseSecurityLevel(nivelSeguridad),
            ratingPromedio = ratingPromedio ?: 0.0,
            totalResenas = totalResenas ?: 0,
            estaAbierto = estaAbierto ?: true,
            tieneCamaras = hasCameras(nivelSeguridad),
            tieneVigilancia24h = has24hSurveillance(nivelSeguridad),

            // IMÁGENES - IMPORTANTE: Corregir localhost
            imagenUrl = fixLocalhostUrl(imagenPrincipal ?: getDefaultImage()),
            imagenes = imagenes?.map { it.toParkingImage() } ?: emptyList(),
            imagenPrincipal = fixLocalhostUrl(imagenPrincipal ?: ""),

            // Campos adicionales
            totalPlazas = totalPlazas ?: 0,
            telefono = telefono,
            descripcion = descripcion,
            horarioApertura = horarioApertura,
            horarioCierre = horarioCierre,
            nivelSeguridadDesc = nivelSeguridad,
            servicios = servicios ?: emptyList(),
            aprobado = aprobado ?: true,
            activo = activo ?: false,
            coordenadas = coordenadas
        )
    }

    private fun parseCoordinates(coordenadas: String?): Pair<Double, Double> {
        return try {
            if (!coordenadas.isNullOrEmpty() && coordenadas.contains(",")) {
                val parts = coordenadas.split(",")
                val lat = parts[0].trim().toDouble()
                val lng = parts[1].trim().toDouble()
                lat to lng
            } else {
                -8.111667 to -79.028889
            }
        } catch (e: Exception) {
            -8.111667 to -79.028889
        }
    }

    private fun formatPrice(tarifa: Double?): String {
        return if (tarifa != null && tarifa > 0) {
            "S/ ${"%.2f".format(tarifa)}"
        } else {
            "S/ 0.00"
        }
    }

    private fun parseSecurityLevel(nivel: String?): Int {
        return when (nivel?.lowercase()?.trim()) {
            "básico", "basico", "baja", "low", "1" -> 1
            "estándar", "estandar", "media", "medium", "2" -> 2
            "alto", "high", "3" -> 3
            else -> 1
        }
    }

    private fun hasCameras(nivel: String?): Boolean = parseSecurityLevel(nivel) >= 2
    private fun has24hSurveillance(nivel: String?): Boolean = parseSecurityLevel(nivel) >= 3

    private fun getDefaultImage(): String {
        return "https://via.placeholder.com/400x300/2196F3/FFFFFF?text=Parking"
    }

    // ✅ NUEVO: Función para corregir localhost en emulador
    private fun fixLocalhostUrl(url: String?): String {
        if (url.isNullOrEmpty()) return getDefaultImage()

        // Reemplazar localhost por 10.0.2.2 para emulador
        return if (url.contains("localhost")) {
            url.replace("http://localhost:8000", "http://10.0.2.2:8000")
        } else {
            url
        }
    }
}

// Modelo para imágenes del backend - CORREGIDO
data class ParkingImageResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("imagen") val imagen: String?,
    @SerializedName("imagen_url") val imagenUrl: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("creado_en") val creadoEn: String?
) {
    fun toParkingImage(): ParkingImage {
        return ParkingImage(
            id = id,
            imagen = imagen ?: "",

            imagenUrl = fixLocalhostUrl(imagenUrl ?: imagen ?: ""),
            descripcion = descripcion,
            creadoEn = creadoEn,
            esPrincipal = false
        )
    }


    private fun fixLocalhostUrl(url: String): String {
        // Reemplazar localhost por 10.0.2.2 para emulador
        return if (url.contains("localhost")) {
            url.replace("http://localhost:8000", "http://10.0.2.2:8000")
        } else {
            url
        }
    }
}

// Response para endpoint /mapa/
data class ParkingMapaResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<ParkingSpotResponse>
)