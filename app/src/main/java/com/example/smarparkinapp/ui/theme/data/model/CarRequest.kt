
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class CarRequest(
    @SerializedName("placa")
    val placa: String,

    @SerializedName("marca")
    val marca: String,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("color")
    val color: String
)