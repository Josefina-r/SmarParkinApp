package com.example.smarparkinapp.ui.theme.services

/* service/ReservationService.kt
import retrofit2.Response
import retrofit2.http.*
import java.util.UUID

interface ReservationService {

    /*Reservas
    @GET("reservations/")
    suspend fun getReservations(): Response<List<Reservation>>

    @GET("reservations/{id}/")
    suspend fun getReservation(@Path("id") id: Int): Response<Reservation>

    @POST("reservations/")
    suspend fun createReservation(@Body request: CreateReservationRequest): Response<Reservation>

    @PUT("reservations/{id}/")
    suspend fun updateReservation(@Path("id") id: Int, @Body reservation: Reservation): Response<Reservation>

    @DELETE("reservations/{id}/")
    suspend fun deleteReservation(@Path("id") id: Int): Response<Unit>

    // Acciones específicas
    @POST("reservations/{codigoReserva}/cancel/")
    suspend fun cancelReservation(@Path("codigoReserva") codigoReserva: UUID): Response<Unit>

    @POST("reservations/{codigoReserva}/extend/")
    suspend fun extendReservation(
        @Path("codigoReserva") codigoReserva: UUID,
        @Body request: ExtendReservationRequest
    ): Response<Reservation>

    // Check-in/out
    @POST("reservations/{codigoReserva}/checkin/")
    suspend fun checkIn(@Path("codigoReserva") codigoReserva: UUID): Response<CheckInResponse>

    @POST("reservations/{codigoReserva}/checkout/")
    suspend fun checkOut(@Path("codigoReserva") codigoReserva: UUID): Response<CheckOutResponse>

    // Consultas específicas
    @GET("reservations/user/active/")
    suspend fun getUserActiveReservations(): Response<List<Reservation>>

    @GET("reservations/parking/{parkingId}/")
    suspend fun getParkingReservations(@Path("parkingId") parkingId: Int): Response<List<Reservation>>

    @GET("reservations/stats/")
    suspend fun getReservationStats(): Response<ReservationStatsResponse>

    @GET("reservations/tipos/")
    suspend fun getReservationTypes(): Response<List<ReservationTypeInfo>>
}*/