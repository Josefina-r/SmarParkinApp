// data/repository/ReservationRepository.kt
package com.example.smarparkinapp.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.api.GenericResponse
import com.example.smarparkinapp.ui.theme.data.api.ReservationResponse
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
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
                println("🔍 [ReservationRepository] Creando reserva...")
                println("   🅿️ Parking ID: $parkingId")
                println("   🚗 Vehicle ID: $vehicleId")
                println("   ⏰ Inicio: $horaInicio")
                println("   ⏰ Fin: $horaFin")
                println("   📋 Tipo: $tipo")

                // Crear el mapa correctamente - ESTO ES LO QUE ESPERA TU API SERVICE
                val request = mapOf(
                    "estacionamiento" to parkingId,
                    "vehiculo" to vehicleId,
                    "hora_entrada" to horaInicio,
                    "hora_salida" to horaFin,
                    "tipo" to tipo
                )

                println("📤 [ReservationRepository] Enviando reserva a API...")
                println("   📦 Request: $request")

                // CORREGIDO: Quitar el parámetro "Bearer $token" - el interceptor lo maneja
                val response = apiService.createReservation(request)

                if (response.isSuccessful && response.body() != null) {
                    val reservation = response.body()!!
                    println("✅ [ReservationRepository] Reserva creada exitosamente:")
                    println("   🆔 ID: ${reservation.id}")
                    println("   📋 Código: ${reservation.codigo_reserva}")
                    println("   🏢 Parking: ${reservation.estacionamiento?.nombre}")
                    println("   🚗 Vehículo: ${reservation.vehiculo?.placa}")
                    println("   💰 Costo: ${reservation.costo_estimado}")

                    Result.success(reservation)
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    println("❌ [ReservationRepository] Error HTTP $errorCode: $errorBody")

                    val errorMessage = when (errorCode) {
                        400 -> "Datos de reserva inválidos"
                        401 -> "No autorizado - token inválido"
                        403 -> "No tienes permisos para crear reservas"
                        404 -> "Estacionamiento o vehículo no encontrado"
                        else -> "Error del servidor: $errorCode"
                    }

                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                println("❌ [ReservationRepository] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    suspend fun getMyReservations(): Result<List<ReservationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [ReservationRepository] Obteniendo mis reservas...")

                // CORREGIDO: Quitar el parámetro de token
                val response = apiService.getMyReservations()

                if (response.isSuccessful && response.body() != null) {
                    val reservations = response.body()!!
                    println("✅ [ReservationRepository] ${reservations.size} reservas obtenidas")
                    Result.success(reservations)
                } else {
                    val errorCode = response.code()
                    Result.failure(Exception("Error al obtener reservas: $errorCode"))
                }
            } catch (e: Exception) {
                println("❌ [ReservationRepository] Error obteniendo reservas: ${e.message}")
                Result.failure(Exception("Error al obtener reservas: ${e.message}"))
            }
        }
    }

    suspend fun cancelReservation(codigo: String): Result<GenericResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [ReservationRepository] Cancelando reserva: $codigo")

                // CORREGIDO: Quitar el parámetro de token
                val response = apiService.cancelReservation(codigo)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    println("✅ [ReservationRepository] Reserva cancelada: ${result.detail}")
                    Result.success(result)
                } else {
                    val errorCode = response.code()
                    Result.failure(Exception("Error al cancelar reserva: $errorCode"))
                }
            } catch (e: Exception) {
                println("❌ [ReservationRepository] Error cancelando reserva: ${e.message}")
                Result.failure(Exception("Error al cancelar reserva: ${e.message}"))
            }
        }
    }

    suspend fun getActiveReservations(): Result<List<ReservationResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [ReservationRepository] Obteniendo reservas activas...")

                val response = apiService.getActiveReservations()

                if (response.isSuccessful && response.body() != null) {
                    val reservations = response.body()!!
                    println("✅ [ReservationRepository] ${reservations.size} reservas activas obtenidas")
                    Result.success(reservations)
                } else {
                    Result.failure(Exception("Error al obtener reservas activas"))
                }
            } catch (e: Exception) {
                println("❌ [ReservationRepository] Error obteniendo reservas activas: ${e.message}")
                Result.failure(Exception("Error al obtener reservas activas"))
            }
        }
    }

    suspend fun extendReservation(codigo: String, minutosExtras: Int): Result<GenericResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 [ReservationRepository] Extendiendo reserva: $codigo por $minutosExtras minutos")

                val request = mapOf(
                    "minutos_extra" to minutosExtras
                )

                val response = apiService.extendReservation(codigo, request)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    println("✅ [ReservationRepository] Reserva extendida: ${result.detail}")
                    Result.success(result)
                } else {
                    Result.failure(Exception("Error al extender reserva"))
                }
            } catch (e: Exception) {
                println("❌ [ReservationRepository] Error extendiendo reserva: ${e.message}")
                Result.failure(Exception("Error al extender reserva"))
            }
        }
    }
}