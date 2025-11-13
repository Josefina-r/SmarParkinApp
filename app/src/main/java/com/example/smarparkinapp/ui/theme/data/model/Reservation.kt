package com.example.smarparkinapp.ui.theme.data.model
import java.util.UUID
import java.time.LocalDateTime

data class Reservation(
    val id: Int,
    val codigoReserva: UUID,
    val usuario: User,
    val vehiculo: Car,
    val estacionamiento: ParkingLot,
    val horaEntrada: LocalDateTime,
    val horaSalida: LocalDateTime?,
    val duracionMinutos: Int,
    val costoEstimado: Double,
    val estado: ReservationState,
    val tipoReserva: ReservationType,
    val createdAt: LocalDateTime,
    val tiempoRestante: Int? = null,
    val puedeCancelar: Boolean = false
)

enum class ReservationState {
    ACTIVA, FINALIZADA, CANCELADA
}

enum class ReservationType {
    HORA, DIA, MES
}
enum class VehicleType {
    COMPACTO, SEDAN, SUV, CAMIONETA, MOTOCICLETA, VEHICULO_GRANDE
}

data class ParkingLot(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val precioHora: Double,
    val precioDia: Double?,
    val precioMes: Double?,
    val capacidadTotal: Int,
    val plazasDisponibles: Int,
    val tipoReservaPermitido: ReservationType,
    val multiplicadores: Map<VehicleType, Double>
)

data class User(
    val id: Int,
    val email: String,
    val nombre: String,
    val rol: UserRole
)

enum class UserRole {
    USER, OWNER, ADMIN
}