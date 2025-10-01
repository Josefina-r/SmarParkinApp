package com.example.smarparkinapp.ui.theme.data.api

import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

interface ApiService {

    // Registro
    @POST("api/users/register/")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    // Login (JWT)
    @POST("api/users/login/")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Obtener estacionamientos
    @GET("api/parking/")
    suspend fun getParkingSpots(): List<ParkingSpotResponse>
    @POST("auth/password/reset/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>

    @GET("parkingSpot/")
    suspend fun getParkingLots(): List<ParkingSpot>
}

// Modelos de request/response
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val access: String, val refresh: String)
data class RegisterResponse(val id: Int, val username: String, val email: String)
data class ResetPasswordRequest(val email: String)
