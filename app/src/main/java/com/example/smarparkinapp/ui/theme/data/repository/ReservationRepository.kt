package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.model.ReservationRequest
import com.example.smarparkinapp.ui.theme.data.api.GenericResponse
import com.example.smarparkinapp.ui.theme.data.model.Reservation
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import java.util.UUID
import com.example.smarparkinapp.ui.theme.data.model.*
import com.example.smarparkinapp.ui.theme.data.model.ReservationResponse
import com.example.smarparkinapp.ui.theme.data.model.TicketResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Result


class ReservationRepository(private val context: Context) {

    private val apiService by lazy {
        RetrofitInstance.getAuthenticatedApiService(context)
    }



    suspend fun createReservation(request: ReservationRequest): Result<ReservationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println(" Creando reserva...")
                println(" Request: $request")

                val response = apiService.createReservation(request)

                println(" Response: ${response.code()} - ${response.message()}")

                if (response.isSuccessful) {
                    val reservation = response.body()
                    if (reservation != null) {
                        println(" Reserva creada exitosamente:")
                        println("    Código: ${reservation.codigoReserva}")
                        println("    Costo: ${reservation.costoEstimado}")
                        println("    Estado: ${reservation.estado}")
                        println("    Usuario: ${reservation.usuarioNombre}") // ✅ CAMBIADO: Usar usuarioNombre
                        println("    Vehículo ID: ${reservation.vehiculoId}") // ✅ CAMBIADO: vehiculoId en lugar de vehiculo.placa
                        println("    Estacionamiento ID: ${reservation.estacionamientoId}") // ✅ CAMBIADO: estacionamientoId
                        Result.success(reservation)
                    } else {
                        println("❌ Respuesta vacía del servidor")
                        Result.failure(Exception("Respuesta vacía del servidor"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ Error API: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("❌ Exception: ${e.message}")
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    suspend fun getMyReservations(): Result<List<ReservationResponse>> = withContext(Dispatchers.IO) {
        try {
            println("📱 Obteniendo mis reservas...")
            val response = apiService.getMyReservations()

            if (response.isSuccessful) {
                val reservations = response.body() ?: emptyList()
                println("✅ Reservas obtenidas: ${reservations.size}")
                Result.success(reservations)
            } else {
                println("❌ Error obteniendo reservas: ${response.code()}")
                Result.failure(Exception("Error al obtener reservas: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("❌ Exception: ${e.message}")
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun cancelReservation(codigo: String): Result<GenericResponse> = withContext(Dispatchers.IO) {
        try {
            println("📱 Cancelando reserva: $codigo")
            val response = apiService.cancelReservation(codigo)

            if (response.isSuccessful) {
                val result = response.body() ?: GenericResponse("Cancelado")
                println("✅ Reserva cancelada: $codigo")
                Result.success(result)
            } else {
                println("❌ Error cancelando: ${response.code()}")
                Result.failure(Exception("Error al cancelar reserva"))
            }
        } catch (e: Exception) {
            println("❌ Exception: ${e.message}")
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun getActiveReservations(): Result<List<ReservationResponse>> = withContext(Dispatchers.IO) {
        try {
            println("📱 Obteniendo reservas activas...")
            val response = apiService.getActiveReservations()

            if (response.isSuccessful) {
                val reservations = response.body() ?: emptyList()
                println("✅ Reservas activas: ${reservations.size}")
                Result.success(reservations)
            } else {
                println("❌ Error obteniendo reservas activas")
                Result.failure(Exception("Error al obtener reservas activas"))
            }
        } catch (e: Exception) {
            println("❌ Exception: ${e.message}")
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    // ================== PAGOS ==================
// En ReservationRepository - createPayment
    // En ReservationRepository - createPayment
    suspend fun createPayment(reservationId: Long, metodo: String, monto: Double): Result<Payment> {
        return withContext(Dispatchers.IO) {
            try {
                println("💰 [ReservationRepository] === CREANDO PAGO REAL ===")

                val request = when (metodo.lowercase()) {
                    "efectivo" -> PaymentRequest.forEfectivo(reservationId, monto)
                    "yape" -> PaymentRequest.forYape(reservationId, monto)
                    "plin" -> PaymentRequest.forPlin(reservationId, monto)
                    "tarjeta" -> PaymentRequest.forTarjeta(reservationId, "token_simulado", monto)
                    else -> PaymentRequest.forEfectivo(reservationId, monto)
                }

                println("📤 [ReservationRepository] Enviando: $request")
                val response = apiService.createPayment(request)

                println("📡 [ReservationRepository] Respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    // ✅ LA API SOLO DEVUELVE {"reserva":12,"metodo":"yape"}
                    // CREAMOS EL PAYMENT MANUALMENTE
                    val payment = Payment(
                        id = UUID.randomUUID().toString(),
                        monto = monto,
                        metodo = metodo,
                        estado = "pendiente",
                        referenciaPago = "ref-${System.currentTimeMillis()}",
                        moneda = "PEN",
                        fechaCreacion = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
                    )

                    println("✅ [ReservationRepository] PAGO CREADO MANUALMENTE:")
                    println("   📋 ID: ${payment.id}")
                    println("   💰 Monto: ${payment.monto}")
                    println("   📍 Método: ${payment.metodo}")
                    println("   📊 Estado: ${payment.estado}")

                    Result.success(payment)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [ReservationRepository] ERROR: ${response.code()} - $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("💥 [ReservationRepository] EXCEPCIÓN: ${e.message}")
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }
    suspend fun processPayment(paymentId: String): Result<Payment> = withContext(Dispatchers.IO) {
        try {
            println("📱 Procesando pago: $paymentId")
            val response = apiService.processPayment(paymentId)

            if (response.isSuccessful) {
                val payment = response.body()
                if (payment != null) {
                    println("✅ Pago procesado: ${payment.estado}")
                    Result.success(payment)
                } else {
                    Result.failure(Exception("Respuesta vacía del servidor"))
                }
            } else {
                Result.failure(Exception("Error procesando pago"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    // ================== TICKETS ==================
    suspend fun getTicketByReservation(reservationId: Long): Result<TicketResponse> = withContext(Dispatchers.IO) {
        try {
            println("📱 Obteniendo ticket para reserva: $reservationId")
            val response = apiService.getTicketByReservation(reservationId)

            if (response.isSuccessful) {
                val ticket = response.body()
                if (ticket != null) {
                    println("✅ Ticket obtenido: ${ticket.codigoTicket}")
                    Result.success(ticket)
                } else {
                    Result.failure(Exception("No se encontró ticket"))
                }
            } else {
                Result.failure(Exception("Error obteniendo ticket"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun getValidTickets(): Result<List<TicketResponse>> = withContext(Dispatchers.IO) {
        try {
            println("📱 Obteniendo tickets válidos...")
            val response = apiService.getValidTickets()

            if (response.isSuccessful) {
                val tickets = response.body() ?: emptyList()
                println("✅ Tickets válidos: ${tickets.size}")
                Result.success(tickets)
            } else {
                Result.failure(Exception("Error obteniendo tickets"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }
}
