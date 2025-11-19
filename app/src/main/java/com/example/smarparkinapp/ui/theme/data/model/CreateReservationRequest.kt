package com.example.smarparkinapp.ui.theme.data.model
import com.google.gson.annotations.SerializedName

data class CreateReservationRequest(
    @SerializedName("estacionamiento") val parkingId: Int,
    @SerializedName("vehiculo") val vehicleId: Int,
    @SerializedName("hora_entrada") val startTime: String,
    @SerializedName("hora_salida") val endTime: String,
    @SerializedName("tipo") val type: String = "normal"
)