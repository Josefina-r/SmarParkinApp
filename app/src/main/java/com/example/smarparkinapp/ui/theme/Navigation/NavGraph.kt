package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import  com.example.smarparkinapp.ui.theme.data.model.EstacionamientoDetalleScreen
import com.example.smarparkinapp.ui.theme.screens.ReservationScreen
import com.example.smarparkinapp.ui.theme.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        // Splash
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onTimeout = { navController.navigate(NavRoutes.Login.route) }
            )
        }

        // Login
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavRoutes.Home.route) },
                onRegisterClick = { navController.navigate(NavRoutes.Register.route) }
            )
        }

        // Register
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { userId ->
                    navController.navigate(NavRoutes.CompleteProfile.createRoute(userId)) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.navigate(NavRoutes.Login.route) }
            )
        }

        // Complete Profile
        composable(NavRoutes.CompleteProfile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            CompleteProfileScreen(
                userId = userId,
                onProfileCompleted = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                }
            )
        }

        //  HOME: Ahora conecta con el Detalle
        composable(NavRoutes.Home.route) {
            HomeScreen(
                navController = navController,
                // Acción 1: Clic simple en el mapa o lista -> Va al detalle
                onParkingClick = { parkingId ->
                    navController.navigate(NavRoutes.ParkingDetail.createRoute(parkingId))
                },
                // Acción 2: Clic en "Reservar" -> Va al detalle (o directo a reserva si prefieres)
                // En este caso, lo enviamos al detalle para que vea la info completa antes.
                onReservationClick = { _, _, _, _ ->
                    // Nota: Si tienes el ID disponible en este callback en HomeScreen, úsalo.
                    // Si no, asegúrate de actualizar HomeScreen para pasar el ID aquí también.
                    // Por ahora, asumiremos que onParkingClick maneja la navegación principal.
                }
            )
        }


        // Perfil
        composable(NavRoutes.Perfil.route) {
            PerfilScreen(
                onCerrarSesion = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Historial
        composable(NavRoutes.Historial.route) {
            HistoryScreen(navController = navController)
        }

        // Reservation
        composable(NavRoutes.Reservation.route) { backStackEntry ->

            val parkingId = backStackEntry.arguments
                ?.getString("parkingId")
                ?.toIntOrNull()
                ?: 0

            ReservationScreen(
                parkingId = parkingId,
                onSuccessNavigate = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Reservation.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavRoutes.ParkingDetail.route
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getString("parkingId")
            requireNotNull(parkingId) { "parkingId parameter wasn't found. Please make sure it's set!" }
            EstacionamientoDetalleScreen(navController = navController, parkingId = parkingId)
        }
        // ✅ NUEVA PANTALLA: Detalle del Estacionamiento
        composable(
            route = NavRoutes.ParkingDetail.route,
            arguments = listOf(navArgument("parkingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getInt("parkingId") ?: 0

            // Llamamos a la pantalla que creamos anteriormente
            ParkingDetailScreen(
                navController = navController,
                parkingId = parkingId
            )
        }
    }

}
