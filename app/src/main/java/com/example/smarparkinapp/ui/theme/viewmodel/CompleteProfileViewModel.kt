package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CompleteProfileViewModel : ViewModel() {

    // Campos observables (Compose)
    var placa by mutableStateOf("")
    var modelo by mutableStateOf("")
    var marca by mutableStateOf("")
    var color by mutableStateOf("")
    var metodoPago by mutableStateOf("")
    var isSuccess by mutableStateOf(false)

    // Estado general de la UI (Flow)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    // ✅ CORREGIDO - Usa apiService directamente
    private val apiService = RetrofitInstance.apiService

    fun saveProfile(userId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState(isLoading = true)

                val response = apiService.addCar(
                    userId,
                    CarRequest(
                        placa = placa,
                        modelo = modelo,
                        color = color,
                        brand = marca,
                        tipo = "auto", // ✅ Asigna un valor real
                        paymentMethod = metodoPago // ✅ Usa el valor del campo
                    )
                )

                if (response.isSuccessful) {
                    isSuccess = true
                    _uiState.value = ProfileUiState()
                } else {
                    _uiState.value = ProfileUiState(errorMessage = "Error al guardar el perfil: ${response.code()}")
                }

            } catch (e: Exception) {
                _uiState.value = ProfileUiState(errorMessage = "Error de conexión: ${e.message}")
            }
        }
    }
}
// ✅ ELIMINA esta función extra que está al final - no es necesaria