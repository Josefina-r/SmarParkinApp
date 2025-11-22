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
import com.example.smarparkinapp.data.model.VehicleShort
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.data.repository.ReservationRepository
import com.example.smarparkinapp.data.repository.PaymentRepository
import com.example.smarparkinapp.data.repository.VehicleRepository
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

// ENUM para controlar la pantalla actual
enum class ReservationScreen {
    VEHICLE_SELECTION,
    RESERVATION_FORM,
    PAYMENT_METHOD,
    CONFIRMATION
}

class ReservationViewModel(
    private val context: Context
) : ViewModel() {

    // ✅ REPOSITORIOS CON DEPENDENCIAS CORRECTAS
    private val apiService = RetrofitInstance.apiService
    private val reservationRepository = ReservationRepository(context)
    private val paymentRepository = PaymentRepository(context)
    private val vehicleRepository = VehicleRepository(context, apiService)

    // ========== ESTADOS DE NAVEGACIÓN ==========
    var currentScreen by mutableStateOf(ReservationScreen.VEHICLE_SELECTION)
        private set

    // ✅ ESTADOS DE NAVEGACIÓN PARA NAVGRAPH - CORREGIDOS
    var shouldNavigateToVehicleSelectionFlag by mutableStateOf(false)
        private set

    var shouldReturnToReservationFlag by mutableStateOf(false)
        private set

    // ✅ HACER PÚBLICAS estas propiedades
    var selectedParking by mutableStateOf<ParkingLot?>(null)
    var selectedVehicle by mutableStateOf<Car?>(null)

    var showAddVehicleDialog by mutableStateOf(false)
        private set

    // ========== ESTADOS DE RESERVA ==========
    var reservationDate by mutableStateOf("")
        private set

    var reservationStartTime by mutableStateOf("")
        private set

    var reservationEndTime by mutableStateOf("")
        private set

    var reservationType by mutableStateOf("hora")
        private set

    // ========== ESTADOS DE PAGO ==========
    var selectedPaymentMethod by mutableStateOf<String?>(null)
        private set

    // ========== ESTADOS DE UI ==========
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createdReservation = MutableStateFlow<Reservation?>(null)
    val createdReservation: StateFlow<Reservation?> = _createdReservation.asStateFlow()

    private val _myReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val myReservations: StateFlow<List<Reservation>> = _myReservations.asStateFlow()

    // ========== ESTADOS DE VEHÍCULOS ==========
    private val _vehicles = MutableStateFlow<List<Car>>(emptyList())
    val vehicles: StateFlow<List<Car>> = _vehicles.asStateFlow()

    // ========== CAMPOS FORMULARIO VEHÍCULO ==========
    var vehicleBrand by mutableStateOf("")
    var vehicleModel by mutableStateOf("")
    var vehicleColor by mutableStateOf("")
    var vehiclePlate by mutableStateOf("")

    // ========== FUNCIONES DE NAVEGACIÓN PARA NAVGRAPH ==========
    fun navigateToVehicleSelection() {
        shouldNavigateToVehicleSelectionFlag = true
        println("🔍 [ReservationViewModel] Solicitando navegación a selección de vehículos")
    }

    fun setReturnToReservationFlag(value: Boolean) {
        shouldReturnToReservationFlag = value
        println("🔍 [ReservationViewModel] Set shouldReturnToReservationFlag: $value")
    }

    fun resetNavigationFlags() {
        shouldNavigateToVehicleSelectionFlag = false
        shouldReturnToReservationFlag = false
        println("🔍 [ReservationViewModel] Flags de navegación reiniciados")
    }

    // ✅ MÉTODO PARA SELECCIONAR VEHÍCULO DESDE VEHICLE SELECTION
    fun selectVehicle(vehicle: Car) {
        selectedVehicle = vehicle
        println("🚗 [ReservationViewModel] Vehículo seleccionado: ${vehicle.plate}")
    }

    // ========== FUNCIONES NUEVAS PARA MANEJAR ParkingSpot ==========
    fun startReservationFlowWithParkingSpot(parkingSpot: ParkingSpot) {
        println("🚗 [ReservationViewModel] Iniciando flujo con ParkingSpot: ${parkingSpot.name}")

        selectedParking = convertParkingSpotToParkingLot(parkingSpot)
        currentScreen = ReservationScreen.VEHICLE_SELECTION
        loadUserVehicles()

        println("✅ [ReservationViewModel] Flujo iniciado - Parking: ${selectedParking?.nombre}")
    }

    fun setSelectedVehicleFromOutside(vehicle: Car) {
        selectedVehicle = vehicle
        println("🚗 [ReservationViewModel] Vehículo establecido desde fuera: ${vehicle.plate}")
    }

    private fun convertParkingSpotToParkingLot(parkingSpot: ParkingSpot): ParkingLot {
        val coordenadas = "${parkingSpot.latitude},${parkingSpot.longitude}"
        val totalPlazas = parkingSpot.availableSpots + 5
        val plazasDisponibles = parkingSpot.availableSpots

        return ParkingLot(
            id = parkingSpot.id.toLong(),
            nombre = parkingSpot.name,
            direccion = parkingSpot.address,
            coordenadas = coordenadas,
            telefono = null,
            descripcion = "Estacionamiento ${parkingSpot.name} ubicado en ${parkingSpot.address}",
            horario_apertura = "07:00",
            horario_cierre = "23:00",
            nivel_seguridad = parkingSpot.nivelSeguridad.toString(),
            tarifa_hora = extractPriceFromString(parkingSpot.price),
            total_plazas = totalPlazas,
            plazas_disponibles = plazasDisponibles,
            rating_promedio = parkingSpot.ratingPromedio,
            total_resenas = parkingSpot.totalResenas,
            aprobado = true,
            activo = true,
            dueno = null,
            esta_abierto = parkingSpot.estaAbierto,
            imagen_principal = parkingSpot.imagenUrl,
            dueno_nombre = null
        )
    }

    private fun extractPriceFromString(priceString: String): Double {
        return try {
            val numericPart = priceString.replace(Regex("[^0-9.]"), "")
            numericPart.toDoubleOrNull() ?: 5.0
        } catch (e: Exception) {
            5.0
        }
    }

    // ========== FUNCIONES EXISTENTES ==========
    fun startReservationFlow(parking: ParkingLot) {
        selectedParking = parking
        currentScreen = ReservationScreen.VEHICLE_SELECTION
        loadUserVehicles()
    }

    fun navigateToReservationForm(vehicle: Car) {
        selectedVehicle = vehicle
        currentScreen = ReservationScreen.RESERVATION_FORM
        println("🔍 [ReservationViewModel] Navegando a formulario con vehículo: ${vehicle.plate}")
    }

    fun navigateToPaymentMethod() {
        currentScreen = ReservationScreen.PAYMENT_METHOD
        println("🔍 [ReservationViewModel] Navegando a método de pago")
    }

    fun navigateToConfirmation() {
        currentScreen = ReservationScreen.CONFIRMATION
        println("🔍 [ReservationViewModel] Navegando a confirmación")
    }

    fun resetReservationFlow() {
        selectedParking = null
        selectedVehicle = null
        selectedPaymentMethod = null
        reservationDate = ""
        reservationStartTime = ""
        reservationEndTime = ""
        currentScreen = ReservationScreen.VEHICLE_SELECTION
        _createdReservation.value = null
        resetNavigationFlags()
        println("🔍 [ReservationViewModel] Flujo de reserva reiniciado")
    }

    fun goBackToVehicleSelection() {
        currentScreen = ReservationScreen.VEHICLE_SELECTION
    }

    fun goBackToReservationForm() {
        currentScreen = ReservationScreen.RESERVATION_FORM
    }

    // ========== FUNCIONES VEHÍCULOS ==========
    fun loadUserVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = vehicleRepository.getUserVehicles()
                if (result.isSuccess) {
                    _vehicles.value = result.getOrNull() ?: emptyList()
                    _error.value = null
                    println("✅ [ReservationViewModel] ${_vehicles.value.size} vehículos cargados")
                } else {
                    _error.value = "Error al cargar vehículos: ${result.exceptionOrNull()?.message}"
                    _vehicles.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                _vehicles.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

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
                val result = vehicleRepository.createVehicle(
                    plate = vehiclePlate,
                    brand = vehicleBrand,
                    model = vehicleModel,
                    color = vehicleColor
                )

                if (result.isSuccess) {
                    val newVehicle = result.getOrNull()
                    if (newVehicle != null) {
                        loadUserVehicles()
                        hideAddVehicleForm()
                        _error.value = null
                        selectVehicle(newVehicle)
                        setReturnToReservationFlag(true)
                        println("✅ [ReservationViewModel] Nuevo vehículo guardado y seleccionado: ${newVehicle.plate}")
                    } else {
                        _error.value = "Error: No se pudo crear el vehículo"
                    }
                } else {
                    _error.value = "Error al guardar vehículo: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
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

    // ========== FUNCIONES RESERVA ==========
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

    fun validateReservationForm(): Boolean {
        return selectedVehicle != null &&
                reservationDate.isNotEmpty() &&
                reservationStartTime.isNotEmpty() &&
                reservationEndTime.isNotEmpty() &&
                selectedParking != null
    }

    fun createReservation() {
        if (!validateReservationForm()) {
            _error.value = "Complete todos los campos de la reserva"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val parking = selectedParking ?: throw Exception("Estacionamiento no seleccionado")
                val vehicle = selectedVehicle ?: throw Exception("Vehículo no seleccionado")

                println("🔍 [ReservationViewModel] Creando reserva:")
                println("   🅿️ Parking: ${parking.nombre} (ID: ${parking.id})")
                println("   🚗 Vehículo: ${vehicle.plate} (ID: ${vehicle.id})")
                println("   📅 Fecha: $reservationDate")
                println("   ⏰ Inicio: $reservationStartTime")
                println("   ⏰ Fin: $reservationEndTime")
                println("   📋 Tipo: $reservationType")

                val result = reservationRepository.createReservation(
                    parkingId = parking.id,
                    vehicleId = vehicle.id,
                    horaInicio = "$reservationDate $reservationStartTime:00",
                    horaFin = "$reservationDate $reservationEndTime:00",
                    tipo = reservationType
                )

                if (result.isSuccess) {
                    val reservationResponse = result.getOrNull()
                    val createdReservation = convertToReservation(
                        reservationResponse = reservationResponse,
                        parking = parking,
                        vehicle = vehicle,
                        reservationDate = reservationDate
                    )

                    _createdReservation.value = createdReservation
                    println("✅ [ReservationViewModel] Reserva creada exitosamente: ${createdReservation.codigo}")
                    navigateToPaymentMethod()
                    loadMyReservations() // ✅ AHORA ESTÁ DEFINIDA
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al crear reserva"
                    println("❌ [ReservationViewModel] Error creando reserva: $errorMsg")
                    _error.value = errorMsg
                }

            } catch (e: Exception) {
                val errorMsg = "Error al crear reserva: ${e.message}"
                println("❌ [ReservationViewModel] Exception: $errorMsg")
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ FUNCIÓN loadMyReservations AGREGADA
    fun loadMyReservations() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = reservationRepository.getMyReservations()
                if (result.isSuccess) {
                    val reservationsResponse = result.getOrNull() ?: emptyList()
                    val reservations = reservationsResponse.map { response ->
                        convertReservationResponseToReservation(response)
                    }
                    _myReservations.value = reservations
                    println("✅ [ReservationViewModel] ${reservations.size} reservas cargadas")
                } else {
                    val errorMsg = "Error al cargar reservas: ${result.exceptionOrNull()?.message}"
                    println("❌ [ReservationViewModel] $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                println("❌ [ReservationViewModel] Exception cargando reservas: $errorMsg")
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun convertToReservation(
        reservationResponse: Any?,
        parking: ParkingLot,
        vehicle: Car,
        reservationDate: String
    ): Reservation {
        return try {
            if (reservationResponse == null) {
                return createDefaultReservationWithParking(parking, vehicle, reservationDate)
            }

            val codigo = (getPropertySafely(reservationResponse, "codigo_reserva") as? String) ?: "RES${getPropertySafely(reservationResponse, "id") ?: System.currentTimeMillis()}"
            val tipo = (getPropertySafely(reservationResponse, "tipo") as? String) ?: reservationType
            val estado = (getPropertySafely(reservationResponse, "estado") as? String) ?: "pendiente"
            val precio = (getPropertySafely(reservationResponse, "costo_estimado") as? Double) ?: calculatePrice(
                parking.tarifa_hora,
                reservationStartTime,
                reservationEndTime
            )
            val createdAt = (getPropertySafely(reservationResponse, "created_at") as? String) ?: ""
            val updatedAt = (getPropertySafely(reservationResponse, "updated_at") as? String) ?: ""

            Reservation(
                id = (getPropertySafely(reservationResponse, "id") as? Int ?: 0).toLong(),
                codigo = codigo,
                parking = ParkingShort(
                    id = parking.id,
                    nombre = parking.nombre,
                    direccion = parking.direccion,
                    tarifaHora = parking.tarifa_hora
                ),
                vehicle = VehicleShort(
                    id = vehicle.id,
                    licensePlate = vehicle.plate,
                    brand = vehicle.brand,
                    model = vehicle.model
                ),
                fecha = reservationDate,
                horaInicio = "$reservationDate $reservationStartTime:00",
                horaFin = "$reservationDate $reservationEndTime:00",
                tipo = tipo,
                estado = estado,
                precio = precio,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        } catch (e: Exception) {
            createDefaultReservationWithParking(parking, vehicle, reservationDate)
        }
    }

    // Función auxiliar para acceder a propiedades de forma segura
    private fun getPropertySafely(obj: Any?, propertyName: String): Any? {
        return try {
            val field = obj?.javaClass?.getDeclaredField(propertyName)
            field?.isAccessible = true
            field?.get(obj)
        } catch (e: Exception) {
            null
        }
    }

    private fun createDefaultReservationWithParking(parking: ParkingLot, vehicle: Car, reservationDate: String): Reservation {
        return Reservation(
            id = 0,
            codigo = "RES${System.currentTimeMillis()}",
            parking = ParkingShort(
                id = parking.id,
                nombre = parking.nombre,
                direccion = parking.direccion,
                tarifaHora = parking.tarifa_hora
            ),
            vehicle = VehicleShort(
                id = vehicle.id,
                licensePlate = vehicle.plate,
                brand = vehicle.brand,
                model = vehicle.model
            ),
            fecha = reservationDate,
            horaInicio = "$reservationDate $reservationStartTime:00",
            horaFin = "$reservationDate $reservationEndTime:00",
            tipo = reservationType,
            estado = "pendiente",
            precio = calculatePrice(
                parking.tarifa_hora,
                reservationStartTime,
                reservationEndTime
            ),
            createdAt = "",
            updatedAt = ""
        )
    }

    private fun calculatePrice(tarifaHora: Double?, horaInicio: String, horaFin: String): Double {
        return try {
            val tarifa = tarifaHora ?: 5.0
            val inicioParts = horaInicio.split(":")
            val finParts = horaFin.split(":")
            val inicioHoras = inicioParts[0].toDouble() + inicioParts[1].toDouble() / 60
            val finHoras = finParts[0].toDouble() + finParts[1].toDouble() / 60
            val horas = finHoras - inicioHoras
            max(horas * tarifa, tarifa)
        } catch (e: Exception) {
            10.0
        }
    }

    // ========== FUNCIONES PAGO ==========
    fun selectPaymentMethod(method: String) {
        selectedPaymentMethod = method
        println("🔍 [ReservationViewModel] Método de pago seleccionado: $method")
    }

    fun processPayment() {
        val reservation = _createdReservation.value ?: run {
            _error.value = "No hay reserva para pagar"
            return
        }

        val paymentMethod = selectedPaymentMethod ?: run {
            _error.value = "Seleccione un método de pago"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("🔍 [ReservationViewModel] Procesando pago:")
                println("   💰 Reserva: ${reservation.codigo}")
                println("   💳 Método: $paymentMethod")
                println("   💵 Monto: ${reservation.precio}")

                val result = paymentRepository.processPayment(
                    reservationId = reservation.id,
                    amount = reservation.precio,
                    paymentMethod = paymentMethod
                )

                if (result.isSuccess) {
                    println("✅ [ReservationViewModel] Pago procesado exitosamente")
                    navigateToConfirmation()
                    _error.value = null
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error al procesar pago"
                    println("❌ [ReservationViewModel] Error en pago: $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error en pago: ${e.message}"
                println("❌ [ReservationViewModel] Exception en pago: $errorMsg")
                _error.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun convertReservationResponseToReservation(response: Any?): Reservation {
        return try {
            if (response == null) {
                return createDefaultReservation()
            }

            val codigo = (getPropertySafely(response, "codigo_reserva") as? String) ?: "RES${getPropertySafely(response, "id") ?: System.currentTimeMillis()}"
            val horaInicio = (getPropertySafely(response, "hora_inicio") as? String) ?: ""
            val horaFin = (getPropertySafely(response, "hora_fin") as? String) ?: ""
            val tipo = (getPropertySafely(response, "tipo") as? String) ?: "hora"
            val estado = (getPropertySafely(response, "estado") as? String) ?: "pendiente"
            val precio = (getPropertySafely(response, "costo_estimado") as? Double) ?: 0.0
            val createdAt = (getPropertySafely(response, "created_at") as? String) ?: ""
            val updatedAt = (getPropertySafely(response, "updated_at") as? String) ?: ""

            val fecha = try {
                horaInicio.substringBefore(" ")
            } catch (e: Exception) {
                ""
            }

            Reservation(
                id = (getPropertySafely(response, "id") as? Int ?: 0).toLong(),
                codigo = codigo,
                parking = null,
                vehicle = null,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                tipo = tipo,
                estado = estado,
                precio = precio,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        } catch (e: Exception) {
            createDefaultReservation()
        }
    }

    private fun createDefaultReservation(): Reservation {
        return Reservation(
            id = 0,
            codigo = "RES${System.currentTimeMillis()}",
            parking = null,
            vehicle = null,
            fecha = "",
            horaInicio = "",
            horaFin = "",
            tipo = "hora",
            estado = "pendiente",
            precio = 0.0,
            createdAt = "",
            updatedAt = ""
        )
    }

    fun cancelReservation(codigo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                println("🔍 [ReservationViewModel] Cancelando reserva: $codigo")
                val result = reservationRepository.cancelReservation(codigo)
                if (result.isSuccess) {
                    println("✅ [ReservationViewModel] Reserva cancelada exitosamente")
                    loadMyReservations()
                    _error.value = null
                } else {
                    val errorMsg = "Error al cancelar reserva: ${result.exceptionOrNull()?.message}"
                    println("❌ [ReservationViewModel] $errorMsg")
                    _error.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                println("❌ [ReservationViewModel] Exception cancelando reserva: $errorMsg")
                _error.value = errorMsg
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

    fun getReservationPrice(): Double {
        return selectedParking?.let { parking ->
            calculatePrice(parking.tarifa_hora, reservationStartTime, reservationEndTime)
        } ?: 0.0
    }

    private fun getModelosForBrand(brand: String): List<String> {
        return when (brand) {
            "Toyota" -> listOf("Corolla", "Camry", "RAV4", "Hilux", "Yaris", "Prius", "4Runner", "Highlander", "Tacoma", "Sienna", "Otro")
            "Honda" -> listOf("Civic", "Accord", "CR-V", "HR-V", "Pilot", "City", "Fit", "Odyssey", "Ridgeline", "Passport", "Otro")
            "Ford" -> listOf("F-150", "Focus", "Escape", "Explorer", "Mustang", "Ranger", "Fusion", "Edge", "Expedition", "Bronco", "Otro")
            "Chevrolet" -> listOf("Spark", "Aveo", "Cruze", "Malibu", "Trax", "Equinox", "Tracker", "Blazer", "Tahoe", "Silverado", "Otro")
            "Nissan" -> listOf("Sentra", "Versa", "Altima", "Kicks", "X-Trail", "Frontier", "Murano", "Pathfinder", "Rogue", "Maxima", "Otro")
            "Hyundai" -> listOf("Accent", "Elantra", "Tucson", "Santa Fe", "Creta", "i10", "Sonata", "Kona", "Palisade", "Venue", "Otro")
            "Kia" -> listOf("Rio", "Forte", "Seltos", "Sportage", "Sorento", "Picanto", "Cerato", "Carnival", "Stonic", "Niro", "Otro")
            "Volkswagen" -> listOf("Golf", "Jetta", "Tiguan", "Polo", "Virtus", "Taos", "Passat", "T-Cross", "Arteon", "Atlas", "Otro")
            "BMW" -> listOf("Serie 3", "Serie 5", "X1", "X3", "X5", "Serie 1", "X7", "Serie 7", "X2", "X4", "Otro")
            "Mercedes-Benz" -> listOf("Clase A", "Clase C", "Clase E", "GLA", "GLC", "GLE", "Clase S", "GLS", "CLA", "GLB", "Otro")
            "Audi" -> listOf("A3", "A4", "A6", "Q3", "Q5", "Q7", "A5", "Q8", "A7", "A8", "Otro")
            "Mazda" -> listOf("Mazda 2", "Mazda 3", "CX-3", "CX-5", "CX-9", "Mazda 6", "CX-30", "MX-5", "CX-50", "BT-50", "Otro")
            "Subaru" -> listOf("Impreza", "Legacy", "Forester", "Outback", "Crosstrek", "WRX", "Ascent", "BRZ", "Otro")
            "Lexus" -> listOf("ES", "RX", "NX", "IS", "UX", "LX", "LS", "GX", "RC", "LC", "Otro")
            "Volvo" -> listOf("S60", "S90", "XC40", "XC60", "XC90", "V60", "V90", "C40", "XC70", "Otro")
            "Mitsubishi" -> listOf("Lancer", "Outlander", "Eclipse Cross", "ASX", "Montero", "Mirage", "Pajero", "Triton", "Otro")
            "Jeep" -> listOf("Wrangler", "Grand Cherokee", "Cherokee", "Compass", "Renegade", "Gladiator", "Wagoneer", "Otro")
            "Renault" -> listOf("Duster", "Sandero", "Logan", "Kwid", "Captur", "Koleos", "Clio", "Megane", "Otro")
            "Peugeot" -> listOf("208", "308", "2008", "3008", "5008", "508", "Partner", "Rifter", "Otro")
            "Citroën" -> listOf("C3", "C4", "C5", "Berlingo", "C-Elysee", "C4 Cactus", "Otro")
            "Fiat" -> listOf("500", "Panda", "Tipo", "Punto", "Cronos", "Argo", "Mobi", "Otro")
            "Suzuki" -> listOf("Swift", "Vitara", "S-Cross", "Jimny", "Ciaz", "Ertiga", "Baleno", "Otro")
            "Isuzu" -> listOf("D-Max", "MU-X", "Otro")
            "Chrysler" -> listOf("300", "Pacifica", "Voyager", "Otro")
            "Dodge" -> listOf("Charger", "Challenger", "Durango", "Journey", "Otro")
            else -> listOf("Selecciona primero la marca")
        }
    }

    fun updateVehicleBrand(brand: String) {
        vehicleBrand = brand
        if (vehicleModel.isNotEmpty() && !getModelosForBrand(brand).contains(vehicleModel)) {
            vehicleModel = ""
        }
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

    // ========== INICIALIZACIÓN ==========
    init {
        loadMyReservations() // ✅ AHORA ESTÁ DEFINIDA
        loadUserVehicles()
        println("🔍 [ReservationViewModel] Inicializado")
    }
}