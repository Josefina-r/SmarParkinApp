package com.example.smarparkinapp.ui.theme.data.model

import com.google.gson.annotations.SerializedName
import com.example.smarparkinapp.ui.theme.data.api.UserResponse

data class ReservationResponse(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("codigo_reserva")
    val codigoReserva: String = "",

    @SerializedName("usuario")
    val usuario: UserResponse? = null,

    @SerializedName("usuario_nombre")
    val usuarioNombre: String = "",

    // ESTOS CAMPOS LOS VAMOS A MANEJAR CON UN DESERIALIZADOR
    // Mantenemos el nombre pero usaremos un custom adapter
    @SerializedName("vehiculo")
    val vehiculoId: Long = 0,

    @SerializedName("estacionamiento")
    val estacionamientoId: Long = 0,

    @SerializedName("hora_entrada")
    val horaEntrada: String = "",

    @SerializedName("hora_salida")
    val horaSalida: String? = null,

    @SerializedName("duracion_minutos")
    val duracionMinutos: Int? = null,

    @SerializedName("costo_estimado")
    val costoEstimado: Double? = null,

    @SerializedName("estado")
    val estado: String = "",

    @SerializedName("tipo_reserva")
    val tipoReserva: String = "",

    @SerializedName("created_at")
    val createdAt: String = "",

    // FIJATE: En el JSON dice "tiempo_restante" NO "tiempo_restante_minutos"
    @SerializedName("tiempo_restante")
    val tiempoRestante: Int? = null,

    @SerializedName("puede_cancelar")
    val puedeCancelar: Boolean = false
)

// Modelos auxiliares OPCIONALES (si los necesitas)
data class UserShortResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)

data class VehicleShortResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("placa")
    val placa: String,

    @SerializedName("modelo")
    val modelo: String,

    @SerializedName("color")
    val color: String? = null
)

data class ParkingShortResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("direccion")
    val direccion: String? = null,

    @SerializedName("tarifa_hora")
    val tarifaHora: Double? = null
)