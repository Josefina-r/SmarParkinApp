package com.example.smarparkinapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.example.smarparkinapp.ui.theme.viewmodel.LocationViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.LocationViewModelFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import com.example.smarparkinapp.ui.theme.theme.*
import com.google.android.gms.maps.CameraUpdateFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onParkingClick: (Int) -> Unit,
    onReservationClick: (parkingName: String, plate: String, duration: Int, total: Double) -> Unit
) {
    val context = LocalContext.current
    val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModelFactory(context))
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(locationViewModel))
    val scope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val parkingSpots by homeViewModel.filteredParkingSpots.collectAsState()
    val userLocation by homeViewModel.userLocation.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            homeViewModel.startLocationUpdates()
            homeViewModel.fetchParkingSpots()
        } else {
            homeViewModel.fetchParkingSpots()
        }
    }

    // ✅ Verificar permisos al iniciar
    LaunchedEffect(Unit) {
        val fineGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            homeViewModel.startLocationUpdates()
            homeViewModel.fetchParkingSpots()
        }
    }

    // ✅ Configuración de cámara inicial
    val defaultLocation = LatLng(-8.111667, -79.028889)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    // ✅ Centrar cámara automáticamente cuando cambia la ubicación del usuario
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.background(VerdeSecundario)) {
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge.copy(color = AzulSecundario)
                )
                NavigationDrawerItem(
                    label = { Text("Home", color = AzulSecundario) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil", color = AzulSecundario) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Historial", color = AzulSecundario) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {

                // ✅ Google Maps Compose con tu API Key
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = userLocation != null
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        myLocationButtonEnabled = false // ❌ se quitó el botón azul
                    )
                ) {
                    // Marcadores de estacionamientos
                    parkingSpots.forEach { spot ->
                        Marker(
                            state = MarkerState(LatLng(spot.latitude, spot.longitude)),
                            title = spot.name,
                            snippet = "Precio: ${spot.price} - ${spot.availableSpots} disponibles",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )
                    }

                    // Marcador del usuario
                    userLocation?.let {
                        Marker(
                            state = MarkerState(it),
                            title = "Tu ubicación",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }
                }

                // ✅ Botón para abrir menú lateral
                Box(modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .size(56.dp)
                            .background(VerdeSecundario, MaterialTheme.shapes.large)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                }

                // ✅ Mensaje de error si algo falla
                errorMessage?.let {
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                    ) {
                        Text(it)
                    }
                }
            }
        }
    }
}
