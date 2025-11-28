package com.example.smarparkinapp.ui.theme.data.model
import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    val id: String,
    val name: String,
    val emoji: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

enum class PaymentStatus {
    @SerializedName("pendiente")
    PENDIENTE,

    @SerializedName("procesando")
    PROCESANDO,

    @SerializedName("pagado")
    PAGADO,

    @SerializedName("fallido")
    FALLIDO,

    @SerializedName("reembolsado")
    REEMBOLSADO,

    @SerializedName("cancelado")
    CANCELADO
}