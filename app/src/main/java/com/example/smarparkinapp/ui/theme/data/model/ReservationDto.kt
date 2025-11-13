package com.example.smarparkinapp.ui.theme.data.model

import java.time.LocalDateTime
import java.util.UUID

data class CreateReservationRequest(
    val vehiculo: Int,
    val estacionamiento: Int,
    val horaEntrada: LocalDateTime,
    val duracionMinutos: Int,
    val tipoReserva: ReservationType
)

data class ExtendReservationRequest(
    val minutosExtra: Int,
    val tipoReserva: ReservationType? = null
)

data class CheckInResponse(
    val detail: String,
    val reserva: Reservation
)

data class CheckOutResponse(
    val detail: String,
    val costoFinal: Double,
    val tiempoEstacionadoMinutos: Double,
    val tipoReserva: ReservationType,
    val reserva: Reservation
)

data class ReservationStatsResponse(
    val totalReservas: Int? = null,
    val reservasActivas: Int? = null,
    val porTipoReserva: Map<String, Int>? = null,
    val porTipoVehiculo: List<VehicleTypeStats>? = null
)

data class VehicleTypeStats(
    val tipoVehiculo: String,
    val total: Int
)

data class ReservationTypeInfo(
    val value: String,
    val label: String,
    val duracionMinima: Int
)