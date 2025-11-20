// data/repository/ReservationRepository.kt
package com.example.smarparkinapp.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.api.ReservationResponse
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.GenericResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReservationRepository(private val context: Context) {

    private val apiService by lazy {
        RetrofitInstance.getAuthenticatedApiService(context)
    }

    suspend fun createReservation(
        parkingId: Long,
        vehicleId: Int,
        horaInicio: String,
        horaFin: String,
        tipo: String = "normal"
    ): Result<ReservationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = mapOf(
                    "estacionamiento" to parkingId,
                    "vehiculo" to vehicleId,
                    "hora_entrada" to horaInicio,
                    "hora_salida" to horaFin,
                    "tipo" to tipo
                )
                val response = apiService.createReservation(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getMyReservations(): Result<List<ReservationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMyReservations()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getActiveReservations(): Result<List<ReservationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getActiveReservations()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun cancelReservation(codigo: String): Result<GenericResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelReservation(codigo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkIn(codigo: String): Result<GenericResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkIn(codigo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun checkOut(codigo: String): Result<GenericResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkOut(codigo)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}