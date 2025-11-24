package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirm")
    val passwordConfirm: String,      // se enviará como "password_confirm" al backend
    val first_name: String? = null,
    val last_name: String? = null,
    val telefono: String? = null
)