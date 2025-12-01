package com.example.smarparkinapp.repository

import com.example.smarparkinapp.data.config.StripeConfig
import com.example.smarparkinapp.model.CardDetails
import com.example.smarparkinapp.model.StripePaymentRequest
import com.example.smarparkinapp.model.StripePaymentResponse
import com.example.smarparkinapp.model.StripePaymentResult
import com.example.smarparkinapp.network.StripeService
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StripeRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.stripe.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(StripeService::class.java)

    suspend fun createPaymentIntent(amount: Double): StripePaymentResponse {
        return try {
            val amountInCents = (amount * 100).toLong()
            val request = StripePaymentRequest(amount = amountInCents)

            val response = service.createPaymentIntent(request)
            StripePaymentResponse(success = true, paymentIntent = response)
        } catch (e: Exception) {
            StripePaymentResponse(success = false, error = e.message)
        }
    }

    // FUNCIÓN CORREGIDA - Solo recibe CardDetails y amount
    suspend fun processStripePayment(
        cardDetails: CardDetails,  // Primer parámetro: CardDetails
        amount: Double             // Segundo parámetro: Double
    ): StripePaymentResult {
        return try {
            // 1. Crear Payment Intent en Stripe
            val paymentIntent = createPaymentIntent(amount)

            if (!paymentIntent.success) {
                return StripePaymentResult(
                    success = false,
                    error = paymentIntent.error ?: "Error al crear payment intent"
                )
            }

            // 2. Simular procesamiento de pago
            delay(2000L)

            // 3. Verificar si es tarjeta de prueba
            val cleanCardNumber = cardDetails.number.replace(" ", "")
            val isTestCard = cleanCardNumber in listOf(
                StripeConfig.TestCards.VISA_SUCCESS,
                StripeConfig.TestCards.MASTERCARD_SUCCESS
            )

            if (isTestCard) {
                StripePaymentResult(
                    success = true,
                    transactionId = paymentIntent.paymentIntent?.id ?: generateStripeId(),
                    paymentMethod = "stripe",
                    amount = amount
                )
            } else {
                StripePaymentResult(
                    success = false,
                    error = "Para testing usa: 4242 4242 4242 4242"
                )
            }
        } catch (e: Exception) {
            StripePaymentResult(success = false, error = e.message)
        }
    }

    private fun generateStripeId(): String {
        return "pi_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}