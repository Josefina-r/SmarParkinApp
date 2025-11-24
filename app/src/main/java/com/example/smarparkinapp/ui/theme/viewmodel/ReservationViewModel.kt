// ui/theme/viewmodel/ReservationViewModel.kt
package com.example.smarparkinapp.ui.theme.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.data.model.Car
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel : ViewModel() {

    // ========== ESTADOS DEL VIEWMODEL ==========
    var selectedParking by mutableStateOf<ParkingLot?>(null)
    // ELIMINADO: private set - ahora será público para poder asignarlo directamente

    var selectedVehicle by mutableStateOf<Car?>(null)
        private set

    // Estados de reserva
    var reservationDate by mutableStateOf("")
    var reservationStartTime by mutableStateOf("")
    var reservationEndTime by mutableStateOf("")
    var reservationType by mutableStateOf("hora") // "hora" o "dia"

    // Estados de pago
    var selectedPaymentMethod by mutableStateOf<String?>(null)
        private set

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createdReservation = MutableStateFlow<Map<String, Any>?>(null)
    val createdReservation: StateFlow<Map<String, Any>?> = _createdReservation.asStateFlow()

    private val _paymentStatus = MutableStateFlow<String?>(null)
    val paymentStatus: StateFlow<String?> = _paymentStatus.asStateFlow()

    // Lista de vehículos
    private val _vehicles = MutableStateFlow<List<Car>>(emptyList())
    val vehicles: StateFlow<List<Car>> = _vehicles.asStateFlow()

    // Métodos de pago disponibles - SIMPLIFICADO para Yape y Plin como en tu backend
    val availablePaymentMethods = listOf("Yape", "Plin")

    // ========== FUNCIONES PARA EL ADD VEHICLE DIALOG ==========
    var showAddVehicleDialog by mutableStateOf(false)
    var vehicleBrand by mutableStateOf("")
    var vehicleModel by mutableStateOf("")
    var vehicleColor by mutableStateOf("")
    var vehiclePlate by mutableStateOf("")

    // ========== FUNCIONES PRINCIPALES ==========

    // ELIMINADA: fun setSelectedParking(parking: ParkingLot) - ya no es necesaria
    // Ahora se puede asignar directamente: viewModel.selectedParking = parking

    fun selectVehicle(vehicle: Car) {
        selectedVehicle = vehicle
    }

    fun updateReservationDate(date: String) {
        reservationDate = date
    }

    fun updateReservationStartTime(time: String) {
        reservationStartTime = time
    }

    fun updateReservationEndTime(time: String) {
        reservationEndTime = time
    }

    fun updateReservationType(type: String) {
        reservationType = type
    }

    fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method
    }

    // ========== VALIDACIONES ==========

    fun validateReservationForm(): Boolean {
        return selectedVehicle != null &&
                reservationDate.isNotEmpty() &&
                reservationStartTime.isNotEmpty() &&
                reservationEndTime.isNotEmpty() &&
                reservationStartTime < reservationEndTime
    }

    // ========== CÁLCULO DE PRECIO ==========

    fun getReservationPrice(): Double {
        selectedParking?.let { parking ->
            val basePrice = parking.tarifa_hora ?: 5.0

            return when (reservationType) {
                "hora" -> {
                    val hours = calculateHoursBetween(reservationStartTime, reservationEndTime)
                    basePrice * hours
                }
                "dia" -> basePrice * 8
                else -> basePrice * 2
            }
        }
        return 0.0
    }

    private fun calculateHoursBetween(start: String, end: String): Double {
        return try {
            val startParts = start.split(":")
            val endParts = end.split(":")
            val startHour = startParts[0].toDouble() + startParts[1].toDouble() / 60.0
            val endHour = endParts[0].toDouble() + endParts[1].toDouble() / 60.0
            if (endHour > startHour) endHour - startHour else (24 - startHour) + endHour
        } catch (e: Exception) {
            2.0
        }
    }

    // ========== PROCESAMIENTO DE PAGO - ACTUALIZADO PARA TU BACKEND ==========

    fun processPayment() {
        if (selectedPaymentMethod == null) {
            _error.value = "Selecciona un método de pago"
            return
        }

        if (!validateReservationForm()) {
            _error.value = "Completa todos los campos de la reserva"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _paymentStatus.value = "Procesando pago..."

            try {
                // Simular procesamiento según tu backend Django
                kotlinx.coroutines.delay(2000)

                // Crear objeto de reserva simulado
                val reservation = createReservationObject()
                _createdReservation.value = reservation

                // Simular el estado de pago según tu backend
                when (selectedPaymentMethod?.lowercase()) {
                    "yape", "plin" -> {
                        // Para Yape/Plin, el pago queda pendiente
                        _paymentStatus.value = "Pago pendiente - Verificando..."

                        // Simular verificación periódica como en tu backend
                        kotlinx.coroutines.delay(3000)
                        _paymentStatus.value = "Pago confirmado ✅"
                    }
                    else -> {
                        _paymentStatus.value = "Pago procesado exitosamente ✅"
                    }
                }

            } catch (e: Exception) {
                _error.value = "Error al procesar el pago: ${e.message}"
                _paymentStatus.value = "Error en el pago"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createReservationObject(): Map<String, Any> {
        val totalPrice = getReservationPrice()

        // CORREGIDO: Crear el mapa explícitamente sin valores nulos problemáticos
        val reservationMap = mutableMapOf<String, Any>()

        reservationMap["id"] = System.currentTimeMillis()
        reservationMap["codigo"] = "RES${System.currentTimeMillis()}"

        // Información del parking - manejar valores nulos explícitamente
        val parkingInfo = mutableMapOf<String, Any?>()
        parkingInfo["id"] = selectedParking?.id
        parkingInfo["nombre"] = selectedParking?.nombre ?: ""
        parkingInfo["direccion"] = selectedParking?.direccion ?: ""
        parkingInfo["tarifa_hora"] = selectedParking?.tarifa_hora ?: 0.0

        // Convertir a Map<String, Any> eliminando valores nulos
        reservationMap["parking"] = parkingInfo.mapValues { (_, value) ->
            value ?: "" // Reemplazar null con string vacío o valor por defecto
        }

        // Información del vehículo - manejar valores nulos explícitamente
        val vehicleInfo = mutableMapOf<String, Any?>()
        vehicleInfo["id"] = selectedVehicle?.id
        vehicleInfo["plate"] = selectedVehicle?.plate ?: ""
        vehicleInfo["brand"] = selectedVehicle?.brand ?: ""
        vehicleInfo["model"] = selectedVehicle?.model ?: ""

        reservationMap["vehicle"] = vehicleInfo.mapValues { (_, value) ->
            value ?: "" // Reemplazar null con string vacío
        }

        // Información básica de la reserva
        reservationMap["fecha"] = reservationDate
        reservationMap["hora_inicio"] = "$reservationDate $reservationStartTime:00"
        reservationMap["hora_fin"] = "$reservationDate $reservationEndTime:00"
        reservationMap["tipo"] = reservationType
        reservationMap["estado"] = "pendiente"
        reservationMap["costo_estimado"] = totalPrice
        reservationMap["metodo_pago"] = selectedPaymentMethod ?: ""
        reservationMap["fecha_creacion"] = System.currentTimeMillis().toString()
        reservationMap["payment_status"] = "pending"

        return reservationMap
    }

    // ========== GESTIÓN DE VEHÍCULOS ==========

    fun loadUserVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simular carga de vehículos
                kotlinx.coroutines.delay(500)

                // Vehículos de ejemplo - CORREGIDO: usar solo campos existentes
                val sampleVehicles = listOf(
                    Car(
                        id = 1,
                        plate = "ABC123",
                        brand = "Toyota",
                        model = "Corolla",
                        color = "Blanco",
                        active = true
                    ),
                    Car(
                        id = 2,
                        plate = "XYZ789",
                        brand = "Honda",
                        model = "Civic",
                        color = "Negro",
                        active = true
                    )
                )

                _vehicles.value = sampleVehicles

            } catch (e: Exception) {
                _error.value = "Error al cargar vehículos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funciones para el diálogo de agregar vehículo
    fun showAddVehicleForm() {
        showAddVehicleDialog = true
    }

    fun hideAddVehicleForm() {
        showAddVehicleDialog = false
        clearVehicleForm()
    }

    fun saveNewVehicleAndNavigate() {
        if (vehicleBrand.isEmpty() || vehicleModel.isEmpty() ||
            vehicleColor.isEmpty() || vehiclePlate.isEmpty()) {
            _error.value = "Todos los campos son obligatorios"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simular guardado
                kotlinx.coroutines.delay(1000)

                val newVehicle = Car(
                    id = (_vehicles.value.maxByOrNull { it.id }?.id ?: 0) + 1,
                    plate = vehiclePlate,
                    brand = vehicleBrand,
                    model = vehicleModel,
                    color = vehicleColor,
                    active = true
                )

                _vehicles.value = _vehicles.value + newVehicle
                selectVehicle(newVehicle)
                hideAddVehicleForm()

            } catch (e: Exception) {
                _error.value = "Error al guardar vehículo"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun clearVehicleForm() {
        vehicleBrand = ""
        vehicleModel = ""
        vehicleColor = ""
        vehiclePlate = ""
    }

    // ========== FUNCIONES ESPECÍFICAS PARA PAGOS ==========

    fun simulatePaymentVerification() {
        viewModelScope.launch {
            _paymentStatus.value = "Verificando pago..."
            kotlinx.coroutines.delay(2000)
            _paymentStatus.value = "Pago verificado exitosamente ✅"
        }
    }

    fun simulatePaymentRefund() {
        viewModelScope.launch {
            _paymentStatus.value = "Procesando reembolso..."
            kotlinx.coroutines.delay(1500)
            _paymentStatus.value = "Reembolso completado ✅"
        }
    }

    // ========== UTILIDADES ==========

    fun clearError() {
        _error.value = null
    }

    fun clearPaymentStatus() {
        _paymentStatus.value = null
    }

    fun clearCreatedReservation() {
        _createdReservation.value = null
    }

    // ========== INICIALIZACIÓN ==========

    init {
        loadUserVehicles()
    }
}