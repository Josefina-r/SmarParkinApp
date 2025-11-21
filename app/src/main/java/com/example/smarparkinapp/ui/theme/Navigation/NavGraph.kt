package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smarparkinapp.components.AddVehicleDialog
import com.example.smarparkinapp.screens.VehicleSelectionScreen
import com.example.smarparkinapp.ui.theme.data.model.ParkingLot
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import com.example.smarparkinapp.ui.theme.screens.HistoryScreen
import com.example.smarparkinapp.ui.theme.screens.HomeScreen
import com.example.smarparkinapp.ui.theme.screens.LoginScreen
import com.example.smarparkinapp.ui.theme.screens.ParkingDetailScreen
import com.example.smarparkinapp.ui.theme.screens.ProfileOverviewScreen
import com.example.smarparkinapp.ui.theme.screens.RegisterScreen
import com.example.smarparkinapp.ui.theme.screens.ReservationScreen
import com.example.smarparkinapp.ui.theme.screens.SplashScreen
import com.example.smarparkinapp.ui.theme.screens.profile.ProfileScreen
import com.example.smarparkinapp.ui.theme.viewmodel.ProfileViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current

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

        // Home
        composable(NavRoutes.Home.route) {
            HomeScreen(
                navController = navController,
                onParkingClick = { parkingId ->
                    navController.navigate(NavRoutes.ParkingDetail.createRoute(parkingId))
                },
                onReservationClick = { parkingId, vehicleId, startTime, endTime ->
                    navController.navigate(
                        NavRoutes.Reservation.createRoute(parkingId, vehicleId, startTime, endTime)
                    )
                }
            )
        }

        // Historial
        composable(NavRoutes.Historial.route) {
            HistoryScreen(navController = navController)
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

        // Reservation
        composable(
            route = NavRoutes.Reservation.route,
            arguments = listOf(navArgument("parkingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getInt("parkingId") ?: 0

            // TODO: Obtener el objeto ParkingLot completo desde tu API
            val parkingLot = ParkingLot(
                id = parkingId.toLong(),
                nombre = "Estacionamiento Temporal",
                direccion = "Dirección temporal",
                coordenadas = null,
                telefono = null,
                descripcion = null,
                horario_apertura = null,
                horario_cierre = null,
                nivel_seguridad = null,
                tarifa_hora = 5.0,
                total_plazas = 50,
                plazas_disponibles = 25,
                rating_promedio = null,
                total_resenas = null,
                aprobado = true,
                activo = true,
                dueno = null,
                esta_abierto = true,
                imagen_principal = null,
                dueno_nombre = null
            )

            val reservationViewModel: ReservationViewModel = viewModel(
                factory = ReservationViewModelFactory(context)
            )

            ReservationScreen(
                parking = parkingLot,
                onSuccessNavigate = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Reservation.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Vehicle Selection
        composable(NavRoutes.VehicleSelection.route) {
            val viewModel: ReservationViewModel = viewModel(
                factory = ReservationViewModelFactory(context)
            )

            VehicleSelectionScreen(
                onBack = { navController.popBackStack() },
                onVehicleSelected = { car ->
                    navController.previousBackStackEntry?.savedStateHandle?.set<Int>(
                        key = "selectedVehicleId",
                        value = car.id
                    )
                    navController.popBackStack()
                },
                onAddVehicle = {
                    viewModel.showAddVehicleForm()
                },
                viewModel = viewModel
            )

            if (viewModel.showAddVehicleDialog) {
                AddVehicleDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.hideAddVehicleForm() },
                    onSave = { viewModel.saveNewVehicle() }
                )
            }
        }

        // Perfil
        composable(NavRoutes.Perfil.route) {
            ProfileConditionalScreen(
                navController = navController,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Edición de Perfil
        composable("profile_edit") {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// Componente para decidir qué pantalla de perfil mostrar
@Composable
fun ProfileConditionalScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Cargar perfil al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile(context)
    }

    // Mostrar loading mientras se carga
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Lógica condicional: Decidir qué pantalla mostrar
    if (isProfileComplete(userProfile)) {
        // Perfil completo → Mostrar Overview
        ProfileOverviewScreen(
            onBack = onBackClick,
            onEditProfile = {
                navController.navigate("profile_edit")
            },
            onPaymentMethods = {
                // navController.navigate("payment_methods")
            },
            onMyVehicles = {
                // navController.navigate("my_vehicles")
            }
        )
    } else {
        // Perfil incompleto → Mostrar formulario de completar
        ProfileScreen(
            onBackClick = onBackClick
        )
    }
}

// Función para verificar si el perfil está completo
private fun isProfileComplete(userProfile: UserProfile?): Boolean {
    return userProfile?.let { profile ->
        profile.firstName.isNotEmpty() &&
                profile.lastName.isNotEmpty() &&
                profile.phone.isNotEmpty() &&
                profile.tipoDocumento?.isNotEmpty() == true &&
                profile.numeroDocumento?.isNotEmpty() == true
    } ?: false
}