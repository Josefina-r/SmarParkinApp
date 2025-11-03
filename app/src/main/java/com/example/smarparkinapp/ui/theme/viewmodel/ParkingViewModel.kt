package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(private val context: Context) : ViewModel() {

    private val _parkingLots = MutableStateFlow<List<ParkingLot>>(emptyList())
    val parkingLots: StateFlow<List<ParkingLot>> = _parkingLots

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiService = RetrofitInstance.getAuthenticatedApiService(context)
    private val authManager = AuthManager(context)

    fun loadAllParkingLots() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                println("📡 [PARKING] Cargando todos los estacionamientos...")
                val response = apiService.getApprovedParkingLots()

                if (response.isSuccessful) {
                    val parkingLotResponse = response.body()
                    val lots = parkingLotResponse?.results ?: emptyList()
                    _parkingLots.value = lots
                    println("✅ [PARKING] Estacionamientos cargados: ${lots.size}")
                } else {
                    _error.value = "Error al cargar estacionamientos: ${response.code()}"
                    println("❌ [PARKING] Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                println("💥 [PARKING] Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}