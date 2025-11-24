package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val _currentTheme = MutableStateFlow(getSavedTheme())
    val currentTheme: StateFlow<String> = _currentTheme.asStateFlow()

    private val _currentLanguage = MutableStateFlow(getSavedLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(getSavedNotifications())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Función para actualizar el tema
    fun updateTheme(theme: String) {
        viewModelScope.launch {
            saveTheme(theme)
            _currentTheme.value = theme
        }
    }

    // Función para actualizar el idioma
    fun updateLanguage(language: String) {
        viewModelScope.launch {
            saveLanguage(language)
            _currentLanguage.value = language
            // Actualizar el idioma de la app
            updateAppLanguage(language)
        }
    }

    // Función para actualizar notificaciones
    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            saveNotifications(enabled)
            _notificationsEnabled.value = enabled
        }
    }

    // Funciones para obtener nombres display
    fun getThemeDisplayName(theme: String): String {
        val language = _currentLanguage.value
        return when (theme) {
            "light" -> if (language == "en") "Light" else if (language == "pt") "Claro" else "Claro"
            "dark" -> if (language == "en") "Dark" else if (language == "pt") "Escuro" else "Oscuro"
            else -> if (language == "en") "System" else if (language == "pt") "Sistema" else "Sistema"
        }
    }

    fun getLanguageDisplayName(language: String): String {
        return when (language) {
            "es" -> "Español"
            "en" -> "English"
            "pt" -> "Português"
            else -> "Español"
        }
    }

    // Funciones de persistencia usando SharedPreferences
    private fun getSavedTheme(): String {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPref.getString("theme", "system") ?: "system"
    }

    private fun saveTheme(theme: String) {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit().putString("theme", theme).apply()
    }

    private fun getSavedLanguage(): String {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPref.getString("language", "es") ?: "es"
    }

    private fun saveLanguage(language: String) {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit().putString("language", language).apply()
    }

    private fun getSavedNotifications(): Boolean {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("notifications", true)
    }

    private fun saveNotifications(enabled: Boolean) {
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("notifications", enabled).apply()
    }

    private fun updateAppLanguage(languageCode: String) {
        // Esta función prepara el cambio de idioma
        // En una implementación real, necesitarías recrear la actividad
        // para aplicar completamente el cambio de idioma
        val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPref.edit().putString("language", languageCode).apply()
    }
}