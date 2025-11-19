package com.example.smarparkinapp.ui.theme.theme

import androidx.compose.ui.graphics.Color

// AZULES VIBRANTES (primarios - confianza, tecnología)
val AzulPrincipal = Color(0xFF0066CC)       // Azul vibrante principal
val AzulSecundario = Color(0xFF0099FF)      // Azul brillante para botones
val AzulClaro = Color(0xFF66CCFF)           // Azul claro para fondos

// VERDES ENERGÉTICOS (secundarios - disponibilidad, éxito)
val VerdePrincipal = Color(0xFF00AA55)      // Verde vibrante para acciones
val VerdeSecundario = Color(0xFF00CC66)     // Verde brillante para estados activos
val VerdeClaro = Color(0xFF66FFAA)          // Verde claro para highlights

// NEUTROS MODERNOS
val Blanco = Color(0xFFFFFFFF)              // Blanco puro
val GrisClaro = Color(0xFFF5F7FA)           // Gris muy claro para fondos
val GrisMedio = Color(0xFFE1E8F0)           // Gris medio para bordes
val GrisTexto = Color(0xFF666666)           // Gris para texto secundario

// COLORES DE ESTADO
val SuccessColor = Color(0xFF00C853)        // Verde éxito
val WarningColor = Color(0xFFFF9800)        // Naranja advertencia   // Rojo error
val InfoColor = Color(0xFF2196F3)           // Azul información

// GRADIENTES PARA BOTONES Y TARJETAS
val GradientAzul = listOf(AzulPrincipal, AzulSecundario)
val GradientVerde = listOf(VerdePrincipal, VerdeSecundario)