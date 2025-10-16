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

    // Ubicación del usuario para calcular cercanía
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

    // Actualiza ubicación del usuario y recalcula cercanía
    fun updateUserLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
        _filteredParkingSpots.value = _parkingSpots.value.sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    // Búsqueda por nombre en tiempo real
    fun searchParking(query: String) {
        _filteredParkingSpots.value = _parkingSpots.value
            .filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy { distanceToUser(it.latitude, it.longitude) }
    }

    // Calcula "distancia" simple (no es Haversine exacta, pero sirve para ordenamiento rápido)
    private fun distanceToUser(lat: Double, lng: Double): Double {
        val dx = lat - userLat
        val dy = lng - userLng
        return sqrt(dx * dx + dy * dy)
    }
}
