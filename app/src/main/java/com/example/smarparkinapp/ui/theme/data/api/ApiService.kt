package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.model.ParkingLotResponse
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // 🔐 AUTENTICACIÓN
    @POST("api/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/simple-login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ✅ AGREGADO: Recuperación de contraseña
    @POST("api/password/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    // 🅿️ ESTACIONAMIENTOS
    @GET("api/parking/")
    suspend fun getApprovedParkingLots(
        @Query("search") search: String? = null,
    ): Response<ParkingLotResponse>

    @GET("api/parking/cerca/")
    suspend fun getNearbyParkingLots(
        @Query("lat") latitud: Double,
        @Query("lng") longitud: Double
    ): Response<List<ParkingLot>>

    @GET("api/parking/mejores_calificados/")
    suspend fun getTopRatedParkingLots(): Response<List<ParkingLot>>

    // 🚗 VEHÍCULOS
    @POST("api/users/{id}/cars/")
    suspend fun addCar(@Path("id") userId: Int, @Body car: CarRequest): Response<CarResponse>

    // 📅 RESERVAS
    @POST("api/reservations/")
    suspend fun createReservation(@Body reservation: ReservationRequest): Response<ReservationResponse>

    @GET("api/reservations/user/{userId}/")
    suspend fun getUserReservations(@Path("userId") userId: Int): Response<List<ReservationResponse>>

    // 💳 PAGOS
    @POST("api/payments/")
    suspend fun processPayment(@Body payment: PaymentRequest): Response<PaymentResponse>

    @GET("api/parking/spots/")
    suspend fun getParkingSpots(): Response<List<ParkingSpotResponse>>

    @GET("api/parking/mapa/")
    suspend fun getParkingMapa(): Response<ParkingLotResponse>

    @GET("api/parking/mas_economicos/")
    suspend fun getMasEconomicos(): Response<List<ParkingLot>>
}

// 📋 MODELOS que SOLO existen para la API (no en models/)
data class LoginRequest(val username: String, val password: String)

// ✅ CORREGIDO: Formato que coincide con Django JWT
data class LoginResponse(
    val access: String? = null,      // Django JWT usa "access"
    val refresh: String? = null,     // Token de refresh
    val user: UserResponse? = null   // Información del usuario
)

data class RegisterResponse(val id: Int, val username: String, val email: String)

// ✅ CORREGIDO: Modelo que coincide con Django User
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String? = null,
    val last_name: String? = null,
    val is_staff: Boolean? = false,
    val is_superuser: Boolean? = false
)

// ✅ AGREGADO: Modelos para recuperación de contraseña
data class ResetPasswordRequest(
    val email: String
)

data class ResetPasswordResponse(
    val detail: String? = null,
    val message: String? = null,
    val status: String? = null
)

data class ReservationRequest(
    val estacionamiento: Long,
    val usuario: Int,
    val hora_entrada: String,
    val hora_salida: String,
    val vehiculo: Int
)

data class ReservationResponse(
    val id: Long,
    val estacionamiento: ParkingShort,
    val estado: String,
    val hora_entrada: String,
    val hora_salida: String
)

data class PaymentRequest(
    val reserva: Long,
    val monto: Double,
    val metodo_pago: String
)

data class PaymentResponse(
    val id: Long,
    val estado: String,
    val transaction_id: String?
)

data class ParkingShort(
    val id: Long,
    val nombre: String
)