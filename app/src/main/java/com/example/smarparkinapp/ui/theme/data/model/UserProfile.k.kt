package com.example.smarparkinapp.ui.theme.data.model


import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("rol")
    val rol: String,

    @SerializedName("rol_display")
    val rolDisplay: String,

    @SerializedName("first_name")
    val firstName: String?,

    @SerializedName("last_name")
    val lastName: String?,

    @SerializedName("telefono")
    val phone: String?,

    @SerializedName("is_admin")
    val isAdmin: Boolean,

    @SerializedName("is_owner")
    val isOwner: Boolean,

    @SerializedName("is_client")
    val isClient: Boolean,

    // Campos adicionales que podrían venir de tu API
    @SerializedName("fecha_registro")
    val dateJoined: String? = null,

    @SerializedName("imagen_perfil")
    val profileImage: String? = null
)


