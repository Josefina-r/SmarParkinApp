// data/repository/PaymentRepository.kt
package com.example.smarparkinapp.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.api.PaymentRequest
import com.example.smarparkinapp.ui.theme.data.api.PaymentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaymentRepository(private val context: Context) {

    private val apiService by lazy {
        RetrofitInstance.getAuthenticatedApiService(context)
    }

    suspend fun processPayment(
        reservationId: Long,
        amount: Double,
        paymentMethod: String
    ): Result<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = PaymentRequest(
                    reserva = reservationId,
                    monto = amount,
                    metodo_pago = paymentMethod
                )

                val authManager = com.example.smarparkinapp.ui.theme.data.AuthManager(context)
                val token = authManager.getAuthToken()

                if (token == null) {
                    Result.failure(Exception("No authentication token available"))
                } else {
                    val response = apiService.processPayment(request, "Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        Result.success(response.body()!!)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        Result.failure(Exception("Error ${response.code()}: $errorBody"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}