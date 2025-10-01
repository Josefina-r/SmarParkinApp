package com.example.smarparkinapp.ui.theme.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color




// Colores primarios
val Negro = Color(0xFF000000)
val ErrorColor = Color(0xFFB00020)


// 🔹 Esquemas de color
private val DarkColorScheme = darkColorScheme(
    primary = VerdePrincipal,
    onPrimary = Blanco,
    secondary = AzulPrincipal,
    onSecondary = Blanco,
    background = Color.Black,
    onBackground = Blanco,
    surface = Color.DarkGray,
    onSurface = Blanco,
    error = ErrorColor,
    onError = Blanco
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Blanco,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeSecundario,

    secondary = AzulPrincipal,
    onSecondary = Blanco,
    secondaryContainer = AzulClaro,
    onSecondaryContainer = AzulSecundario,

    background = GrisClaro,
    onBackground = Negro,
    surface = Blanco,
    onSurface = Negro,
    error = ErrorColor,
    onError = Blanco
)
@Composable
fun SmarParkinAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}