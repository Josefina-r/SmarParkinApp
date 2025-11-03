package com.example.smarparkinapp.ui.theme.data.repository

import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import javax.inject.Inject

// Define el Result personalizado FUERA de la clase
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class ParkingRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getNearbyParkingLots(lat: Double, lng: Double): Result<List<ParkingLot>> {
        return try {
            val response = apiService.getNearbyParkingLots(lat, lng)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun getAllParkingLots(): Result<List<ParkingLot>> {
        return try {
            val response = apiService.getApprovedParkingLots()
            if (response.isSuccessful) {
                Result.Success(response.body()?.results ?: emptyList())
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.message}")
        }
    }

    suspend fun searchParkingLots(query: String): Result<List<ParkingLot>> {
        return try {
            val response = apiService.getApprovedParkingLots(search = query)
            if (response.isSuccessful) {
                Result.Success(response.body()?.results ?: emptyList())
            } else {
                Result.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.message}")
        }
    }
}