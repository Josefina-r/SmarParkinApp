package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _registeredUserId = MutableStateFlow<Int?>(null)
    val registeredUserId: StateFlow<Int?> = _registeredUserId

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    fun register(
        username: String,
        email: String,
        password: String,
        passwordConfirm: String,
        phone: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // ✅ Usar el endpoint correcto según tu documentación
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    password_confirm = passwordConfirm,
                    telefono = phone
                )

                val response = apiService.register(request)
                _registeredUserId.value = response.id.toInt()

            } catch (e: IOException) {
                _errorMessage.value = "Error de conexión: Verifica tu internet"
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> _errorMessage.value = "Error en los datos: Verifica que las contraseñas coincidan y el usuario/email no exista"
                    else -> _errorMessage.value = "Error del servidor: ${e.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearRegisteredUserId() {
        _registeredUserId.value = null
    }
}