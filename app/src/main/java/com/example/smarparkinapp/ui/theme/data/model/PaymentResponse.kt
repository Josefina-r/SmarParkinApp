package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("referencia_pago")
    val referenciaPago: String,

    @SerializedName("reserva")
    val reserva: ReservationResponse?,

    @SerializedName("usuario")
    val usuario: UserShortResponse?,

    @SerializedName("monto")
    val monto: Double,

    @SerializedName("moneda")
    val moneda: String,

    @SerializedName("metodo")
    val metodo: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("id_transaccion")
    val idTransaccion: String?,

    @SerializedName("datos_gateway")
    val datosGateway: Map<String, Any>?,

    @SerializedName("comision_plataforma")
    val comisionPlataforma: Double,

    @SerializedName("monto_propietario")
    val montoPropietario: Double,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String,

    @SerializedName("fecha_pago")
    val fechaPago: String?,

    @SerializedName("fecha_reembolso")
    val fechaReembolso: String?,

    @SerializedName("intentos")
    val intentos: Int,

    @SerializedName("ultimo_error")
    val ultimoError: String?
)


data class RefundRequest(
    @SerializedName("pago_id")
    val pagoId: String,

    @SerializedName("monto_parcial")
    val montoParcial: Double? = null,

    @SerializedName("motivo")
    val motivo: String? = null
)
data class RefundResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("pago_id")
    val pagoId: String,

    @SerializedName("monto_reembolsado")
    val montoReembolsado: Double,

    @SerializedName("moneda")
    val moneda: String,

    @SerializedName("estado")
    val estado: String, // procesando, completado, fallido

    @SerializedName("referencia_reembolso")
    val referenciaReembolso: String,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String,

    @SerializedName("fecha_completado")
    val fechaCompletado: String?,

    @SerializedName("motivo")
    val motivo: String?
)


data class PaymentHistory(
    @SerializedName("id")
    val id: Long,

    @SerializedName("payment_id")
    val paymentId: String,

    @SerializedName("estado_anterior")
    val estadoAnterior: String,

    @SerializedName("estado_nuevo")
    val estadoNuevo: String,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("datos_adicionales")
    val datosAdicionales: Map<String, Any>?,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String,

    @SerializedName("ip_address")
    val ipAddress: String?
)
