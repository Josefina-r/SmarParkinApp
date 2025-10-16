package com.example.smarparkinapp.ui.theme.data.model

data class CarResponse(
    val id: Int,
    val placa: String,
    val modelo: String,
    val tipo: String,
    val color: String,
    val usuario: Int,
    val created_at: String
)
