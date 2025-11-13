package com.example.smarparkinapp.ui.theme.data.repository
/* repository/ReservationRepository.kt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class ReservationRepository @Inject constructor(
    private val reservationService: ReservationService
) {

    suspend fun getReservations(): Flow<Resource<List<Reservation>>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.getReservations()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error al obtener reservas: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun createReservation(request: CreateReservationRequest): Flow<Resource<Reservation>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.createReservation(request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al crear reserva: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun cancelReservation(codigoReserva: UUID): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.cancelReservation(codigoReserva)
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Error al cancelar reserva: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun extendReservation(codigoReserva: UUID, minutosExtra: Int, tipoReserva: ReservationType? = null): Flow<Resource<Reservation>> = flow {
        emit(Resource.Loading())
        try {
            val request = ExtendReservationRequest(minutosExtra, tipoReserva)
            val response = reservationService.extendReservation(codigoReserva, request)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al extender reserva: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun checkIn(codigoReserva: UUID): Flow<Resource<CheckInResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.checkIn(codigoReserva)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al hacer check-in: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun checkOut(codigoReserva: UUID): Flow<Resource<CheckOutResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.checkOut(codigoReserva)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Error al hacer check-out: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getUserActiveReservations(): Flow<Resource<List<Reservation>>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.getUserActiveReservations()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error al obtener reservas activas: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getReservationTypes(): Flow<Resource<List<ReservationTypeInfo>>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.getReservationTypes()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                emit(Resource.Error("Error al obtener tipos de reserva: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.message}"))
        }
    }
}

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}*/