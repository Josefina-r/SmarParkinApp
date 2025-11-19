package com.example.smarparkinapp.ui.theme.data.model
import com.example.smarparkinapp.ui.theme.data.api.ParkingShort
import com.google.gson.annotations.SerializedName
data class ReservationResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("codigo") val code: String,
    @SerializedName("estacionamiento") val parking: ParkingShort,
    @SerializedName("vehiculo") val vehicle: VehicleShort,
    @SerializedName("hora_entrada") val startTime: String,
    @SerializedName("hora_salida") val endTime: String,
    @SerializedName("tipo") val type: String,
    @SerializedName("estado") val status: String,
    @SerializedName("total") val total: Double,
    @SerializedName("created_at") val createdAt: String
)