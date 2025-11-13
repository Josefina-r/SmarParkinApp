package com.example.smarparkinapp.ui.theme.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(
    private val locationViewModel: LocationViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(locationViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}