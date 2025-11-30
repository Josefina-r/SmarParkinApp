package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class Car(
    @SerializedName("id")
    val id: Long = 0L,  // ✅ CAMBIADO de Int a Long

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
    val userId: Long? = null,  // ✅ CAMBIADO de Int? a Long?

    @SerializedName("fecha_creacion")
    val fechaCreacion: String? = null,

    @SerializedName("fecha_actualizacion")
    val fechaActualizacion: String? = null

) {
    // Función auxiliar para mostrar información del vehículo
    fun getDisplayInfo(): String {
        return "$brand $model - $plate"
    }
}