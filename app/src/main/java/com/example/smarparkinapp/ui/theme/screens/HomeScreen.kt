package com.example.smarparkinapp.ui.theme.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.Navigation.NavRoutes
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    onParkingClick: (Int) -> Unit,
    onReservationClick: (parkingName: String, plate: String, duration: Int, total: Double) -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context.applicationContext)
    )

    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberBottomSheetScaffoldState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val parkingSpots by viewModel.filteredParkingSpots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(searchQuery) {
        viewModel.searchParking(searchQuery)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchParkingSpots()
        viewModel.updateUserLocation(-8.111667, -79.028889)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.background(VerdeSecundario)) {
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AzulSecundario
                    )
                )

                Divider(color = AzulSecundario.copy(alpha = 0.3f))

                // Home
                NavigationDrawerItem(
                    label = { Text("Home", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.Home.route)
                    }
                )

                // Perfil
                NavigationDrawerItem(
                    label = { Text("Perfil", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.Perfil.route)
                    }
                )

                // Historial
                NavigationDrawerItem(
                    label = { Text("Historial", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.Historial.route)
                    }
                )

                // Reservar
                NavigationDrawerItem(
                    label = { Text("Reservar", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onReservationClick("Estacionamiento Central", "ABC-123", 1, 5.0)
                    }
                )

                // Notificaciones
                NavigationDrawerItem(
                    label = { Text("Notificaciones", color = AzulSecundario) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(NavRoutes.Notificaciones.route)
                    }
                )
            }
        }
    ) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 250.dp,
            sheetContent = {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar estacionamiento...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // ✅ CORREGIDO: FilterChip con parámetros correctos
                        CustomFilterChip(
                            text = "Precio",
                            color = VerdeSecundario,
                            onClick = { viewModel.fetchMasEconomicos() }
                        )
                        CustomFilterChip(
                            text = "Mejor Rating",
                            color = VerdePrincipal,
                            onClick = { viewModel.fetchMejoresCalificados() }
                        )
                        CustomFilterChip(
                            text = "Alta Seguridad",
                            color = VerdePrincipal,
                            onClick = { viewModel.filterBySecurity(3) }
                        )
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
                        items(parkingSpots) { parking ->
                            // ✅ CORREGIDO: ParkingSpotCard
                            CustomParkingSpotCard(
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
                                },
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
                            state = MarkerState(LatLng(spot.latitude, spot.longitude)),
                            title = spot.name,
                            snippet = buildMarkerSnippet(spot) // ✅ Esta función está abajo
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier.size(56.dp).background(VerdeSecundario, CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                }
            }
        }
    }
}

// ✅ CORREGIDO: FilterChip personalizado
@Composable
fun CustomFilterChip(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ✅ CORREGIDO: ParkingSpotCard personalizado
@Composable
fun CustomParkingSpotCard(
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
            Text(
                text = parkingSpot.name,
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (parkingSpot.ratingPromedio > 0) {
                    StarRating(rating = parkingSpot.ratingPromedio)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${parkingSpot.totalResenas})",
                        color = GrisClaro,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (parkingSpot.nivelSeguridad >= 4) {
                    Text("🔒", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (parkingSpot.tieneCamaras) {
                    Text("📹", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (parkingSpot.tieneVigilancia24h) {
                    Text("🛡️", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!parkingSpot.estaAbierto) {
                    Text(
                        text = "🔴 Cerrado",
                        color = Color.Red,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = parkingSpot.address,
                fontSize = 12.sp,
                color = GrisClaro
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = parkingSpot.price,
                    fontWeight = FontWeight.Bold,
                    color = VerdePrincipal
                )
                Text(
                    text = "${parkingSpot.availableSpots} disponibles",
                    color = VerdeSecundario
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onReserveClick,
                enabled = parkingSpot.availableSpots > 0 && parkingSpot.estaAbierto
            ) {
                Text(
                    text = if (parkingSpot.availableSpots > 0 && parkingSpot.estaAbierto) {
                        "Reservar"
                    } else if (!parkingSpot.estaAbierto) {
                        "Cerrado"
                    } else {
                        "Lleno"
                    }
                )
            }
        }
    }
}

// ✅ FUNCIONES AUXILIARES
private fun buildMarkerSnippet(spot: ParkingSpot): String {
    val baseInfo = "${spot.price} - ${spot.availableSpots} lugares"

    val extraInfo = buildString {
        if (spot.ratingPromedio > 0) {
            append(" ★${"%.1f".format(spot.ratingPromedio)}")
        }
        if (spot.nivelSeguridad >= 4) {
            append(" 🔒")
        }
        if (!spot.estaAbierto) {
            append(" 🔴 Cerrado")
        }
    }

    return if (extraInfo.isNotEmpty()) {
        "$baseInfo • $extraInfo"
    } else {
        baseInfo
    }
}

@Composable
fun StarRating(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color(0xFFCCCCCC),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        onParkingClick = { },
        onReservationClick = { _, _, _, _ -> }
    )
}