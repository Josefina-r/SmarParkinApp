// ProfileViewModel.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userRepository = com.example.smarparkinapp.ui.theme.data.repository.UserRepository()

    private val _userProfile = MutableStateFlow<com.example.smarparkinapp.ui.theme.data.model.UserProfile?>(null)
    val userProfile: StateFlow<com.example.smarparkinapp.ui.theme.data.model.UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    private val _hasLoadedProfile = MutableStateFlow(false)
    val hasLoadedProfile: StateFlow<Boolean> = _hasLoadedProfile.asStateFlow()

    fun initializeRepository(context: Context) {
        if (!userRepository.isInitialized()) {
            userRepository.initialize(context)
        }
    }

    fun clearProfileData() {
        _userProfile.value = null
        _hasLoadedProfile.value = false
        _updateSuccess.value = false
        _errorMessage.value = null
        _validationErrors.value = emptyMap()
    }

    fun loadUserProfile(context: Context, forceReload: Boolean = false) {
        // SOLUCIÓN: SIEMPRE recargar cuando se fuerza
        if (_hasLoadedProfile.value && !forceReload) {
            println("✅ ProfileViewModel - Perfil ya cargado, pero cargaremos de todos modos")
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                initializeRepository(context)
                println("🔄 ProfileViewModel - Cargando perfil desde repository...")
                val profile = userRepository.getUserProfile()

                // DEBUG DETALLADO
                println("📋 ProfileViewModel - DATOS OBTENIDOS:")
                println("   📞 Teléfono: '${profile.phone}'")
                println("   🏠 Dirección: '${profile.address}'")
                println("   📄 Documento: '${profile.tipoDocumento} - ${profile.numeroDocumento}'")
                println("   👤 Nombre: '${profile.firstName} ${profile.lastName}'")

                _userProfile.value = profile
                _hasLoadedProfile.value = true

                println("✅ ProfileViewModel - Perfil cargado exitosamente")

            } catch (e: Exception) {
                val errorMsg = "Error al cargar perfil: ${e.message}"
                _errorMessage.value = errorMsg
                _hasLoadedProfile.value = false
                println("❌ ProfileViewModel - $errorMsg")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun forceReloadProfile(context: Context) {
        println("🔄 ProfileViewModel - FORZANDO RECARGA DEL PERFIL")
        _hasLoadedProfile.value = false
        loadUserProfile(context, forceReload = true)
    }

    fun updateProfile(
        context: Context,
        firstName: String,
        lastName: String,
        phone: String? = null,
        address: String? = null,
        documentType: String? = null,
        documentNumber: String? = null,
        birthDate: String? = null,
        postalCode: String? = null,
        country: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _validationErrors.value = emptyMap()

            try {
                // Validaciones
                val errors = validateProfileData(
                    firstName, lastName, phone, documentType, documentNumber, birthDate
                )

                if (errors.isNotEmpty()) {
                    _validationErrors.value = errors
                    println(" ProfileViewModel - Errores de validación: $errors")
                    return@launch
                }

                initializeRepository(context)

                val backendDocumentType = documentType?.let { mapDocumentTypeToBackendFormat(it) }

                println(" ProfileViewModel - Actualizando perfil...")
                val updatedProfile = userRepository.updateUserProfile(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    address = address,
                    documentType = backendDocumentType,
                    documentNumber = documentNumber,
                    birthDate = birthDate,
                    postalCode = postalCode,
                    country = country
                )

                // ACTUALIZAR LOCALMENTE
                _userProfile.value = updatedProfile
                _updateSuccess.value = true
                _hasLoadedProfile.value = true

                println("✅ ProfileViewModel - Perfil actualizado exitosamente")
                println("   📞 Nuevo teléfono: '${updatedProfile.phone}'")
                println("   🏠 Nueva dirección: '${updatedProfile.address}'")
                println("   📄 Nuevo documento: '${updatedProfile.tipoDocumento} - ${updatedProfile.numeroDocumento}'")

            } catch (e: Exception) {
                val errorMsg = "Error al actualizar: ${e.message}"
                _errorMessage.value = errorMsg
                println("❌ ProfileViewModel - $errorMsg")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateProfileData(
        firstName: String,
        lastName: String,
        phone: String?,
        documentType: String?,
        documentNumber: String?,
        birthDate: String?
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (firstName.isBlank()) errors["firstName"] = "El nombre es obligatorio"
        if (lastName.isBlank()) errors["lastName"] = "El apellido es obligatorio"

        phone?.let {
            if (it.length != 9 || !it.all { char -> char.isDigit() }) {
                errors["phone"] = "El celular debe tener 9 dígitos"
            }
        } ?: run {
            errors["phone"] = "El celular es obligatorio"
        }

        documentType?.let {
            if (it.isBlank()) {
                errors["documentType"] = "Seleccione un tipo de documento"
            }
        } ?: run {
            errors["documentType"] = "Seleccione un tipo de documento"
        }

        documentNumber?.let {
            when (documentType) {
                "DNI" -> {
                    if (it.length != 8 || !it.all { char -> char.isDigit() }) {
                        errors["documentNumber"] = "El DNI debe tener 8 dígitos"
                    }
                }
                "Pasaporte" -> {
                    if (it.length < 6 || it.length > 12) {
                        errors["documentNumber"] = "El pasaporte debe tener 6-12 caracteres"
                    }
                }
                "Carnet de Extranjería" -> {
                    if (it.length < 9 || it.length > 12) {
                        errors["documentNumber"] = "El carnet debe tener 9-12 caracteres"
                    }
                }
            }
        } ?: run {
            errors["documentNumber"] = "El número de documento es obligatorio"
        }

        birthDate?.let {
            if (!isValidDateFormat(it)) {
                errors["birthDate"] = "Formato de fecha inválido (dd/mm/yyyy)"
            }
        }

        return errors
    }

    private fun isValidDateFormat(date: String): Boolean {
        return try {
            val parts = date.split("/")
            if (parts.size != 3) return false

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            day in 1..31 && month in 1..12 && year in 1900..2100
        } catch (e: Exception) {
            false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun clearValidationErrors() {
        _validationErrors.value = emptyMap()
    }

    private fun mapDocumentTypeToBackendFormat(documentType: String): String {
        return when (documentType) {
            "DNI" -> "dni"
            "Pasaporte" -> "pasaporte"
            "Carnet de Extranjería" -> "carnet_extranjeria"
            else -> documentType
        }
    }
}