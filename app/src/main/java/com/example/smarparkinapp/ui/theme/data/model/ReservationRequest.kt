package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ReservationRequest(
    @SerializedName("estacionamiento")
    val estacionamientoId: Long,

    @SerializedName("vehiculo")
    val vehiculoId: Int,

    @SerializedName("hora_entrada")
    val horaEntrada: String,

    @SerializedName("hora_salida")
    val horaSalida: String,

    @SerializedName("tipo_reserva")
    val tipoReserva: String, // "hora" o "dia"

    @SerializedName("duracion_minutos")
    val duracionMinutos: Int
)