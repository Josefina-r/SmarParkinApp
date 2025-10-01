package com.example.smarparkinapp.ui.theme.data.model

data class Estacionamiento(
    val nombre: String,
    val direccion: String,
    val precioHora: Double,
    val horario: String,
    val amenidades: List<String>,
    val telefono: String
)