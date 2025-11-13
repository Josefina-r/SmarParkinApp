package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarparkinapp.ui.theme.services.LocationService

@Suppress("UNCHECKED_CAST")
class LocationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            val locationService = LocationService(context)
            return LocationViewModel(locationService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}