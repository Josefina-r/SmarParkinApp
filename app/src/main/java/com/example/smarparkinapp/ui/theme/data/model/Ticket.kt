package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("id")
    val id: String = "", // UUID

    @SerializedName("codigo_ticket")
    val codigoTicket: String = "",

    @SerializedName("reserva")
    val reserva: Reservation? = null,

    @SerializedName("qr_image_url")
    val qrImageUrl: String? = null,

    @SerializedName("estado")
    val estado: String = "valido", // valido, usado, expirado, cancelado

    @SerializedName("fecha_emision")
    val fechaEmision: String = "",

    @SerializedName("fecha_validez_desde")
    val fechaValidezDesde: String? = null,

    @SerializedName("fecha_validez_hasta")
    val fechaValidezHasta: String? = null,

    @SerializedName("fecha_validacion")
    val fechaValidacion: String? = null,

    @SerializedName("validado_por")
    val validadoPor: UserShort? = null,

    // Campos calculados
    @SerializedName("puede_validar")
    val puedeValidar: PuedeValidar? = null,

    @SerializedName("tiempo_restante")
    val tiempoRestante: Int? = null,

    @SerializedName("qr_data_json")
    val qrDataJson: Map<String, Any>? = null
)

data class PuedeValidar(
    @SerializedName("puede")
    val puede: Boolean,

    @SerializedName("mensaje")
    val mensaje: String
)

data class TicketValidationRequest(
    @SerializedName("codigo_ticket")
    val codigoTicket: String? = null,

    @SerializedName("qr_data")
    val qrData: String? = null
)
data class TicketRequest(
    @SerializedName("reserva")
    val reservaId: Long,

    @SerializedName("tipo")
    val tipo: String, // "entrada" o "salida"

    @SerializedName("generar_qr")
    val generarQr: Boolean = true
)
