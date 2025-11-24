// viewmodel/UserViewModel.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val context: Context) : ViewModel() {
    private val authManager = AuthManager(context)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        // Verificar si hay usuario logueado al iniciar
        checkLoggedInUser()
    }

    fun login(user: User, token: String) {
        viewModelScope.launch {
            _currentUser.value = user
            _isLoggedIn.value = true
            authManager.saveAuthToken(token)
            authManager.saveUserInfo(user.id, user.username)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _currentUser.value = null
            _isLoggedIn.value = false
            authManager.logout()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _currentUser.value = user
        }
    }

    private fun checkLoggedInUser() {
        viewModelScope.launch {
            val userId = authManager.getUserId()
            val username = authManager.getUsername()

            if (userId != -1 && !username.isNullOrEmpty()) {
                // Recuperar información completa del usuario si es necesario
                // Por ahora creamos un usuario básico con los datos guardados
                val user = User(
                    id = userId,
                    username = username,
                    email = "", // Puedes guardar el email también en AuthManager si lo necesitas
                    first_name = null,
                    last_name = null
                )
                _currentUser.value = user
                _isLoggedIn.value = true
            }
        }
    }
}