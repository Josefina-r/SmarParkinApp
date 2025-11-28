// ui/theme/NavRoutes.kt - VERSIÓN CORREGIDA (SOLO LO NECESARIO)
package com.example.smarparkinapp.ui.theme

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object Notificaciones : NavRoutes("notificaciones")
    object Reservar : NavRoutes("reservar")
    object CompleteProfile : NavRoutes("complete_profile/{userId}") {
        fun createRoute(userId: Int) = "complete_profile/$userId"
    }
    object Home : NavRoutes("home")
    object Main : NavRoutes("main")
    object Perfil : NavRoutes("perfil")
    object Historial : NavRoutes("historial")
    object AddVehicle : NavRoutes("add_vehicle")

    // CORRECCIÓN: Cambiar a Long para consistencia
    object Reservation : NavRoutes("reservation/{parkingId}") {
        fun createRoute(parkingId: Long) = "reservation/$parkingId" // ✅ Cambiado a Long
    }

    // CORRECCIÓN: Cambiar a Long para consistencia
    object ParkingDetail : NavRoutes("parking_detail/{parkingId}") {
        fun createRoute(parkingId: Long) = "parking_detail/$parkingId" // ✅ Cambiado a Long
    }

    // CORRECCIÓN: Ya está correcto con Long
    object VehicleSelection : NavRoutes("vehicle_selection/{parkingId}") {
        fun createRoute(parkingId: Long) = "vehicle_selection/$parkingId" // ✅ Ya correcto
    }

    object Chatbot : NavRoutes("chatbot")

    // CORRECCIÓN: Agregar parámetro que falta según tu NavGraph
    object Payment : NavRoutes("payment/{reservationId}") {
        fun createRoute(reservationId: Long) = "payment/$reservationId" // ✅ Agregado parámetro
    }

    // Función auxiliar (se mantiene igual)
    fun createRoute(vararg params: String): String {
        var newRoute = route
        params.forEach { param ->
            newRoute += "/$param"
        }
        return newRoute
    }

    object ReservationOptions : NavRoutes("reservation_options")
    object EmbeddedReservations : NavRoutes("embedded_reservations")

    // CORRECCIÓN: Cambiar a Long para consistencia
    object ReservationDetail : NavRoutes("reservations/{parkingId}") {
        fun createRoute(parkingId: Long) = "reservations/$parkingId" // ✅ Cambiado a Long
    }

    object ReservationSuccess : NavRoutes("reservation_success/{reservationId}") {
        fun createRoute(reservationId: String) = "reservation_success/$reservationId"
    }
    object Ticket : NavRoutes("ticket/{paymentId}") {
        fun createRoute(paymentId: String) = "ticket/$paymentId"
    }
    // Rutas de settings (se mantienen igual)
    object Settings : NavRoutes("settings")
    object Terms : NavRoutes("terms")
    object Privacy : NavRoutes("privacy")
    object HelpCenter : NavRoutes("help_center")
    object About : NavRoutes("about")
}