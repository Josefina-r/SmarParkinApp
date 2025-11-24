// data/repository/PaymentRepository.kt
package com.example.smarparkinapp.data.repository

import android.content.Context
import com.example.smarparkinapp.data.model.PaymentRequest
import com.example.smarparkinapp.data.model.PaymentResponse
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
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
                println("🔍 [PaymentRepository] Procesando pago...")
                println("   💰 Reservation ID: $reservationId")
                println("   💵 Amount: $amount")
                println("   💳 Method: $paymentMethod")

                // Mapear métodos de pago a los que espera Django
                val metodoBackend = when (paymentMethod.lowercase()) {
                    "yape" -> "yape"
                    "plin" -> "plin"
                    "tarjeta de crédito", "credito" -> "tarjeta_credito"
                    "tarjeta de débito", "debito" -> "tarjeta_debito"
                    "efectivo" -> "efectivo"
                    else -> "efectivo" // Por defecto
                }

                // Crear el PaymentRequest correctamente
                val request = PaymentRequest(
                    reservationId = reservationId,
                    amount = amount,
                    paymentMethod = metodoBackend
                )

                println("📤 [PaymentRepository] Enviando pago a API...")
                println("   📦 Request: reserva=${request.reservationId}, monto=${request.amount}, metodo=${request.paymentMethod}")

                val response = apiService.createPayment(request)

                if (response.isSuccessful && response.body() != null) {
                    val paymentResponse = response.body()!!
                    println("✅ [PaymentRepository] Pago creado exitosamente:")
                    println("   🆔 ID: ${paymentResponse.id}")
                    println("   📊 Estado: ${paymentResponse.estado}")
                    println("   💳 Transaction: ${paymentResponse.transactionId}")

                    Result.success(paymentResponse)
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    println("❌ [PaymentRepository] Error HTTP $errorCode: $errorBody")

                    val errorMessage = when (errorCode) {
                        400 -> "Datos de pago inválidos"
                        401 -> "No autorizado - token inválido"
                        403 -> "No tienes permisos para realizar este pago"
                        404 -> "Reserva no encontrada"
                        else -> "Error del servidor: $errorCode"
                    }

                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                println("❌ [PaymentRepository] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    suspend fun getPaymentStatus(paymentId: Long): Result<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [PaymentRepository] Consultando estado de pago: $paymentId")
                val response = apiService.getPayment(paymentId)

                if (response.isSuccessful && response.body() != null) {
                    val payment = response.body()!!
                    println("✅ [PaymentRepository] Estado obtenido: ${payment.estado}")
                    Result.success(payment)
                } else {
                    val errorCode = response.code()
                    val errorMessage = when (errorCode) {
                        404 -> "Pago no encontrado"
                        else -> "Error al obtener estado del pago: $errorCode"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                println("❌ [PaymentRepository] Error consultando pago: ${e.message}")
                Result.failure(Exception("Error al consultar estado del pago: ${e.message}"))
            }
        }
    }

    suspend fun confirmPendingPayment(paymentId: Long): Result<PaymentResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [PaymentRepository] Confirmando pago pendiente: $paymentId")
                val response = apiService.processPayment(paymentId)

                if (response.isSuccessful && response.body() != null) {
                    val payment = response.body()!!
                    println("✅ [PaymentRepository] Pago confirmado: ${payment.estado}")
                    Result.success(payment)
                } else {
                    val errorCode = response.code()
                    Result.failure(Exception("Error al confirmar el pago: $errorCode"))
                }
            } catch (e: Exception) {
                println("❌ [PaymentRepository] Error confirmando pago: ${e.message}")
                Result.failure(Exception("Error al confirmar el pago: ${e.message}"))
            }
        }
    }

    suspend fun getPendingPayments(): Result<List<PaymentResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [PaymentRepository] Obteniendo pagos pendientes...")
                val response = apiService.getPendingPayments()

                if (response.isSuccessful && response.body() != null) {
                    val payments = response.body()!!
                    println("✅ [PaymentRepository] ${payments.size} pagos pendientes obtenidos")
                    Result.success(payments)
                } else {
                    Result.failure(Exception("Error al obtener pagos pendientes: ${response.code()}"))
                }
            } catch (e: Exception) {
                println("❌ [PaymentRepository] Error obteniendo pagos pendientes: ${e.message}")
                Result.failure(Exception("Error al obtener pagos pendientes: ${e.message}"))
            }
        }
    }
}