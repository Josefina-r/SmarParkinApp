// PaymentApiService.kt
/*package com.example.smarparkinapp.data.remote

import com.example.smarparkinapp.data.model.PaymentRequest
import com.example.smarparkinapp.data.model.PaymentResponse
import com.example.smarparkinapp.data.model.PaymentStatusResponse
import com.example.smarparkinapp.data.model.PaymentVerificationRequest
import retrofit2.Response
import retrofit2.http.*

interface PaymentApiService {

    @POST("payments/")
    suspend fun createPayment(@Body request: PaymentRequest): Response<PaymentResponse>

    @GET("payments/{paymentId}/")
    suspend fun getPaymentStatus(@Path("paymentId") paymentId: Int): Response<PaymentStatusResponse>

    @POST("payments/{paymentId}/process/")
    suspend fun verifyPayment(@Path("paymentId") paymentId: Int): Response<PaymentResponse>

    @POST("payments/{paymentId}/refund/")
    suspend fun refundPayment(@Path("paymentId") paymentId: Int): Response<PaymentResponse>

    // Para Yape específicamente
    @GET("payments/yape/qr/")
    suspend fun generateYapeQr(
        @Query("amount") amount: Double,
        @Query("reservation_id") reservationId: Int
    ): Response<PaymentResponse>

    // Para Plin específicamente
    @GET("payments/plin/qr/")
    suspend fun generatePlinQr(
        @Query("amount") amount: Double,
        @Query("reservation_id") reservationId: Int
    ): Response<PaymentResponse>
}*/