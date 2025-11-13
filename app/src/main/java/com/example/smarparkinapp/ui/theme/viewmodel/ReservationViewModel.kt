package com.example.smarparkinapp.ui.theme.viewmodel
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.model.Car
import com.example.smarparkinapp.ui.theme.data.model.*
import com.example.smarparkinapp.ui.theme.data.repository.ReservationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

data class ReservationUiState(
    val cars: List<Car> = emptyList(),
    val selectedCarId: Int? = null,
    val horaEntrada: ZonedDateTime? = null,
    val durationMinutes: Int = 60,
    val loading: Boolean = false,
    val error: String? = null,
    val createdReservation: ReservationResponse? = null
)

class ReservationViewModel(
    private val repo: ReservationRepository,
    private val parkingId: Int,
    private val pricePerHour: Double
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    init {
        loadCars()
        // default horaEntrada to now + 10 minutes or next slot
        val defaultStart = ZonedDateTime.now().plusMinutes(10)
        _uiState.update { it.copy(horaEntrada = defaultStart) }
    }

    fun loadCars() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val cars = repo.getUserCars()
                _uiState.update {
                    it.copy(
                        cars = cars ?: emptyList(),  // Si cars es null, usar lista vacía
                        selectedCarId = cars?.firstOrNull()?.id,
                        loading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = e.localizedMessage ?: "Error cargando vehículos",
                        cars = emptyList()  // Asegurar lista vacía en caso de error
                    )
                }
            }
        }
    }

    fun selectCar(carId: Int) {
        _uiState.update { it.copy(selectedCarId = carId) }
    }

    fun setHoraEntrada(zdt: ZonedDateTime) {
        _uiState.update { it.copy(horaEntrada = zdt) }
    }

    fun setDurationHours(hours: Int) {
        val minutes = hours * 60
        _uiState.update { it.copy(durationMinutes = minutes) }
    }

    fun setDurationMinutes(minutes: Int) {
        _uiState.update { it.copy(durationMinutes = minutes) }
    }

    fun estimatedCost(): Double {
        return (uiState.value.durationMinutes.toDouble() / 60.0) * pricePerHour
    }

    fun createReservation() {
        val state = _uiState.value
        val selectedCar = state.selectedCarId
        val horaEntrada = state.horaEntrada ?: run {
            _uiState.update { it.copy(error = "Selecciona fecha y hora") }
            return
        }
        if (selectedCar == null) {
            _uiState.update { it.copy(error = "Selecciona un vehículo") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            try {
                val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME // produce 2025-11-08T12:00:00+00:00
                val horaEntradaIso = horaEntrada.withZoneSameInstant(ZoneOffset.UTC).format(formatter)
                val payload = ReservationRequest(
                    vehiculo = selectedCar,
                    estacionamiento = parkingId,
                    hora_entrada = horaEntradaIso,
                    duracion_minutos = state.durationMinutes
                )
                val created = repo.createReservation(payload)
                _uiState.update { it.copy(createdReservation = created, loading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = e.localizedMessage ?: "Error creando reserva"
                    )
                }
            }
        }
    }

    // Método opcional para limpiar la reserva creada después de navegar
    fun clearCreatedReservation() {
        _uiState.update { it.copy(createdReservation = null) }
    }

    // Método opcional para limpiar errores
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // Método opcional para validar el estado actual
    fun isValidReservation(): Boolean {
        val state = uiState.value
        return state.selectedCarId != null &&
                state.horaEntrada != null &&
                state.durationMinutes >= 60 &&  // mínimo 1 hora
                !state.loading
    }

    // Método opcional para obtener el carro seleccionado
    fun getSelectedCar(): Car? {
        val state = uiState.value
        return state.cars.find { it.id == state.selectedCarId }
    }
}*/