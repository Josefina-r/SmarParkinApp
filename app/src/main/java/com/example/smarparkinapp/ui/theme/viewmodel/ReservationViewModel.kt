package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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

    private val _userReservations = MutableStateFlow<List<ReservationResponse>>(emptyList())
    val userReservations: StateFlow<List<ReservationResponse>> = _userReservations.asStateFlow()

    // ================== SETTERS ==================
    fun setSelectedVehicle(vehicle: Car) {
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
                        _error.value = "Error desconocido"
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
    fun createReservation(onSuccess: (ReservationResponse) -> Unit = {}) {
        val parkingId = _selectedParking?.id ?: return
        val vehicleId = _selectedVehicle?.id ?: return

        val start = "$_reservationDate ${_reservationStartTime}:00"
        val end = "$_reservationDate ${_reservationEndTime}:00"
        val durationMinutes = calculateDurationMinutes()

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ReservationRequest(
                    estacionamientoId = parkingId,
                    vehiculoId = vehicleId,
                    horaEntrada = start,
                    horaSalida = end,
                    tipoReserva = _reservationType,
                    duracionMinutos = durationMinutes
                )

                reservationRepository.createReservation(request).onSuccess { reservation ->
                    _createdReservation.value = reservation
                    onSuccess(reservation)
                }.onFailure {
                    _error.value = "Error creando reserva"
                }
            } catch (e: Exception) {
                _error.value = "Error creando reserva: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================== PAGO ==================
    fun createPayment(metodo: String, onSuccess: (Payment) -> Unit = {}) {
        val reservation = _createdReservation.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                reservationRepository.createPayment(reservation.id, metodo)
                    .onSuccess { payment ->
                        _createdPayment.value = payment
                        onSuccess(payment)
                    }.onFailure {
                        _error.value = "Error creando pago"
                    }
            } catch (e: Exception) {
                _error.value = "Error creando pago: ${e.message}"
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

    // ================== UTILIDAD ==================
    private fun calculateDurationMinutes(): Int {
        return try {
            val startDateTime = "$_reservationDate $_reservationStartTime:00"
            val endDateTime = "$_reservationDate $_reservationEndTime:00"
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

    fun clearError() {
        _error.value = null
    }

    fun clearData() {
        _createdReservation.value = null
        _createdPayment.value = null
    }
}