package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class TicketResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("codigo_ticket")
    val codigoTicket: String,

    @SerializedName("reserva")
    val reservaId: Int,

    @SerializedName("qr_image")
    val qrImageUrl: String?,

    @SerializedName("qr_data")
    val qrData: String?,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fecha_emision")
    val fechaEmision: String,

    @SerializedName("fecha_validez_desde")
    val fechaValidezDesde: String?,

    @SerializedName("fecha_validez_hasta")
    val fechaValidezHasta: String?,

    @SerializedName("fecha_validacion")
    val fechaValidacion: String?,

    @SerializedName("fecha_expiracion")
    val fechaExpiracion: String?,

    @SerializedName("validado_por")
    val validadoPor: Int?,

    @SerializedName("intentos_validacion")
    val intentosValidacion: Int,

    @SerializedName("ultimo_error")
    val ultimoError: String?
)
