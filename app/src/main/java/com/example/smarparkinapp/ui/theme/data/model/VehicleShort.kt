package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class VehicleShort(
    @SerializedName("id")
    val id: Long,  // ✅ CAMBIADO de Int a Long

    @SerializedName("placa")
    val placa: String,

    @SerializedName("marca")
    val marca: String,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("tipo_vehiculo")
    val tipoVehiculo: String? = null
)