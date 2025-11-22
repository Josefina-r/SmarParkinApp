// data/model/Car.kt
package com.example.smarparkinapp.data.model

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
    val active: Boolean = true
)