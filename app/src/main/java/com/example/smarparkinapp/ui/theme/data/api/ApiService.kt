// ui/theme/data/api/ApiService.kt
package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.data.model.PaymentRequest
import com.example.smarparkinapp.data.model.PaymentResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
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

    @POST("auth/register/client/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/password/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    // ESTACIONAMIENTOS (sin auth header)
    @GET("api/parkings/")
    suspend fun getApprovedParkingLots(): Response<ParkingLotResponse>

    @GET("api/parking/")
    suspend fun searchParkingLots(@Query("search") query: String): Response<ParkingLotResponse>

    @GET("api/parking/cerca/")
    suspend fun getNearbyParkingLots(
        @Query("lat") latitud: Double,
        @Query("lng") longitud: Double
    ): Response<List<ParkingLot>>

    @GET("api/parking/mejores_calificados/")
    suspend fun getTopRatedParkingLots(): Response<List<ParkingLot>>

    @GET("api/parking/mas_economicos/")
    suspend fun getMasEconomicos(): Response<List<ParkingLot>>

    // VEHÍCULOS (sin auth header - manejado por interceptor)
    @POST("api/vehicles/")
    suspend fun addCar(@Body car: CarRequest): Response<CarResponse>

    @GET("api/vehicles/")
    suspend fun getUserVehicles(): Response<PaginatedResponse<CarResponse>>

    @PUT("api/vehicles/{id}/")
    suspend fun updateVehicle(
        @Path("id") vehicleId: Int,
        @Body car: CarRequest
    ): Response<CarResponse>

    @DELETE("api/vehicles/{id}/")
    suspend fun deleteVehicle(@Path("id") vehicleId: Int): Response<GenericResponse>

    // RESERVAS (sin auth header - manejado por interceptor)
    @POST("api/reservations/")
    suspend fun createReservation(@Body body: Map<String, Any>): Response<ReservationResponse>

    @GET("api/reservations/client/mis-reservas/")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

    @GET("api/reservations/client/active/")
    suspend fun getActiveReservations(): Response<List<ReservationResponse>>

    @POST("api/reservations/{codigo}/cancel/")
    suspend fun cancelReservation(@Path("codigo") codigo: String): Response<GenericResponse>

    @POST("api/reservations/{codigo}/extend/")
    suspend fun extendReservation(
        @Path("codigo") codigo: String,
        @Body body: Map<String, Any>
    ): Response<GenericResponse>

    @POST("api/reservations/{codigo}/checkin/")
    suspend fun checkIn(@Path("codigo") codigo: String): Response<GenericResponse>

    @POST("api/reservations/{codigo}/checkout/")
    suspend fun checkOut(@Path("codigo") codigo: String): Response<GenericResponse>

    @GET("api/reservations/tipos/")
    suspend fun getReservationTypes(): Response<List<String>>

    // PAGOS (sin auth header - manejado por interceptor)
    @POST("api/payments/")
    suspend fun createPayment(@Body payment: PaymentRequest): Response<PaymentResponse>

    @GET("api/payments/{id}/")
    suspend fun getPayment(@Path("id") id: Long): Response<PaymentResponse>

    @POST("api/payments/{id}/process/")
    suspend fun processPayment(@Path("id") id: Long): Response<PaymentResponse>

    @GET("api/payments/pending/")
    suspend fun getPendingPayments(): Response<List<PaymentResponse>>

    // PARKING SPOTS (sin auth header)
    @GET("api/parking/spots/")
    suspend fun getParkingSpots(): Response<List<ParkingSpotResponse>>

    @GET("api/parking/mapa/")
    suspend fun getParkingMapa(): Response<ParkingLotResponse>

    // PERFIL DE USUARIO (sin auth header - manejado por interceptor)
    @GET("api/profile/")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("api/profile/update/")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @GET("api/users/profile/")
    suspend fun getUserProfileCompat(): Response<UserProfileResponse>

    @PUT("api/users/profile/update/")
    suspend fun updateUserProfileCompat(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    // DASHBOARD (sin auth header - manejado por interceptor)
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

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val first_name: String? = null,
    val last_name: String? = null
)

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

// MODELOS DE VEHÍCULOS
data class CarRequest(
    val placa: String,
    val marca: String,
    val modelo: String,
    val color: String,
    val year: Int? = null
)

data class CarResponse(
    val id: Int,
    val placa: String,
    val marca: String,
    val modelo: String,
    val color: String,
    val activo: Boolean = true,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null
)

// MODELOS DE RESERVAS - SOLO UNA DECLARACIÓN
data class ReservationResponse(
    val id: Long,
    val codigo_reserva: String,
    val estacionamiento: ParkingShort?,
    val vehiculo: VehicleShort?,
    val hora_entrada: String,
    val hora_salida: String,
    val estado: String,
    val tipo: String,
    val costo_estimado: Double? = 0.0,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null
)

data class ParkingShort(
    val id: Long,
    val nombre: String,
    val direccion: String? = null,
    val tarifa_hora: Double? = 0.0
)

data class VehicleShort(
    val id: Int,
    val placa: String,
    val marca: String,
    val modelo: String,
    val color: String? = null
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