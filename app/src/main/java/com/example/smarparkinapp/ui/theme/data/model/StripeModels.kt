package com.example.smarparkinapp.model

data class StripePaymentRequest(
    val amount: Long, // en centavos
    val currency: String = "pen",
    val payment_method: String? = null,
    val confirm: Boolean = true,
    val return_url: String = "smartparking://stripe_return"
)

data class StripePaymentIntent(
    val id: String,
    val client_secret: String,
    val status: String,
    val amount: Long,
    val currency: String
)

data class StripePaymentResponse(
    val success: Boolean,
    val paymentIntent: StripePaymentIntent? = null,
    val error: String? = null
)

data class CardDetails(
    val number: String,
    val expMonth: Int,
    val expYear: Int,
    val cvc: String,
    val cardholderName: String
)

// Resultado del pago con Stripe
data class StripePaymentResult(
    val success: Boolean,
    val transactionId: String = "",
    val paymentMethod: String = "stripe",
    val amount: Double = 0.0,
    val error: String? = null
)