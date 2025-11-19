// ParkingShort.kt
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ParkingShort(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val name: String,

    @SerializedName("direccion")
    val address: String? = null,

    @SerializedName("precio_hora")
    val pricePerHour: Double? = null
)