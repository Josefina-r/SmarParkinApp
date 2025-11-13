package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.UpdateProfileRequest
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*
import com.example.smarparkinapp.ui.theme.data.model.*
interface ApiService {

    // ✅ AUTENTICACIÓN CORREGIDA - según tus endpoints
    @POST("api/users/auth/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/users/auth/register/client/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/users/auth/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse


    // ============ VEHÍCULOS ============
    @GET("api/cars/")
    suspend fun getUserCars(): Response<List<CarResponse>>

    @POST("api/cars/")
    suspend fun addCar(@Body carRequest: CarRequest): Response<CarResponse>
    //parking
    @GET("api/parking/parkings/mapa/")
    suspend fun getParkingsForMap(
        @Query("disponibles") disponibles: Boolean? = null
    ): List<ParkingSpotResponse>

    @GET("api/parking/parkings/")
    suspend fun getParkings(
        @Query("available") available: Boolean? = null,
        @Query("aprobado") approved: Boolean? = null,
        @Query("activo") active: Boolean? = null,
        @Query("nivel_seguridad") securityLevel: String? = null
    ): Response<List<ParkingSpotResponse>>

    // ✅ Obtener estacionamientos cercanos (si existe en tu API)
    @GET("api/parking/")
    suspend fun getParkingsCerca(
        @Query("available") available: Boolean? = true,
        @Query("aprobado") approved: Boolean? = true,
        @Query("activo") active: Boolean? = true
    ): Response<List<ParkingSpotResponse>>


    // ✅ Obtener mejores calificados
    @GET("api/parking/")
    suspend fun getMejoresCalificados(
        @Query("available") available: Boolean? = true,
        @Query("aprobado") approved: Boolean? = true
    ): Response<List<ParkingSpotResponse>>

    // ✅ Obtener más económicos
    @GET("api/parking/")
    suspend fun getMasEconomicos(
        @Query("available") available: Boolean? = true,
        @Query("aprobado") approved: Boolean? = true
    ): Response<List<ParkingSpotResponse>>
    @GET("api/users/profile/")
    suspend fun getUserProfile(): Response<UserProfile>

    // ✅ Eliminar vehículo
    @DELETE("api/cars/{id}/")
    suspend fun deleteCar(@Path("id") carId: Int): Response<Void>

    // ✅ Actualizar perfil (si lo necesitas)
    @PUT("api/users/profile/")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfile>
}



// ✅ MODELOS CORREGIDOS según tus endpoints
data class LoginRequest(
    val username: String,  // acepta username O email
    val password: String
)

data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: UserData  // Incluye user data
)

data class UserData(
    val id: Long,
    val username: String,
    val email: String,
    val rol: String,
    val rol_display: String,
    val first_name: String?,
    val last_name: String?,
    val is_admin: Boolean,
    val is_owner: Boolean
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val password_confirm: String,
    val telefono: String?
)

data class RegisterResponse(
    val id: Long,
    val username: String,
    val email: String,
    val telefono: String?,
    val rol: String,
    val fecha_registro: String
)

data class RefreshTokenRequest(
    val refresh: String
)

data class RefreshTokenResponse(
    val access: String
)
