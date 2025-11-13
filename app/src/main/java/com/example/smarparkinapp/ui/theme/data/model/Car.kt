package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

// ✅ SOLO UNA DEFINICIÓN de Car
data class Car(
    @SerializedName("id") val id: Int,
    @SerializedName("placa") val placa: String,
    @SerializedName("marca") val marca: String? = null,
    @SerializedName("modelo") val modelo: String? = null,
    @SerializedName("tipo") val tipo: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class CarRequest(
    @SerializedName("placa") val placa: String,
    @SerializedName("modelo") val modelo: String,
    @SerializedName("tipo") val tipo: String = "auto",
    @SerializedName("color") val color: String
)

data class CarResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("usuario") val usuarioId: Int,
    @SerializedName("placa") val placa: String,
    @SerializedName("modelo") val modelo: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("color") val color: String,
    @SerializedName("created_at") val createdAt: String
)