// screens/legal/PrivacyScreen.kt
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
fun PrivacyScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Política de Privacidad",
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
                "POLÍTICA DE PRIVACIDAD",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = AzulPrincipal,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LegalSection(title = "1. Información que Recopilamos") {
                "Recopilamos información que usted nos proporciona directamente, como datos de registro, información de perfil, y datos de reservas."
            }

            LegalSection(title = "2. Uso de la Información") {
                "Utilizamos su información para proporcionar y mejorar nuestros servicios, procesar pagos, y comunicarnos con usted."
            }

            LegalSection(title = "3. Compartición de Información") {
                "No vendemos su información personal. Compartimos datos limitados con propietarios de estacionamientos para procesar reservas."
            }

            LegalSection(title = "4. Seguridad de Datos") {
                "Implementamos medidas de seguridad técnicas y organizativas para proteger su información personal."
            }

            LegalSection(title = "5. Sus Derechos") {
                "Usted tiene derecho a acceder, corregir o eliminar su información personal. Contacte a soporte@parkeaya.com para ejercer estos derechos."
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