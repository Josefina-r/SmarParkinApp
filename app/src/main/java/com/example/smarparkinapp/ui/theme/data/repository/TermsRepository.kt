package com.example.smarparkinapp.ui.theme.data.repository

import android.util.Base64
import com.example.smarparkinapp.ui.theme.data.api.ApiService
import com.example.smarparkinapp.ui.theme.data.model.TermsContent

class TermsRepository(private val apiService: ApiService) {

    suspend fun getTermsContent(code: Int): TermsContent {
        val response = apiService.getTermsByCode(code)
        if (response.isSuccessful) {
            val termsResponse = response.body()!!
            val htmlContent = decodeBase64ToHtml(termsResponse.content_base64) // Cambié aquí
            return TermsContent(htmlContent = htmlContent) // Title tiene valor por defecto
        } else {
            throw Exception("Error ${response.code()}: No se encontraron términos para el código $code")
        }
    }

    private fun decodeBase64ToHtml(base64String: String): String {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            String(decodedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            """
           <html>
               <body style="font-family: Arial, sans-serif; padding: 20px;">
                   <h2>Error al decodificar contenido</h2>
                   <p>El contenido no pudo ser procesado correctamente.</p>
               </body>
           </html>
           """.trimIndent()
        }
    }
}
