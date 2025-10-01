package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.ApiClient
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.LoginRequest
import com.example.smarparkinapp.ui.theme.data.api.ResetPasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class LoginViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    val apiService = ApiClient.retrofit.create(ApiService::class.java)

    var resetMessage by mutableStateOf<String?>(null)

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiClient.retrofit.create(ApiService::class.java)
                val response = apiService.login(LoginRequest(email, password)) // ahora suspend

                if (response.access.isNotEmpty()) {
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = "Credenciales incorrectas"
                }
            } catch (e: IOException) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            } catch (e: HttpException) {
                _errorMessage.value = "Error del servidor"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                val apiService = ApiClient.retrofit.create(ApiService::class.java)
                val response = apiService.resetPassword(ResetPasswordRequest(email))

                if (response.isSuccessful) {
                    resetMessage = "Revisa tu correo para cambiar la contraseña."
                } else {
                    resetMessage = "Error: correo no registrado."
                }
            } catch (e: IOException) {
                resetMessage = "Error de red: ${e.localizedMessage}"
            } catch (e: HttpException) {
                resetMessage = "Error del servidor"
            } catch (e: Exception) {
                resetMessage = "Error de conexión: ${e.message}"
            }
        }
    }

}
