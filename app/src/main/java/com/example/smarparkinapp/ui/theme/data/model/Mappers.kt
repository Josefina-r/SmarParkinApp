package com.example.smarparkinapp.ui.theme.data.model

fun ParkingSpotResponse.toParkingSpot() = ParkingSpot(
    id = id,
    name = nombre,
    address = direccion,
    price = precioHora,
    availableSpots = plazasDisponibles,
    latitude = latitud,
    longitude = longitud,


    nivelSeguridad = nivelSeguridad ?: 1,
    ratingPromedio = ratingPromedio ?: 0.0,
    totalResenas = totalResenas ?: 0,
    estaAbierto = estaAbierto ?: true,
    tieneCamaras = tieneCamaras ?: false,
    tieneVigilancia24h = tieneVigilancia24h ?: false,
    distanciaKm = distanciaKm
)