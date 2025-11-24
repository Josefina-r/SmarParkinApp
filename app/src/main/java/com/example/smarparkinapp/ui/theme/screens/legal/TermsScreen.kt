// screens/legal/TermsScreen.kt
package com.example.smarparkinapp.ui.theme.screens.legal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Términos y Condiciones",
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = AzulPrincipal)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "TÉRMINOS Y CONDICIONES DE USO",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AzulPrincipal,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Contenido de términos y condiciones
            LegalSection(title = "1. Aceptación de los Términos") {
                "Al descargar, instalar o utilizar la aplicación ParkeaYa, usted acepta estar sujeto a estos Términos y Condiciones."
            }

            LegalSection(title = "2. Uso del Servicio") {
                "ParkeaYa proporciona una plataforma para la reserva de estacionamientos. Usted se compromete a utilizar el servicio de forma legal y ética."
            }

            LegalSection(title = "3. Registro y Cuenta") {
                "Para utilizar ciertas funciones, deberá registrarse proporcionando información veraz y actualizada."
            }

            LegalSection(title = "4. Pagos y Tarifas") {
                "Los precios de los estacionamientos son establecidos por los propietarios. ParkeaYa actúa como intermediario."
            }

            LegalSection(title = "5. Cancelaciones y Reembolsos") {
                "Las políticas de cancelación varían según el estacionamiento. Consulte las condiciones específicas antes de reservar."
            }

            Text(
                "Última actualización: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date())}",
                color = GrisTexto,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
fun LegalSection(title: String, content: () -> String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = AzulPrincipal,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = content(),
            fontSize = 14.sp,
            color = GrisTexto,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}