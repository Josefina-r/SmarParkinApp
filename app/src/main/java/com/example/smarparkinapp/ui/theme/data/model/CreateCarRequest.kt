package com.example.smarparkinapp.ui.theme.data.model


import com.google.gson.annotations.SerializedName

// Para crear un nuevo vehículo (POST)
data class CreateCarRequest(
    @SerializedName("placa")
    val placa: String,

    @SerializedName("marca")
    val marca: String,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("color")
    val color: String
)

// Para actualizar un vehículo (PUT/PATCH)
data class UpdateCarRequest(
    @SerializedName("placa")
    val placa: String? = null,

    @SerializedName("marca")
    val marca: String? = null,

    @SerializedName("modelo")
    val modelo: String? = null,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("activo")
    val activo: Boolean? = null
)