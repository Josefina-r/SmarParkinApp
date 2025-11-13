package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CompleteProfileViewModel : ViewModel() {
    var placa by mutableStateOf("")
    var modelo by mutableStateOf("")
    var color by mutableStateOf("")
    var tipoVehiculo by mutableStateOf("auto")
    var isSuccess by mutableStateOf(false)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    fun saveProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState(isLoading = true)

                // Verificar que tenemos token antes de hacer la petición
                val response = apiService.addCar(
                    CarRequest(
                        placa = placa,
                        modelo = modelo,
                        tipo = tipoVehiculo,
                        color = color
                    )
                )

                if (response.isSuccessful) {
                    isSuccess = true
                    _uiState.value = ProfileUiState()
                } else {
                    when (response.code()) {
                        401 -> _uiState.value = ProfileUiState(
                            errorMessage = "Sesión expirada. Por favor inicia sesión nuevamente."
                        )
                        else -> {
                            val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                            _uiState.value = ProfileUiState(errorMessage = "Error: $errorBody")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(errorMessage = "Error de conexión: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.value = ProfileUiState(errorMessage = null)
    }

    // Método para resetear el formulario
    fun resetForm() {
        placa = ""
        modelo = ""
        color = ""
        tipoVehiculo = "auto"
        isSuccess = false
        _uiState.value = ProfileUiState()
    }
}