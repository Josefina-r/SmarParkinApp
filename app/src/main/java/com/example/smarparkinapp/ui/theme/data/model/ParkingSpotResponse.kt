package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ParkingSpotResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val name: String,

    @SerializedName("descripcion")
    val description: String? = null,  // ← Puede ser null

    @SerializedName("direccion")
    val address: String,

    @SerializedName("ciudad")
    val city: String? = null,  // ← Puede ser null

    @SerializedName("latitud")
    val latitude: Double,

    @SerializedName("longitud")
    val longitude: Double,

    @SerializedName("total_plazas")
    val totalSpots: Int,

    @SerializedName("plazas_disponibles")
    val availableSpots: Int,

    @SerializedName("tarifa_horaria")
    val hourlyRate: String,

    @SerializedName("tarifa_diaria")
    val dailyRate: String? = null,  // ← Puede ser null

    @SerializedName("nivel_seguridad")
    val securityLevel: String? = null,  // ← IMPORTANTE: Puede ser null

    @SerializedName("calificacion_promedio")
    val averageRating: Double? = null,  // ← Puede ser null

    @SerializedName("numero_resenas")
    val reviewCount: Int,

    @SerializedName("activo")
    val active: Boolean,

    @SerializedName("aprobado")
    val approved: Boolean,

    @SerializedName("imagen_principal")
    val mainImage: String? = null,

    @SerializedName("horario_apertura")
    val openingTime: String? = null,  // ← Puede ser null

    @SerializedName("horario_cierre")
    val closingTime: String? = null,  // ← Puede ser null

    @SerializedName("acepta_tarjeta")
    val acceptsCard: Boolean,

    @SerializedName("acepta_efectivo")
    val acceptsCash: Boolean,
    @SerializedName("amenidades")  // ← El JSON viene como "amenidades"
    val amenidades: String? = null,
)
