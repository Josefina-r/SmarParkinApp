package com.example.smarparkinapp.ui.theme.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.services.LocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel(private val locationService: LocationService) : ViewModel() {

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _locationUpdatesActive = MutableStateFlow(false)
    val locationUpdatesActive: StateFlow<Boolean> = _locationUpdatesActive.asStateFlow()

    // ✅ Obtener ubicación actual una vez
    fun getCurrentLocation() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val task = locationService.getCurrentLocation()
                task.addOnSuccessListener { location ->
                    _isLoading.value = false
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        _currentLocation.value = latLng
                    } ?: run {
                        _errorMessage.value = "No se pudo obtener la ubicación"
                    }
                }.addOnFailureListener { exception ->
                    _isLoading.value = false
                    _errorMessage.value = "Error: ${exception.message}"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Error obteniendo ubicación: ${e.message}"
            }
        }
    }

    // ✅ Iniciar actualizaciones continuas de ubicación
    fun startLocationUpdates() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                locationService.getLocationUpdates()
                    .catch { exception ->
                        _errorMessage.value = "Error en actualizaciones: ${exception.message}"
                        _locationUpdatesActive.value = false
                        _isLoading.value = false
                    }
                    .collect { location ->
                        _isLoading.value = false
                        _locationUpdatesActive.value = true
                        val latLng = LatLng(location.latitude, location.longitude)
                        _currentLocation.value = latLng
                    }
            } catch (e: Exception) {
                _errorMessage.value = "Error iniciando actualizaciones: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // ✅ Detener actualizaciones
    fun stopLocationUpdates() {
        locationService.stopLocationUpdates()
        _locationUpdatesActive.value = false
    }

    // ✅ Actualizar ubicación manualmente (para testing)
    fun setMockLocation(lat: Double, lng: Double) {
        _currentLocation.value = LatLng(lat, lng)
    }
}
