package com.example.smarparkinapp.ui.theme.data.model



import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Payment(
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),

    @SerializedName("referencia_pago")
    val referenciaPago: String = "",

    @SerializedName("reserva")
    val reserva: ReservationResponse? = null,

    @SerializedName("usuario")
    val usuario: UserShortResponse? = null,

    @SerializedName("monto")
    val monto: Double = 0.0,

    @SerializedName("moneda")
    val moneda: String = "PEN",

    @SerializedName("metodo")
    val metodo: String = "", // O usar: val metodo: PaymentMethod = PaymentMethod.TARJETA

    @SerializedName("estado")
    val estado: String = "pendiente", // O usar: val estado: PaymentStatus = PaymentStatus.PENDIENTE

    @SerializedName("id_transaccion")
    val idTransaccion: String? = null,

    @SerializedName("datos_gateway")
    val datosGateway: Map<String, Any>? = null,

    @SerializedName("comision_plataforma")
    val comisionPlataforma: Double = 0.0,

    @SerializedName("monto_propietario")
    val montoPropietario: Double = 0.0,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String = "",

    @SerializedName("fecha_pago")
    val fechaPago: String? = null,

    @SerializedName("fecha_reembolso")
    val fechaReembolso: String? = null,

    @SerializedName("intentos")
    val intentos: Int = 0,

    @SerializedName("ultimo_error")
    val ultimoError: String? = null
) {
    // Propiedades computadas útiles
    val puedeReembolsar: Boolean
        get() = estado == "pagado" && fechaPago != null

    val montoFormateado: String
        get() = "S/ ${"%.2f".format(monto)}"

    val comisionFormateada: String
        get() = "S/ ${"%.2f".format(comisionPlataforma)}"

    val montoPropietarioFormateado: String
        get() = "S/ ${"%.2f".format(montoPropietario)}"
}

data class PaymentShortResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("referencia_pago")
    val referenciaPago: String,

    @SerializedName("monto")
    val monto: Double,

    @SerializedName("metodo")
    val metodo: String,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String
)

data class PaymentStatusResponse(
    @SerializedName("estado")
    val estado: String,

    @SerializedName("ultima_actualizacion")
    val ultimaActualizacion: String,

    @SerializedName("puede_reintentar")
    val puedeReintentar: Boolean,

    @SerializedName("mensaje_usuario")
    val mensajeUsuario: String?
)