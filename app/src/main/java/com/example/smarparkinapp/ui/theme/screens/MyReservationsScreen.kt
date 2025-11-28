package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smarparkinapp.ui.theme.screens.webview.WebViewComposable

@Composable
fun MyReservationsScreen(
    jwtToken: String = "",
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Opción 1: WebView embebido
        WebViewComposable(
            jwt = jwtToken,
            mode = "mis_reservas",
            modifier = Modifier.weight(1f),
            onFinish = onBack
        )

        // Opción 2: Botón para abrir en Activity (alternativa)
        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Volver")
        }
    }
}