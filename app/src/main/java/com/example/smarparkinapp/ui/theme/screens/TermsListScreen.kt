package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarparkinapp.ui.theme.theme.AzulPrincipal
import com.example.smarparkinapp.ui.theme.theme.Blanco
import com.example.smarparkinapp.ui.theme.theme.GrisTexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsListScreen(
    navController: NavController,
    onBackClick: () -> Unit
) {
    // Lista de documentos legales disponibles
    val legalDocuments = listOf(
        LegalDocument(
            code = 100,
            title = "Términos y Condiciones Generales",
            description = "Términos generales de uso del servicio Parkeaya",
            icon = Icons.Default.Description,
            category = "TÉRMINOS"
        ),
        LegalDocument(
            code = 101,
            title = "Términos Específicos - Reservas",
            description = "Condiciones específicas para reservas de estacionamiento",
            icon = Icons.Default.EventNote,
            category = "TÉRMINOS"
        ),
        LegalDocument(
            code = 102,
            title = "Condiciones de Uso - Vehículos",
            description = "Políticas sobre registro y uso de vehículos",
            icon = Icons.Default.DirectionsCar,
            category = "TÉRMINOS"
        ),
        LegalDocument(
            code = 103,
            title = "Política de Cancelaciones",
            description = "Condiciones para cancelar reservas y reembolsos",
            icon = Icons.Default.Cancel,
            category = "TÉRMINOS"
        ),
        LegalDocument(
            code = 104,
            title = "Condiciones de Tarifas",
            description = "Información sobre tarifas, precios y cargos",
            icon = Icons.Default.AttachMoney,
            category = "TÉRMINOS"
        ),
        LegalDocument(
            code = 200,
            title = "Política de Privacidad",
            description = "Cómo manejamos y protegemos tus datos personales",
            icon = Icons.Default.PrivacyTip,
            category = "PRIVACIDAD"
        ),
        LegalDocument(
            code = 201,
            title = "Política de Cookies",
            description = "Uso de cookies y tecnologías similares",
            icon = Icons.Default.Cookie,
            category = "PRIVACIDAD"
        ),
        LegalDocument(
            code = 300,
            title = "Preguntas Frecuentes (FAQ)",
            description = "Respuestas a las preguntas más comunes",
            icon = Icons.Default.Help,
            category = "AYUDA"
        ),
        LegalDocument(
            code = 400,
            title = "Contacto y Soporte",
            description = "Información de contacto y canales de soporte",
            icon = Icons.Default.ContactSupport,
            category = "AYUDA"
        )
    )

    // Agrupar por categoría
    val groupedByCategory = legalDocuments.groupBy { it.category }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Documentos Legales",
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = AzulPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Encabezado
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Documentación Legal",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "Consulta nuestros términos, políticas y condiciones de uso",
                        fontSize = 14.sp,
                        color = GrisTexto,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Documentos agrupados por categoría
            groupedByCategory.forEach { (category, documents) ->
                item {
                    Text(
                        text = category,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color(0xFFE3F2FD))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }

                items(documents) { document ->
                    LegalDocumentCard(
                        document = document,
                        onClick = {
                            // Navegar al detalle con el código
                            navController.navigate("terms/${document.code}")
                        }
                    )
                }

                // Espacio entre categorías
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Pie de página
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Versión 1.0.0",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                    Text(
                        text = "Última actualización: Noviembre 2025",
                        fontSize = 12.sp,
                        color = GrisTexto,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

data class LegalDocument(
    val code: Int,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val category: String
)

@Composable
fun LegalDocumentCard(
    document: LegalDocument,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = document.icon,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF333333),
                    maxLines = 2
                )

                Text(
                    text = document.description,
                    fontSize = 14.sp,
                    color = GrisTexto,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )

                Text(
                    text = "Código: ${document.code}",
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver documento",
                tint = Color(0xFF999999),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
