// ReviewViewModel.kt - VERSIÓN ACTUALIZADA
package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.model.ParkingReview
import com.example.smarparkinapp.ui.theme.data.model.ParkingReviewsResponse
import com.example.smarparkinapp.ui.theme.data.repository.ParkingRepository
import com.example.smarparkinapp.ui.theme.data.repository.ReviewRepository
import com.example.smarparkinapp.ui.theme.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val parkingRepository: ParkingRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    // Estados para reseñas
    private val _reviewsResponse = MutableStateFlow<ParkingReviewsResponse?>(null)
    val reviewsResponse: StateFlow<ParkingReviewsResponse?> = _reviewsResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _reviewCreated = MutableStateFlow(false)
    val reviewCreated: StateFlow<Boolean> = _reviewCreated

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Cargar reseñas de un parking
    fun loadParkingReviews(parkingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = reviewRepository.getReviewsByParking(parkingId)) {
                is Result.Success -> {
                    _reviewsResponse.value = result.data
                    println("✅ [ReviewViewModel] Reseñas cargadas: ${result.data.reviews.size}")
                }
                is Result.Error -> {
                    _error.value = result.message
                    println("❌ [ReviewViewModel] Error: ${result.message}")
                }
                Result.Loading -> {
                    // Ya estamos manejando loading
                }
            }

            _isLoading.value = false
        }
    }

    // Crear una nueva reseña
    fun createReview(parkingId: Int, rating: Float, comment: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _reviewCreated.value = false

            when (val result = reviewRepository.createReview(parkingId, rating, comment)) {
                is Result.Success -> {
                    _reviewCreated.value = true
                    println("✅ [ReviewViewModel] Reseña creada exitosamente")

                    // Recargar las reseñas después de crear una nueva
                    loadParkingReviews(parkingId)
                }
                is Result.Error -> {
                    _error.value = result.message
                    println("❌ [ReviewViewModel] Error creando reseña: ${result.message}")
                }
                Result.Loading -> {
                    // Ya estamos manejando loading
                }
            }

            _isLoading.value = false
        }
    }

    // Resetear estados
    fun resetStates() {
        _reviewCreated.value = false
        _error.value = null
    }

    // Obtener reseñas del usuario actual
    fun loadUserReviews() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = reviewRepository.getUserReviews()) {
                is Result.Success -> {
                    // Puedes manejar las reseñas del usuario aquí
                    println("✅ [ReviewViewModel] Reseñas del usuario cargadas: ${result.data.size}")
                }
                is Result.Error -> {
                    _error.value = result.message
                    println("❌ [ReviewViewModel] Error cargando reseñas de usuario: ${result.message}")
                }
                Result.Loading -> {
                    // Ya estamos manejando loading
                }
            }

            _isLoading.value = false
        }
    }
}