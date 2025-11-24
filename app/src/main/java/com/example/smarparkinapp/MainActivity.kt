package com.example.smarparkinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.smarparkinapp.ui.theme.Navigation.AppNavGraph
import com.example.smarparkinapp.ui.theme.data.AppStrings
import com.example.smarparkinapp.ui.theme.data.LocalStrings
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.SettingsViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DynamicTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun DynamicTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )

    // Usar .value en lugar de by para collectAsState
    val currentThemeState = settingsViewModel.currentTheme.collectAsState()
    val currentTheme = currentThemeState.value

    val currentLanguageState = settingsViewModel.currentLanguage.collectAsState()
    val currentLanguage = currentLanguageState.value

    // Usar strings según el idioma seleccionado
    val currentStrings = remember(currentLanguage) {
        AppStrings().getStringsForLanguage(currentLanguage)
    }

    val isSystemInDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (currentTheme) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme // "system"
    }

    // Esquemas de color
    val lightColorScheme = lightColorScheme(
        primary = VerdePrincipal,
        onPrimary = Blanco,
        secondary = VerdeSecundario,
        onSecondary = Blanco,
        tertiary = AzulPrincipal,
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
    )

    val darkColorScheme = darkColorScheme(
        primary = VerdePrincipal,
        onPrimary = Blanco,
        secondary = VerdeSecundario,
        onSecondary = Blanco,
        tertiary = AzulPrincipal,
        background = Color(0xFF1C1B1F),
        surface = Color(0xFF1C1B1F),
        onBackground = Color(0xFFFFFBFE),
        onSurface = Color(0xFFFFFBFE),
    )

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        content = {
            // Proveer los strings a toda la app según el idioma
            LocalStrings provides currentStrings
            content()
        }
    )
}