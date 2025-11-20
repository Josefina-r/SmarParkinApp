package com.example.smarparkinapp.data.model

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("reserva")
    val reservationId: Long,

    @SerializedName("monto")
    val amount: Double,

    @SerializedName("metodo_pago")
    val paymentMethod: String
)

data class PaymentResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("transaction_id")
    val transactionId: String?,

    @SerializedName("fecha_pago")
    val fechaPago: String? = null
)