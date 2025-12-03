package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class TicketValidationResponse(
    @SerializedName("valido")
    val valido: Boolean,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("ticket")
    val ticket: TicketResponse? = null
)