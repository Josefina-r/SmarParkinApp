// Reservation.kt
package com.example.smarparkinapp.data.model

import com.google.gson.annotations.SerializedName
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
data class Reservation(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("codigo")
    val codigo: String = "",

    @SerializedName("estacionamiento")
    val parking: ParkingLot? = null,

    @SerializedName("vehiculo")
    val car: Car? = null,

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