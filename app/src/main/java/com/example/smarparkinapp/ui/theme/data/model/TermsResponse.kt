package com.example.smarparkinapp.ui.theme.data.model

data class TermsRequest(val code: Int)

data class TermsResponse(
    val content_base64: String
)

data class TermsContent(
    val htmlContent: String,
    val title: String = "Términos y Condiciones"
)
