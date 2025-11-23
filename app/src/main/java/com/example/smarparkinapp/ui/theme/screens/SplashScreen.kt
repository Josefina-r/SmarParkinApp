package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.smarparkinapp.ui.theme.theme.VerdePrincipal
import com.example.smarparkinapp.ui.theme.theme.Blanco
import com.example.smarparkinapp.ui.theme.theme.VerdeSecundario

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Simula un delay de 2 segundos antes de ir al Login
    LaunchedEffect(true) {
        delay(2000)
        onTimeout()
    }

    // Fondo verde intenso en toda la pantalla
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = VerdePrincipal // Verde intenso del tema
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Logo/Texto de la app
                Text(
                    text = "ParkeaYa",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Blanco, // Texto blanco para contraste
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CircularProgressIndicator(
                    color = Blanco,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )

                Text(
                    text = "Cargando...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Blanco.copy(alpha = 0.8f)
                )
            }
        }
    }
}
