package com.example.smarparkinapp.ui.theme.data.repository

import android.content.Context
import com.example.smarparkinapp.ui.theme.data.model.ReservationRequest
import com.example.smarparkinapp.ui.theme.data.api.GenericResponse
import com.example.smarparkinapp.ui.theme.data.api.ValidateTicketRequest
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

    suspend fun getMyReservations(): Result<List<ReservationResponse>> =
        withContext(Dispatchers.IO) {
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

    suspend fun cancelReservation(codigo: String): Result<GenericResponse> =
        withContext(Dispatchers.IO) {
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

    suspend fun getActiveReservations(): Result<List<ReservationResponse>> =
        withContext(Dispatchers.IO) {
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
                        fechaCreacion = java.text.SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date())
                    )

                    println(" [ReservationRepository] PAGO CREADO MANUALMENTE:")
                    println("    ID: ${payment.id}")
                    println("    Monto: ${payment.monto}")
                    println("    Método: ${payment.metodo}")
                    println("    Estado: ${payment.estado}")

                    Result.success(payment)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println(" [ReservationRepository] ERROR: ${response.code()} - $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println(" [ReservationRepository] EXCEPCIÓN: ${e.message}")
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }
    }

    suspend fun processPayment(paymentId: String): Result<Payment> = withContext(Dispatchers.IO) {
        try {
            println(" Procesando pago: $paymentId")
            val response = apiService.processPayment(paymentId)

            if (response.isSuccessful) {
                val payment = response.body()
                if (payment != null) {
                    println(" Pago procesado: ${payment.estado}")
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

    // ================== MÉTODOS PARA TICKETS REALES ==================

    suspend fun getUserTickets(): Result<List<TicketResponse>> = withContext(Dispatchers.IO) {
        try {
            println("📱 [TICKET API REAL] Obteniendo tickets del usuario...")
            val response = apiService.getUserTickets()

            println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

            if (response.isSuccessful) {
                val paginatedResponse = response.body()
                if (paginatedResponse != null) {
                    val tickets = paginatedResponse.results ?: emptyList()

                    println("✅ [TICKET API REAL] Respuesta paginada:")
                    println("   Total: ${paginatedResponse.count ?: 0}")
                    println("   Tickets obtenidos: ${tickets.size}")

                    tickets.forEachIndexed { index, ticket ->
                        println("   [$index] Ticket ID: ${ticket.id}")
                        println("       Estado: ${ticket.estado}")
                        println("       Código: ${ticket.codigoTicket}")
                        println("       Reserva ID: ${ticket.reservaId ?: "N/A"}")

                        // Si quieres debuggear el JSON de reserva
                        if (ticket.reservaRaw != null) {
                            try {
                                val reservaId = ticket.reservaRaw.asJsonObject?.get("id")?.asLong
                                println("       Reserva ID (raw): $reservaId")
                            } catch (e: Exception) {
                                println("       Error parseando reserva: ${e.message}")
                            }
                        }
                    }

                    Result.success(tickets)
                } else {
                    println("❌ [TICKET API REAL] Respuesta paginada vacía")
                    Result.success(emptyList())
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            println("❌ [TICKET API REAL] Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener ticket específico por ID (API REAL)
     */
    suspend fun getTicketById(ticketId: String): Result<TicketResponse> =
        withContext(Dispatchers.IO) {
            try {
                println("📱 [TICKET API REAL] Obteniendo ticket: $ticketId")
                val response = apiService.getTicketById(ticketId)

                println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val ticket = response.body()
                    if (ticket != null) {
                        println("✅ [TICKET API REAL] Ticket obtenido:")
                        println("   ID: ${ticket.id}")
                        println("   Código: ${ticket.codigoTicket}")
                        println("   Estado: ${ticket.estado}")

                        // O si quieres ver el JSON completo:
                        if (ticket.reservaRaw != null) {
                            println("   Reserva JSON: ${ticket.reservaRaw}")
                        }

                        println("   Fecha Emisión: ${ticket.fechaEmision}")
                        println("   Fecha Validez Hasta: ${ticket.fechaValidezHasta}")
                        Result.success(ticket)
                    } else {
                        println("❌ [TICKET API REAL] Ticket no encontrado")
                        Result.failure(Exception("Ticket no encontrado"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("❌ [TICKET API REAL] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }

    /**
     * Obtener tickets de un parking (para owner) (API REAL)
     */
    suspend fun getTicketsByParking(parkingId: Long): Result<List<TicketResponse>> =
        withContext(Dispatchers.IO) {
            try {
                println("📱 [TICKET API REAL] Obteniendo tickets del parking: $parkingId")
                val response = apiService.getTicketsByParking(parkingId)

                println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val tickets = when {
                        // Si es una lista directa
                        body is List<*> -> {
                            body.filterIsInstance<TicketResponse>()
                        }
                        // Si es paginada
                        body != null -> {
                            try {
                                val gson = com.google.gson.Gson()
                                val jsonString = gson.toJson(body)
                                val paginatedResponse = gson.fromJson(
                                    jsonString,
                                    PaginatedTicketResponse::class.java
                                )
                                paginatedResponse.results ?: emptyList()
                            } catch (e: Exception) {
                                println("❌ Error parseando tickets paginados: ${e.message}")
                                emptyList()
                            }
                        }

                        else -> emptyList()
                    }

                    println("✅ [TICKET API REAL] Tickets del parking: ${tickets.size}")

                    tickets.forEachIndexed { index, ticket ->
                        println("   [$index] Ticket: ${ticket.codigoTicket}, Estado: ${ticket.estado}")
                    }

                    Result.success(tickets)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("❌ [TICKET API REAL] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }

    /**
     * Validar ticket por ID (owner acepta reserva) (API REAL)
     * Usa el endpoint POST /tickets/{id}/validate/
     */
    suspend fun validateTicketById(ticketId: String): Result<TicketResponse> =
        withContext(Dispatchers.IO) {
            try {
                println("📱 [TICKET API REAL] Validando ticket por ID: $ticketId")
                val response = apiService.validateTicket(ticketId)

                println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        println("✅ [TICKET API REAL] Validación recibida:")
                        println("   Válido: ${result.valido}")
                        println("   Mensaje: ${result.mensaje}")

                        // Tu API Service retorna GenericValidationResponse
                        if (result.valido && result.ticket != null) {
                            Result.success(result.ticket)
                        } else {
                            Result.failure(Exception("Validación fallida: ${result.mensaje}"))
                        }
                    } else {
                        Result.failure(Exception("Respuesta vacía"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("❌ [TICKET API REAL] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }


    /**
     * Versión alternativa que retorna TicketResponse directamente
     */
    suspend fun validateTicketAndGetTicket(ticketId: String): Result<TicketResponse> =
        withContext(Dispatchers.IO) {
            try {
                println("📱 [TICKET API REAL] Validando y obteniendo ticket: $ticketId")
                val response = apiService.validateTicket(ticketId)

                println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val validationResult = response.body()
                    if (validationResult != null && validationResult.valido && validationResult.ticket != null) {
                        println("✅ [TICKET API REAL] Ticket validado exitosamente")
                        Result.success(validationResult.ticket)
                    } else {
                        val errorMsg = validationResult?.mensaje ?: "Validación fallida"
                        Result.failure(Exception(errorMsg))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("❌ [TICKET API REAL] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }

    /**
     * Cancelar ticket (API REAL)
     */
    suspend fun cancelTicket(
        ticketId: String,
        motivo: String = "Cancelado desde app móvil"
    ): Result<TicketResponse> = withContext(Dispatchers.IO) {
        try {
            println("📱 [TICKET API REAL] Cancelando ticket: $ticketId")



            val response = apiService.cancelTicket(ticketId, mapOf("motivo" to motivo))

            println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

            if (response.isSuccessful) {
                val ticket = response.body()
                if (ticket != null) {
                    println("✅ [TICKET API REAL] Ticket cancelado: ${ticket.estado}")
                    Result.success(ticket)
                } else {
                    Result.failure(Exception("Respuesta vacía"))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            println("❌ [TICKET API REAL] Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }


    suspend fun validateTicketByCode(codigoTicket: String): Result<TicketResponse> =
        withContext(Dispatchers.IO) {
            try {
                println("📱 [TICKET API REAL] Validando ticket por código: $codigoTicket")

                // Primero, necesitas agregar este endpoint a tu API Service:
                // @POST("api/tickets/validate/")
                // suspend fun validateTicketByCode(@Body request: ValidateTicketRequest): Response<TicketValidationResponse>

                val request = ValidateTicketRequest(codigoTicket = codigoTicket)

                // Este método NO existe en tu API Service actualmente
                // Debes agregarlo primero
                // val response = apiService.validateTicketByCode(request)

                // Temporalmente, podemos usar el endpoint por ID si tenemos el ID
                // O comentar esta función hasta que agregues el endpoint

                println("⚠️ [TICKET API REAL] Endpoint no implementado: validateTicketByCode")
                println("⚠️ Necesitas agregar el endpoint a tu API Service primero")

                Result.failure(Exception("Endpoint no implementado. Agrega validateTicketByCode a tu API Service"))

                // Cuando tengas el endpoint, descomenta esto:
                /*
                val response = apiService.validateTicketByCode(request)

                println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        println("✅ [TICKET API REAL] Validación por código recibida:")
                        println("   Válido: ${result.valido}")
                        println("   Mensaje: ${result.mensaje}")

                        if (result.valido && result.ticket != null) {
                            Result.success(result.ticket)
                        } else {
                            Result.failure(Exception("Validación fallida: ${result.mensaje}"))
                        }
                    } else {
                        Result.failure(Exception("Respuesta vacía"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
                */
            } catch (e: Exception) {
                println("❌ [TICKET API REAL] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }

    /**
     * Obtener tickets por reserva (API REAL)
     */
    suspend fun getTicketsByReservation(reservationId: Long): Result<List<TicketResponse>> =
        withContext(Dispatchers.IO) {
            try {
                println("📱 [TICKET API REAL] Obteniendo tickets por reserva: $reservationId")
                val response = apiService.getTicketsByReservation(reservationId)

                println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    val tickets = when {
                        body is List<*> -> {
                            body.filterIsInstance<TicketResponse>()
                        }
                        body != null -> {
                            try {
                                val gson = com.google.gson.Gson()
                                val jsonString = gson.toJson(body)
                                gson.fromJson(
                                    jsonString,
                                    Array<TicketResponse>::class.java
                                ).toList()
                            } catch (e: Exception) {
                                println("❌ Error parseando tickets por reserva: ${e.message}")
                                emptyList()
                            }
                        }
                        else -> emptyList()
                    }

                    println("✅ [TICKET API REAL] Tickets por reserva: ${tickets.size}")
                    Result.success(tickets)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                    Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                println("❌ [TICKET API REAL] Exception: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Error de conexión: ${e.message}"))
            }
        }

    /**
     * Obtener tickets válidos del usuario (API REAL)
     * Usa el endpoint GET /tickets/validos/
     */
    suspend fun getValidTickets(): Result<List<TicketResponse>> = withContext(Dispatchers.IO) {
        try {
            println("📱 [TICKET API REAL] Obteniendo tickets válidos...")
            val response = apiService.getValidTickets()

            println("📡 [TICKET API REAL] Código respuesta: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                val tickets = when {
                    body is List<*> -> {
                        body.filterIsInstance<TicketResponse>()
                    }
                    body != null && body.toString().contains("\"results\"") -> {
                        try {
                            val gson = com.google.gson.Gson()
                            val jsonString = gson.toJson(body)
                            val paginatedResponse = gson.fromJson(
                                jsonString,
                                PaginatedTicketResponse::class.java
                            )
                            paginatedResponse.results ?: emptyList()
                        } catch (e: Exception) {
                            println("❌ Error parseando tickets válidos: ${e.message}")
                            emptyList()
                        }
                    }
                    else -> emptyList()
                }

                println("✅ [TICKET API REAL] Tickets válidos: ${tickets.size}")

                tickets.forEachIndexed { index, ticket ->
                    println("   [$index] ID: ${ticket.id}, Estado: ${ticket.estado}")
                }

                Result.success(tickets)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                println("❌ [TICKET API REAL] Error ${response.code()}: $errorBody")
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            println("❌ [TICKET API REAL] Exception: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

        }