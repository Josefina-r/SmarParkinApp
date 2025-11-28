package com.example.smarparkinapp.ui.theme.data.model


import com.google.gson.annotations.SerializedName

data class Reservation(
    @SerializedName("id")
    val id: Long = 0,

    @SerializedName("codigo_reserva")
    val codigoReserva: String = "",

    @SerializedName("usuario")
    val usuario: UserShort? = null,

    @SerializedName("vehiculo")
    val vehiculo: VehicleShort? = null,

    @SerializedName("estacionamiento")
    val estacionamiento: ParkingShort? = null,

    @SerializedName("hora_entrada")
    val horaEntrada: String = "",

    @SerializedName("hora_salida")
    val horaSalida: String = "",

    @SerializedName("duracion_minutos")
    val duracionMinutos: Int = 0,

    @SerializedName("costo_estimado")
    val costoEstimado: Double = 0.0,

    @SerializedName("estado")
    val estado: String = "activa", // activa, finalizada, cancelada

    @SerializedName("tipo_reserva")
    val tipoReserva: String = "hora", // hora, dia, mes

    @SerializedName("created_at")
    val createdAt: String = "",

    // Campos calculados del backend
    @SerializedName("tiempo_restante")
    val tiempoRestante: Int? = null,

    @SerializedName("puede_cancelar")
    val puedeCancelar: Boolean = false
)


data class UserShort(
    @SerializedName("id")
    val id: Long,

    @SerializedName("email")
    val email: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String
)