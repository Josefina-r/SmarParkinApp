package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smarparkinapp.ui.screens.ReservationScreen
import com.example.smarparkinapp.ui.theme.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                onTimeout = { navController.navigate(NavRoutes.Login.route) }
            )
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavRoutes.Home.route) },
                onRegisterClick = { navController.navigate(NavRoutes.Register.route) }
            )
        }

        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(NavRoutes.Home.route) },
                onLoginClick = { navController.navigate(NavRoutes.Login.route) }
            )
        }

        //  HOME
        composable(NavRoutes.Home.route) {
            HomeScreen(
                navController = navController,
                onParkingClick = { /* Por ahora vacío */ },
                onReservationClick = { parkingName, plate, duration, total ->
                    navController.navigate(
                        NavRoutes.Reservation.createRoute(parkingName, plate, duration, total)
                    )
                }
            )
        }

        //  PERFIL
        composable(NavRoutes.Perfil.route) {
            PerfilScreen(
                onCerrarSesion = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(NavRoutes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        //  HISTORIAL
        composable(NavRoutes.Historial.route) {
            HistoryScreen()
        }

        //  RESERVATION
        composable(NavRoutes.Reservation.route) { backStackEntry ->
            val parkingName = backStackEntry.arguments?.getString("parkingName") ?: "Estacionamiento"
            val plate = backStackEntry.arguments?.getString("plate") ?: ""
            val duration = backStackEntry.arguments?.getString("duration")?.toInt() ?: 1
            val total = backStackEntry.arguments?.getString("total")?.toDouble() ?: 0.0

            ReservationScreen(
                parkingName = parkingName,
                pricePerHour = total / duration
            )
        }
    }
}
