package com.example.smarparkinapp.data.model

import com.google.gson.annotations.SerializedName

data class Reservation(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("codigo")
    val codigo: String = "",

    @SerializedName("estacionamiento")
    val parking: ParkingShort? = null,

    @SerializedName("vehiculo")
    val vehicle: VehicleShort? = null,

    @SerializedName("fecha")
    val fecha: String = "",

    @SerializedName("hora_entrada")
    val horaInicio: String = "",

    @SerializedName("hora_salida")
    val horaFin: String = "",

    @SerializedName("tipo")
    val tipo: String = "normal",

    @SerializedName("estado")
    val estado: String = "pendiente",

    @SerializedName("total")
    val precio: Double = 0.0,

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = ""
)

// Modelos auxiliares para las respuestas
data class VehicleShort(
    @SerializedName("id")
    val id: Int,

    @SerializedName("license_plate")
    val licensePlate: String,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("model")
    val model: String
)

data class ParkingShort(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("direccion")
    val direccion: String? = null,

    @SerializedName("tarifa_hora")
    val tarifaHora: Double? = null
)