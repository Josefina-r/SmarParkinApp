package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
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

class LoginViewModel(private val context: Context) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val apiService = RetrofitInstance.apiService
    private val authManager = AuthManager(context)

    var resetMessage by mutableStateOf<String?>(null)

    fun login(username: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                println("🔐 [LOGIN] Intentando login con usuario: $username")
                val response = apiService.login(LoginRequest(username, password))
                println("📡 [LOGIN] Respuesta del servidor - Código: ${response.code()}, Éxito: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    println("✅ [LOGIN] Login exitoso - Access Token: ${loginResponse?.access?.take(10)}..., Refresh Token: ${loginResponse?.refresh?.take(10)}..., User: ${loginResponse?.user}")

                    // ✅ CORREGIDO: Usa "access" en lugar de "token"
                    if (loginResponse?.access?.isNotEmpty() == true) {
                        // ✅ GUARDAR TOKEN Y INFO DEL USUARIO
                        authManager.saveAuthToken(loginResponse.access)
                        loginResponse.user?.let { user ->
                            authManager.saveUserInfo(user.id, user.username)
                            println("💾 [LOGIN] Usuario guardado - ID: ${user.id}, Username: ${user.username}")
                        }
                        _loginSuccess.value = true
                        println("🎯 [LOGIN] _loginSuccess cambiado a: true")
                    } else {
                        println("❌ [LOGIN] Access token vacío o nulo en la respuesta")
                        _errorMessage.value = "Credenciales incorrectas"
                    }
                } else {
                    println("❌ [LOGIN] Error HTTP: ${response.code()}")
                    try {
                        val errorBody = response.errorBody()?.string()
                        println("❌ [LOGIN] Error Body: $errorBody")
                    } catch (e: Exception) {
                        println("❌ [LOGIN] No se pudo leer el error body")
                    }
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: IOException) {
                println("💥 [LOGIN] Error de red: ${e.localizedMessage}")
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            } catch (e: HttpException) {
                println("💥 [LOGIN] Error del servidor: ${e.code()}")
                _errorMessage.value = "Error del servidor: ${e.code()}"
            } catch (e: Exception) {
                println("💥 [LOGIN] Error general: ${e.message}")
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
                println("⏹️ [LOGIN] Loading terminado")
            }
        }
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
        println("🧹 [LOGIN] Estado de loginSuccess limpiado")
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                // La llamada a la API en sí misma puede arrojar una excepción si no es exitosa (código 4xx o 5xx)
                // Por lo tanto, envolvemos la llamada en el bloque try.
                // Si llega a la siguiente línea, la solicitud fue exitosa (código 2xx).
                apiService.resetPassword(ResetPasswordRequest(email))
                resetMessage = "Revisa tu correo para cambiar la contraseña."
            } catch (e: IOException) {
                resetMessage = "Error de red: ${e.localizedMessage}"
            } catch (e: HttpException) {
                // Si la API devuelve un error (ej. 400, 404, 500), se captura aquí.
                resetMessage = "Error: correo no registrado o error del servidor (${e.code()})."
            } catch (e: Exception) {
                resetMessage = "Error de conexión: ${e.message}"
            }
        }
    }
}