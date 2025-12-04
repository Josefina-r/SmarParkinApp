// En tu data/model/ReservationPaginatedResponse.kt
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ReservationPaginatedResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?,

    @SerializedName("results")
    val results: List<ReservationResponse>
)