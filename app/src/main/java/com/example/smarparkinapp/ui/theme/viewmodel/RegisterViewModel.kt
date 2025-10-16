package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Nuevo: id del usuario registrado (null hasta que el registro sea exitoso)
    private val _registeredUserId = MutableStateFlow<Int?>(null)
    val registeredUserId: StateFlow<Int?> = _registeredUserId

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {

                val response = apiService.register(request)

                _registeredUserId.value = response.id
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRegisteredUserId() {
        _registeredUserId.value = null
    }
}
