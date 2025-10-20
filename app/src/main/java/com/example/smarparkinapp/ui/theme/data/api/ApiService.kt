package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


    @POST("api/users/register/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/users/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/password/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>

    // ESTACIONAMIENTOS
    @GET("api/parking/")
    suspend fun getParkingSpots(): List<ParkingSpotResponse>

    @GET("parkingSpot/")
    suspend fun getParkingLots(): List<ParkingSpot>


    @POST("api/users/{id}/cars/")
    suspend fun addCar(@Path("id") userId: Int, @Body car: CarRequest): Response<CarResponse>


    @GET("parking/mapa/")
    suspend fun getParkingMapa(): ParkingMapaResponse

    @GET("parking/mejores_calificados/")
    suspend fun getMejoresCalificados(): List<ParkingSpotResponse>

    @GET("parking/mas_economicos/")
    suspend fun getMasEconomicos(): List<ParkingSpotResponse>

    @GET("parking/")
    suspend fun getParkingWithFilters(
        @Query("available") available: String? = null,
        @Query("search") search: String? = null,
        @Query("precio_max") precioMax: Double? = null,
        @Query("min_rating") minRating: Double? = null
    ): List<ParkingSpotResponse>
}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val access: String, val refresh: String)
data class RegisterResponse(val id: Int, val username: String, val email: String)
data class ResetPasswordRequest(val email: String)

data class ParkingMapaResponse(
    val count: Int,
    val results: List<ParkingSpotResponse>
)