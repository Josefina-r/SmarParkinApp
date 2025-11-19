package com.example.smarparkinapp.ui.theme.Navigation

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
    object Reservation : NavRoutes("reservation/{parkingName}/{plate}/{duration}/{total}") {
        fun createRoute(parkingName: String, plate: String, duration: Int, total: Double) =
            "reservation/$parkingName/$plate/$duration/$total"
    }
    object ParkingDetail : NavRoutes("parking_detail/{parkingId}") {
        fun createRoute(parkingId: Int) = "parking_detail/$parkingId"
    }

    // ✅ CORRECTO: VehicleSelection como objeto separado
    object VehicleSelection : NavRoutes("vehicle_selection")
}