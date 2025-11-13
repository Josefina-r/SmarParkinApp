package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

// ✅ Data classes de estado
data class UserState(
    val user: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class VehiclesState(
    val vehicles: List<CarResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ✅ Modelo para actualizar perfil (agregar en ApiService o aquí)
data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("telefono") val phone: String?,
    @SerializedName("email") val email: String?)
data class UserProfileResponse(
    val id: Long,
    val username: String,
    val email: String,
    val rol: String,
    val rol_display: String,
    val first_name: String?,
    val last_name: String?,
    val telefono: String?,
    val is_admin: Boolean,
    val is_owner: Boolean,
    val is_client: Boolean
)