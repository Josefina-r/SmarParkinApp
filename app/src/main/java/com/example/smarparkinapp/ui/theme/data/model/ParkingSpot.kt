// ParkingSpot.kt - VERSIÓN COMPLETA CON TODOS LOS CAMPOS
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

    // ✅ IMAGEN PRINCIPAL (para compatibilidad)
    val imagenUrl: String = "",

    // ✅ NUEVO: Campos para imágenes del backend
    val imagenes: List<ParkingImage> = emptyList(),
    val imagenPrincipal: String? = null,

    // ✅ NUEVO: Campos adicionales del backend
    val totalPlazas: Int = 0,
    val telefono: String? = null,
    val descripcion: String? = null,
    val horarioApertura: String? = null,
    val horarioCierre: String? = null,
    val nivelSeguridadDesc: String? = null,
    val servicios: List<String> = emptyList(),
    val aprobado: Boolean = true,

    val activo: Boolean = true,
    val coordenadas: String? = null
)

// ✅ Modelo para imágenes (debe estar en el MISMO archivo o importado)
data class ParkingImage(
    val id: Int,
    val imagen: String, // Ruta relativa
    val imagenUrl: String, // URL completa
    val descripcion: String?,
    val creadoEn: String?,
    val esPrincipal: Boolean = false
)