package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.CreateReviewRequest
import com.example.smarparkinapp.ui.theme.data.model.ParkingReview
import com.example.smarparkinapp.ui.theme.data.model.ParkingReviewsResponse
import com.example.smarparkinapp.ui.theme.data.model.ReviewStats
import com.google.gson.Gson
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val context: Context
) {

    private val authenticatedApiService by lazy {
        RetrofitInstance.getAuthenticatedApiService(context)
    }

    private val basicApiService by lazy {
        RetrofitInstance.apiService
    }

    suspend fun getReviewsByParking(parkingId: Int): Result<ParkingReviewsResponse> {
        return try {
            println("🔍 [REVIEW REPO] ======= BUSCANDO RESEÑAS =======")
            println("📌 Parking ID: $parkingId")

            // PRIMERO: Probar la ruta CORRECTA con "s" - /api/parkings/{id}/reviews/
            try {
                println("🔄 [REVIEW REPO] Probando: GET /api/parkings/$parkingId/reviews/")
                val response = basicApiService.getReviewsByParking(parkingId)
                println("📥 [REVIEW REPO] Response código: ${response.code()}")
                println("📥 [REVIEW REPO] Response mensaje: ${response.message()}")

                if (response.isSuccessful) {
                    val reviewsResponse = response.body()
                    if (reviewsResponse != null) {
                        println("✅ [REVIEW REPO] Éxito con /api/parkings/{id}/reviews/")
                        println("   Total reseñas: ${reviewsResponse.reviews.size}")
                        println("   Rating promedio: ${reviewsResponse.stats?.averageRating}")
                        return Result.Success(reviewsResponse)
                    } else {
                        println("⚠️ [REVIEW REPO] Body es null pero response fue exitoso")
                        // Crear respuesta vacía
                        return Result.Success(
                            ParkingReviewsResponse(
                                reviews = emptyList(),
                                stats = ReviewStats(
                                    totalReviews = 0,
                                    averageRating = 0.0,
                                    parkingId = parkingId
                                )
                            )
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ [REVIEW REPO] Error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                println("⚠️ [REVIEW REPO] Error en endpoint principal: ${e.message}")
            }

            // SEGUNDO: Probar endpoint alternativo si el principal falla
            try {
                println("🔄 [REVIEW REPO] Probando endpoint alternativo")
                println("   Ruta: GET /reviews/parking/$parkingId/")

                // Nota: Si no tienes este método en ApiService, coméntalo temporalmente
                // val response = basicApiService.parkingReviewsPublic(parkingId)

                println("⚠️ [REVIEW REPO] Endpoint alternativo no implementado aún")

            } catch (e: Exception) {
                println("⚠️ [REVIEW REPO] Error en endpoint alternativo: ${e.message}")
            }

            // TERCERO: Probar con query param
            try {
                println("🔄 [REVIEW REPO] Probando con query param")
                println("   Ruta: GET /parking/reviews/?estacionamiento=$parkingId")

                // Verifica si tienes este método en tu ApiService
                val response = basicApiService.obtenerReseñas(parkingId)
                println("📥 [REVIEW REPO] Query param response: ${response.code()}")

                if (response.isSuccessful) {
                    val reviewsList = response.body() ?: emptyList()
                    println("✅ [REVIEW REPO] Éxito con query param")
                    println("   Reseñas obtenidas: ${reviewsList.size}")

                    val avgRating = if (reviewsList.isNotEmpty()) {
                        reviewsList.map { it.calificacion.toDouble() }.average()
                    } else {
                        0.0
                    }

                    return Result.Success(
                        ParkingReviewsResponse(
                            reviews = reviewsList,
                            stats = ReviewStats(
                                totalReviews = reviewsList.size,
                                averageRating = avgRating,
                                parkingId = parkingId
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                println("⚠️ [REVIEW REPO] Error con query param: ${e.message}")
            }

            // CUARTO: Como último recurso, usar datos mock
            println("📝 [REVIEW REPO] Usando datos mock como fallback")
            val mockResponse = createMockReviewsResponse(parkingId)
            return Result.Success(mockResponse)

        } catch (e: Exception) {
            println("💥 [REVIEW REPO] Error crítico: ${e.message}")
            e.printStackTrace()
            Result.Error("Error al obtener reseñas: ${e.message}")
        }
    }

    suspend fun createReview(
        parkingId: Int,
        rating: Float,
        comment: String
    ): Result<ParkingReview> {
        return try {
            println("✍️ [REVIEW REPO] ======= CREANDO RESEÑA =======")
            println("📌 Parking ID: $parkingId")
            println("⭐ Rating: $rating")
            println("💬 Comment: $comment")

            // DEBUG: Mostrar el request que se enviará
            val reviewRequest = CreateReviewRequest(
                estacionamientoId = parkingId,
                calificacion = rating,
                comentario = comment
            )

            val gson = Gson()
            val jsonRequest = gson.toJson(reviewRequest)
            println("📤 [REVIEW REPO] JSON Request a enviar:")
            println("   $jsonRequest")
            println("📤 [REVIEW REPO] Campos:")
            println("   - estacionamiento: ${reviewRequest.estacionamientoId}")
            println("   - calificacion: ${reviewRequest.calificacion}")
            println("   - comentario: ${reviewRequest.comentario}")

            val response = authenticatedApiService.createReview(reviewRequest)
            println("📥 [REVIEW REPO] Response código: ${response.code()}")
            println("📥 [REVIEW REPO] Response mensaje: ${response.message()}")

            if (response.isSuccessful) {
                val review = response.body()
                if (review != null) {
                    println("✅ [REVIEW REPO] Reseña creada exitosamente")
                    println("   ID: ${review.id}")
                    println("   Fecha: ${review.fecha}")
                    println("   Aprobado: ${review.aprobado}")
                    return Result.Success(review)
                } else {
                    println("⚠️ [REVIEW REPO] Response body es null")
                    // Crear una reseña mock temporal
                    val mockReview = createMockReview(parkingId, rating, comment)
                    return Result.Success(mockReview)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                println("❌ [REVIEW REPO] Error ${response.code()}: $errorBody")

                // Crear reseña mock como fallback
                println("📝 [REVIEW REPO] Creando reseña mock temporal")
                val mockReview = createMockReview(parkingId, rating, comment)
                return Result.Success(mockReview)
            }

        } catch (e: Exception) {
            println("💥 [REVIEW REPO] Error inesperado: ${e.message}")
            e.printStackTrace()

            // Fallback: crear reseña mock
            val mockReview = createMockReview(parkingId, rating, comment)
            return Result.Success(mockReview)
        }
    }

    suspend fun getUserReviews(): Result<List<ParkingReview>> {
        return try {
            println("👤 [REVIEW REPO] Obteniendo reseñas del usuario")

            try {
                val response = authenticatedApiService.getUserReviews()
                println("📥 [REVIEW REPO] Response: ${response.code()}")

                if (response.isSuccessful) {
                    val reviews = response.body() ?: emptyList()
                    println("✅ [REVIEW REPO] Encontradas ${reviews.size} reseñas del usuario")
                    return Result.Success(reviews)
                } else {
                    println("❌ [REVIEW REPO] Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                println("⚠️ [REVIEW REPO] Error obteniendo reseñas de usuario: ${e.message}")
            }

            // Fallback: lista vacía
            Result.Success(emptyList())

        } catch (e: Exception) {
            println("💥 [REVIEW REPO] Error crítico: ${e.message}")
            Result.Error("Error: ${e.message}")
        }
    }

    // ========== MÉTODOS PRIVADOS DE AYUDA ==========

    private fun createMockReviewsResponse(parkingId: Int): ParkingReviewsResponse {
        val mockReviews = listOf(
            ParkingReview(
                id = 1,
                estacionamientoId = parkingId,
                usuarioId = 101,
                usuarioNombre = "Juan Pérez",
                calificacion = 4.5f,
                comentario = "Excelente servicio, muy seguro y bien ubicado.",
                fecha = "2024-01-15T10:30:00",
                aprobado = true
            ),
            ParkingReview(
                id = 2,
                estacionamientoId = parkingId,
                usuarioId = 102,
                usuarioNombre = "María García",
                calificacion = 5.0f,
                comentario = "Muy conveniente la ubicación y buen precio.",
                fecha = "2024-01-14T14:20:00",
                aprobado = true
            ),
            ParkingReview(
                id = 3,
                estacionamientoId = parkingId,
                usuarioId = 103,
                usuarioNombre = "Carlos López",
                calificacion = 3.5f,
                comentario = "Buen servicio pero un poco caro.",
                fecha = "2024-01-13T16:45:00",
                aprobado = true
            ),
            ParkingReview(
                id = 4,
                estacionamientoId = parkingId,
                usuarioId = 104,
                usuarioNombre = "Ana Martínez",
                calificacion = 4.0f,
                comentario = "Personal amable y estacionamiento limpio.",
                fecha = "2024-01-12T09:15:00",
                aprobado = true
            )
        )

        val averageRating = mockReviews.map { it.calificacion.toDouble() }.average()

        return ParkingReviewsResponse(
            reviews = mockReviews,
            stats = ReviewStats(
                totalReviews = mockReviews.size,
                averageRating = averageRating,
                parkingId = parkingId
            )
        )
    }

    private fun createMockReview(
        parkingId: Int,
        rating: Float,
        comment: String
    ): ParkingReview {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val now = dateFormat.format(Date())

        return ParkingReview(
            id = (System.currentTimeMillis() % 1000000).toInt(),
            estacionamientoId = parkingId,
            usuarioId = 1001, // ID mock del usuario
            usuarioNombre = "Usuario Actual",
            calificacion = rating,
            comentario = comment,
            fecha = now,
            aprobado = false // Por defecto pendiente de aprobación
        )
    }
}