// ParkingSpotResponse.kt
package com.example.smarparkinapp.ui.theme.data.model

data class ParkingSpotResponse(
    val id: Int,
    val name: String,
    val address: String,
    val price: String,
    val distance: String,
    val availableSpots: Int,
    val rating: Float,
    val securityLevel: String,
    val latitude: Double,
    val longitude: Double
)