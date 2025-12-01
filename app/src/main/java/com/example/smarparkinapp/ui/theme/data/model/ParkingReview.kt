// ParkingReview.kt - Modelo ACTUALIZADO
package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName

data class ParkingReview(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("estacionamiento")
    val estacionamientoId: Int = 0,

    @SerializedName("usuario")
    val usuarioId: Int = 0,

    @SerializedName("usuario_nombre")
    val usuarioNombre: String = "",

    @SerializedName("usuario_foto")
    val usuarioFoto: String? = null,

    @SerializedName("calificacion")
    val calificacion: Float = 0f,

    @SerializedName("comentario")
    val comentario: String = "",

    @SerializedName("fecha")
    val fecha: String = "",

    @SerializedName("aprobado")
    val aprobado: Boolean = true
)

data class CreateReviewRequest(
    @SerializedName("estacionamiento")
    val estacionamientoId: Int,

    @SerializedName("calificacion")
    val calificacion: Float,

    @SerializedName("comentario")
    val comentario: String
)

data class ParkingReviewsResponse(
    @SerializedName("reviews")
    val reviews: List<ParkingReview> = emptyList(),

    @SerializedName("stats")
    val stats: ReviewStats? = null
)

data class ReviewStats(
    @SerializedName("total_reviews")
    val totalReviews: Int = 0,

    @SerializedName("average_rating")
    val averageRating: Double = 0.0,

    @SerializedName("parking_id")
    val parkingId: Int = 0
)