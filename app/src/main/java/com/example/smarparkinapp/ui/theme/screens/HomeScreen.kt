package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.Navigation.NavRoutes
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.data.model.*
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onParkingClick: (Int) -> Unit,
    onReservationClick: (parkingName: String, plate: String, duration: Int, total: Double) -> Unit,
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberBottomSheetScaffoldState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val parkingSpots by viewModel.parkingSpots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchParkingSpots() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.background(VerdeSecundario)
            ) {
                // Título del menú
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AzulSecundario
                    )
                )

                NavigationDrawerItem(
                    label = { Text("Perfil", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.Perfil.route)
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Historial") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.Historial.route)
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Reservar", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onReservationClick("Estacionamiento Central", "ABC-123", 1, 5.0)
                    }
                )

            }
        }
    ) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 250.dp,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Barra de búsqueda
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar estacionamiento...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filtros rápidos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip("Precio", VerdeSecundario)
                        FilterChip("Distancia", VerdePrincipal)
                        FilterChip("Seguridad", VerdePrincipal)
                        FilterChip("Disponible", Color(0xFF4CAF50))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Estacionamientos cercanos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(parkingSpots.filter { it.name.contains(searchQuery, ignoreCase = true) }) { parking ->
                            ParkingSpotCard(
                                parkingSpot = parking,
                                onReserveClick = {
                                    navController.navigate(
                                        NavRoutes.Reservation.createRoute(
                                            parking.name,
                                            "ABC-123",
                                            1,
                                            parking.price.toDoubleOrNull() ?: 0.0
                                        )
                                    )
                                }
                                ,
                                onDetailClick = { onParkingClick(parking.id) }
                            )
                        }

                    }

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    errorMessage?.let {
                        Text(it, color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Mapa de Google
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(-8.111667, -79.028889),
                        14f
                    )
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    cameraPositionState = cameraPositionState
                ) {
                    parkingSpots.forEach { spot ->
                        Marker(
                            state = MarkerState(position = LatLng(spot.latitude, spot.longitude)),
                            title = spot.name,
                            snippet = "${spot.price} - ${spot.availableSpots} lugares"
                        )
                    }
                }

                // Botón menú
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .size(56.dp)
                            .background(VerdeSecundario, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, color: Color) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { },
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ParkingSpotCard(
    parkingSpot: ParkingSpot,
    onReserveClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(16.dp))
            .clickable { onDetailClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(parkingSpot.name, fontWeight = FontWeight.Bold, color = AzulPrincipal)
            Spacer(modifier = Modifier.height(4.dp))
            Text(parkingSpot.address, fontSize = 12.sp, color = GrisClaro)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(parkingSpot.price, fontWeight = FontWeight.Bold, color = VerdePrincipal)
                Text("${parkingSpot.availableSpots} disponibles", color = VerdeSecundario)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onReserveClick, enabled = parkingSpot.availableSpots > 0) {
                Text(if (parkingSpot.availableSpots > 0) "Reservar" else "Lleno")
            }
        }
    }
}
