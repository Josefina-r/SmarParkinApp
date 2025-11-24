// ui/theme/Navigation/NavGraph.kt
package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.example.smarparkinapp.ui.theme.screens.SettingsScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.example.smarparkinapp.ui.theme.screens.ChatbotScreen
import com.example.smarparkinapp.ui.theme.screens.HistoryScreen
import com.example.smarparkinapp.ui.theme.screens.HomeScreen
import com.example.smarparkinapp.ui.theme.screens.LoginScreen
import com.example.smarparkinapp.ui.theme.screens.ParkingDetailScreen
import com.example.smarparkinapp.ui.theme.screens.ProfileOverviewScreen
import com.example.smarparkinapp.ui.theme.screens.RegisterScreen
import com.example.smarparkinapp.ui.theme.screens.ReservationScreen
import com.example.smarparkinapp.ui.theme.screens.SplashScreen
import com.example.smarparkinapp.ui.theme.screens.profile.ProfileScreen
import com.example.smarparkinapp.ui.theme.NavRoutes
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

        // HomeScreen
        composable(NavRoutes.Home.route) {
            HomeScreen(
                navController = navController,
                onParkingClick = { parkingId ->
                    navController.navigate(NavRoutes.ParkingDetail.createRoute(parkingId))
                },
                onReservationClick = { parkingName, plate, duration, total ->
                    navController.navigate(NavRoutes.Reservation.createRoute(1))
                }
            )
        }

        // Historial
        composable(NavRoutes.Historial.route) {
            HistoryScreen(navController = navController)
        }

        // ✅ NUEVO: Chatbot de Soporte
        composable(NavRoutes.Chatbot.route) {
            ChatbotScreen(navController = navController)
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController = navController)
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

        // ✅ CORREGIDO: Vehicle Selection - Usando NavRoutes.VehicleSelection.route
        composable(
            route = NavRoutes.VehicleSelection.route,
            arguments = listOf(navArgument("parkingId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getInt("parkingId") ?: -1

            VehicleSelectionScreen(
                navController = navController,
                parkingId = if (parkingId != -1) parkingId else null
            )
        }

        // ✅ CORREGIDO: Add Vehicle Screen - Usando NavRoutes.AddVehicle.route
        composable(NavRoutes.AddVehicle.route) {
            BasicAddVehicleScreen(navController = navController)
        }

        // Reservation - CORREGIDO: Usando asignación directa
        composable(
            route = NavRoutes.Reservation.route,
            arguments = listOf(navArgument("parkingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getInt("parkingId") ?: 0

            val reservationViewModel: ReservationViewModel = viewModel(
                factory = ReservationViewModelFactory(context)
            )

            // Crear un ParkingLot de ejemplo
            val parkingLot = ParkingLot(
                id = parkingId.toLong(),
                nombre = "Estacionamiento $parkingId",
                direccion = "Dirección del estacionamiento $parkingId",
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

            // CORREGIDO: Asignar directamente en lugar de usar startReservationFlow
            LaunchedEffect(parkingLot) {
                reservationViewModel.selectedParking = parkingLot
            }

            ReservationScreen(
                viewModel = reservationViewModel,
                selectedParking = parkingLot, // Pasar el parkingLot seleccionado
                onSuccessNavigate = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Reservation.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )

            // Mostrar diálogo de agregar vehículo si es necesario
            if (reservationViewModel.showAddVehicleDialog) {
                AddVehicleDialog(
                    viewModel = reservationViewModel,
                    onDismiss = { reservationViewModel.hideAddVehicleForm() },
                    onSave = { reservationViewModel.saveNewVehicleAndNavigate() }
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

        // Complete Profile
        composable(
            route = NavRoutes.CompleteProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// ✅ CORREGIDO: Pantalla básica para agregar vehículo con todos los imports
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicAddVehicleScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Vehículo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Funcionalidad de agregar vehículo",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Aquí iría el formulario para agregar un nuevo vehículo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.popBackStack() }
            ) {
                Text("Volver")
            }
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