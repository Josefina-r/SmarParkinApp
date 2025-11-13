package com.example.smarparkinapp.ui.theme.Navigation

import android.R.attr.type
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
//import com.example.smarparkinapp.ui.screens.ReservationScreen
import com.example.smarparkinapp.ui.theme.screens.*
import com.example.smarparkinapp.ui.screens.HomeScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument
//import com.example.smarparkinapp.ui.theme.data.repository.ReservationRepository
//import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
//import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory

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
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(NavRoutes.Login.route)
                }
            )
        }


        // Complete Profile
        composable(
            route = NavRoutes.CompleteProfile.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
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
        composable(
            route = NavRoutes.Reservation.route,
            arguments = listOf(
                navArgument("parkingName") { type = NavType.StringType },
                navArgument("plate") { type = NavType.StringType },
                navArgument("duration") { type = NavType.IntType },
                navArgument("total") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val parkingName = backStackEntry.arguments?.getString("parkingName") ?: "Estacionamiento Central"
            val total = backStackEntry.arguments?.getFloat("total")?.toDouble() ?: 5.0
            val duration = backStackEntry.arguments?.getInt("duration") ?: 1

            val pricePerHour = total / duration

            //val repo = ReservationRepository()
           // val viewModel: ReservationViewModel = viewModel(
            //    factory = ReservationViewModelFactory(
                 //   repo = repo,
            //        parkingId = 1,          // reemplaza con el ID real del parking si lo tienes
            //        pricePerHour = pricePerHour
             //   )
           // )

           // ReservationScreen(
            //    navController = navController,
           //     viewModel = viewModel,
           //     parkingName = parkingName
           // )//
        }

        composable(
            route = NavRoutes.ParkingDetail.route
        ) { backStackEntry ->
            EstacionamientoDetalleScreen()
        }
    }

}
