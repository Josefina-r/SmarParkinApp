// ReviewViewModelFactory.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarparkinapp.ui.theme.data.repository.ParkingRepository
import com.example.smarparkinapp.ui.theme.data.repository.ReviewRepository

class ReviewViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            val parkingRepository = ParkingRepository(context)
            val reviewRepository = ReviewRepository(context)
            return ReviewViewModel(parkingRepository, reviewRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}