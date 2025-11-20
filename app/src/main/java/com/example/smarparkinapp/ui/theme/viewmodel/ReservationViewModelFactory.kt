// ui/theme/viewmodel/ReservationViewModelFactory.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReservationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            return ReservationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}