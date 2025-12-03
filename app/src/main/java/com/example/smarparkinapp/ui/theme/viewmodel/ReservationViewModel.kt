package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.smarparkinapp.ui.theme.data.model.TicketResponse
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.repository.ReservationRepository
import com.example.smarparkinapp.ui.theme.data.repository.VehicleRepository
import com.example.smarparkinapp.ui.theme.data.repository.ParkingRepository
import com.example.smarparkinapp.ui.theme.data.model.Car
import com.example.smarparkinapp.ui.theme.data.model.ReservationResponse
import com.example.smarparkinapp.ui.theme.data.model.Payment
import com.example.smarparkinapp.ui.theme.data.model.ReservationRequest

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val reservationRepository: ReservationRepository,
    private val vehicleRepository: VehicleRepository,
    private val parkingRepository: ParkingRepository
) : ViewModel() {

    // ================== ESTADOS UI ==================
    private var _selectedParking by mutableStateOf<ParkingLot?>(null)
    val selectedParking: ParkingLot? get() = _selectedParking

    private var _selectedVehicle by mutableStateOf<Car?>(null)
    val selectedVehicle: Car? get() = _selectedVehicle

    private var _reservationDate by mutableStateOf("")
    val reservationDate: String get() = _reservationDate

    private var _reservationStartTime by mutableStateOf("")
    val reservationStartTime: String get() = _reservationStartTime

    private var _reservationEndTime by mutableStateOf("")
    val reservationEndTime: String get() = _reservationEndTime

    private var _reservationTime by mutableStateOf("")
    val reservationTime: String get() = _reservationTime

    private var _reservationType by mutableStateOf("hora")
    val reservationType: String get() = _reservationType

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _vehicles = MutableStateFlow<List<Car>>(emptyList())
    val vehicles: StateFlow<List<Car>> = _vehicles.asStateFlow()

    private val _createdReservation = MutableStateFlow<ReservationResponse?>(null)
    val createdReservation: StateFlow<ReservationResponse?> = _createdReservation.asStateFlow()

    private val _createdPayment = MutableStateFlow<Payment?>(null)
    val createdPayment: StateFlow<Payment?> = _createdPayment.asStateFlow()

    private val _userTickets = MutableStateFlow<List<TicketResponse>>(emptyList())
    val userTickets: StateFlow<List<TicketResponse>> = _userTickets.asStateFlow()

    private val _currentTicket = MutableStateFlow<TicketResponse?>(null)
    val currentTicket: StateFlow<TicketResponse?> = _currentTicket.asStateFlow()
    fun updateReservationTime(time: String) {
        _reservationTime = time
    }

    private val _userReservations = MutableStateFlow<List<ReservationResponse>>(emptyList())
    val userReservations: StateFlow<List<ReservationResponse>> = _userReservations.asStateFlow()

    // ================== SETTERS ==================
    fun setSelectedVehicle(vehicle: Car) {
        println(" [ReservationViewModel] setSelectedVehicle: ${vehicle.plate}")
        println(" [ReservationViewModel] Instancia: ${this.hashCode()}")
        _selectedVehicle = vehicle
    }

    fun setReservationDate(date: String) {
        _reservationDate = date
    }

    fun setReservationStartTime(time: String) {
        _reservationStartTime = time
    }

    fun setReservationEndTime(time: String) {
        _reservationEndTime = time
    }

    fun setReservationType(type: String) {
        _reservationType = type
        // Limpiar tiempos cuando se cambia el tipo
        if (type == "dia") {
            _reservationStartTime = ""
            _reservationEndTime = ""
        } else {
            _reservationTime = ""
        }
    }

    fun setSelectedParking(parking: ParkingLot) {
        _selectedParking = parking
        println(" [ReservationViewModel] Parking establecido: ${parking.nombre} - Precio: S/ ${parking.tarifa_hora}")
    }

    // ================== CARGAR DATOS ==================
    fun loadParkingById(parkingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = parkingRepository.getParkingById(parkingId)) {
                    is com.example.smarparkinapp.ui.theme.data.repository.Result.Success -> {
                        _selectedParking = result.data
                        println(" [ReservationViewModel] Parking cargado: ${result.data.nombre} - Precio: S/ ${result.data.tarifa_hora}")
                    }
                    is com.example.smarparkinapp.ui.theme.data.repository.Result.Error -> {
                        _error.value = "Error cargando estacionamiento: ${result.message}"
                        println(" [ReservationViewModel] Error: ${result.message}")
                    }
                    else -> {
                        _error.value = "Estado desconocido"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                vehicleRepository.getUserVehicles().onSuccess {
                    _vehicles.value = it
                }.onFailure {
                    _error.value = "Error cargando vehículos"
                }
            } catch (e: Exception) {
                _error.value = "Error cargando vehículos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================== GESTIÓN DE VEHÍCULOS ==================
    fun addVehicle(
        plate: String,
        brand: String,
        model: String,
        color: String,
        onSuccess: (Car) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                vehicleRepository.createVehicle(plate, brand, model, color).onSuccess { car ->
                    println(" [ReservationViewModel] Vehículo agregado exitosamente: ${car.plate}")
                    loadUserVehicles()
                    onSuccess(car)
                }.onFailure { exception ->
                    println("❌ [ReservationViewModel] Error agregando vehículo: ${exception.message}")
                    _error.value = exception.message
                    onError(exception.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                println(" [ReservationViewModel] Exception agregando vehículo: ${e.message}")
                _error.value = e.message
                onError(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun deleteVehicle(vehicleId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                vehicleRepository.deleteVehicle(vehicleId).onSuccess {
                    println("✅ [ReservationViewModel] Vehículo eliminado exitosamente: $vehicleId")

                    loadUserVehicles()

                    if (_selectedVehicle?.id == vehicleId) {
                        println("🔄 [ReservationViewModel] Vehículo seleccionado eliminado, limpiando selección")
                        _selectedVehicle = null
                    }
                }.onFailure { exception ->
                    println("❌ [ReservationViewModel] Error eliminando vehículo: ${exception.message}")
                    _error.value = "Error al eliminar el vehículo: ${exception.message}"
                }
            } catch (e: Exception) {
                println("❌ [ReservationViewModel] Exception eliminando vehículo: ${e.message}")
                _error.value = "Error al eliminar el vehículo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================== CREAR RESERVA ==================
    fun createReservation(onSuccess: (ReservationResponse) -> Unit = {}) {
        val parkingId = _selectedParking?.id ?: return
        val vehicleId = _selectedVehicle?.id ?: return

        // DEPURACIÓN
        println("🔍 DEBUG - Creando reserva:")
        println("   - Parking: $parkingId (${_selectedParking?.nombre})")
        println("   - Vehículo: $vehicleId (${_selectedVehicle?.plate})")

        val (start, end) = if (_reservationType == "hora") {
            "$_reservationDate ${_reservationStartTime}:00" to "$_reservationDate ${_reservationEndTime}:00"
        } else {
            "$_reservationDate ${_reservationTime}:00" to "$_reservationDate 23:59:00"
        }

        val durationMinutes = calculateDurationMinutes()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ReservationRequest(
                    estacionamiento = parkingId,
                    vehiculo = vehicleId,
                    horaEntrada = start,
                    horaSalida = end,
                    tipoReserva = _reservationType,
                    duracionMinutos = durationMinutes
                )

                println(" Enviando reserva REAL: $request")

                reservationRepository.createReservation(request).onSuccess { reservation ->
                    println(" Reserva creada exitosamente: ${reservation.id}")
                    _createdReservation.value = reservation
                    onSuccess(reservation)
                }.onFailure { error ->
                    println("❌ Error creando reserva: ${error.message}")
                    _error.value = "Error creando reserva: ${error.message}"
                }
            } catch (e: Exception) {
                println("❌ Exception creando reserva: ${e.message}")
                _error.value = "Error creando reserva: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================== PAGO ==================
    fun createPayment(metodo: String, onSuccess: (Payment) -> Unit = {}, onError: (String) -> Unit = {}) {
        val reservation = _createdReservation.value
        if (reservation == null) {
            val errorMsg = "❌ [ReservationViewModel] No hay reserva creada para procesar pago"
            println(errorMsg)
            onError(errorMsg)
            return
        }


        val montoReal = try {

            reservation.costoEstimado?.toString()?.toDoubleOrNull()
        } catch (e: Exception) {
            println("⚠️ [ReservationViewModel] Error convirtiendo monto: ${e.message}")
            null
        }

        if (montoReal == null) {
            val errorMsg = "❌ [ReservationViewModel] No se pudo obtener el monto. Valor: '${reservation.costoEstimado}' (Tipo: ${reservation.costoEstimado?.javaClass?.simpleName})"
            println(errorMsg)
            onError(errorMsg)
            return
        }

        println(" [ReservationViewModel] Creando pago - Reserva: ${reservation.id}, Método: $metodo, Monto: $montoReal")

        viewModelScope.launch {
            _isLoading.value = true
            try {
                reservationRepository.createPayment(reservation.id, metodo, montoReal)
                    .onSuccess { payment ->
                        println(" [ReservationViewModel] Pago creado exitosamente: ${payment.id}")
                        _createdPayment.value = payment
                        onSuccess(payment)
                    }.onFailure { exception ->
                        val errorMsg = "❌ [ReservationViewModel] Error creando pago: ${exception.message}"
                        println(errorMsg)
                        onError(errorMsg)
                    }
            } catch (e: Exception) {
                val errorMsg = "💥 [ReservationViewModel] Exception creando pago: ${e.message}"
                println(errorMsg)
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================== OBTENER RESERVAS ==================
    fun loadUserReservations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reservationRepository.getMyReservations().onSuccess {
                    _userReservations.value = it
                }.onFailure {
                    _error.value = "Error cargando reservas"
                }
            } catch (e: Exception) {
                _error.value = "Error cargando reservas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================== LIMPIAR DATOS ==================
    fun clearFormData() {
        println(" [ReservationViewModel] Limpiando datos del formulario...")
        _reservationDate = ""
        _reservationStartTime = ""
        _reservationEndTime = ""
        _reservationTime = ""
        _reservationType = "hora"
        _createdReservation.value = null
        _createdPayment.value = null
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun clearData() {
        _createdReservation.value = null
        _createdPayment.value = null
        _reservationDate = ""
        _reservationStartTime = ""
        _reservationEndTime = ""
        _reservationTime = ""
        _reservationType = "hora"
    }

    // ================== CÁLCULOS ==================
    private fun calculateDurationMinutes(): Int {
        return try {
            val (startDateTime, endDateTime) = if (_reservationType == "hora") {
                "$_reservationDate ${_reservationStartTime}:00" to "$_reservationDate ${_reservationEndTime}:00"
            } else {
                // Para reserva por día, calcular desde la hora de reserva hasta fin de día
                "$_reservationDate ${_reservationTime}:00" to "$_reservationDate 23:59:00"
            }

            val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            val start = format.parse(startDateTime)
            val end = format.parse(endDateTime)
            if (start != null && end != null) {
                ((end.time - start.time) / (1000 * 60)).toInt()
            } else {
                60
            }
        } catch (e: Exception) {
            60
        }
    }

    // ================== VALIDACIONES ==================
    fun hasSelectedVehicle(): Boolean {
        return _selectedVehicle != null
    }

    fun hasSelectedParking(): Boolean {
        return _selectedParking != null
    }

    fun isReservationDataComplete(): Boolean {
        return when (_reservationType) {
            "hora" -> _reservationDate.isNotEmpty() && _reservationStartTime.isNotEmpty() && _reservationEndTime.isNotEmpty()
            "dia" -> _reservationDate.isNotEmpty() && _reservationTime.isNotEmpty()
            else -> false
        }
    }

    // ================== MÉTODOS PARA TICKETS REALES ==================

    /**
     * Cargar tickets del usuario (API REAL)
     */
    fun loadUserTickets() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reservationRepository.getUserTickets().onSuccess { tickets ->
                    _userTickets.value = tickets
                    println("✅ [Ticket] Tickets cargados: ${tickets.size}")

                    // Mostrar cada ticket
                    tickets.forEachIndexed { index, ticket ->
                        println("   [$index] Ticket: ${ticket.codigoTicket}, Estado: ${ticket.estado}")
                    }
                }.onFailure { error ->
                    println("❌ [Ticket] Error cargando tickets: ${error.message}")
                    _error.value = "Error cargando tickets"
                }
            } catch (e: Exception) {
                println("❌ [Ticket] Exception: ${e.message}")
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar ticket específico por ID
     */
    fun loadTicketById(ticketId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reservationRepository.getTicketById(ticketId).onSuccess { ticket ->
                    _currentTicket.value = ticket
                    println("✅ [Ticket] Ticket cargado: ${ticket.codigoTicket}")

                    // Cargar detalles de la reserva asociada
                    // CAMBIA ESTO:
                    // val reservaId = ticket.reserva?.id

                    // POR ESTO:
                    val reservaId = ticket.reservaId

                    if (reservaId != null) {
                        loadReservationDetailsForTicket(reservaId.toLong())
                    } else {
                        println("⚠️ [Ticket] Ticket no tiene ID de reserva asociado")

                        // También puedes intentar obtenerlo del código de reserva
                        if (ticket.codigoReserva != null) {
                            println("⚠️ [Ticket] Pero tiene código de reserva: ${ticket.codigoReserva}")
                            // Podrías buscar la reserva por código aquí
                        }
                    }
                }.onFailure { error ->
                    println("❌ [Ticket] Error cargando ticket: ${error.message}")
                    _error.value = "Error cargando ticket"
                }
            } catch (e: Exception) {
                println("❌ [Ticket] Exception: ${e.message}")
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cancelar ticket (usuario cancela su reserva)
     */
    fun cancelUserTicket(ticketId: String, motivo: String = "Cancelado por usuario") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reservationRepository.cancelTicket(ticketId, motivo).onSuccess { ticket ->
                    println("✅ [Ticket] Ticket cancelado: ${ticket.estado}")

                    // Actualizar ticket actual
                    _currentTicket.value = ticket

                    // Refrescar lista
                    loadUserTickets()

                }.onFailure { error ->
                    println("❌ [Ticket] Error cancelando: ${error.message}")
                    _error.value = "Error cancelando ticket"
                }
            } catch (e: Exception) {
                println("❌ [Ticket] Exception: ${e.message}")
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Buscar ticket por ID de reserva
     */
    fun findTicketByReservationId(reservationId: Long): TicketResponse? {
        return _userTickets.value.find {
            it.reservaId?.toLong() == reservationId
        }
    }

    /**
     * Cargar detalles de reserva para un ticket
     */
    private fun loadReservationDetailsForTicket(reservationId: Long) {
        viewModelScope.launch {
            try {
                // Buscar en las reservas existentes
                val reservation = _userReservations.value.find { it.id == reservationId }
                if (reservation != null) {
                    _createdReservation.value = reservation
                    println("✅ [Ticket] Reserva encontrada para ticket")

                    // Cargar detalles del parking y vehículo (USANDO TUS FUNCIONES EXISTENTES)
                    if (reservation.estacionamientoId != 0L) {
                        // CORRECCIÓN: Quitar "viewModel." - estamos dentro del ViewModel
                        loadParkingDetailsForTicket(reservation.estacionamientoId)
                    }
                    if (reservation.vehiculoId != 0L) {
                        // CORRECCIÓN: Quitar "viewModel." - estamos dentro del ViewModel
                        loadVehicleDetailsForTicket(reservation.vehiculoId)
                    }
                } else {
                    println("⚠️ [Ticket] Reserva no encontrada para ticket")
                }
            } catch (e: Exception) {
                println("⚠️ [Ticket] Error cargando reserva: ${e.message}")
            }
        }
    }

    /**
     * Función para verificar estado del ticket después del pago
     */
    fun checkTicketStatusAfterPayment(reservationId: Long) {
        viewModelScope.launch {
            println("💰 [Ticket] Verificando estado del ticket después del pago...")

            // Esperar y refrescar
            kotlinx.coroutines.delay(3000L)
            loadUserTickets()
            loadUserReservations()

            // Buscar ticket asociado
            val ticket = findTicketByReservationId(reservationId)

            if (ticket != null) {
                println("✅ [Ticket] Ticket encontrado después del pago:")
                println("   - Estado: ${ticket.estado}")

                // Mostrar mensaje según estado
                when (ticket.estado) {
                    "pendiente" -> {
                        _error.value = "✅ Pago exitoso! Esperando confirmación del estacionamiento"
                    }
                    "valido" -> {
                        _error.value = "🎉 Ticket confirmado! Ya puedes usar tu reserva"
                    }
                    "cancelado" -> {
                        _error.value = "❌ Ticket cancelado"
                    }
                }

                // Cargar detalles del ticket
                loadTicketById(ticket.id)
            } else {
                println("⏳ [Ticket] Aún no hay ticket. El owner debe aceptar la reserva")
                _error.value = "⏳ Esperando que el estacionamiento confirme tu reserva"
            }
        }
    }

// ================== FUNCIONES AUXILIARES ==================

    fun getTicketStatusColor(estado: String): androidx.compose.ui.graphics.Color {
        return when (estado.lowercase()) {
            "valido", "validado" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            "pendiente" -> androidx.compose.ui.graphics.Color(0xFFFFC107)
            "cancelado" -> androidx.compose.ui.graphics.Color(0xFFF44336)
            else -> androidx.compose.ui.graphics.Color.Gray
        }
    }

    fun getTicketStatusText(estado: String): String {
        return when (estado.lowercase()) {
            "valido", "validado" -> "Confirmado"
            "pendiente" -> "Pendiente"
            "cancelado" -> "Cancelado"
            else -> estado
        }
    }

    fun canUserCancelTicket(ticket: TicketResponse): Boolean {
        return ticket.estado == "pendiente"
    }

// ================== FUNCIONES EXISTENTES QUE DEBES TENER ==================

    /**
     * Cargar detalles del estacionamiento para el ticket
     */
    fun loadParkingDetailsForTicket(parkingId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                when (val result = parkingRepository.getParkingById(parkingId)) {
                    is com.example.smarparkinapp.ui.theme.data.repository.Result.Success -> {
                        _selectedParking = result.data
                        println("✅ [Ticket] Detalles del estacionamiento cargados: ${result.data.nombre}")
                    }
                    is com.example.smarparkinapp.ui.theme.data.repository.Result.Error -> {
                        _error.value = "Error cargando estacionamiento: ${result.message}"
                        println("❌ [Ticket] Error cargando estacionamiento: ${result.message}")
                    }
                    else -> {
                        _error.value = "Estado desconocido al cargar estacionamiento"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error cargando estacionamiento: ${e.message}"
                println("❌ [Ticket] Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cargar detalles del vehículo para el ticket
     */
    fun loadVehicleDetailsForTicket(vehicleId: Long) {
        viewModelScope.launch {
            try {
                vehicleRepository.getUserVehicles().onSuccess { vehicles ->
                    val vehicle = vehicles.find { it.id.toLong() == vehicleId }
                    if (vehicle != null) {
                        _selectedVehicle = vehicle
                        println("✅ [Ticket] Detalles del vehículo cargados: ${vehicle.plate}")
                    } else {
                        println("❌ [Ticket] Vehículo no encontrado: $vehicleId")
                    }
                }.onFailure { error ->
                    println("❌ [Ticket] Error cargando vehículos: ${error.message}")
                }
            } catch (e: Exception) {
                println("❌ [Ticket] Exception cargando vehículo: ${e.message}")
            }
        }
    }

    /**
     * Cargar todos los detalles para el ticket
     */
    fun loadTicketDetails(reservation: ReservationResponse) {
        println("🎫 [Ticket] Cargando detalles para ticket...")
        println("   - Estacionamiento ID: ${reservation.estacionamientoId}")
        println("   - Vehículo ID: ${reservation.vehiculoId}")

        // Cargar detalles del estacionamiento
        if (reservation.estacionamientoId != 0L) {
            loadParkingDetailsForTicket(reservation.estacionamientoId)
        }

        // Cargar detalles del vehículo
        if (reservation.vehiculoId != 0L) {
            loadVehicleDetailsForTicket(reservation.vehiculoId)
        }
    }
}