package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smarparkinapp.components.AddVehicleDialog
import com.example.smarparkinapp.screens.VehicleSelectionScreen
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
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
        composable(
            route = NavRoutes.CompleteProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
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

        // HOME - CORREGIDO
        composable(NavRoutes.Home.route) {
            HomeScreen(
                navController = navController,
                onParkingClick = { parkingId ->
                    navController.navigate(NavRoutes.ParkingDetail.createRoute(parkingId))
                },
                onReservationClick = { parkingId, vehicleId, startTime, endTime ->
                    // Manejar la reserva - puedes navegar a ReservationScreen
                    navController.navigate(
                        NavRoutes.Reservation.createRoute(parkingId, vehicleId, startTime, endTime)
                    )
                }
            )
        }

        // Perfil
        composable(NavRoutes.Perfil.route) {
            PerfilScreen(
                navController = navController,
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
            arguments = listOf(navArgument("parkingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getInt("parkingId") ?: 0

            ReservationScreen(
                parkingId = parkingId,
                onSuccessNavigate = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Reservation.route) { inclusive = true }
                    }
                }
            )
        }

        // Parking Detail
        composable(
            route = NavRoutes.ParkingDetail.route,
            arguments = listOf(navArgument("parkingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getInt("parkingId") ?: 0

            ParkingDetailScreen(
                navController = navController,
                parkingId = parkingId
            )
        }

        // VEHICLE SELECTION
        composable(NavRoutes.VehicleSelection.route) {
            val viewModel: ReservationViewModel = viewModel()

            VehicleSelectionScreen(
                onBack = { navController.popBackStack() },
                onVehicleSelected = { car ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selectedVehicle",
                        car
                    )
                    navController.popBackStack()
                },
                onAddVehicle = {
                    viewModel.showAddVehicleForm()
                },
                viewModel = viewModel
            )

            // Modal para agregar vehículo
            if (viewModel.showAddVehicleDialog) {
                AddVehicleDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.hideAddVehicleForm() },
                    onSave = { viewModel.saveNewVehicle() }
                )
            }
        }
    }
}