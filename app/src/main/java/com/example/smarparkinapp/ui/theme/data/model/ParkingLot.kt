package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ParkingLot(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("direccion")
    val direccion: String,

    @SerializedName("coordenadas")
    val coordenadas: String?,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("horario_apertura")
    val horario_apertura: String?,

    @SerializedName("horario_cierre")
    val horario_cierre: String?,

    @SerializedName("nivel_seguridad")
    val nivel_seguridad: String?,

    @SerializedName("tarifa_hora")
    val tarifa_hora: Double,

    @SerializedName("total_plazas")
    val total_plazas: Int,

    @SerializedName("plazas_disponibles")
    val plazas_disponibles: Int,

    @SerializedName("rating_promedio")
    val rating_promedio: Double?,

    @SerializedName("total_resenas")
    val total_resenas: Int?,

    @SerializedName("aprobado")
    val aprobado: Boolean,

    @SerializedName("activo")
    val activo: Boolean,

    @SerializedName("dueno")
    val dueno: Long?,

    @SerializedName("esta_abierto")
    val esta_abierto: Boolean?,

    @SerializedName("imagen_principal")
    val imagen_principal: String?,

    @SerializedName("dueno_nombre")
    val dueno_nombre: String?
)

data class ParkingLotResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("results")
    val results: List<ParkingLot>
)

