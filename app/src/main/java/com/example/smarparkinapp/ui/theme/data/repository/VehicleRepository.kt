package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.model.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Result

class VehicleRepository(
    private val context: Context
) {
    private val authManager = AuthManager(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("vehicle_prefs", Context.MODE_PRIVATE)

    private val apiService by lazy {
        RetrofitInstance.getAuthenticatedApiService(context)
    }

    suspend fun getUserVehicles(): Result<List<Car>> = withContext(Dispatchers.IO) {
        try {
            println("🚗 [VehicleRepository] === INICIANDO OBTENCIÓN DE VEHÍCULOS ===")

            val response = apiService.getUserVehicles()

            println("📡 [VehicleRepository] Respuesta HTTP: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    val carResponses = paginatedResponse.results

                    println("✅ [VehicleRepository] DATOS OBTENIDOS EXITOSAMENTE:")
                    println("   - Total en BD: ${paginatedResponse.count}")
                    println("   - En esta página: ${carResponses.size}")

                    // ✅ CORREGIDO: Convertir Int a Long para el ID
                    val vehicles = carResponses.map { carResponse ->
                        Car(
                            id = carResponse.id.toLong(),  // ✅ CONVERTIR Int a Long
                            plate = carResponse.placa,
                            brand = carResponse.marca,
                            model = carResponse.modelo,
                            color = carResponse.color,
                            active = carResponse.activo,
                            userId = carResponse.usuario?.toLong(),  // ✅ CONVERTIR si es necesario
                            fechaCreacion = carResponse.fecha_creacion,
                            fechaActualizacion = carResponse.fecha_actualizacion
                        )
                    }

                    // DEBUG DETALLADO
                    println("📋 [VehicleRepository] LISTA COMPLETA DE VEHÍCULOS:")
                    if (vehicles.isEmpty()) {
                        println("   ⚠️  No se encontraron vehículos para este usuario")
                    } else {
                        vehicles.forEachIndexed { index, car ->
                            println("   ${index + 1}. ID: ${car.id} | Placa: ${car.plate} | Marca: ${car.brand} | Modelo: ${car.model}")
                        }
                    }

                    // Actualizar vehículo por defecto
                    val defaultVehicleId = getDefaultVehicleIdFromList(vehicles)
                    saveDefaultVehicleId(defaultVehicleId)
                    println("⭐ [VehicleRepository] Vehículo por defecto establecido: ID $defaultVehicleId")

                    Result.success(vehicles)
                } else {
                    println("❌ [VehicleRepository] Respuesta paginada es NULL")
                    Result.success(emptyList())
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ [VehicleRepository] ERROR EN RESPUESTA API: ${response.code()} - $errorBody")
                Result.failure(Exception("Error del servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] EXCEPCIÓN NO CONTROLADA: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createVehicle(
        plate: String,
        brand: String,
        model: String,
        color: String
    ): Result<Car> = withContext(Dispatchers.IO) {
        try {
            println("🚗 [VehicleRepository] === CREANDO NUEVO VEHÍCULO ===")

            if (!isValidPlateFormat(plate)) {
                return@withContext Result.failure(Exception("Formato de placa inválido. Use: ABC123 o similar"))
            }

            val cleanedPlate = plate.uppercase().replace(" ", "").replace("-", "")
            val carRequest = com.example.smarparkinapp.ui.theme.data.model.CarRequest(
                placa = cleanedPlate,
                marca = brand,
                modelo = model,
                color = color,
            )

            println("📤 [VehicleRepository] Enviando solicitud a API...")
            val response = apiService.addCar(carRequest)

            println("📡 [VehicleRepository] Respuesta crear vehículo: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val carResponse = response.body()
                if (carResponse != null) {
                    // ✅ CORREGIDO: Convertir Int a Long
                    val newCar = Car(
                        id = carResponse.id.toLong(),  // ✅ CONVERTIR Int a Long
                        plate = carResponse.placa,
                        brand = carResponse.marca,
                        model = carResponse.modelo,
                        color = carResponse.color,
                        active = carResponse.activo,
                        userId = carResponse.usuario?.toLong(),  // ✅ CONVERTIR si es necesario
                    )
                    println("✅ [VehicleRepository] VEHÍCULO CREADO EXITOSAMENTE: ID ${newCar.id}")

                    // Establecer como vehículo por defecto si es el primero
                    setAsDefaultVehicleIfFirst(newCar.id)

                    Result.success(newCar)
                } else {
                    println("❌ [VehicleRepository] Respuesta vacía del servidor")
                    Result.failure(Exception("Error: Respuesta vacía del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ [VehicleRepository] ERROR CREANDO VEHÍCULO: ${response.code()} - $errorBody")
                Result.failure(Exception("Error creando vehículo: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] EXCEPCIÓN CREANDO VEHÍCULO: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ✅ CORREGIDO: Función updateVehicle con Long
    suspend fun updateVehicle(vehicle: Car): Result<Car> = withContext(Dispatchers.IO) {
        try {
            println("🔄 [VehicleRepository] Actualizando vehículo ID: ${vehicle.id}")

            if (!isValidPlateFormat(vehicle.plate)) {
                return@withContext Result.failure(Exception("Formato de placa inválido"))
            }

            val carRequest = com.example.smarparkinapp.ui.theme.data.model.CarRequest(
                placa = vehicle.plate,
                marca = vehicle.brand,
                modelo = vehicle.model,
                color = vehicle.color,
            )

            // ✅ CORREGIDO: Convertir Long a Int para la API
            val vehicleIdInt = vehicle.id.toInt()
            val response = apiService.updateVehicle(vehicleIdInt, carRequest)

            println("📡 [VehicleRepository] Respuesta actualizar vehículo: ${response.code()}")

            if (response.isSuccessful) {
                val carResponse = response.body()
                if (carResponse != null) {
                    // ✅ CORREGIDO: Convertir Int a Long
                    val updatedCar = Car(
                        id = carResponse.id.toLong(),  // ✅ CONVERTIR Int a Long
                        plate = carResponse.placa,
                        brand = carResponse.marca,
                        model = carResponse.modelo,
                        color = carResponse.color,
                        active = carResponse.activo
                    )
                    println("✅ [VehicleRepository] Vehículo actualizado exitosamente")
                    Result.success(updatedCar)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error actualizando vehículo: $errorBody"))
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] Error actualizando vehículo: ${e.message}")
            Result.failure(e)
        }
    }

    // ✅ CORREGIDO: Función deleteVehicle con Long
    suspend fun deleteVehicle(vehicleId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            println("🗑️ [VehicleRepository] === ELIMINANDO VEHÍCULO ===")
            println("   - ID del vehículo: $vehicleId")

            // ✅ CORREGIDO: Convertir Long a Int para la API
            val vehicleIdInt = vehicleId.toInt()
            val response = apiService.deleteVehicle(vehicleIdInt)

            println("📡 [VehicleRepository] Respuesta eliminar vehículo: ${response.code()}")

            if (response.isSuccessful) {
                // Si se eliminó el vehículo por defecto, limpiar la preferencia
                val currentDefaultId = getDefaultVehicleId()
                if (currentDefaultId == vehicleIdInt) {
                    clearDefaultVehicle()
                    println("🧹 [VehicleRepository] Vehículo por defecto eliminado de preferencias")
                }

                println("✅ [VehicleRepository] VEHÍCULO ELIMINADO EXITOSAMENTE")
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ [VehicleRepository] ERROR ELIMINANDO VEHÍCULO: ${response.code()} - $errorBody")
                Result.failure(Exception("Error eliminando vehículo: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] EXCEPCIÓN ELIMINANDO VEHÍCULO: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    // ✅ NUEVO: Método para obtener vehículo por ID con Long
    suspend fun getVehicleById(vehicleId: Long): Result<Car> = withContext(Dispatchers.IO) {
        try {
            println("🔍 [VehicleRepository] Buscando vehículo por ID: $vehicleId")

            // Obtener todos los vehículos y filtrar por ID
            val vehiclesResult = getUserVehicles()
            if (vehiclesResult.isSuccess) {
                val vehicles = vehiclesResult.getOrNull() ?: emptyList()
                val vehicle = vehicles.find { it.id == vehicleId }  // ✅ Ahora ambos son Long
                if (vehicle != null) {
                    println("✅ [VehicleRepository] Vehículo encontrado: $vehicle")
                    Result.success(vehicle)
                } else {
                    println("❌ [VehicleRepository] Vehículo no encontrado con ID: $vehicleId")
                    Result.failure(Exception("Vehículo no encontrado"))
                }
            } else {
                val error = vehiclesResult.exceptionOrNull() ?: Exception("Error obteniendo vehículos")
                Result.failure(error)
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] Error obteniendo vehículo por ID: ${e.message}")
            Result.failure(e)
        }
    }

    // ✅ CORREGIDO: setDefaultVehicle con Long
    suspend fun setDefaultVehicle(vehicleId: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            println("⭐ [VehicleRepository] Intentando establecer vehículo por defecto: ID $vehicleId")

            // Primero verificar que el vehículo existe
            val vehicleResult = getVehicleById(vehicleId)
            if (vehicleResult.isSuccess) {
                saveDefaultVehicleId(vehicleId.toInt())  // ✅ Guardar como Int en SharedPreferences
                println("✅ [VehicleRepository] Vehículo por defecto establecido: ID $vehicleId")
                Result.success(true)
            } else {
                val errorMsg = "Vehículo no encontrado"
                println("❌ [VehicleRepository] $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] Error estableciendo vehículo por defecto: ${e.message}")
            Result.failure(e)
        }
    }

    // ✅ CORREGIDO: getDefaultVehicle con Long
    suspend fun getDefaultVehicle(): Result<Car?> = withContext(Dispatchers.IO) {
        try {
            val defaultVehicleId = getDefaultVehicleId()
            if (defaultVehicleId == -1) {
                println("ℹ️ [VehicleRepository] No hay vehículo por defecto establecido")
                Result.success(null)
            } else {
                println("🔍 [VehicleRepository] Buscando vehículo por defecto ID: $defaultVehicleId")
                // ✅ Convertir Int a Long para la búsqueda
                val vehicleResult = getVehicleById(defaultVehicleId.toLong())
                if (vehicleResult.isSuccess) {
                    val vehicle = vehicleResult.getOrNull()
                    println("✅ [VehicleRepository] Vehículo por defecto encontrado: $vehicle")
                    Result.success(vehicle)
                } else {
                    // Si el vehículo por defecto no existe, limpiar la preferencia
                    clearDefaultVehicle()
                    println("🔄 [VehicleRepository] Vehículo por defecto eliminado (no encontrado en BD)")
                    Result.success(null)
                }
            }
        } catch (e: Exception) {
            println("💥 [VehicleRepository] Error obteniendo vehículo por defecto: ${e.message}")
            Result.failure(e)
        }
    }

    // ========== MÉTODOS AUXILIARES CORREGIDOS ==========

    private fun saveDefaultVehicleId(vehicleId: Int) {
        prefs.edit().putInt("default_vehicle_id", vehicleId).apply()
        println("💾 [VehicleRepository] Vehículo por defecto guardado: ID $vehicleId")
    }

    private fun getDefaultVehicleId(): Int {
        return prefs.getInt("default_vehicle_id", -1)
    }

    private fun clearDefaultVehicle() {
        prefs.edit().remove("default_vehicle_id").apply()
        println("🧹 [VehicleRepository] Vehículo por defecto eliminado de preferencias")
    }

    private fun getDefaultVehicleIdFromList(vehicles: List<Car>): Int {
        if (vehicles.isEmpty()) return -1

        // Si ya hay un vehículo por defecto y existe en la lista, mantenerlo
        val currentDefault = getDefaultVehicleId()
        if (currentDefault != -1 && vehicles.any { it.id == currentDefault.toLong() }) {
            return currentDefault
        }

        // Si no, usar el primer vehículo de la lista (convertir Long a Int)
        return vehicles.first().id.toInt()
    }

    private fun setAsDefaultVehicleIfFirst(newVehicleId: Long) {
        val currentDefault = getDefaultVehicleId()
        if (currentDefault == -1) {
            saveDefaultVehicleId(newVehicleId.toInt())  // ✅ Convertir Long a Int
            println("⭐ [VehicleRepository] Nuevo vehículo establecido como predeterminado")
        }
    }

    fun isValidPlateFormat(plate: String): Boolean {
        val cleanedPlate = plate.uppercase().replace(" ", "").replace("-", "")
        val plateRegex = Regex("^[A-Z]{2,3}[0-9]{3,4}[A-Z]?$")
        return plateRegex.matches(cleanedPlate)
    }


    suspend fun vehicleExists(vehicleId: Long): Boolean {
        return try {
            val vehiclesResult = getUserVehicles()
            if (vehiclesResult.isSuccess) {
                val vehicles = vehiclesResult.getOrNull() ?: emptyList()
                vehicles.any { it.id == vehicleId } 
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}