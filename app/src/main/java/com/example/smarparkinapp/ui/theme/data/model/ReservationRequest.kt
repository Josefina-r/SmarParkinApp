package com.example.smarparkinapp.data.model

import com.google.gson.annotations.SerializedName


import com.example.smarparkinapp.ui.theme.data.model.ParkingLot

data class ReservationRequest(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("codigo")
    val codigo: String = "",

    // CORRECCIÓN: Coincide con el nombre del campo en el serializer de Django
    @SerializedName("estacionamiento_detalle") // O el nombre que uses para el objeto anidado
    val parking: ParkingLot?,

    // CORRECCIÓN: Coincide con el nombre del campo en el serializer de Django
    @SerializedName("vehiculo_detalle") // O el nombre que uses para el objeto anidado
    val car: Car?,

    @SerializedName("fecha")
    val fecha: String = "",

    // CORRECCIÓN: Nombres de campo del JSON
    @SerializedName("hora_inicio")
    val horaInicio: String = "",

    @SerializedName("hora_fin")
    val horaFin: String = "",

    @SerializedName("tipo")
    val tipo: String = "normal",

    @SerializedName("estado")
    val estado: String = "pendiente",

    // CORRECCIÓN: "total" es un nombre más probable en el serializer
    @SerializedName("total")
    val precio: Double = 0.0,

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = ""
)
