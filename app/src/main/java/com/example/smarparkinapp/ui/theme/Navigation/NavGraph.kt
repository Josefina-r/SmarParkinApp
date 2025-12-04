
package com.example.smarparkinapp.ui.theme.Navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.screens.VehicleSelectionScreen
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import com.example.smarparkinapp.ui.theme.screens.ChatbotScreen
import com.example.smarparkinapp.ui.theme.screens.HistoryScreen
import com.example.smarparkinapp.ui.theme.screens.HomeScreen
import com.example.smarparkinapp.ui.theme.screens.LoginScreen
import com.example.smarparkinapp.ui.theme.screens.ParkingDetailScreen
import com.example.smarparkinapp.ui.theme.screens.ProfileOverviewScreen
import com.example.smarparkinapp.ui.theme.screens.RegisterScreen
import com.example.smarparkinapp.ui.theme.screens.SplashScreen
import com.example.smarparkinapp.ui.theme.screens.profile.ProfileScreen
import com.example.smarparkinapp.ui.theme.screens.MyReservationsScreen
import com.example.smarparkinapp.ui.theme.screens.PaymentScreen
import com.example.smarparkinapp.ui.theme.screens.TicketScreen
import com.example.smarparkinapp.ui.theme.viewmodel.ProfileViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
import com.example.smarparkinapp.ui.theme.screens.ReservationScreen
import com.example.smarparkinapp.ui.theme.screens.SettingsScreen
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory

import com.example.smarparkinapp.ui.theme.screens.ChangePasswordScreen
import com.example.smarparkinapp.ui.theme.screens.TermsListScreen
import com.example.smarparkinapp.ui.theme.screens.TermsScreen
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current


    val reservationViewModel: ReservationViewModel = viewModel(
        factory = ReservationViewModelFactory(context)
    )

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
                onRegisterClick = { navController.navigate(NavRoutes.Register.route) },
                navController = navController
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
                    println(" [NavGraph] Navegando a ParkingDetail con ID: $parkingId")
                    navController.navigate(NavRoutes.ParkingDetail.createRoute(parkingId.toLong()))
                },
                onReservationClick = { parkingId ->
                    println(" [NavGraph] Navegando a VehicleSelection con ID: $parkingId")
                    navController.navigate(NavRoutes.VehicleSelection.createRoute(parkingId.toLong()))
                }
            )
        }

        // Historial
        composable(NavRoutes.Historial.route) {
            HistoryScreen(navController = navController)
        }

        // Chatbot de Soporte
        composable(NavRoutes.Chatbot.route) {
            ChatbotScreen(navController = navController)
        }

        // Settings
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController = navController)
        }

        // Parking Detail
        composable(
            route = NavRoutes.ParkingDetail.route,
            arguments = listOf(navArgument("parkingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getLong("parkingId") ?: 0L

            ParkingDetailScreen(
                navController = navController,
                parkingId = parkingId
            )
        }

        // Vehicle Selection
        composable(
            route = NavRoutes.VehicleSelection.route,
            arguments = listOf(navArgument("parkingId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getLong("parkingId") ?: -1L

            VehicleSelectionScreen(
                navController = navController,
                parkingId = if (parkingId != -1L) parkingId else null,

                viewModel = reservationViewModel
            )
        }

        // Reservation
        composable(
            route = NavRoutes.Reservation.route,
            arguments = listOf(navArgument("parkingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val parkingId = backStackEntry.arguments?.getLong("parkingId")

            LaunchedEffect(parkingId) {
                parkingId?.let { id ->
                    reservationViewModel.loadParkingById(id)
                }
            }

            ReservationScreen(
                navController = navController,

                viewModel = reservationViewModel
            )
        }


        composable(NavRoutes.Payment.route) {
            PaymentScreen(
                navController = navController,
                reservationId = null,
                viewModel = reservationViewModel
            )
        }

        // Ticket
        // Ruta para ver TODOS los tickets del usuario
        composable("tickets") {
            TicketScreen(
                navController = navController,
                ticketId = null,
                reservationId = null,
                viewModel = reservationViewModel
            )
        }

// Ruta para ver un ticket ESPECÍFICO por su ID
        composable(
            route = "ticket/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId")

            TicketScreen(
                navController = navController,
                ticketId = ticketId,
                reservationId = null,
                viewModel = reservationViewModel
            )
        }

// Ruta para ver ticket por ID de RESERVA
        composable(
            route = "ticket/reservation/{reservationId}",
            arguments = listOf(navArgument("reservationId") { type = NavType.LongType })
        ) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getLong("reservationId")

            TicketScreen(
                navController = navController,
                ticketId = null,
                reservationId = reservationId,
                viewModel = reservationViewModel
            )
        }

        composable(
            route = NavRoutes.Ticket.route,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("paymentId")

            navController.navigate("tickets") {
                popUpTo(NavRoutes.Ticket.route) { inclusive = true }
            }
        }
        composable("myReservations") {
            MyReservationsScreen(
                navController = navController,
                viewModel = reservationViewModel
            )
        }

        // Perfil
        composable(NavRoutes.Perfil.route) {
            ProfileOverviewScreen(
                onBack = {
                    navController.popBackStack()
                },
                onEditProfile = {
                    navController.navigate("editProfile")
                },
                onPaymentMethods = {
                    // navController.navigate(NavRoutes.PaymentMethods.route)
                },
                onMyVehicles = {
                    navController.navigate("myVehicles") {
                        //navega asia la vehiculos
                    }
                },

                onChangePassword = {
                    navController.navigate("changePassword")
                }
            )
        }

        // Edit Profile
        composable("editProfile") {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onUpdateSuccess = {
                    navController.popBackStack()
                }
            )
        }


        composable("changePassword") {
            ChangePasswordScreen(
                onPasswordChanged = {
                    // Opcional: mostrar mensaje de éxito y volver al perfil
                    navController.popBackStack()
                },
                onBack = {
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
                onBackClick = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(0)
                    }
                },
                onUpdateSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(0)
                    }
                }
            )
        }


        composable("myVehicles") {
            VehicleSelectionScreen(
                navController = navController,
                parkingId = null, // Cuando es null, se usa para solo ver vehículos
                viewModel = reservationViewModel
            )
        }
        composable("terms_list") {
            TermsListScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "terms/{code}",
            arguments = listOf(navArgument("code") { type = NavType.IntType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getInt("code") ?: 100

            TermsScreen(
                navController = navController,
                code = code,
                onBackClick = { navController.popBackStack() }
            )
        }

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