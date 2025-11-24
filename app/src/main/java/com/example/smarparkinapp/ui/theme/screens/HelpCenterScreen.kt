// screens/HelpCenterScreen.kt
package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Centro de Ayuda",
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
                "¿Cómo podemos ayudarte?",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = AzulPrincipal,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Preguntas frecuentes
            HelpSection(title = "Preguntas Frecuentes") {
                HelpItem(
                    question = "¿Cómo reservo un estacionamiento?",
                    answer = "1. Busca estacionamientos en el mapa\n2. Toca en 'Ver cochera'\n3. Selecciona fecha y hora\n4. Elige tu vehículo\n5. Confirma la reserva"
                )

                HelpItem(
                    question = "¿Qué métodos de pago aceptan?",
                    answer = "Aceptamos: ParkeaYa Saldo, tarjetas de crédito/débito, Yape, Plin y otras billeteras digitales."
                )

                HelpItem(
                    question = "¿Puedo cancelar una reserva?",
                    answer = "Sí, las cancelaciones son posibles según la política de cada estacionamiento. Consulta los términos antes de reservar."
                )

                HelpItem(
                    question = "¿Cómo contacto al soporte?",
                    answer = "Puedes usar nuestro chat de soporte en la app, enviar un email a soporte@parkeaya.com o llamar al +51 123 456 789."
                )
            }

            // Guías de uso
            HelpSection(title = "Guías de Uso") {
                HelpItem(
                    question = "Primeros pasos en ParkeaYa",
                    answer = "1. Completa tu perfil\n2. Agrega tus vehículos\n3. Explora estacionamientos cercanos\n4. Realiza tu primera reserva"
                )

                HelpItem(
                    question = "Gestión de vehículos",
                    answer = "Puedes agregar múltiples vehículos en 'Mi perfil' → 'Mis vehículos'. Esto agiliza el proceso de reserva."
                )

                HelpItem(
                    question = "Recarga de saldo",
                    answer = "Ve a 'ParkeaYa saldo' en el menú y sigue las instrucciones para recargar. Tu saldo se actualiza instantáneamente."
                )
            }

            // Contacto de emergencia
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = VerdePrincipal.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "¿Necesitas ayuda inmediata?",
                        fontWeight = FontWeight.Bold,
                        color = VerdePrincipal,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Nuestro equipo de soporte está disponible 24/7",
                        color = GrisTexto,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { navController.navigate("chatbot") },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chatear con soporte")
                    }
                }
            }
        }
    }
}

@Composable
fun HelpSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = AzulPrincipal,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun HelpItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) } // ✅ CORREGIDO: Agregado remember

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded } // ✅ CORREGIDO: Ahora funciona el "!"
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    tint = GrisMedio
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    color = GrisTexto,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}