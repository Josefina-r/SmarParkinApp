package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import  com.example.smarparkinapp.ui.theme.data.model.EstacionamientoDetalleScreen
import com.example.smarparkinapp.ui.screens.ReservationScreen
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

        // Home
        composable(NavRoutes.Home.route) {
            HomeScreen(
                navController = navController,
                onParkingClick = { /* acción */ },
                onReservationClick = { parkingName, plate, duration, total ->
                    navController.navigate(
                        NavRoutes.Reservation.createRoute(parkingName, plate, duration, total)
                    )
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
            val parkingName = backStackEntry.arguments?.getString("parkingName") ?: "Estacionamiento"
            val plate = backStackEntry.arguments?.getString("plate") ?: ""
            val duration = backStackEntry.arguments?.getString("duration")?.toInt() ?: 1
            val total = backStackEntry.arguments?.getString("total")?.toDouble() ?: 0.0

            ReservationScreen(
                navController = navController,
                parkingName = parkingName,
                pricePerHour = total / duration
            )
        }
        composable(
            route = NavRoutes.ParkingDetail.route
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getString("parkingId")
            requireNotNull(parkingId) { "parkingId parameter wasn't found. Please make sure it's set!" }
            EstacionamientoDetalleScreen(navController = navController, parkingId = parkingId)
        }
    }

}
