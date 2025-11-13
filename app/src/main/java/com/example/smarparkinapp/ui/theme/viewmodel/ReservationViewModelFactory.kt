package com.example.smarparkinapp.ui.theme.viewmodel
/*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarparkinapp.ui.theme.data.repository.ReservationRepository

class ReservationViewModelFactory(
    private val repo: ReservationRepository,
    private val parkingId: Int,
    private val pricePerHour: Double
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(repo, parkingId, pricePerHour) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
*/