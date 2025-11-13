package com.example.smarparkinapp.ui.theme.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.toParkingSpot
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*
import com.example.smarparkinapp.ui.theme.data.model.*

class HomeViewModel(
    private val locationViewModel: LocationViewModel
) : ViewModel() {

    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots.asStateFlow()

    private val _filteredParkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val filteredParkingSpots: StateFlow<List<ParkingSpot>> = _filteredParkingSpots.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    // ✅ NUEVOS MÉTODOS PARA MANEJAR ERRORES
    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    init {
        // Observar cambios de ubicación del usuario
        viewModelScope.launch {
            locationViewModel.currentLocation.collect { location ->
                location?.let {
                    _userLocation.value = it
                    // Actualizar estacionamientos cercanos cuando cambia la ubicación
                    if (_parkingSpots.value.isNotEmpty()) {
                        _filteredParkingSpots.value = _parkingSpots.value
                            .sortedBy { spot -> calculateDistance(it, spot) }
                    }
                }
            }
        }
    }

    // ✅ MÉTODOS PARA UBICACIÓN
    fun startLocationUpdates() {
        locationViewModel.startLocationUpdates()
    }

    fun getCurrentLocation() {
        locationViewModel.getCurrentLocation()
    }

    // ✅ MÉTODO CORREGIDO - CON ARRAY DIRECTO
    fun fetchParkingSpots() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)

                // ✅ Ahora devuelve List<ParkingSpotResponse> directamente (sin Response)
                val response = apiService.getParkingsForMap(disponibles = true)

                // ✅ La respuesta YA es un List, no necesita .body()
                val spots: List<ParkingSpot> = response.mapNotNull { responseItem ->
                    try {
                        responseItem.toParkingSpot()
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Error convirtiendo parking ${responseItem.id}: ${e.message}")
                        null
                    }
                }

                _parkingSpots.value = spots

                // Ordenar por distancia
                val currentLocation = _userLocation.value
                _filteredParkingSpots.value = if (currentLocation != null) {
                    spots.sortedBy { calculateDistance(currentLocation, it) }
                } else {
                    spots
                }

                Log.d("HomeViewModel", "✅ ${spots.size} estacionamientos cargados")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "💥 Error: ${e.message}", e)
                setErrorMessage("Error al cargar estacionamientos: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    // ✅ BUSCAR ESTACIONAMIENTOS CERCANOS
    fun fetchParkingsCerca(radiusKm: Double = 5.0) {
        val currentLocation = _userLocation.value
        if (currentLocation == null) {
            setErrorMessage("No se pudo obtener tu ubicación")
            return
        }

        _filteredParkingSpots.value = _parkingSpots.value
            .filter { calculateDistance(currentLocation, it) <= radiusKm * 1000 }
            .sortedBy { calculateDistance(currentLocation, it) }
    }

    // ✅ BÚSQUEDA POR TEXTO
    fun searchParking(query: String) {
        if (query.isEmpty()) {
            val currentLocation = _userLocation.value
            _filteredParkingSpots.value = if (currentLocation != null) {
                _parkingSpots.value.sortedBy { calculateDistance(currentLocation, it) }
            } else {
                _parkingSpots.value
            }
        } else {
            val currentLocation = _userLocation.value
            val filteredList = _parkingSpots.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
            }

            _filteredParkingSpots.value = if (currentLocation != null) {
                filteredList.sortedBy { calculateDistance(currentLocation, it) }
            } else {
                filteredList
            }
        }
    }

    // ✅ MEJORES CALIFICADOS
    fun fetchMejoresCalificados() {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.ratingPromedio >= 4.0 }
            .sortedByDescending { it.ratingPromedio }
    }

    // ✅ MÁS ECONÓMICOS
    fun fetchMasEconomicos() {
        _filteredParkingSpots.value = _parkingSpots.value
            .sortedBy {
                it.price.replace("$", "").replace(",", "").toDoubleOrNull() ?: Double.MAX_VALUE
            }
    }

    // ✅ FILTRO POR SEGURIDAD
    fun filterBySecurity(minSecurity: Int) {
        val currentLocation = _userLocation.value
        val filteredList = _parkingSpots.value.filter { it.nivelSeguridad >= minSecurity }

        _filteredParkingSpots.value = if (currentLocation != null) {
            filteredList.sortedBy { calculateDistance(currentLocation, it) }
        } else {
            filteredList
        }
    }

    // ✅ FILTRO POR PRECIO MÁXIMO
    fun filterByPrice(maxPrice: Double) {
        val currentLocation = _userLocation.value
        val filteredList = _parkingSpots.value.filter {
            val price = it.price.replace("$", "").replace(",", "").toDoubleOrNull() ?: Double.MAX_VALUE
            price <= maxPrice
        }

        _filteredParkingSpots.value = if (currentLocation != null) {
            filteredList.sortedBy { calculateDistance(currentLocation, it) }
        } else {
            filteredList
        }
    }

    // ✅ RESET FILTROS
    fun resetFilters() {
        val currentLocation = _userLocation.value
        _filteredParkingSpots.value = if (currentLocation != null) {
            _parkingSpots.value.sortedBy { calculateDistance(currentLocation, it) }
        } else {
            _parkingSpots.value
        }
    }

    // ✅ CÁLCULO DE DISTANCIA (Haversine)
    fun calculateDistance(userLocation: LatLng, parking: ParkingSpot): Double {
        val earthRadius = 6371000.0 // metros
        val lat1 = Math.toRadians(userLocation.latitude)
        val lon1 = Math.toRadians(userLocation.longitude)
        val lat2 = Math.toRadians(parking.latitude)
        val lon2 = Math.toRadians(parking.longitude)
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    // ✅ FORMATO DE DISTANCIA LEGIBLE
    fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${meters.toInt()} m"
            else -> "${(meters / 1000).format(1)} km"
        }
    }

    // ✅ ACTUALIZAR UBICACIÓN MANUAL
    fun updateUserLocation(lat: Double, lng: Double) {
        _userLocation.value = LatLng(lat, lng)
        if (_parkingSpots.value.isNotEmpty()) {
            _filteredParkingSpots.value = _parkingSpots.value
                .sortedBy { calculateDistance(LatLng(lat, lng), it) }
        }
    }
}

// ✅ EXTENSIÓN PARA FORMATEAR DECIMALES
private fun Double.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}
