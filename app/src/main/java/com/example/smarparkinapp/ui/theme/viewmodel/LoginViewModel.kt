package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.LoginRequest
import com.example.smarparkinapp.ui.theme.data.api.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    // ✅ ELIMINAR resetMessage ya que no existe ese endpoint
    // var resetMessage by mutableStateOf<String?>(null)

    fun login(username: String, password: String) {
        // ✅ Validación básica
        if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiClient.retrofit.create(ApiService::class.java)
                val response = apiService.login(LoginRequest(username, password))

                // ✅ Guardar tokens
                ApiClient.setToken(response.access)

                _loginSuccess.value = true

            } catch (e: IOException) {
                _errorMessage.value = "Error de conexión: Verifica tu internet"
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _errorMessage.value = "Usuario o contraseña incorrectos"
                    400 -> _errorMessage.value = "Datos inválidos"
                    else -> _errorMessage.value = "Error del servidor: ${e.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
    }

}