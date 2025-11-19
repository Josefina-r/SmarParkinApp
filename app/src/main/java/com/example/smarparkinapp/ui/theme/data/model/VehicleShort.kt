// VehicleShort.kt
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class VehicleShort(
    @SerializedName("id")
    val id: Int,

    @SerializedName("marca")
    val brand: String,

    @SerializedName("modelo")
    val model: String,

    @SerializedName("placa")
    val licensePlate: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("tipo")
    val type: String
)