// data/repository/UserRepository.kt
package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import android.util.Log
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.UpdateProfileRequest
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import com.example.smarparkinapp.ui.theme.data.model.UserProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class UserRepository {
    private lateinit var apiService: ApiService
    private val TAG = "UserRepository"

    fun initialize(context: Context) {
        apiService = RetrofitInstance.getAuthenticatedApiService(context)
        Log.d(TAG, "Repository inicializado")
    }

    suspend fun getUserProfile(): UserProfile = withContext(Dispatchers.IO) {
        if (!::apiService.isInitialized) {
            throw Exception("Repositorio no inicializado. Llama a initialize() primero.")
        }

        Log.d(TAG, "Obteniendo perfil de usuario...")

        try {
            // ✅ RUTA PRINCIPAL
            try {
                Log.d(TAG, "Intentando ruta 1: profile/")
                val response = apiService.getUserProfile()
                if (response.isSuccessful) {
                    val profile = response.body()
                    Log.d(TAG, "✅ Perfil obtenido exitosamente")
                    return@withContext profile?.toUserProfile() ?: throw Exception("Perfil vacío")
                } else {
                    Log.e(TAG, "Error HTTP ruta 1: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en ruta 1: ${e.message}")
            }

            // ✅ RUTA DE COMPATIBILIDAD
            try {
                Log.d(TAG, "Intentando ruta 2: users/profile/")
                val response = apiService.getUserProfileCompat()
                if (response.isSuccessful) {
                    val profile = response.body()
                    Log.d(TAG, "✅ Perfil obtenido exitosamente desde ruta 2")
                    return@withContext profile?.toUserProfile() ?: throw Exception("Perfil vacío")
                } else {
                    Log.e(TAG, "Error HTTP ruta 2: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en ruta 2: ${e.message}")
            }

            throw Exception("No se pudo obtener el perfil desde ninguna ruta")

        } catch (e: Exception) {
            Log.e(TAG, "Error general: ${e.message}")
            throw Exception("Error de conexión: ${e.message}")
        }
    }

    suspend fun updateUserProfile(
        firstName: String,
        lastName: String,
        phone: String? = null,
        address: String? = null,
        documentType: String? = null,
        documentNumber: String? = null,
        birthDate: String? = null,
        postalCode: String? = null,
        country: String? = null
    ): UserProfile = withContext(Dispatchers.IO) {
        if (!::apiService.isInitialized) {
            throw Exception("Repositorio no inicializado.")
        }

        Log.d(TAG, "Actualizando perfil...")

        val request = UpdateProfileRequest(
            firstName = firstName,
            lastName = lastName,
            telefono = phone,
            email = null, // ✅ Correcto - no enviar email si no se cambia
            tipoDocumento = documentType,
            numeroDocumento = documentNumber,
            fechaNacimiento = birthDate,
            direccion = address,
            codigoPostal = postalCode,
            pais = country
        )

        try {
            // ✅ RUTA PRINCIPAL
            try {
                Log.d(TAG, "Intentando actualizar en ruta 1: profile/update/")
                val response = apiService.updateUserProfile(request)
                if (response.isSuccessful) {
                    val updatedProfile = response.body()
                    Log.d(TAG, "✅ Perfil actualizado exitosamente")
                    return@withContext updatedProfile?.toUserProfile() ?: throw Exception("Respuesta inválida")
                } else {
                    Log.e(TAG, "Error HTTP ruta 1: ${response.code()} - ${response.message()}")
                    // Leer error body para más detalles
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error body ruta 1: $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en actualización ruta 1: ${e.message}")
            }

            // ✅ RUTA DE COMPATIBILIDAD - CORREGIDA
            try {
                Log.d(TAG, "Intentando actualizar en ruta 2: users/profile/update/") // ✅ CORREGIDO
                val response = apiService.updateUserProfileCompat(request)
                if (response.isSuccessful) {
                    val updatedProfile = response.body()
                    Log.d(TAG, "✅ Perfil actualizado desde ruta 2")
                    return@withContext updatedProfile?.toUserProfile() ?: throw Exception("Respuesta inválida")
                } else {
                    Log.e(TAG, "Error HTTP ruta 2: ${response.code()} - ${response.message()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error body ruta 2: $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en actualización ruta 2: ${e.message}")
            }

            throw Exception("No se pudo actualizar el perfil en ninguna ruta")

        } catch (e: Exception) {
            Log.e(TAG, "Error general al actualizar: ${e.message}")
            throw Exception("Error de actualización: ${e.message}")
        }
    }

    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getUserProfile()
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    fun isInitialized(): Boolean {
        return ::apiService.isInitialized
    }
}