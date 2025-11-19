package com.example.smarparkinapp.ui.theme.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.api.RetrofitInstance
import com.example.smarparkinapp.ui.theme.data.model.Reservation
import com.example.smarparkinapp.ui.theme.data.model.GenericResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(private val context: Context) : ViewModel() {

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    val reservations: StateFlow<List<Reservation>> = _reservations

    private val _activeReservations = MutableStateFlow<List<Reservation>>(emptyList())
    val activeReservations: StateFlow<List<Reservation>> = _activeReservations

    private val _createdReservation = MutableStateFlow<Reservation?>(null)
    val createdReservation: StateFlow<Reservation?> = _createdReservation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiService = RetrofitInstance.getAuthenticatedApiService(context)
    private val authManager = AuthManager(context)


    // ============================================================
    // 1. OBTENER TODAS MIS RESERVAS
    // ============================================================
    fun loadMyReservations() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = apiService.getMyReservations()
                if (response.isSuccessful) {
                    _reservations.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Código de error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadActiveReservations() {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val response = apiService.getActiveReservations()
                if (response.isSuccessful) {
                    _activeReservations.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Código: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun createReservation(
        parkingId: Int,
        carId: Int,
        horaInicio: String,
        horaFin: String,
        tipoReserva: String,
        estado: String = "pendiente",
        montoTotal: Double? = null,
        onSuccess: (Reservation) -> Unit = {}
    ) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            val body = mutableMapOf(
                "parking_lot" to parkingId,
                "car" to carId,
                "hora_inicio" to horaInicio,
                "hora_fin" to horaFin,
                "tipo_reserva" to tipoReserva,
                "estado" to estado
            )

            // Agregar monto_total solo si no es null
            montoTotal?.let {
                body["monto_total"] = it
            }

            try {
                val response = apiService.createReservation(body)

                if (response.isSuccessful) {
                    val reservation = response.body()
                    _createdReservation.value = reservation
                    reservation?.let { onSuccess(it) }
                } else {
                    _error.value = "Error creando reserva: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelReservation(codigo: String, onSuccess: () -> Unit = {}) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = apiService.cancelReservation(codigo)
                if (response.isSuccessful) onSuccess()
                else _error.value = "Error: ${response.code()}"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun extendReservation(codigo: String, minutes: Int, onSuccess: () -> Unit = {}) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = apiService.extendReservation(codigo, mapOf("minutes" to minutes))
                if (response.isSuccessful) onSuccess()
                else _error.value = "Error: ${response.code()}"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun checkIn(codigo: String, onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.checkIn(codigo)
                if (response.isSuccessful) onSuccess()
                else _error.value = "Código: ${response.code()}"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkOut(codigo: String, onSuccess: () -> Unit = {}) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.checkOut(codigo)
                if (response.isSuccessful) onSuccess()
                else _error.value = "Código: ${response.code()}"
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}