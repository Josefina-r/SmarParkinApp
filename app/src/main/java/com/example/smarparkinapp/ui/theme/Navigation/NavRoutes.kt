// ui/theme/NavRoutes.kt - VERSIÓN SIMPLIFICADA
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

    object Reservation : NavRoutes("reservation/{parkingId}") {
        fun createRoute(parkingId: Int) = "reservation/$parkingId"
    }

    object ParkingDetail : NavRoutes("parking_detail/{parkingId}") {
        fun createRoute(parkingId: Int) = "parking_detail/$parkingId"
    }

    // ✅ CORREGIDO: Usar el mismo formato que los demás - parámetro de ruta
    object VehicleSelection : NavRoutes("vehicle_selection/{parkingId}") {
        fun createRoute(parkingId: Int) = "vehicle_selection/$parkingId"
    }
}