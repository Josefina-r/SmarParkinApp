package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class PaginatedTicketResponse(
    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<TicketResponse>? = null
)

// También para otros modelos si es necesario
data class PaginatedReservationResponse(
    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<ReservationResponse>? = null
)