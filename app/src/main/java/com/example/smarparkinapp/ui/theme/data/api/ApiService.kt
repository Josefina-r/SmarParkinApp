package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.api.CarRequest
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.model.ParkingLotResponse
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.GenericResponse
import com.example.smarparkinapp.ui.theme.data.model.PaginatedResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.UpdateProfileRequest
import com.example.smarparkinapp.ui.theme.data.model.UserProfileResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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
    @POST("api/vehicles/")  // ← URL CORRECTA
    suspend fun addCar(
        @Header("Authorization") token: String,
        @Body car: CarRequest
    ): Response<CarResponse>


    // En tu ApiService interface, agrega:
    @GET("api/vehicles/")
    suspend fun getUserVehicles(
        @Header("Authorization") token: String
    ): Response<PaginatedResponse<CarResponse>>

    // Agrega estos endpoints en tu ApiService:

    @PUT("api/vehicles/{id}/")  // ← URL CORRECTA
    suspend fun updateVehicle(
        @Header("Authorization") token: String,
        @Path("id") vehicleId: Int,
        @Body car: CarRequest
    ): Response<CarResponse>

    @DELETE("api/vehicles/{id}/")  // ← URL CORRECTA
    suspend fun deleteVehicle(
        @Header("Authorization") token: String,
        @Path("id") vehicleId: Int
    ): Response<GenericResponse>

    // RESERVAS - VERIFICAR si estas rutas existen
    // ========== RESERVAS ==========
    @POST("reservations/")
    suspend fun createReservation(@Body body: Map<String, Any>): Response<ReservationResponse>

    @GET("api/reservations/client/mis-reservas/")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

    @GET("reservations/client/active/")
    suspend fun getActiveReservations(): Response<List<ReservationResponse>>

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

    @GET("reservations/tipos/")
    suspend fun getReservationTypes(): Response<List<String>>

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

    //  RUTAS PARA PERFIL
    @GET("api/profile/")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("api/profile/update/")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @GET("api/users/profile/")
    suspend fun getUserProfileCompat(): Response<UserProfileResponse>

    @PUT("api/users/profile/update/")
    suspend fun updateUserProfileCompat(@Body request: UpdateProfileRequest): Response<UserProfileResponse>
}

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

// MODELOS DE DASHBOARD
data class DashboardStatsResponse(
    val total_reservations: Int,
    val active_reservations: Int,
    val favorite_parkings: Int,
    val total_spent: Double
)

// En tu ApiService, cambia los modelos de vehículos:

// MODELOS DE VEHÍCULOS - ACTUALIZADOS para coincidir con Django
data class CarRequest(
    val placa: String,        // ← igual que en Django
    val marca: String,        // ← igual que en Django
    val modelo: String,       // ← igual que en Django
    val color: String,        // ← igual que en Django
    val year: Int? = null     // Opcional si tu modelo no lo tiene
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
// MODELOS DE RESERVAS
data class ReservationRequest(
    val estacionamiento: Long,
    val usuario: Int,
    val hora_entrada: String,
    val hora_salida: String,
    val vehiculo: Int
)

// ✅ CORREGIDO: Eliminada la definición duplicada de ReservationResponse
// Mantenemos solo esta definición que es más completa

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

data class ResetPasswordRequest(
    val email: String
)

data class ResetPasswordResponse(
    val detail: String,
    val message: String? = null
)

