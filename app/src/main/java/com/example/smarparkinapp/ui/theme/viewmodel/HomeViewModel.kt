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

class HomeViewModel : ViewModel() {
    private val _parkingSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val parkingSpots: StateFlow<List<ParkingSpot>> = _parkingSpots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchParkingSpots() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val apiService = ApiClient.retrofit.create(ApiService::class.java)
                val response: List<ParkingSpotResponse> = apiService.getParkingSpots()
                // mapear aquí 👇
                _parkingSpots.value = response.map { it.toParkingSpot() }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar estacionamientos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
