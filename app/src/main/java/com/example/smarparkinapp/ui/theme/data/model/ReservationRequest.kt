package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ReservationRequest(
    @SerializedName("estacionamiento")
    val estacionamiento: Long,

    @SerializedName("vehiculo")
    val vehiculo: Long,  // ✅ CAMBIADO de Int a Long

    @SerializedName("hora_entrada")
    val horaEntrada: String,

    @SerializedName("hora_salida")
    val horaSalida: String,

    @SerializedName("tipo_reserva")
    val tipoReserva: String,

    @SerializedName("duracion_minutos")
    val duracionMinutos: Int
)