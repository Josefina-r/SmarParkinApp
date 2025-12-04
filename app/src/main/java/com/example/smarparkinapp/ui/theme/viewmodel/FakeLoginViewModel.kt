package com.example.smarparkinapp.ui.theme.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class FakeLoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    var resetMessage: String? = null

    // 🔥 Simula un login (sin servidor real)
    fun login(username: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            // Simulación
            kotlinx.coroutines.delay(300)

            if (username == "admin" && password == "123456") {
                _loginSuccess.value = true
            } else {
                _errorMessage.value = "Credenciales incorrectas"
            }

            _isLoading.value = false
        }
    }

    fun clearLoginSuccess() {
        _loginSuccess.value = false
    }

    fun resetPassword(email: String) {
        // Simulación: siempre funciona
        resetMessage = "Correo de recuperación enviado"
    }

    fun clearResetMessage() {
        resetMessage = null
    }
}
