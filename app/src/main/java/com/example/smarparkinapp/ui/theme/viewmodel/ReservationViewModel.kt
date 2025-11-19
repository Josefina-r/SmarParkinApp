package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.data.model.Car
import com.example.smarparkinapp.data.model.Reservation  // ✅ Debe apuntar a data.model
import com.example.smarparkinapp.data.model.VehicleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel : ViewModel() {

    // ========== ESTADOS PARA VEHÍCULOS ==========
    var vehicles by mutableStateOf<List<Car>>(emptyList())
        private set

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

    // ========== ESTADOS PARA RESERVAS (USANDO STATE FLOW) ==========
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createdReservation = MutableStateFlow<Reservation?>(null)
    val createdReservation: StateFlow<Reservation?> = _createdReservation.asStateFlow()

    // ========== FUNCIONES PARA VEHÍCULOS ==========
    fun showAddVehicleForm() {
        showAddVehicleDialog = true
    }

    fun hideAddVehicleForm() {
        showAddVehicleDialog = false
        clearForm()
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
        if (vehicleBrand.isEmpty() || vehicleModel.isEmpty() || vehicleColor.isEmpty() || vehiclePlate.isEmpty()) {
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

        vehicles = vehicles + newCar
        hideAddVehicleForm()
        _error.value = null
    }

    private fun generateVehicleId(): Int {
        return (vehicles.maxOfOrNull { it.id } ?: 0) + 1
    }

    fun deleteVehicle(vehicleId: Int) {
        vehicles = vehicles.filter { it.id != vehicleId }
    }

    fun getVehicleById(vehicleId: Int): Car? {
        return vehicles.find { it.id == vehicleId }
    }

    private fun clearForm() {
        vehicleType = VehicleType.AUTOMOVIL
        vehicleBrand = ""
        vehicleModel = ""
        vehicleColor = ""
        vehiclePlate = ""
    }

    // ========== FUNCIONES PARA RESERVAS ==========
    fun createReservation(
        parkingId: Int,
        carId: Int,
        horaInicio: String,
        horaFin: String,
        tipoReserva: String = "normal",
        estado: String = "pendiente",
        montoTotal: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Validaciones básicas
                if (carId <= 0) {
                    _error.value = "ID de vehículo inválido"
                    _isLoading.value = false
                    return@launch
                }

                if (horaInicio.isEmpty() || horaFin.isEmpty()) {
                    _error.value = "Las horas de inicio y fin son obligatorias"
                    _isLoading.value = false
                    return@launch
                }

                // Buscar el vehículo seleccionado
                val selectedVehicle = vehicles.find { it.id == carId }
                if (selectedVehicle == null) {
                    _error.value = "Vehículo no encontrado"
                    _isLoading.value = false
                    return@launch
                }

                // TODO: Aquí va la llamada REAL a tu API Django
                // Por ahora simulamos una reserva exitosa
                val nuevaReserva = Reservation(
                    id = (1..1000).random(),
                    codigo = "RES${System.currentTimeMillis()}",
                    car = selectedVehicle,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    tipo = tipoReserva,
                    estado = estado,
                    precio = montoTotal ?: calcularPrecio(tipoReserva)
                )

                _createdReservation.value = nuevaReserva

            } catch (e: Exception) {
                _error.value = "Error al crear reserva: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calcularPrecio(tipo: String): Double {
        return when (tipo) {
            "premium" -> 15.0
            "vip" -> 25.0
            else -> 8.0 // normal
        }
    }

    // ========== FUNCIONES UTILITARIAS ==========
    fun clearError() {
        _error.value = null
    }

    fun clearCreatedReservation() {
        _createdReservation.value = null
    }

    fun resetForm() {
        clearForm()
        clearError()
        clearCreatedReservation()
    }

    // ========== INICIALIZACIÓN VACÍA ==========
    init {
        // Listas vacías - se llenarán con datos reales de tu API
        vehicles = emptyList()
        // Añadimos un vehículo de ejemplo para demostración
        vehicles = listOf(
            Car(
                id = 1,
                plate = "ABC-123",
                model = "Corolla",
                brand = "Toyota",
                color = "Plata",
                type = VehicleType.AUTOMOVIL)
        )
    }
}