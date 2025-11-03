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
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class HomeViewModel(private val context: Context) : ViewModel() {

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots

    private val _filteredParkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val filteredParkingSpots: StateFlow<List<ParkingSpot>> = _filteredParkingSpots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var userLat: Double = 0.0
    private var userLng: Double = 0.0

    private val apiService = RetrofitInstance.getAuthenticatedApiService(context)
    private val authManager = AuthManager(context)

    // FUNCIÓN AUXILIAR PARA CONVERTIR ParkingLot A ParkingSpot
    private fun ParkingLot.toParkingSpot(): ParkingSpot {
        println("🎯 [TO_PARKING_SPOT] Transformando: $nombre")
        println("🎯 [TO_PARKING_SPOT] Activo: $activo, Aprobado: $aprobado")

        // Parsear coordenadas
        val (lat, lng) = parseCoordinates(this.coordenadas)

        // Determinar si está abierto
        val isOpen = calculateIsOpen(this.horario_apertura, this.horario_cierre)

        val spot = ParkingSpot(
            id = this.id.toInt(),
            name = this.nombre,
            address = this.direccion,
            price = "S/ ${"%.2f".format(this.tarifa_hora)}",
            availableSpots = this.plazas_disponibles,
            latitude = lat,
            longitude = lng,
            nivelSeguridad = parseSecurityLevel(this.nivel_seguridad),
            ratingPromedio = this.rating_promedio ?: 0.0,
            totalResenas = this.total_resenas ?: 0,
            estaAbierto = this.activo && this.aprobado && isOpen,
            tieneCamaras = hasCameras(this.nivel_seguridad),
            tieneVigilancia24h = has24hSurveillance(this.nivel_seguridad)
        )

        println("🎯 [TO_PARKING_SPOT] Resultado: ${spot.name} -> Activo: ${spot.estaAbierto}")
        return spot
    }

    // FUNCIONES AUXILIARES PARA EL MAPEO
    private fun parseCoordinates(coordenadas: String?): Pair<Double, Double> {
        println("🎯 [PARSE_COORD] Coordenadas: $coordenadas")

        if (coordenadas.isNullOrEmpty()) {
            println("🎯 [PARSE_COORD] Coordenadas vacías, usando Trujillo por defecto")
            return -8.111667 to -79.028889
        }

        return try {
            val parts = coordenadas.split(",")
            if (parts.size >= 2) {
                val lat = parts[0].trim().toDouble()
                val lng = parts[1].trim().toDouble()
                println("🎯 [PARSE_COORD] Parseadas correctamente: $lat, $lng")
                lat to lng
            } else {
                println("🎯 [PARSE_COORD] Formato incorrecto, usando default")
                -8.111667 to -79.028889
            }
        } catch (e: Exception) {
            println("🎯 [PARSE_COORD] Error parseando: ${e.message}")
            -8.111667 to -79.028889
        }
    }

    private fun parseSecurityLevel(nivel: String?): Int {
        val securityLevel = when (nivel?.lowercase()) {
            "baja" -> 1
            "media" -> 2
            "buena" -> 3
            "alta" -> 4
            "muy alta" -> 5
            else -> {
                println("🎯 [SECURITY] Nivel desconocido: '$nivel', usando 1")
                1
            }
        }
        println("🎯 [SECURITY] Nivel: '$nivel' -> $securityLevel")
        return securityLevel
    }

    private fun hasCameras(nivel: String?): Boolean {
        val hasCams = when (nivel?.lowercase()) {
            "buena", "alta", "muy alta" -> true
            else -> false
        }
        println("🎯 [CAMERAS] Nivel: '$nivel' -> Tiene cámaras: $hasCams")
        return hasCams
    }

    private fun has24hSurveillance(nivel: String?): Boolean {
        val has24h = when (nivel?.lowercase()) {
            "alta", "muy alta" -> true
            else -> false
        }
        println("🎯 [24H] Nivel: '$nivel' -> Vigilancia 24h: $has24h")
        return has24h
    }

    private fun calculateIsOpen(apertura: String?, cierre: String?): Boolean {
        // Para testing, considerar siempre abierto
        val isOpen = true
        println("🎯 [HORARIO] Apertura: $apertura, Cierre: $cierre -> Abierto: $isOpen")
        return isOpen
    }

    // FUNCIONES PRINCIPALES CORREGIDAS
    fun fetchParkingSpots() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                println("🎯 [HOME] Iniciando carga de estacionamientos...")

                // ✅ CORREGIDO: Usar el endpoint correcto
                val response = apiService.getApprovedParkingLots()
                println("🎯 [HOME] Response code: ${response.code()}")
                println("🎯 [HOME] Response isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val parkingLotResponse = response.body()
                    println("🎯 [HOME] ParkingLotResponse recibido")
                    println("🎯 [HOME] Count: ${parkingLotResponse?.count ?: 0}")
                    println("🎯 [HOME] Results size: ${parkingLotResponse?.results?.size ?: 0}")

                    // DEBUG: Mostrar cada estacionamiento recibido
                    parkingLotResponse?.results?.forEachIndexed { index, parkingLot ->
                        println("🎯 [HOME] ParkingLot $index:")
                        println("   ID: ${parkingLot.id}")
                        println("   Nombre: ${parkingLot.nombre}")
                        println("   Activo: ${parkingLot.activo}")
                        println("   Aprobado: ${parkingLot.aprobado}")
                        println("   Plazas disponibles: ${parkingLot.plazas_disponibles}")
                        println("   Coordenadas: ${parkingLot.coordenadas}")
                        println("   Tarifa: ${parkingLot.tarifa_hora}")
                    }

                    val spots = parkingLotResponse?.results?.map { parkingLot ->
                        val spot = parkingLot.toParkingSpot()
                        println("🎯 [HOME] Transformado: ${spot.name} -> Activo: ${spot.estaAbierto}")
                        spot
                    } ?: emptyList()

                    _parkingSpots.value = spots
                    _filteredParkingSpots.value = spots
                    println("✅ [HOME] Estacionamientos cargados: ${spots.size}")

                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _errorMessage.value = "Error ${response.code()}: $errorBody"
                    println("❌ [HOME] Error response: $errorBody")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.message}"
                println("💥 [HOME] Exception: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchParkingMapa() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                println("🎯 [MAPA] Cargando datos para mapa...")
                val response = apiService.getApprovedParkingLots()

                if (response.isSuccessful) {
                    val parkingLotResponse = response.body()
                    val spots = parkingLotResponse?.results?.map { it.toParkingSpot() } ?: emptyList()
                    _parkingSpots.value = spots
                    _filteredParkingSpots.value = spots
                    println("✅ [MAPA] Datos cargados: ${spots.size}")
                } else {
                    _errorMessage.value = "Error al cargar mapa: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión en mapa: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMejoresCalificados() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                println("🎯 [RATING] Cargando mejores calificados...")
                val response = apiService.getTopRatedParkingLots()

                if (response.isSuccessful) {
                    val parkingLots = response.body() ?: emptyList()
                    val spots = parkingLots.map { it.toParkingSpot() }
                    _filteredParkingSpots.value = spots.sortedByDescending { it.ratingPromedio }
                    println("✅ [RATING] Mejores calificados cargados: ${spots.size}")
                } else {
                    applyLocalRatingFilter()
                    _errorMessage.value = "Usando filtro local para rating"
                }
            } catch (e: Exception) {
                applyLocalRatingFilter()
                _errorMessage.value = "Usando filtro local: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMasEconomicos() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                println("🎯 [PRECIO] Cargando más económicos...")
                val response = apiService.getMasEconomicos()

                if (response.isSuccessful) {
                    val parkingLots = response.body() ?: emptyList()
                    val spots = parkingLots.map { it.toParkingSpot() }
                    _filteredParkingSpots.value = spots.sortedBy { parsePrice(it.price) }
                    println("✅ [PRECIO] Más económicos cargados: ${spots.size}")
                } else {
                    applyLocalPriceFilter()
                    _errorMessage.value = "Usando filtro local para precios"
                }
            } catch (e: Exception) {
                applyLocalPriceFilter()
                _errorMessage.value = "Usando filtro local: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ MÉTODOS DE FILTRO
    fun filterBySecurity(minSecurity: Int) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.nivelSeguridad >= minSecurity }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun filterByRating(minRating: Double) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.ratingPromedio >= minRating }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun filterByPrice(maxPrice: Double) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter {
                val price = parsePrice(it.price)
                price <= maxPrice
            }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun filterByVigilancia24h() {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.tieneVigilancia24h }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun filterByCamaras() {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.tieneCamaras }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun filterByAbiertos() {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.estaAbierto }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    // FUNCIONES AUXILIARES
    private fun applyLocalRatingFilter() {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.ratingPromedio >= 4.0 }
            .sortedByDescending { it.ratingPromedio }
    }

    private fun applyLocalPriceFilter() {
        _filteredParkingSpots.value = _parkingSpots.value
            .sortedBy { parsePrice(it.price) }
            .take(10)
    }

    private fun parsePrice(price: String): Double {
        return price.replace("S/", "").replace(",", "").trim().toDoubleOrNull() ?: Double.MAX_VALUE
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
        _filteredParkingSpots.value = _parkingSpots.value.sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun searchParking(query: String) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    private fun distanceToUser(lat: Double, lng: Double): Double {
        val dx = lat - userLat
        val dy = lng - userLng
        return sqrt(dx * dx + dy * dy)
    }

    fun resetFilters() {
        _filteredParkingSpots.value = _parkingSpots.value
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun fetchNearbyParking() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                if (userLat != 0.0 && userLng != 0.0) {
                    println("🎯 [CERCA] Buscando estacionamientos cercanos...")
                    val response = apiService.getNearbyParkingLots(userLat, userLng)

                    if (response.isSuccessful) {
                        val parkingLots = response.body() ?: emptyList()
                        val spots = parkingLots.map { it.toParkingSpot() }
                        _parkingSpots.value = spots
                        _filteredParkingSpots.value = spots.sortedBy { distanceToUser(it.latitude, it.longitude) }
                        println("✅ [CERCA] Estacionamientos cercanos cargados: ${spots.size}")
                    } else {
                        _errorMessage.value = "Error al cargar cercanos: ${response.code()}"
                    }
                } else {
                    _errorMessage.value = "Ubicación no disponible"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar cercanos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}