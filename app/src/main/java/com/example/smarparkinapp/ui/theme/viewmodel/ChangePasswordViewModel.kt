package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ChangePasswordViewModel(private val context: Context) : ViewModel() {
    // ✅ USAR EL SERVICIO AUTENTICADO
    private val apiService: ApiService = RetrofitInstance.getAuthenticatedApiService(context)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _passwordChanged = MutableStateFlow(false)
    val passwordChanged: StateFlow<Boolean> = _passwordChanged

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        // Validaciones locales primero
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            _errorMessage.value = "Todos los campos son requeridos"
            return
        }

        if (newPassword != confirmPassword) {
            _errorMessage.value = "Las nuevas contraseñas no coinciden"
            return
        }

        if (newPassword.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (oldPassword == newPassword) {
            _errorMessage.value = "La nueva contraseña debe ser diferente a la actual"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                println("🔐 [CHANGE_PASSWORD] Iniciando cambio de contraseña...")

                val response = apiService.changePassword(
                    ChangePasswordRequest(
                        old_password = oldPassword,
                        new_password = newPassword,
                        confirm_password = confirmPassword
                    )
                )

                if (response.isSuccessful) {
                    val result = response.body()
                    println("✅ [CHANGE_PASSWORD] Cambio exitoso: ${result?.message}")

                    _successMessage.value = result?.message ?: "Contraseña cambiada exitosamente"
                    _passwordChanged.value = true

                    // Limpiar mensaje después de 3 segundos
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(3000)
                        _successMessage.value = null
                    }
                } else {
                    // Intentar parsear el error
                    val errorBody = response.errorBody()?.string()
                    println("❌ [CHANGE_PASSWORD] Error HTTP: ${response.code()}, Body: $errorBody")

                    when (response.code()) {
                        400 -> {
                            _errorMessage.value = extractErrorMessage(errorBody) ?: "Datos inválidos"
                        }
                        401 -> {
                            _errorMessage.value = "Sesión expirada. Por favor, inicia sesión nuevamente"
                        }
                        403 -> {
                            _errorMessage.value = "No tienes permiso para realizar esta acción"
                        }
                        else -> {
                            _errorMessage.value = "Error del servidor: ${response.code()}"
                        }
                    }
                }
            } catch (e: IOException) {
                println("💥 [CHANGE_PASSWORD] Error de red: ${e.message}")
                _errorMessage.value = "Error de conexión. Verifica tu internet"
            } catch (e: HttpException) {
                println("💥 [CHANGE_PASSWORD] Error HTTP: ${e.code()}")
                _errorMessage.value = "Error del servidor: ${e.code()}"
            } catch (e: Exception) {
                println("💥 [CHANGE_PASSWORD] Error general: ${e.message}")
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("⏹️ [CHANGE_PASSWORD] Loading terminado")
            }
        }
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        return try {
            when {
                errorBody?.contains("old_password") == true -> "Contraseña actual incorrecta"
                errorBody?.contains("password") == true -> "La contraseña no cumple los requisitos"
                errorBody?.contains("current") == true -> "Contraseña actual incorrecta"
                else -> "Error al cambiar contraseña"
            }
        } catch (e: Exception) {
            "Error al procesar la respuesta"
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun resetPasswordChanged() {
        _passwordChanged.value = false
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun validatePasswords(oldPassword: String, newPassword: String, confirmPassword: String): ValidationResult {
        return when {
            oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() ->
                ValidationResult.Error("Todos los campos son requeridos")
            newPassword.length < 6 ->
                ValidationResult.Error("La contraseña debe tener al menos 6 caracteres")
            newPassword != confirmPassword ->
                ValidationResult.Error("Las contraseñas no coinciden")
            oldPassword == newPassword ->
                ValidationResult.Error("La nueva contraseña debe ser diferente")
            else -> ValidationResult.Valid
        }
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}