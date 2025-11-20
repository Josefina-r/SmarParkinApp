package com.example.smarparkinapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val id: Int = 0,
    val plate: String,
    val model: String,
    val brand: String,
    val color: String,
    val type: VehicleType = VehicleType.AUTOMOVIL
) : Parcelable // ← ¡Falta esta implementación!

@Parcelize // Si también quieres que VehicleType sea Parcelable
enum class VehicleType : Parcelable {
    AUTOMOVIL
}