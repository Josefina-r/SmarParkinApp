package com.example.smarparkinapp.ui.theme.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val password2: String,  // Confirmación de password
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null
)