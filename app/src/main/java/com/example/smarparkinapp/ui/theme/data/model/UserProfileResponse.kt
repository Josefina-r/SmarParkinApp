// data/model/UserProfileResponse.kt
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("rol") val rol: String?,
    @SerializedName("rol_display") val rolDisplay: String?,
    @SerializedName("is_admin") val isAdmin: Boolean?,
    @SerializedName("is_owner") val isOwner: Boolean?,
    @SerializedName("is_client") val isClient: Boolean?,
    //  NUEVOS CAMPOS
    @SerializedName("tipo_documento") val tipoDocumento: String?,
    @SerializedName("numero_documento") val numeroDocumento: String?,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("codigo_postal") val codigoPostal: String?,
    @SerializedName("pais") val pais: String?
) {
    fun toUserProfile(): UserProfile {
        return UserProfile(
            id = this.id,
            username = this.username,
            email = this.email,
            firstName = this.firstName ?: "",
            lastName = this.lastName ?: "",
            phone = this.telefono ?: "",
            address = this.direccion ?: "",
            role = this.rol ?: "client",
            roleDisplay = this.rolDisplay ?: "Cliente",
            isAdmin = this.isAdmin ?: false,
            isOwner = this.isOwner ?: false,
            isClient = this.isClient ?: true,
            // NUEVOS CAMPOS
            tipoDocumento = this.tipoDocumento ?: "",
            numeroDocumento = this.numeroDocumento ?: "",
            fechaNacimiento = this.fechaNacimiento ?: "",
            codigoPostal = this.codigoPostal ?: "",
            pais = this.pais ?: "Perú"
        )
    }
}

data class UserProfile(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val address: String,
    val role: String,
    val roleDisplay: String,
    val isAdmin: Boolean,
    val isOwner: Boolean,
    val isClient: Boolean,

    val tipoDocumento: String,
    val numeroDocumento: String,
    val fechaNacimiento: String,
    val codigoPostal: String,
    val pais: String
)

data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("email") val email: String? = null,
    //  NUEVOS CAMPOS
    @SerializedName("tipo_documento") val tipoDocumento: String?,
    @SerializedName("numero_documento") val numeroDocumento: String?,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("codigo_postal") val codigoPostal: String?,
    @SerializedName("pais") val pais: String?
)

// Alias para respuesta de actualización
typealias UpdateProfileResponse = UserProfileResponse