package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

data class TicketResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("codigo_ticket")
    val codigoTicket: String,

    @SerializedName("reserva")
    val reservaRaw: JsonElement? = null,

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
) {
    // AGREGA ESTAS PROPIEDADES CALCULADAS:
    val reservaId: Long?
        get() {
            return try {
                // Intenta obtener el ID del objeto reserva
                reservaRaw?.asJsonObject?.get("id")?.asLong
            } catch (e: Exception) {
                null
            }
        }

    val codigoReserva: String?
        get() {
            return try {
                reservaRaw?.asJsonObject?.get("codigo_reserva")?.asString
            } catch (e: Exception) {
                null
            }
        }

    val estadoReserva: String?
        get() {
            return try {
                reservaRaw?.asJsonObject?.get("estado")?.asString
            } catch (e: Exception) {
                null
            }
        }

    // Para obtener la reserva como objeto si la necesitas
    fun getReservaCompleta(): TicketReservaCompletaResponse? {
        return try {
            val gson = com.google.gson.Gson()
            gson.fromJson(reservaRaw, TicketReservaCompletaResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Método para debug
    fun printReservaInfo() {
        println("Reserva ID: $reservaId")
        println("Código Reserva: $codigoReserva")
        println("Estado Reserva: $estadoReserva")
    }
}

// Mantén tus otras clases como están...
data class TicketReservaResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("codigo_reserva")
    val codigoReserva: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("hora_entrada")
    val horaEntrada: String,

    @SerializedName("hora_salida")
    val horaSalida: String,

    @SerializedName("costo_estimado")
    val costoEstimado: String? = null
)

data class TicketReservaCompletaResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("codigo_reserva")
    val codigoReserva: String,

    @SerializedName("usuario")
    val usuario: UsuarioSimpleResponse? = null,

    @SerializedName("estacionamiento")
    val estacionamiento: EstacionamientoSimpleResponse? = null,

    @SerializedName("vehiculo")
    val vehiculo: VehiculoSimpleResponse? = null,

    @SerializedName("hora_entrada")
    val horaEntrada: String,

    @SerializedName("hora_salida")
    val horaSalida: String,

    @SerializedName("costo_estimado")
    val costoEstimado: String? = null,

    @SerializedName("estado")
    val estado: String
)

data class UsuarioSimpleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String
)

data class EstacionamientoSimpleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("direccion") val direccion: String
)

data class VehiculoSimpleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("placa") val placa: String,
    @SerializedName("modelo") val modelo: String
)