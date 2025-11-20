// ui/theme/viewmodel/ReservationViewModel.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.data.model.Car
import com.example.smarparkinapp.data.model.Reservation
import com.example.smarparkinapp.data.model.ParkingShort
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.data.model.VehicleType
import com.example.smarparkinapp.data.repository.ReservationRepository
import com.example.smarparkinapp.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class ReservationViewModel(private val context: Context) : ViewModel() {

    private val reservationRepository = ReservationRepository(context)
    private val paymentRepository = PaymentRepository(context)

    private val _vehicles = MutableStateFlow<List<Car>>(emptyList())
    val vehicles: StateFlow<List<Car>> = _vehicles.asStateFlow()

    var showAddVehicleDialog by mutableStateOf(false)
        private set

    var vehicleType by mutableStateOf(VehicleType.AUTOMOVIL)
        private set

    var vehicleBrand by mutableStateOf("")
        private set

    var vehicleModel by mutableStateOf("")
        private set

    var vehicleColor by mutableStateOf("")
        private set

    var vehiclePlate by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createdReservation = MutableStateFlow<Reservation?>(null)
    val createdReservation: StateFlow<Reservation?> = _createdReservation.asStateFlow()

    private val _myReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val myReservations: StateFlow<List<Reservation>> = _myReservations.asStateFlow()

    // ========== FUNCIONES VEHÍCULOS ==========
    fun showAddVehicleForm() {
        showAddVehicleDialog = true
    }

    fun hideAddVehicleForm() {
        showAddVehicleDialog = false
        clearVehicleForm()
    }

    fun updateVehicleType(type: VehicleType) {
        vehicleType = type
    }

    fun updateVehicleBrand(brand: String) {
        vehicleBrand = brand
    }

    fun updateVehicleModel(model: String) {
        vehicleModel = model
    }

    fun updateVehicleColor(color: String) {
        vehicleColor = color
    }

    fun updateVehiclePlate(plate: String) {
        vehiclePlate = plate
    }

    fun saveNewVehicle() {
        if (vehicleBrand.isEmpty() || vehicleModel.isEmpty() ||
            vehicleColor.isEmpty() || vehiclePlate.isEmpty()) {
            _error.value = "Todos los campos son obligatorios"
            return
        }

        val newCar = Car(
            id = generateVehicleId(),
            plate = vehiclePlate,
            model = vehicleModel,
            brand = vehicleBrand,
            color = vehicleColor,
            type = vehicleType
        )

        // Actualizar la lista de vehículos
        _vehicles.value = _vehicles.value + newCar
        hideAddVehicleForm()
        _error.value = null
    }

    private fun generateVehicleId(): Int {
        // CORREGIDO: Acceder a .value del StateFlow
        val currentVehicles = _vehicles.value
        return (currentVehicles.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun getVehicleById(vehicleId: Int): Car? {
        // CORREGIDO: Acceder a .value del StateFlow
        return _vehicles.value.find { it.id == vehicleId }
    }

    private fun clearVehicleForm() {
        vehicleType = VehicleType.AUTOMOVIL
        vehicleBrand = ""
        vehicleModel = ""
        vehicleColor = ""
        vehiclePlate = ""
    }

    // ========== FUNCIONES RESERVAS ==========
    fun createReservation(
        parking: ParkingLot,
        vehicleId: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        tipoReserva: String = "normal"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Validaciones
                if (vehicleId <= 0) {
                    _error.value = "ID de vehículo inválido"
                    _isLoading.value = false
                    return@launch
                }

                if (fecha.isEmpty() || horaInicio.isEmpty() || horaFin.isEmpty()) {
                    _error.value = "Fecha y horas son obligatorias"
                    _isLoading.value = false
                    return@launch
                }

                // Crear reserva en la API
                val result = reservationRepository.createReservation(
                    parkingId = parking.id,
                    vehicleId = vehicleId,
                    horaInicio = "$fecha $horaInicio:00",
                    horaFin = "$fecha $horaFin:00",
                    tipo = tipoReserva
                )

                if (result.isSuccess) {
                    val reservationResponse = result.getOrNull()

                    // Crear objeto Reservation local
                    val reservation = Reservation(
                        id = reservationResponse?.id ?: 0,
                        codigo = "RES${reservationResponse?.id}",
                        parking = ParkingShort(
                            id = parking.id,
                            nombre = parking.nombre,
                            direccion = parking.direccion,
                            tarifaHora = parking.tarifa_hora
                        ),
                        vehicle = null,
                        fecha = fecha,
                        horaInicio = horaInicio,
                        horaFin = horaFin,
                        tipo = tipoReserva,
                        estado = reservationResponse?.estado ?: "pendiente",
                        precio = reservationResponse?.total ?: calculatePrice(parking.tarifa_hora, horaInicio, horaFin),
                        createdAt = "",
                        updatedAt = ""
                    )
                    _createdReservation.value = reservation

                    // Recargar lista de reservas
                    loadMyReservations()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al crear reserva"
                }

            } catch (e: Exception) {
                _error.value = "Error al crear reserva: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculatePrice(tarifaHora: Double, horaInicio: String, horaFin: String): Double {
        try {
            val inicioParts = horaInicio.split(":")
            val finParts = horaFin.split(":")

            val inicioHoras = inicioParts[0].toDouble() + inicioParts[1].toDouble() / 60
            val finHoras = finParts[0].toDouble() + finParts[1].toDouble() / 60

            val horas = finHoras - inicioHoras
            return max(horas * tarifaHora, tarifaHora) // Mínimo 1 hora
        } catch (e: Exception) {
            return tarifaHora * 2.0 // Precio por defecto: 2 horas
        }
    }

    fun loadMyReservations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = reservationRepository.getMyReservations()
                if (result.isSuccess) {
                    val reservationsResponse = result.getOrNull() ?: emptyList()
                    val reservations = reservationsResponse.map { response ->
                        Reservation(
                            id = response.id,
                            codigo = "RES${response.id}",
                            parking = ParkingShort(
                                id = response.estacionamiento.id,
                                nombre = response.estacionamiento.nombre,
                                direccion = response.estacionamiento.direccion,
                                tarifaHora = null
                            ),
                            vehicle = null,
                            fecha = response.hora_entrada.split(" ")[0],
                            horaInicio = response.hora_entrada.split(" ")[1],
                            horaFin = response.hora_salida.split(" ")[1],
                            tipo = "normal",
                            estado = response.estado,
                            precio = response.total ?: 0.0,
                            createdAt = "",
                            updatedAt = ""
                        )
                    }
                    _myReservations.value = reservations
                } else {
                    _error.value = "Error al cargar reservas: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelReservation(codigo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = reservationRepository.cancelReservation(codigo)
                if (result.isSuccess) {
                    loadMyReservations()
                } else {
                    _error.value = "Error al cancelar reserva: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== FUNCIONES PAGOS ==========
    fun processPayment(reservationId: Long, amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = paymentRepository.processPayment(
                    reservationId = reservationId,
                    amount = amount,
                    paymentMethod = paymentMethod
                )

                if (result.isSuccess) {
                    _error.value = "Pago procesado exitosamente"
                    loadMyReservations()
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Error al procesar pago"
                }
            } catch (e: Exception) {
                _error.value = "Error en pago: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== UTILITARIOS ==========
    fun clearError() {
        _error.value = null
    }

    fun clearCreatedReservation() {
        _createdReservation.value = null
    }

    // ========== INICIALIZACIÓN ==========
    init {
        // Inicializar sin datos ficticios
        loadMyReservations()
    }
}