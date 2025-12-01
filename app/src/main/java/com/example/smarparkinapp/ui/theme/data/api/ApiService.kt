// ui/theme/data/api/ApiService.kt
package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import com.google.gson.annotations.SerializedName
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingReviewsResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.model.ParkingReview
import com.example.smarparkinapp.ui.theme.data.model.CreateReviewRequest
import com.example.smarparkinapp.ui.theme.data.model.Payment
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import com.example.smarparkinapp.ui.theme.data.model.ReservationRequest
import com.example.smarparkinapp.ui.theme.data.model.ReservationResponse
import com.example.smarparkinapp.ui.theme.data.model.PaymentRequest
import com.example.smarparkinapp.ui.theme.data.model.TicketResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // AUTENTICACIÓN (sin auth header - se maneja en el interceptor)
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/auth/register/client/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/users/password/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    @PUT("api/users/change-password/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<GenericResponse>

    @GET("api/parkings/")
    suspend fun getApprovedParkingLots(): Response<ParkingLotResponse>

    @GET("api/parking/")
    suspend fun searchParkingLots(@Query("search") query: String): Response<ParkingLotResponse>

    @GET("api/parkings/cerca/")
    suspend fun getNearbyParkingLots(
        @Query("lat") latitud: Double,
        @Query("lng") longitud: Double
    ): Response<List<ParkingLot>>

    @GET("api/parkings/mejores_calificados/")
    suspend fun getTopRatedParkingLots(): Response<List<ParkingLot>>

    @GET("api/parkings/mas_economicos/")
    suspend fun getMasEconomicos(): Response<List<ParkingLot>>

    @GET("api/parking/{id}/")
    suspend fun getParkingById(@Path("id") parkingId: Long): Response<ParkingLot>

    @GET("api/vehicles/")
    suspend fun getUserVehicles(): Response<PaginatedResponse<CarResponse>>

    @POST("api/vehicles/")
    suspend fun addCar(@Body car: CarRequest): Response<CarResponse>

    @PUT("api/vehicles/{id}/")
    suspend fun updateVehicle(
        @Path("id") vehicleId: Int,
        @Body car: CarRequest
    ): Response<CarResponse>

    @DELETE("api/vehicles/{id}/")
    suspend fun deleteVehicle(@Path("id") vehicleId: Int): Response<GenericResponse>

    // Reservas
    @POST("api/reservation/")
    suspend fun createReservation(@Body request: ReservationRequest): Response<ReservationResponse>

    @GET("api/reservations/mis-reservas/")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

    @POST("api/reservations/{codigo}/cancel/")
    suspend fun cancelReservation(@Path("codigo") codigo: String): Response<GenericResponse>

    @GET("api/reservations/activas/")
    suspend fun getActiveReservations(): Response<List<ReservationResponse>>

    // Pagos
    @POST("api/payments/")
    suspend fun createPayment(@Body request: PaymentRequest): Response<JsonObject>

    @POST("api/payments/{id}/process/")
    suspend fun processPayment(@Path("id") paymentId: String): Response<Payment>

    // Tickets
    @GET("api/tickets/validos/")
    suspend fun getValidTickets(): Response<List<TicketResponse>>

    @GET("api/tickets/reserva/{reservationId}/")
    suspend fun getTicketByReservation(@Path("reservationId") reservationId: Long): Response<TicketResponse>

    // PARKING SPOTS (sin auth header)
    @GET("api/parking/spots/")
    suspend fun getParkingSpots(): Response<List<ParkingSpotResponse>>

    @GET("api/parking/mapa/")
    suspend fun getParkingMapa(): Response<ParkingLotResponse>

    @GET("parking/reviews/")
    suspend fun obtenerReseñas(
        @Query("estacionamiento") estacionamientoId: Int
    ): Response<List<ParkingReview>>


    @GET("api/parkings/{parkingId}/reviews/")
    suspend fun getReviewsByParking(
        @Path("parkingId") parkingId: Int
    ): Response<ParkingReviewsResponse>

    // Crear una reseña
    @POST("api/reviews/")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): Response<ParkingReview>

    @GET("reviews/parking/{parking_id}/")
    suspend fun parkingReviewsPublic(
        @Path("parking_id") parkingId: Int
    ): Response<ParkingReviewsResponse>
    @GET("api/user/reviews/")
    suspend fun getUserReviews(): Response<List<ParkingReview>>



    // RUTAS PARA PERFIL
    @GET("api/users/profile/")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("api/users/profile/update/")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @GET("api/users/profile/")
    suspend fun getUserProfileCompat(): Response<UserProfileResponse>

    @PUT("api/users/profile/update/")
    suspend fun updateUserProfileCompat(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @GET("api/dashboard/stats/")
    suspend fun getDashboardStats(): Response<DashboardStatsResponse>
}

// MODELOS DE AUTENTICACIÓN
data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: UserResponse? = null
)

data class RefreshTokenRequest(val refresh: String)

data class RefreshTokenResponse(val access: String)

data class RegisterResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null
)

// MODELOS DE USUARIO
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val is_staff: Boolean? = false,
    val is_superuser: Boolean? = false
)

// MODELOS DE ESTACIONAMIENTOS
data class ParkingLotResponse(
    val count: Int? = 0,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ParkingLot> = emptyList()
)

data class ParkingSpotResponse(
    val id: Long,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val precio: String,
    val disponible: Boolean,
    val distancia: Double? = null,
    val rating: Double? = 0.0
)

// MODELOS DE PERFIL
data class UserProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val phone: String? = null,
    val address: String? = null,
    val profile_picture: String? = null
)

data class UpdateProfileRequest(
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null,
    val address: String? = null
)

// MODELOS DE DASHBOARD
data class DashboardStatsResponse(
    val total_reservations: Int,
    val active_reservations: Int,
    val favorite_parkings: Int,
    val total_spent: Double
)

// MODELOS DE RESPUESTA GENÉRICA
data class GenericResponse(
    val detail: String,
    val message: String? = null,
    val success: Boolean? = true
)

// MODELOS DE PAGINACIÓN
data class PaginatedResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

// MODELOS DE RESET PASSWORD
data class ResetPasswordRequest(
    val email: String
)

data class ResetPasswordResponse(
    val detail: String,
    val message: String? = null
)

// MODELOS DE CAMBIO DE CONTRASEÑA
data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String,
    val confirm_password: String? = null
)

// Modelos adicionales para respuestas
data class ReportReviewRequest(
    @SerializedName("motivo")
    val motivo: String
)