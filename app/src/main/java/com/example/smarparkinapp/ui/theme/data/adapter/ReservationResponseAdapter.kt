package com.example.smarparkinapp.ui.theme.data.adapter

import com.example.smarparkinapp.ui.theme.data.model.ReservationResponse
import com.example.smarparkinapp.ui.theme.data.api.UserResponse
import com.google.gson.*
import java.lang.reflect.Type

class ReservationResponseAdapter : JsonDeserializer<ReservationResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ReservationResponse {
        val jsonObject = json.asJsonObject

        // Función para extraer ID (puede ser objeto o número)
        fun extractId(fieldName: String): Long {
            if (!jsonObject.has(fieldName)) return 0L

            val element = jsonObject.get(fieldName)
            return when {
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    obj.get("id")?.asLong ?: 0L
                }
                element.isJsonPrimitive && element.asJsonPrimitive.isNumber -> {
                    element.asLong
                }
                else -> 0L
            }
        }

        // Función para obtener string con valor por defecto
        fun getString(fieldName: String, default: String = ""): String {
            return if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull) {
                jsonObject.get(fieldName).asString
            } else {
                default
            }
        }

        // Función para obtener valor nullable
        fun getNullableInt(fieldName: String): Int? {
            return if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull) {
                try {
                    jsonObject.get(fieldName).asInt
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

        fun getNullableDouble(fieldName: String): Double? {
            return if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull) {
                try {
                    jsonObject.get(fieldName).asDouble
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

        fun getBoolean(fieldName: String, default: Boolean = false): Boolean {
            return if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull) {
                try {
                    jsonObject.get(fieldName).asBoolean
                } catch (e: Exception) {
                    default
                }
            } else {
                default
            }
        }

        // Deserializar usuario si existe
        val usuario = if (jsonObject.has("usuario") && !jsonObject.get("usuario").isJsonNull) {
            try {
                context.deserialize<UserResponse>(jsonObject.get("usuario"), UserResponse::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        return ReservationResponse(
            id = if (jsonObject.has("id")) jsonObject.get("id").asLong else 0L,
            codigoReserva = getString("codigo_reserva"),
            usuario = usuario, // Ahora sí con el UserResponse
            usuarioNombre = getString("usuario_nombre"),
            vehiculoId = extractId("vehiculo"),
            estacionamientoId = extractId("estacionamiento"),
            horaEntrada = getString("hora_entrada"),
            horaSalida = if (jsonObject.has("hora_salida") && !jsonObject.get("hora_salida").isJsonNull) {
                jsonObject.get("hora_salida").asString
            } else {
                null
            },
            duracionMinutos = getNullableInt("duracion_minutos"),
            costoEstimado = getNullableDouble("costo_estimado"),
            estado = getString("estado"),
            tipoReserva = getString("tipo_reserva"),
            createdAt = getString("created_at"),
            tiempoRestante = getNullableInt("tiempo_restante"),
            puedeCancelar = getBoolean("puede_cancelar")
        )
    }
}