package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import javax.inject.Inject

// Result personalizado
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class ParkingRepository @Inject constructor(
    private val context: Context
) {

    private val authenticatedApiService by lazy {
        RetrofitInstance.getAuthenticatedApiService(context)
    }

    private val basicApiService by lazy {
        RetrofitInstance.apiService
    }

    suspend fun getNearbyParkingLots(lat: Double, lng: Double): Result<List<ParkingLot>> {
        return try {
            println("🔄 [REPO] Buscando estacionamientos cercanos...")
            val response = authenticatedApiService.getNearbyParkingLots(lat, lng)
            println("✅ [REPO] Respuesta cercanos: ${response.code()}")

            if (response.isSuccessful) {
                val parkingLots = response.body() ?: emptyList()
                println("📍 [REPO] Encontrados ${parkingLots.size} estacionamientos cercanos")
                Result.Success(parkingLots)
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                println("❌ [REPO] $errorMsg")
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("💥 [REPO] $errorMsg")
            Result.Error(errorMsg)
        }
    }

    suspend fun getAllParkingLots(): Result<List<ParkingLot>> {
        return try {
            println("🔄 [REPO] === INICIANDO CARGA DESDE: /api/parking/ ===")

            val response = basicApiService.getApprovedParkingLots()
            println("✅ [REPO] Código: ${response.code()}")
            println("✅ [REPO] Éxito: ${response.isSuccessful}")
            println("✅ [REPO] Mensaje: ${response.message()}")

            if (response.isSuccessful) {
                val parkingLotResponse = response.body()
                println("📦 [REPO] Response Body: $parkingLotResponse")

                val parkingLots = parkingLotResponse?.results ?: emptyList()
                println("🏢 [REPO] Encontrados: ${parkingLots.size} estacionamientos")

                // DEBUG detallado
                parkingLots.forEachIndexed { index, parking ->
                    println("   🅿️ [$index] ID: ${parking.id}, Nombre: ${parking.nombre}")
                    println("        Dirección: ${parking.direccion}")
                    println("        Precio: ${parking.tarifa_hora}")
                    println("        Disponibles: ${parking.plazas_disponibles}")
                }

                Result.Success(parkingLots)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                println("❌ [REPO] Error Body: $errorBody")
                Result.Error("Error ${response.code()}: $errorBody")
            }
        } catch (e: Exception) {
            println("💥 [REPO] Excepción: ${e.message}")
            e.printStackTrace()
            Result.Error("Error: ${e.message}")
        }
    }

    suspend fun searchParkingLots(query: String): Result<List<ParkingLot>> {
        return try {
            println("🔍 [REPO] Buscando: '$query'")

            // ✅ CORREGIDO: Usar el método específico para búsqueda
            val response = if (query.isBlank()) {
                basicApiService.getApprovedParkingLots() // Si no hay query, traer todos
            } else {
                basicApiService.searchParkingLots(query) // Si hay query, usar búsqueda
            }

            println("✅ [REPO] Respuesta búsqueda: ${response.code()}")

            if (response.isSuccessful) {
                val parkingLotResponse = response.body()
                val parkingLots = parkingLotResponse?.results ?: emptyList()
                println("🔎 [REPO] Encontrados ${parkingLots.size} resultados para '$query'")
                Result.Success(parkingLots)
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                println("❌ [REPO] $errorMsg")
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("💥 [REPO] $errorMsg")
            Result.Error(errorMsg)
        }
    }

    // Método para estacionamientos públicos (sin autenticación)
    suspend fun getPublicParkingLots(): Result<List<ParkingLot>> {
        return try {
            println("🔄 [REPO] Cargando estacionamientos públicos...")
            val response = basicApiService.getApprovedParkingLots()
            println("✅ [REPO] Respuesta públicos: ${response.code()}")

            if (response.isSuccessful) {
                val parkingLotResponse = response.body()
                val parkingLots = parkingLotResponse?.results ?: emptyList()
                println(" [REPO] Encontrados ${parkingLots.size} estacionamientos públicos")
                Result.Success(parkingLots)
            } else {
                val errorMsg = "Error ${response.code()}: ${response.message()}"
                println(" [REPO] $errorMsg")
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("💥 [REPO] $errorMsg")
            Result.Error(errorMsg)
        }
    }
}