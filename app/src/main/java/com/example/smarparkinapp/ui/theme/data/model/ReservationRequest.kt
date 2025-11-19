package com.example.smarparkinapp.data.model

import com.google.gson.annotations.SerializedName

data class CreateReservationRequest(
    @SerializedName("parking_lot") val parkingLot: Int,
    @SerializedName("car") val car: Int,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String,
    @SerializedName("tipo_reserva") val tipoReserva: String = "normal"
)

data class ExtendReservationRequest(
    @SerializedName("nueva_hora_fin") val nuevaHoraFin: String
)