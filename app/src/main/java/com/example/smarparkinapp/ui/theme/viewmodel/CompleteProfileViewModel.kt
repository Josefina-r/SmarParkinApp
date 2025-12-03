package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.CarRequest as ApiCarRequest
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


    private val apiService = RetrofitInstance.getAuthenticatedApiService(context)
    private val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveProfile(userId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = ProfileUiState(isLoading = true)



                println(" [PROFILE] Guardando vehículo: $marca $modelo - $placa")

                val response = apiService.addCar(
                    ApiCarRequest(
                        placa = placa.uppercase().replace(" ", "").replace("-", ""),
                        marca = marca,
                        modelo = modelo,
                        color = color,
                    )
                )

                if (response.isSuccessful) {
                    isSuccess = true
                    _uiState.value = ProfileUiState()
                    println("✅ [PROFILE] Vehículo guardado exitosamente")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    val errorMsg = when (response.code()) {
                        400 -> "Datos inválidos: $errorBody"
                        401 -> "Sesión expirada. Inicia sesión nuevamente."
                        409 -> "La placa ya está registrada"
                        else -> "Error ${response.code()}: $errorBody"
                    }
                    _uiState.value = ProfileUiState(errorMessage = errorMsg)
                    println("❌ [PROFILE] $errorMsg")
                }

            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.message}"
                _uiState.value = ProfileUiState(errorMessage = errorMsg)
                println("💥 [PROFILE] $errorMsg")
                e.printStackTrace()
            }
        }
    }


    fun isValidPlateFormat(plate: String): Boolean {
        val cleanedPlate = plate.uppercase().replace(" ", "").replace("-", "")
        // Formatos comunes: ABC123, ABC12D, AB123C, etc.
        val plateRegex = Regex("^[A-Z]{2,3}[0-9]{3,4}[A-Z]?$")
        return plateRegex.matches(cleanedPlate)
    }

    fun validateForm(): Boolean {
        return placa.isNotBlank() &&
                marca.isNotBlank() &&
                modelo.isNotBlank() &&
                color.isNotBlank() &&
                isValidPlateFormat(placa)
    }


    fun clearForm() {
        placa = ""
        modelo = ""
        marca = ""
        color = ""
        metodoPago = ""
        isSuccess = false
        _uiState.value = ProfileUiState()
    }

    fun isUserAuthenticated(): Boolean {
        return getAuthToken().isNotEmpty()
    }

    private fun getAuthToken(): String {
        return prefs.getString("auth_token", "") ?: ""
    }
}