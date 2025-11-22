package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.api.CarRequest as ApiCarRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import android.content.Context
import android.content.SharedPreferences

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CompleteProfileViewModel(private val context: Context) : ViewModel() {

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

    private val apiService = RetrofitInstance.apiService
    private val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveProfile(userId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState(isLoading = true)

                // Obtener el token de autenticación
                val authToken = getAuthToken()
                if (authToken.isEmpty()) {
                    _uiState.value = ProfileUiState(errorMessage = "No estás autenticado. Inicia sesión nuevamente.")
                    return@launch
                }

                // ✅ CORREGIDO: Pasar el token como primer parámetro
                val response = apiService.addCar(
                    token = "Bearer $authToken", // ← Token como primer parámetro
                    car = ApiCarRequest(        // ← CarRequest como segundo parámetro
                        placa = placa,
                        marca = marca,
                        modelo = modelo,
                        color = color,
                        year = Calendar.getInstance().get(Calendar.YEAR)
                    )
                )

                if (response.isSuccessful) {
                    isSuccess = true
                    _uiState.value = ProfileUiState()
                    println("✅ [PROFILE] Perfil guardado exitosamente")
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    _uiState.value = ProfileUiState(errorMessage = errorMsg)
                    println("❌ [PROFILE] $errorMsg")
                }

            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.message}"
                _uiState.value = ProfileUiState(errorMessage = errorMsg)
                println("💥 [PROFILE] $errorMsg")
            }
        }
    }

    private fun getAuthToken(): String {
        return prefs.getString("auth_token", "") ?: ""
    }
}