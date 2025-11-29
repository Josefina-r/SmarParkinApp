package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class Car(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("placa")
    val plate: String,

    @SerializedName("marca")
    val brand: String,

    @SerializedName("modelo")
    val model: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("activo")
    val active: Boolean = true,

    @SerializedName("usuario")
    val userId: Int? = null,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String? = null,

    @SerializedName("fecha_actualizacion")
    val fechaActualizacion: String? = null

)
