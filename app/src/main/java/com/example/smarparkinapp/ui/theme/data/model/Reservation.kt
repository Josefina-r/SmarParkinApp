package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class Reservation(
    val id: Int,
    @SerializedName("codigo_reserva") val codigo: String,
    @SerializedName("parking_lot") val parkingLot: ParkingLot?,
    @SerializedName("car") val car: Car?,
    @SerializedName("hora_inicio") val horaInicio: String,
    @SerializedName("hora_fin") val horaFin: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("tipo_reserva") val tipoReserva: String,
    @SerializedName("monto_total") val montoTotal: Double?
)
