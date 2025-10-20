package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpotResponse
import com.example.smarparkinapp.ui.theme.data.model.toParkingSpot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class HomeViewModel : ViewModel() {


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


    fun fetchParkingSpots() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)
                val response: List<ParkingSpotResponse> = apiService.getParkingSpots()
                val spots: List<ParkingSpot> = response.map { it.toParkingSpot() }
                _parkingSpots.value = spots
                // Inicializa la lista filtrada
                _filteredParkingSpots.value = spots.sortedBy { distanceToUser(it.latitude, it.longitude) }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar estacionamientos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
        _filteredParkingSpots.value = _parkingSpots.value.sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    fun searchParking(query: String) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }


    private fun distanceToUser(lat: Double, lng: Double): Double {
        val dx = lat - userLat
        val dy = lng - userLng
        return sqrt(dx * dx + dy * dy)
    }




    fun fetchParkingMapa() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)
                val response = apiService.getParkingMapa()
                val spots = response.results.map { it.toParkingSpot() }
                _parkingSpots.value = spots
                _filteredParkingSpots.value = spots.sortedBy { distanceToUser(it.latitude, it.longitude) }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar datos del mapa"
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
                val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)
                val response = apiService.getMejoresCalificados()
                val spots = response.map { it.toParkingSpot() }
                _filteredParkingSpots.value = spots.sortedByDescending { it.ratingPromedio }
            } catch (e: Exception) {
                // Si falla, filtramos localmente los existentes
                _filteredParkingSpots.value = _parkingSpots.value
                    .filter { it.ratingPromedio >= 4.0 }
                    .sortedByDescending { it.ratingPromedio }
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
                val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)
                val response = apiService.getMasEconomicos()
                val spots = response.map { it.toParkingSpot() }
                _filteredParkingSpots.value = spots.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
            } catch (e: Exception) {
                // Si falla, filtramos localmente los existentes
                _filteredParkingSpots.value = _parkingSpots.value
                    .sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
                    .take(10)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun filterByRating(minRating: Double) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.ratingPromedio >= minRating }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }


    fun filterBySecurity(minSecurity: Int) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.nivelSeguridad >= minSecurity }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }


    fun filterByPrice(maxPrice: Double) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter {
                val price = it.price.toDoubleOrNull() ?: Double.MAX_VALUE
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


    fun resetFilters() {
        _filteredParkingSpots.value = _parkingSpots.value
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    // BÚSQUEDA AVANZADA
    fun advancedSearch(
        query: String = "",
        minRating: Double = 0.0,
        maxPrice: Double? = null,
        minSecurity: Int = 0,
        onlyOpen: Boolean = false,
        onlyWithCameras: Boolean = false,
        only24h: Boolean = false
    ) {
        var filteredList = _parkingSpots.value

        // Aplicar filtros en cascada
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }
        }

        if (minRating > 0) {
            filteredList = filteredList.filter { it.ratingPromedio >= minRating }
        }

        if (maxPrice != null) {
            filteredList = filteredList.filter {
                val price = it.price.toDoubleOrNull() ?: Double.MAX_VALUE
                price <= maxPrice
            }
        }

        if (minSecurity > 0) {
            filteredList = filteredList.filter { it.nivelSeguridad >= minSecurity }
        }

        if (onlyOpen) {
            filteredList = filteredList.filter { it.estaAbierto }
        }

        if (onlyWithCameras) {
            filteredList = filteredList.filter { it.tieneCamaras }
        }

        if (only24h) {
            filteredList = filteredList.filter { it.tieneVigilancia24h }
        }

        _filteredParkingSpots.value = filteredList.sortedBy { distanceToUser(it.latitude, it.longitude) }
    }
}