// viewmodel/ChatbotViewModel.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean, // true si es usuario, false si es bot
    val timestamp: Long = System.currentTimeMillis()
)

class ChatbotViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen.asStateFlow()

    init {
        // Mensaje de bienvenida inicial
        addBotMessage("¡Hola! Soy tu asistente de ParkeaYa. ¿En qué puedo ayudarte?")
    }

    fun sendUserMessage(message: String) {
        if (message.isBlank()) return

        // Agregar mensaje del usuario
        addUserMessage(message)

        // Procesar y responder
        viewModelScope.launch {
            // Simular delay de respuesta
            kotlinx.coroutines.delay(1000)
            val response = generateBotResponse(message)
            addBotMessage(response)
        }
    }

    fun toggleChat() {
        _isChatOpen.value = !_isChatOpen.value
    }

    fun openChat() {
        _isChatOpen.value = true
    }

    fun closeChat() {
        _isChatOpen.value = false
    }

    private fun addUserMessage(text: String) {
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = text,
            isUser = true
        )
        _messages.value = _messages.value + newMessage
    }

    private fun addBotMessage(text: String) {
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = text,
            isUser = false
        )
        _messages.value = _messages.value + newMessage
    }

    private fun generateBotResponse(userMessage: String): String {
        val lowerMessage = userMessage.lowercase()

        return when {
            lowerMessage.contains("hola") || lowerMessage.contains("hi") || lowerMessage.contains("buenas") ->
                "¡Hola! 😊 Soy tu asistente de ParkeaYa. Puedo ayudarte con:\n\n• Cómo usar la app\n• Encontrar estacionamiento\n• Hacer reservas\n• Gestionar tu cuenta\n\n¿Qué necesitas saber?"

            lowerMessage.contains("cómo") && lowerMessage.contains("usar") ->
                "*Guía de uso de ParkeaYa:*\n\n" +
                        "1. *Buscar estacionamiento*: Usa el mapa o el buscador\n" +
                        "2. *Filtrar resultados*: Por distancia, precio o tipo\n" +
                        "3. *Ver detalles*: Toca cualquier cochera para más info\n" +
                        "4. *Reservar*: Selecciona fecha, hora y vehículo\n" +
                        "5. *Pagar*: Usa ParkeaYa saldo o tu método preferido\n\n" +
                        "¿Te gustaría saber más sobre algún paso en específico?"

            lowerMessage.contains("reservar") || lowerMessage.contains("reserva") ->
                "*Para hacer una reserva:*\n\n" +
                        "1. Encuentra una cochera en el mapa\n" +
                        "2. Toca 'Ver cochera'\n" +
                        "3. Selecciona fecha y hora\n" +
                        "4. Elige tu vehículo\n" +
                        "5. Confirma la reserva\n" +
                        "6. Realiza el pago\n\n" +
                        "¡Recibirás una confirmación al instante! ✅"

            lowerMessage.contains("pago") || lowerMessage.contains("pagar") || lowerMessage.contains("saldo") ->
                "*Opciones de pago:*\n\n" +
                        "💳 *ParkeaYa Saldo*: Recarga y paga fácilmente\n" +
                        "💳 *Tarjeta crédito/débito*: Pago seguro\n" +
                        "📱 *Billeteras digitales*: Yape, Plin, etc.\n\n" +
                        "Puedes recargar tu saldo desde el menú 'ParkeaYa saldo'"

            lowerMessage.contains("precio") || lowerMessage.contains("cost") ->
                "Los precios varían según:\n\n" +
                        "🏢 *Tipo de cochera*: Edificio, casa, playa\n" +
                        "📍 *Ubicación*: Zona céntrica vs periferia\n" +
                        "⏰ *Horario*: Día vs noche\n" +
                        "🚗 *Tipo vehículo*: Auto, moto, camioneta\n\n" +
                        "Cada cochera muestra su precio antes de reservar."

            lowerMessage.contains("problema") || lowerMessage.contains("error") || lowerMessage.contains("no funciona") ->
                "Lamento escuchar que tienes un problema. 😔\n\n" +
                        "*Soluciones rápidas:*\n" +
                        "• Verifica tu conexión a internet\n" +
                        "• Reinicia la aplicación\n" +
                        "• Actualiza a la última versión\n\n" +
                        "Si el problema persiste, contacta a soporte técnico."

            lowerMessage.contains("contacto") || lowerMessage.contains("soporte") || lowerMessage.contains("ayuda") ->
                "*Canales de soporte:*\n\n" +
                        "📧 Email: soporte@parkeaya.com\n" +
                        "📞 Teléfono: +51 123 456 789\n" +
                        "💬 Chat en vivo: Disponible 24/7\n" +
                        "🕐 Horario: Lunes a Domingo 7am - 11pm"

            lowerMessage.contains("gracias") || lowerMessage.contains("thanks") ->
                "¡De nada! 😊 ¿Hay algo más en lo que pueda ayudarte?"

            else ->
                "Entiendo que quieres saber sobre: \"$userMessage\"\n\n" +
                        "Como asistente de ParkeaYa, puedo ayudarte con:\n\n" +
                        "🔍 *Buscar estacionamiento*\n" +
                        "📅 *Hacer reservas*\n" +
                        "💳 *Métodos de pago*\n" +
                        "👤 *Gestionar cuenta*\n" +
                        "❓ *Problemas técnicos*\n\n" +
                        "¿Sobre cuál de estos temas necesitas ayuda?"
        }
    }
}