package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.CarRequest
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.smarparkinapp.ui.theme.data.model.*

class ProfileViewModel : ViewModel() {

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    // 🔹 Estado del usuario
    private val _userState = mutableStateOf(UserState())
    val userState: State<UserState> = _userState

    // 🔹 Estado de los vehículos
    private val _vehiclesState = mutableStateOf(VehiclesState())
    val vehiclesState: State<VehiclesState> = _vehiclesState

    // ✅ Cargar todos los datos del usuario
    fun loadUserData() {
        loadUserProfile()
        loadUserVehicles()
    }

    // ✅ Cargar perfil del usuario
    private fun loadUserProfile() {
        viewModelScope.launch {
            _userState.value = _userState.value.copy(isLoading = true, error = null)
            try {
                val response: Response<UserProfile> = apiService.getUserProfile()
                if (response.isSuccessful) {
                    _userState.value = UserState(
                        user = response.body(),
                        isLoading = false
                    )
                } else {
                    _userState.value = UserState(
                        error = "Error ${response.code()}: ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _userState.value = UserState(
                    error = "Error de conexión: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // ✅ Cargar vehículos del usuario
    private fun loadUserVehicles() {
        viewModelScope.launch {
            _vehiclesState.value = _vehiclesState.value.copy(isLoading = true, error = null)
            try {
                val response: Response<List<CarResponse>> = apiService.getUserCars()
                if (response.isSuccessful) {
                    _vehiclesState.value = VehiclesState(
                        vehicles = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _vehiclesState.value = VehiclesState(
                        error = "Error ${response.code()}: ${response.message()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _vehiclesState.value = VehiclesState(
                    error = "Error de conexión: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // ✅ Agregar vehículo - CORREGIDO
    fun addVehicle(placa: String, color: String, modelo: String, tipo: String = "auto") {
        viewModelScope.launch {
            _vehiclesState.value = _vehiclesState.value.copy(isLoading = true, error = null)
            try {
                val request = CarRequest(
                    placa = placa,
                    modelo = modelo,
                    tipo = tipo,
                    color = color
                )
                val response: Response<CarResponse> = apiService.addCar(request)
                if (response.isSuccessful) {
                    // Recargar la lista de vehículos
                    loadUserVehicles()
                } else {
                    _vehiclesState.value = _vehiclesState.value.copy(
                        error = "Error al agregar vehículo: ${response.code()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _vehiclesState.value = _vehiclesState.value.copy(
                    error = "Error de conexión: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // ✅ Eliminar vehículo
    fun deleteVehicle(carId: Int) {
        viewModelScope.launch {
            try {
                val response: Response<Void> = apiService.deleteCar(carId)
                if (response.isSuccessful) {
                    // Recargar la lista de vehículos
                    loadUserVehicles()
                } else {
                    _vehiclesState.value = _vehiclesState.value.copy(
                        error = "Error al eliminar vehículo: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _vehiclesState.value = _vehiclesState.value.copy(
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    // ✅ Actualizar perfil (opcional)
    fun updateProfile(firstName: String?, lastName: String?, phone: String?, email: String?) {
        viewModelScope.launch {
            _userState.value = _userState.value.copy(isLoading = true, error = null)
            try {
                val request = UpdateProfileRequest(firstName, lastName, phone, email)
                val response: Response<UserProfile> = apiService.updateProfile(request)
                if (response.isSuccessful) {
                    loadUserProfile()
                } else {
                    _userState.value = _userState.value.copy(
                        error = "Error al actualizar perfil: ${response.code()}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    error = "Error de conexión: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // ✅ Limpiar errores
    fun clearErrors() {
        _userState.value = _userState.value.copy(error = null)
        _vehiclesState.value = _vehiclesState.value.copy(error = null)
    }
}
