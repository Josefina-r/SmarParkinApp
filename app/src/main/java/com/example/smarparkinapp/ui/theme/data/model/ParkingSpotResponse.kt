package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

// ✅ RESPONSE PARA ENDPOINT NORMAL
data class ParkingSpotResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("precio_hora") val precioHora: String,
    @SerializedName("plazas_disponibles") val plazasDisponibles: Int,
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,

    // ✅ NUEVOS CAMPOS (nullable para compatibilidad)
    @SerializedName("nivel_seguridad") val nivelSeguridad: Int? = 1,
    @SerializedName("rating_promedio") val ratingPromedio: Double? = 0.0,
    @SerializedName("total_reseñas") val totalResenas: Int? = 0,
    @SerializedName("esta_abierto") val estaAbierto: Boolean? = true,
    @SerializedName("tiene_camaras") val tieneCamaras: Boolean? = false,
    @SerializedName("tiene_vigilancia_24h") val tieneVigilancia24h: Boolean? = false,
    @SerializedName("distancia_km") val distanciaKm: Double? = null
) {
    fun toParkingSpot(): ParkingSpot {
        return ParkingSpot(
            id = id,
            name = nombre,
            address = direccion,
            price = precioHora,
            availableSpots = plazasDisponibles,
            latitude = latitud,
            longitude = longitud,
            nivelSeguridad = nivelSeguridad ?: 1,
            ratingPromedio = ratingPromedio ?: 0.0,
            totalResenas = totalResenas ?: 0,
            estaAbierto = estaAbierto ?: true,
            tieneCamaras = tieneCamaras ?: false,
            tieneVigilancia24h = tieneVigilancia24h ?: false,
            distanciaKm = distanciaKm
        )
    }
}

// ✅ RESPONSE PARA ENDPOINT /mapa/
data class ParkingMapaResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("results") val results: List<ParkingSpotResponse>
)