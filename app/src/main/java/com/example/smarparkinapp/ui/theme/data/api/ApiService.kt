package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.model.ParkingLotResponse
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.GenericResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.Reservation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // AUTENTICACIÓN
    @POST("auth/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register/client/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/password/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>
    // ESTACIONAMIENTOS - CORREGIDO
    @GET("api/parkings/")
    suspend fun getApprovedParkingLots(): Response<ParkingLotResponse>

    @GET("api/parking/")
    suspend fun searchParkingLots(@Query("search") query: String): Response<ParkingLotResponse> // ✅ Método separado para búsqueda

    @GET("api/parking/cerca/")
    suspend fun getNearbyParkingLots(
        @Query("lat") latitud: Double,
        @Query("lng") longitud: Double
    ): Response<List<ParkingLot>>

    @GET("api/parking/mejores_calificados/")
    suspend fun getTopRatedParkingLots(): Response<List<ParkingLot>>

    @GET("api/parking/mas_economicos/")
    suspend fun getMasEconomicos(): Response<List<ParkingLot>>

    // 🚗 VEHÍCULOS
    @POST("cars/")
    suspend fun addCar(@Body car: CarRequest): Response<CarResponse> // ✅ Solo CarRequest
    // RESERVAS - VERIFICAR si estas rutas existen
    // ========== RESERVAS ==========
    @GET("reservations/client/mis-reservas/")
    suspend fun getMyReservations(): Response<List<Reservation>>

    @GET("reservations/client/active/")
    suspend fun getActiveReservations(): Response<List<Reservation>>

    @POST("reservations/{codigo}/cancel/")
    suspend fun cancelReservation(@Path("codigo") codigo: String): Response<GenericResponse>

    @POST("reservations/{codigo}/extend/")
    suspend fun extendReservation(
        @Path("codigo") codigo: String,
        @Body body: Map<String, Any>
    ): Response<GenericResponse>

    @POST("reservations/{codigo}/checkin/")
    suspend fun checkIn(@Path("codigo") codigo: String): Response<GenericResponse>

    @POST("reservations/{codigo}/checkout/")
    suspend fun checkOut(@Path("codigo") codigo: String): Response<GenericResponse>

    @POST("reservations/reservations/")
    suspend fun createReservation(
        @Body body: Map<String, Any>
    ): Response<Reservation>

    // PAGOS - VERIFICAR si estas rutas existen
    @POST("api/payments/")
    suspend fun processPayment(
        @Body payment: PaymentRequest,
        @Header("Authorization") token: String
    ): Response<PaymentResponse>

    @GET("api/parking/spots/")
    suspend fun getParkingSpots(): Response<List<ParkingSpotResponse>>

    @GET("api/parking/mapa/")
    suspend fun getParkingMapa(): Response<ParkingLotResponse>

    // fun resetPassword(resetPasswordRequest: ResetPasswordRequest) // Esto no puede estar aquí, debe ser suspend y tener anotación HTTP
}

// MODELOS DE AUTENTICACIÓN
data class LoginRequest(val username: String, val password: String)

data class LoginResponse(
    val access: String,      // Token de acceso JWT
    val refresh: String,     // Token de refresh
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

data class UserProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String?,
    val last_name: String?,
    val role: String,  // 'client', 'owner', 'admin'
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

// MODELOS DE VEHÍCULOS
data class CarRequest(
    val brand: String,
    val model: String,
    val year: Int,
    val color: String,
    val license_plate: String
)

data class CarResponse(
    val id: Int,
    val brand: String,
    val model: String,
    val year: Int,
    val color: String,
    val license_plate: String,
    val user: Int
)

// MODELOS DE RESERVAS
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
    val hora_salida: String,
    val total: Double? = null
)

// MODELOS DE PAGOS
data class PaymentRequest(
    val reserva: Long,
    val monto: Double,
    val metodo_pago: String
)

data class PaymentResponse(
    val id: Long,
    val estado: String,
    val transaction_id: String?,
    val fecha_pago: String? = null
)

data class ParkingShort(
    val id: Long,
    val nombre: String,
    val direccion: String? = null
)

// MODELOS PARA RECUPERACIÓN DE CONTRASEÑA (si existen en tu Django)
data class ResetPasswordRequest(
    val email: String
)

data class ResetPasswordResponse(
    val detail: String,
    val message: String? = null
)