package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("reserva")
    val reservaId: Long,

    @SerializedName("metodo")
    val metodo: String, // tarjeta, yape, plin, efectivo

    @SerializedName("token_pago")
    val tokenPago: String? = null,

    @SerializedName("monto")
    val monto: Double? = null,

    @SerializedName("moneda")
    val moneda: String = "PEN",

    @SerializedName("datos_metodo")
    val datosMetodo: Map<String, Any>? = null
) {
    companion object {
        fun forTarjeta(reservaId: Long, tokenPago: String, monto: Double): PaymentRequest {
            return PaymentRequest(
                reservaId = reservaId,
                metodo = "tarjeta",
                tokenPago = tokenPago,
                monto = monto
            )
        }

        fun forYape(reservaId: Long, monto: Double): PaymentRequest {
            return PaymentRequest(
                reservaId = reservaId,
                metodo = "yape",
                monto = monto
            )
        }

        fun forPlin(reservaId: Long, monto: Double): PaymentRequest {
            return PaymentRequest(
                reservaId = reservaId,
                metodo = "plin",
                monto = monto
            )
        }

        fun forEfectivo(reservaId: Long, monto: Double): PaymentRequest {
            return PaymentRequest(
                reservaId = reservaId,
                metodo = "efectivo",
                monto = monto
            )
        }
    }
}