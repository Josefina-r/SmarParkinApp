package com.example.smarparkinapp.ui.theme.data.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val first_name: String?,
    val last_name: String?
)


data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String? = null,
    val user: User? = null
)