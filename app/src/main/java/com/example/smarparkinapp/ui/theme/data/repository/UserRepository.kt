package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import android.util.Log
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    private lateinit var apiService: ApiService
    private val TAG = "UserRepository"

    fun initialize(context: Context) {
        apiService = RetrofitInstance.getAuthenticatedApiService(context)
        Log.d(TAG, "✅ Repository inicializado")
    }

    suspend fun getUserProfile(): com.example.smarparkinapp.ui.theme.data.model.UserProfile =
        withContext(Dispatchers.IO) {
            if (!::apiService.isInitialized) {
                throw Exception("Repositorio no inicializado. Llama a initialize() primero.")
            }

            Log.d(TAG, "🔄 OBTENIENDO PERFIL DE USUARIO")

            try {
                Log.d(TAG, "📞 Llamando a: api/users/profile/")
                val response = apiService.getUserProfile()

                if (response.isSuccessful) {
                    val profileResponse = response.body()

                    if (profileResponse == null) {
                        Log.e(TAG, "❌ Respuesta vacía del servidor")
                        throw Exception("Respuesta vacía del servidor")
                    }

                    // CONVERTIR
                    val userProfile = profileResponse.toUserProfile()

                    Log.d(TAG, "✅ PERFIL OBTENIDO CORRECTAMENTE")
                    Log.d(TAG, "📊 Datos:")
                    Log.d(TAG, "   Nombre: ${userProfile.firstName} ${userProfile.lastName}")
                    Log.d(TAG, "   Teléfono: '${userProfile.phone}'")
                    Log.d(TAG, "   Dirección: '${userProfile.address}'")
                    Log.d(TAG, "   Documento: '${userProfile.tipoDocumento} - ${userProfile.numeroDocumento}'")

                    return@withContext userProfile
                } else {
                    val errorMsg = "❌ Error HTTP ${response.code()}: ${response.message()}"
                    Log.e(TAG, errorMsg)
                    throw Exception(errorMsg)
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error al obtener perfil: ${e.message}", e)
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
    ): com.example.smarparkinapp.ui.theme.data.model.UserProfile = withContext(Dispatchers.IO) {
        if (!::apiService.isInitialized) {
            throw Exception("Repositorio no inicializado.")
        }

        Log.d(TAG, "🔄 ACTUALIZANDO PERFIL")

        // Crear request
        val request = com.example.smarparkinapp.ui.theme.data.model.UpdateProfileRequest(
            firstName = firstName,
            lastName = lastName,
            telefono = phone,
            email = null,
            tipoDocumento = documentType,
            numeroDocumento = documentNumber,
            fechaNacimiento = birthDate,
            direccion = address,
            codigoPostal = postalCode,
            pais = country
        )

        Log.d(TAG, "📤 Request: $request")

        try {
            val response = apiService.updateUserProfile(request)

            if (response.isSuccessful) {
                val updatedProfileResponse = response.body()

                if (updatedProfileResponse == null) {
                    Log.e(TAG, "❌ Respuesta vacía del servidor")
                    throw Exception("Respuesta vacía del servidor")
                }

                // CONVERTIR
                val userProfile = updatedProfileResponse.toUserProfile()

                Log.d(TAG, "✅ PERFIL ACTUALIZADO EXITOSAMENTE")
                Log.d(TAG, "📊 Datos actualizados:")
                Log.d(TAG, "   Teléfono: '${userProfile.phone}'")
                Log.d(TAG, "   Dirección: '${userProfile.address}'")
                Log.d(TAG, "   Documento: '${userProfile.tipoDocumento} - ${userProfile.numeroDocumento}'")

                return@withContext userProfile
            } else {
                val errorMsg = "❌ Error HTTP ${response.code()}: ${response.message()}"
                Log.e(TAG, errorMsg)
                throw Exception(errorMsg)
            }

        } catch (e: Exception) {
            Log.e(TAG, " Error al actualizar: ${e.message}", e)
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