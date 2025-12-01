package com.example.smarparkinapp.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

// Transformación visual para tarjeta de crédito
val CreditCardFilter = VisualTransformation { text ->
    val trimmed = text.text.take(19) // 16 dígitos + 3 espacios
    val output = trimmed.chunked(4).joinToString(" ")
    TransformedText(AnnotatedString(output), OffsetMapping.Identity)
}