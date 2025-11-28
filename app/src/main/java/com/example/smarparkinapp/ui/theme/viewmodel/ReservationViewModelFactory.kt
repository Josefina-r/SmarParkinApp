package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smarparkinapp.ui.theme.data.repository.ReservationRepository
import com.example.smarparkinapp.ui.theme.data.repository.VehicleRepository
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.repository.ParkingRepository

class ReservationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            return ReservationViewModel(
                reservationRepository = ReservationRepository(context),
                vehicleRepository = VehicleRepository(context),
                parkingRepository = ParkingRepository(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}