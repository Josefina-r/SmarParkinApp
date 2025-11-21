package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import com.example.smarparkinapp.ui.theme.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModelSimple : ViewModel() {

    // Repositorio
    private var userRepository: UserRepository? = null

    // Estado del Perfil (Usamos UserProfile, no User)
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // Estado de Carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado de Error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estado de Éxito en Actualización
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    // Inicialización (Llamar desde la UI, por ejemplo en un LaunchedEffect)
    fun initialize(context: Context) {
        if (userRepository == null) {
            userRepository = UserRepository()
            userRepository?.initialize(context)
            loadUserProfile()
        }
    }

    // Cargar Perfil
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val repo = userRepository ?: throw Exception("Repositorio no inicializado")
                val profile = repo.getUserProfile()
                _userProfile.value = profile
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar perfil: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Actualizar Perfil
    fun updateProfile(
        firstName: String,
        lastName: String,
        phone: String,
        address: String,
        // Campos opcionales que agregamos al modelo
        documentType: String? = null,
        documentNumber: String? = null,
        birthDate: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

            try {
                val repo = userRepository ?: throw Exception("Repositorio no inicializado")

                // Llamada al repositorio
                val updatedProfile = repo.updateUserProfile(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    address = address,
                    documentType = documentType,
                    documentNumber = documentNumber,
                    birthDate = birthDate
                )

                // Actualizamos el estado local con los datos nuevos
                _userProfile.value = updatedProfile
                _updateSuccess.value = true
                _errorMessage.value = null // Limpiar errores si hubo éxito

            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar: ${e.message}"
                _updateSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Limpiar mensaje de error (útil para Snackbars)
    fun clearError() {
        _errorMessage.value = null
    }

    // Resetear estado de éxito (útil para navegar atrás después de guardar)
    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }
}
