package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class HomeViewModel(private val context: Context) : ViewModel() {

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    private val _filteredParkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val filteredParkingSpots: StateFlow<List<ParkingSpot>> = _filteredParkingSpots.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var userLat: Double = -8.111667
    private var userLng: Double = -79.028889

    private val apiService = RetrofitInstance.getAuthenticatedApiService(context)
    private val authManager = AuthManager(context)

    init {
        println("🎯 [HOME_VIEWMODEL] Inicializado - Cargando datos reales de Django")
        fetchParkingSpots()
    }

    // ✅ MÉTODOS PARA LOS FILTROS DEL HOME SCREEN
    fun fetchMasEconomicos() {
        println("💰 [VIEWMODEL] Filtrando por precio (más económicos)")
        _filteredParkingSpots.value = _parkingSpots.value.sortedBy { spot ->
            parsePrice(spot.price)
        }
    }

    fun fetchMejoresCalificados() {
        println("⭐ [VIEWMODEL] Filtrando por mejor rating")
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.ratingPromedio >= 4.0 }
            .sortedByDescending { it.ratingPromedio }
    }

    fun filterBySecurity(minSecurity: Int) {
        println("🛡️ [VIEWMODEL] Filtrando por seguridad mínima: $minSecurity")
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.nivelSeguridad >= minSecurity }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    private fun parsePrice(price: String): Double {
        return try {
            price.replace("S/", "")
                .replace(",", "")
                .trim()
                .toDoubleOrNull() ?: Double.MAX_VALUE
        } catch (e: Exception) {
            Double.MAX_VALUE
        }
    }

    // FUNCIÓN PRINCIPAL CORREGIDA - SOLO DATOS REALES
    fun fetchParkingSpots() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                println("🎯 [FETCH] === INICIANDO CARGA DESDE DJANGO ===")

                val response = apiService.getApprovedParkingLots()
                println("🎯 [FETCH] Código de respuesta: ${response.code()}")
                println("🎯 [FETCH] ¿Éxito?: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val parkingLotResponse = response.body()

                    if (parkingLotResponse == null) {
                        _errorMessage.value = "Respuesta vacía del servidor"
                        println("❌ [FETCH] Respuesta body es null")
                        return@launch
                    }

                    println("🎯 [FETCH] Total de estacionamientos: ${parkingLotResponse.count}")
                    println("🎯 [FETCH] Resultados: ${parkingLotResponse.results?.size ?: 0}")

                    // Verificar si hay resultados
                    if (parkingLotResponse.results.isNullOrEmpty()) {
                        _errorMessage.value = "No hay estacionamientos disponibles en el sistema"
                        _parkingSpots.value = emptyList()
                        _filteredParkingSpots.value = emptyList()
                        println("⚠️ [FETCH] Lista de estacionamientos vacía")
                    } else {
                        // Convertir cada ParkingLot a ParkingSpot
                        val spots = mutableListOf<ParkingSpot>()

                        parkingLotResponse.results.forEachIndexed { index, parkingLot ->
                            println("\n🎯 [PARKING_$index] ==========================")
                            println("   ID: ${parkingLot.id}")
                            println("   Nombre: ${parkingLot.nombre}")
                            println("   Dirección: ${parkingLot.direccion}")
                            println("   Activo: ${parkingLot.activo}")
                            println("   Aprobado: ${parkingLot.aprobado}")
                            println("   Plazas disponibles: ${parkingLot.plazas_disponibles}")
                            println("   Tarifa: ${parkingLot.tarifa_hora}")
                            println("   Coordenadas: ${parkingLot.coordenadas}")
                            println("   Rating: ${parkingLot.rating_promedio}")
                            println("   Seguridad: ${parkingLot.nivel_seguridad}")

                            val spot = parkingLot.toParkingSpot()
                            spots.add(spot)
                            println("   → Convertido: ${spot.name} - Abierto: ${spot.estaAbierto}")
                        }

                        _parkingSpots.value = spots
                        _filteredParkingSpots.value = spots
                        _errorMessage.value = null

                        println("\n✅ [FETCH] CARGA EXITOSA: ${spots.size} estacionamientos reales cargados")
                    }

                } else {
                    // Error HTTP
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _errorMessage.value = "Error del servidor: ${response.code()} - $errorBody"
                    println("❌ [FETCH] Error HTTP ${response.code()}: $errorBody")
                }

            } catch (e: Exception) {
                // Error de conexión
                _errorMessage.value = "Error de conexión: ${e.message}"
                println("💥 [FETCH] Excepción: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
                println("🎯 [FETCH] Carga finalizada - Loading: false")
            }
        }
    }

    // FUNCIÓN DE CONVERSIÓN MEJORADA
    private fun ParkingLot.toParkingSpot(): ParkingSpot {
        return try {
            // Coordenadas
            val (lat, lng) = parseCoordinates(this.coordenadas)

            // Verificar si está abierto
            val isOpen = this.activo == true && this.aprobado == true

            // Precio formateado
            val priceFormatted = if (this.tarifa_hora != null) {
                "S/ ${"%.2f".format(this.tarifa_hora)}"
            } else {
                "S/ 0.00"
            }

            // Plazas disponibles
            val availableSpots = this.plazas_disponibles ?: 0

            ParkingSpot(
                id = this.id.toInt(),
                name = this.nombre ?: "Estacionamiento ${this.id}",
                address = this.direccion ?: "Dirección no especificada",
                price = priceFormatted,
                availableSpots = availableSpots,
                latitude = lat,
                longitude = lng,
                nivelSeguridad = parseSecurityLevel(this.nivel_seguridad),
                ratingPromedio = this.rating_promedio ?: 0.0,
                totalResenas = this.total_resenas ?: 0,
                estaAbierto = isOpen && availableSpots > 0,
                tieneCamaras = hasCameras(this.nivel_seguridad),
                tieneVigilancia24h = has24hSurveillance(this.nivel_seguridad)
            )
        } catch (e: Exception) {
            println("💥 [CONVERSION] Error convirtiendo ${this.nombre}: ${e.message}")
            // En caso de error, crear un spot básico pero marcarlo como no disponible
            ParkingSpot(
                id = this.id.toInt(),
                name = this.nombre ?: "Error",
                address = "Error en datos",
                price = "S/ 0.00",
                availableSpots = 0,
                latitude = -8.111667,
                longitude = -79.028889,
                nivelSeguridad = 1,
                ratingPromedio = 0.0,
                totalResenas = 0,
                estaAbierto = false,
                tieneCamaras = false,
                tieneVigilancia24h = false
            )
        }
    }

    private fun parseCoordinates(coordenadas: String?): Pair<Double, Double> {
        return try {
            when {
                coordenadas.isNullOrEmpty() -> {
                    println("⚠️ [COORD] Coordenadas vacías para estacionamiento")
                    -8.111667 to -79.028889 // Trujillo por defecto
                }
                coordenadas.contains(",") -> {
                    val parts = coordenadas.split(",")
                    if (parts.size >= 2) {
                        val lat = parts[0].trim().toDouble()
                        val lng = parts[1].trim().toDouble()
                        lat to lng
                    } else {
                        -8.111667 to -79.028889
                    }
                }
                else -> -8.111667 to -79.028889
            }
        } catch (e: Exception) {
            println("💥 [COORD] Error parseando coordenadas: '$coordenadas' - ${e.message}")
            -8.111667 to -79.028889
        }
    }

    private fun parseSecurityLevel(nivel: String?): Int {
        return when (nivel?.lowercase()?.trim()) {
            "baja", "low" -> 1
            "media", "medium" -> 2
            "buena", "good" -> 3
            "alta", "high" -> 4
            "muy alta", "very high" -> 5
            else -> 1 // Por defecto
        }
    }

    private fun hasCameras(nivel: String?): Boolean {
        return parseSecurityLevel(nivel) >= 3
    }

    private fun has24hSurveillance(nivel: String?): Boolean {
        return parseSecurityLevel(nivel) >= 4
    }

    // MÉTODOS DE FILTRO (mantener igual)
    fun searchParking(query: String) {
        val filtered = if (query.isBlank()) {
            _parkingSpots.value
        } else {
            _parkingSpots.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }
        }
        _filteredParkingSpots.value = filtered.sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
        _filteredParkingSpots.value = _parkingSpots.value.sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    private fun distanceToUser(lat: Double, lng: Double): Double {
        val dx = lat - userLat
        val dy = lng - userLng
        return sqrt(dx * dx + dy * dy)
    }

    fun refreshData() {
        println("🔄 [REFRESH] Recargando datos desde Django...")
        fetchParkingSpots()
    }
}