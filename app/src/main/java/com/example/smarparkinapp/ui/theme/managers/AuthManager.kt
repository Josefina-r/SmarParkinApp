package com.example.smarparkinapp.ui.theme.managers

import android.content.Context
import android.content.SharedPreferences

class AuthManager(private val context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // Guardar token de acceso
    fun saveToken(token: String) {
        sharedPref.edit().putString("access_token", token).apply()
    }

    // Obtener token guardado
    fun getToken(): String? {
        return sharedPref.getString("access_token", null)
    }

    // Verificar si el usuario está logueado
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    // Cerrar sesión
    fun logout() {
        sharedPref.edit().remove("access_token").apply()
    }

    // Guardar información del usuario
    fun saveUserInfo(userId: String, username: String) {
        sharedPref.edit()
            .putString("user_id", userId)
            .putString("username", username)
            .apply()
    }

    // Obtener información del usuario
    fun getUserId(): String? {
        return sharedPref.getString("user_id", null)
    }

    fun getUsername(): String? {
        return sharedPref.getString("username", null)
    }
}