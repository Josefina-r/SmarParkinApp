// En tu data/model/CarResponse.kt
package com.example.smarparkinapp.ui.theme.data.model

data class CarResponse(
    val id: Int,
    val placa: String,
    val marca: String,
    val modelo: String,
    val color: String,
    val activo: Boolean = true,
    val usuario: Int? = null,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null
)