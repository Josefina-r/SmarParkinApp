// data/repository/VehicleRepository.kt
// data/repository/VehicleRepository.kt
package com.example.smarparkinapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.data.model.Car
// ✅ AGREGAR ESTE IMPORT
import com.example.smarparkinapp.ui.theme.data.model.PaginatedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class VehicleRepository(
    private val context: Context,
    private val apiService: ApiService
) {
    // ... resto del código ...

    private val authManager = AuthManager(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("vehicle_prefs", Context.MODE_PRIVATE)

    // ========== OPERACIONES CRUD DE VEHÍCULOS ==========

    suspend fun getUserVehicles(): Result<List<Car>> = withContext(Dispatchers.IO) {
        try {
            println("🔍 Obteniendo vehículos desde API...")

            val authToken = getAuthToken()
            if (authToken.isEmpty()) {
                println("❌ Token vacío, no se puede obtener vehículos")
                return@withContext Result.failure(Exception("No autenticado. Inicia sesión nuevamente."))
            }

            val response = apiService.getUserVehicles("Bearer $authToken")

            println("🔍 Respuesta obtener vehículos: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    // ✅ CORREGIDO: Extraer los vehículos de la propiedad "results"
                    val carResponses = paginatedResponse.results

                    println("✅ Respuesta paginada - Total: ${paginatedResponse.count}, En esta página: ${carResponses.size}")

                    val vehicles = carResponses.map { carResponse ->
                        Car(
                            id = carResponse.id,
                            plate = carResponse.placa,
                            brand = carResponse.marca,
                            model = carResponse.modelo,
                            color = carResponse.color,
                            active = carResponse.activo
                        )
                    }

                    println("✅ ${vehicles.size} vehículos obtenidos exitosamente")
                    vehicles.forEachIndexed { index, car ->
                        println("   🚗 $index: ${car.plate} - ${car.brand} ${car.model}")
                    }

                    // Guardar en caché local si es necesario
                    saveDefaultVehicleId(getDefaultVehicleIdFromList(vehicles))

                    Result.success(vehicles)
                } else {
                    println("❌ Respuesta paginada vacía")
                    Result.success(emptyList())
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ Error API obteniendo vehículos: $errorBody")

                when (response.code()) {
                    401 -> Result.failure(Exception("Sesión expirada. Inicia sesión nuevamente."))
                    403 -> Result.failure(Exception("No tienes permisos para ver los vehículos."))
                    404 -> {
                        println("⚠️ No se encontraron vehículos, retornando lista vacía")
                        Result.success(emptyList())
                    }
                    else -> Result.failure(Exception("Error del servidor: ${response.code()} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            println("❌ Exception obteniendo vehículos: ${e.message}")
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
            println("🚗 Creando vehículo en API...")
            println("   📝 Datos: placa=$plate, marca=$brand, modelo=$model, color=$color")

            val authToken = getAuthToken()
            if (authToken.isEmpty()) {
                println("❌ Token vacío, no se puede crear vehículo")
                return@withContext Result.failure(Exception("No autenticado. Inicia sesión nuevamente."))
            }

            // Validar formato de placa
            if (!isValidPlateFormat(plate)) {
                return@withContext Result.failure(Exception("Formato de placa inválido. Use: ABC123 o similar"))
            }

            val carRequest = com.example.smarparkinapp.ui.theme.data.api.CarRequest(
                placa = plate.uppercase().replace(" ", "").replace("-", ""),
                marca = brand,
                modelo = model,
                color = color,
                year = Calendar.getInstance().get(Calendar.YEAR)
            )

            println("📤 JSON enviado a API: $carRequest")

            val response = apiService.addCar("Bearer $authToken", carRequest)

            println("📥 Respuesta crear vehículo: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val carResponse = response.body()
                if (carResponse != null) {
                    val newCar = Car(
                        id = carResponse.id,
                        plate = carResponse.placa,
                        brand = carResponse.marca,
                        model = carResponse.modelo,
                        color = carResponse.color,
                        active = carResponse.activo
                    )
                    println("✅ Vehículo creado exitosamente: $newCar")

                    // Establecer como vehículo por defecto si es el primero
                    setAsDefaultVehicleIfFirst(newCar.id)

                    Result.success(newCar)
                } else {
                    println("❌ Respuesta vacía del servidor")
                    Result.failure(Exception("Error: Respuesta vacía del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ Error API creando vehículo: $errorBody")

                when {
                    response.code() == 400 -> {
                        when {
                            errorBody.contains("placa", ignoreCase = true) ->
                                Result.failure(Exception("La placa ya está registrada"))
                            errorBody.contains("exist", ignoreCase = true) ->
                                Result.failure(Exception("El vehículo ya existe"))
                            else -> Result.failure(Exception("Datos inválidos: $errorBody"))
                        }
                    }
                    response.code() == 401 -> Result.failure(Exception("Sesión expirada. Inicia sesión nuevamente."))
                    response.code() == 403 -> Result.failure(Exception("No tienes permisos para crear vehículos"))
                    response.code() == 409 -> Result.failure(Exception("El vehículo ya existe"))
                    else -> Result.failure(Exception("Error del servidor: ${response.code()} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            println("❌ Exception creando vehículo: ${e.message}")
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun updateVehicle(vehicle: Car): Result<Car> = withContext(Dispatchers.IO) {
        try {
            println("🔄 Actualizando vehículo ID: ${vehicle.id}")

            val authToken = getAuthToken()
            if (authToken.isEmpty()) {
                return@withContext Result.failure(Exception("No autenticado"))
            }

            // Validar formato de placa
            if (!isValidPlateFormat(vehicle.plate)) {
                return@withContext Result.failure(Exception("Formato de placa inválido"))
            }

            val carRequest = com.example.smarparkinapp.ui.theme.data.api.CarRequest(
                placa = vehicle.plate,
                marca = vehicle.brand,
                modelo = vehicle.model,
                color = vehicle.color,
                year = Calendar.getInstance().get(Calendar.YEAR)
            )

            val response = apiService.updateVehicle("Bearer $authToken", vehicle.id, carRequest)

            println("🔍 Respuesta actualizar vehículo: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                val carResponse = response.body()
                if (carResponse != null) {
                    val updatedCar = Car(
                        id = carResponse.id,
                        plate = carResponse.placa,
                        brand = carResponse.marca,
                        model = carResponse.modelo,
                        color = carResponse.color,
                        active = carResponse.activo
                    )
                    println("✅ Vehículo actualizado exitosamente: $updatedCar")
                    Result.success(updatedCar)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error actualizando vehículo: $errorBody"))
            }
        } catch (e: Exception) {
            println("❌ Error actualizando vehículo: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteVehicle(vehicleId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            println("🗑️ Eliminando vehículo ID: $vehicleId")

            val authToken = getAuthToken()
            if (authToken.isEmpty()) {
                return@withContext Result.failure(Exception("No autenticado"))
            }

            val response = apiService.deleteVehicle("Bearer $authToken", vehicleId)

            println("🔍 Respuesta eliminar vehículo: ${response.code()} - ${response.message()}")

            if (response.isSuccessful) {
                // Si se eliminó el vehículo por defecto, limpiar la preferencia
                if (getDefaultVehicleId() == vehicleId) {
                    clearDefaultVehicle()
                }
                println("✅ Vehículo eliminado exitosamente")
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception("Error eliminando vehículo: $errorBody"))
            }
        } catch (e: Exception) {
            println("❌ Error eliminando vehículo: ${e.message}")
            Result.failure(e)
        }
    }

    // ========== OPERACIONES CON VEHÍCULO POR DEFECTO ==========

    suspend fun getVehicleById(vehicleId: Int): Result<Car> = withContext(Dispatchers.IO) {
        try {
            println("🔍 Buscando vehículo por ID: $vehicleId")

            val authToken = getAuthToken()
            if (authToken.isEmpty()) {
                return@withContext Result.failure(Exception("No autenticado"))
            }

            // Obtener todos los vehículos y filtrar por ID
            val vehiclesResult = getUserVehicles()
            if (vehiclesResult.isSuccess) {
                val vehicles = vehiclesResult.getOrNull() ?: emptyList()
                val vehicle = vehicles.find { it.id == vehicleId }
                if (vehicle != null) {
                    println("✅ Vehículo encontrado: $vehicle")
                    Result.success(vehicle)
                } else {
                    println("❌ Vehículo no encontrado con ID: $vehicleId")
                    Result.failure(Exception("Vehículo no encontrado"))
                }
            } else {
                Result.failure(vehiclesResult.exceptionOrNull() ?: Exception("Error obteniendo vehículos"))
            }
        } catch (e: Exception) {
            println("❌ Error obteniendo vehículo por ID: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun setDefaultVehicle(vehicleId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Primero verificar que el vehículo existe
            val vehicleResult = getVehicleById(vehicleId)
            if (vehicleResult.isSuccess) {
                saveDefaultVehicleId(vehicleId)
                println("⭐ Vehículo por defecto establecido: ID $vehicleId")
                Result.success(true)
            } else {
                Result.failure(vehicleResult.exceptionOrNull() ?: Exception("Vehículo no encontrado"))
            }
        } catch (e: Exception) {
            println("❌ Error estableciendo vehículo por defecto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDefaultVehicle(): Result<Car?> = withContext(Dispatchers.IO) {
        try {
            val defaultVehicleId = getDefaultVehicleId()
            if (defaultVehicleId == -1) {
                println("ℹ️ No hay vehículo por defecto establecido")
                Result.success(null)
            } else {
                val vehicleResult = getVehicleById(defaultVehicleId)
                if (vehicleResult.isSuccess) {
                    Result.success(vehicleResult.getOrNull())
                } else {
                    // Si el vehículo por defecto no existe, limpiar la preferencia
                    clearDefaultVehicle()
                    println("🔄 Vehículo por defecto eliminado (no encontrado)")
                    Result.success(null)
                }
            }
        } catch (e: Exception) {
            println("❌ Error obteniendo vehículo por defecto: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getLastCreatedVehicle(): Result<Car?> = withContext(Dispatchers.IO) {
        try {
            val vehiclesResult = getUserVehicles()
            if (vehiclesResult.isSuccess) {
                val vehicles = vehiclesResult.getOrNull() ?: emptyList()
                val lastVehicle = vehicles.maxByOrNull { it.id }
                Result.success(lastVehicle)
            } else {
                Result.failure(vehiclesResult.exceptionOrNull() ?: Exception("Error obteniendo vehículos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== VALIDACIONES Y UTILIDADES ==========

    fun isValidPlateFormat(plate: String): Boolean {
        val cleanedPlate = plate.uppercase().replace(" ", "").replace("-", "")
        // Formatos comunes: ABC123, ABC12D, AB123C, etc.
        val plateRegex = Regex("^[A-Z]{2,3}[0-9]{3,4}[A-Z]?$")
        val isValid = plateRegex.matches(cleanedPlate)
        println("🔍 Validación placa '$plate': ${if (isValid) "✅ VÁLIDA" else "❌ INVÁLIDA"}")
        return isValid
    }

    fun isUserAuthenticated(): Boolean {
        return getAuthToken().isNotEmpty()
    }

    fun clearAuthData() {
        authManager.logout()
        clearDefaultVehicle()
        println("🔓 Datos de autenticación y vehículo por defecto limpiados")
    }

    // ========== MÉTODOS PRIVADOS ==========

    private fun getAuthToken(): String {
        val token = authManager.getAuthToken() ?: ""
        println("🔐 [VehicleRepository] Token desde AuthManager: ${if (token.isNotEmpty()) "✅ PRESENTE (${token.length} chars)" else "❌ VACÍO"}")
        return token
    }

    private fun saveDefaultVehicleId(vehicleId: Int) {
        prefs.edit().putInt("default_vehicle_id", vehicleId).apply()
        println("💾 Vehículo por defecto guardado: ID $vehicleId")
    }

    private fun getDefaultVehicleId(): Int {
        return prefs.getInt("default_vehicle_id", -1)
    }

    private fun clearDefaultVehicle() {
        prefs.edit().remove("default_vehicle_id").apply()
        println("🧹 Vehículo por defecto eliminado")
    }

    private fun getDefaultVehicleIdFromList(vehicles: List<Car>): Int {
        // Si no hay vehículos, retornar -1
        if (vehicles.isEmpty()) return -1

        // Si ya hay un vehículo por defecto y existe en la lista, mantenerlo
        val currentDefault = getDefaultVehicleId()
        if (currentDefault != -1 && vehicles.any { it.id == currentDefault }) {
            return currentDefault
        }

        // Si no, usar el primer vehículo de la lista
        return vehicles.first().id
    }

    private fun setAsDefaultVehicleIfFirst(newVehicleId: Int) {
        val currentDefault = getDefaultVehicleId()
        if (currentDefault == -1) {
            saveDefaultVehicleId(newVehicleId)
            println("⭐ Nuevo vehículo establecido como predeterminado (era el primero)")
        }
    }

    fun debugAuthStatus() {
        val token = authManager.getAuthToken()
        val defaultVehicleId = getDefaultVehicleId()

        println("=== 🔍 VEHICLE REPOSITORY DEBUG ===")
        println("🔐 Token: ${if (token != null) "PRESENTE (${token.length} chars)" else "AUSENTE"}")
        println("🚗 Vehículo por defecto ID: ${if (defaultVehicleId != -1) defaultVehicleId else "NO ESTABLECIDO"}")
        println("🔐 Autenticado: ${isUserAuthenticated()}")
        println("=== FIN DEBUG ===")
    }
}