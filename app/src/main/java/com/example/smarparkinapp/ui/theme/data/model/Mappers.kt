package com.example.smarparkinapp.ui.theme.data.model


fun ParkingSpotResponse.toParkingSpot() = ParkingSpot(
    id, name, address, price, distance, availableSpots, rating, securityLevel,
    latitude, longitude
)
