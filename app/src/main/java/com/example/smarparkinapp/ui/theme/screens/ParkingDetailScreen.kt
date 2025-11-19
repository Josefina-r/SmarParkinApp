package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.smarparkinapp.ui.theme.Navigation.NavRoutes
import com.example.smarparkinapp.data.model.Car
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun ParkingDetailScreen(
    navController: NavHostController,
    parkingId: Int
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context.applicationContext)
    )

    // Estado para el vehículo seleccionado
    var selectedVehicle by remember { mutableStateOf<Car?>(null) }

    // Escuchar cuando regrese con un vehículo seleccionado
    val returnedVehicle by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Car?>("selectedVehicle", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    // Cuando se selecciona un vehículo
    LaunchedEffect(returnedVehicle) {
        returnedVehicle?.let { vehicle ->
            selectedVehicle = vehicle
            // Limpiar el estado para futuras selecciones
            navController.currentBackStackEntry?.savedStateHandle?.remove<Car>("selectedVehicle")
        }
    }

    val parkingSpots by viewModel.filteredParkingSpots.collectAsState()
    val parkingSpot = parkingSpots.find { it.id == parkingId }

    // Colores de tu diseño
    val BluePrimary = Color(0xFF5555FF)
    val TextBlack = Color(0xFF1A1A1A)
    val TextGray = Color(0xFF757575)
    val BgGray = Color(0xFFF5F5F5)

    if (parkingSpot == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
        LaunchedEffect(Unit) { viewModel.fetchParkingSpots() }
    } else {
        Scaffold(
            bottomBar = {
                // BARRA INFERIOR ACTUALIZADA con información del vehículo seleccionado
                BottomReserveBar(
                    parkingSpot = parkingSpot,
                    selectedVehicle = selectedVehicle,
                    primaryColor = BluePrimary,
                    onSelectVehicle = {
                        // Navegar a la selección de vehículo
                        navController.navigate(NavRoutes.VehicleSelection.route)
                    },
                    onReserve = {
                        // Navegar a la pantalla de reserva con el vehículo seleccionado
                        if (selectedVehicle != null) {
                            navController.navigate(
                                NavRoutes.Reservation.createRoute(
                                    parkingSpot.name,
                                    selectedVehicle!!.plate,
                                    1, // 1 hora por defecto
                                    parkingSpot.price.toDoubleOrNull() ?: 0.0
                                )
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color.White)
            ) {
                // 1. IMAGEN PRINCIPAL Y BOTÓN ATRÁS
                Box(modifier = Modifier.height(250.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = parkingSpot.imagenUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Botón Atrás flotante
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color.White)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextBlack)
                    }

                    // Badge de Tipo (Ej. Edificio)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "Edificio",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    // 2. TÍTULO Y RATING
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = parkingSpot.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Text(
                                text = "Trujillo, Trujillo",
                                fontSize = 14.sp,
                                color = TextGray,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Rating Box
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "%.2f".format(parkingSpot.ratingPromedio),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = "(42 reseñas)",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. INFO BÁSICA (Horario, Seguridad)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(Icons.Default.AccessTime, "Horario", "07:00 - 23:00", BluePrimary)
                        InfoItem(Icons.Default.Security, "Seguridad", "Cámaras 24h", BluePrimary)
                        InfoItem(Icons.Default.LocalParking, "Espacios", "${parkingSpot.availableSpots} libres", BluePrimary)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BgGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. VEHÍCULO SELECCIONADO (Nueva sección)
                    if (selectedVehicle != null) {
                        SelectedVehicleSection(
                            vehicle = selectedVehicle!!,
                            onChangeVehicle = {
                                // Navegar a selección de vehículo para cambiar
                                navController.navigate(NavRoutes.VehicleSelection.route)
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = BgGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 5. DESCRIPCIÓN
                    Text("Acerca de este estacionamiento", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Estacionamiento seguro ubicado en edificio residencial. Cuenta con vigilancia privada las 24 horas, acceso mediante control remoto y cámaras de seguridad. Ideal para dejar tu auto por horas o días completos.",
                        fontSize = 14.sp,
                        color = TextGray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BgGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 6. UBICACIÓN (Pequeño mapa estático)
                    Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                    Text(parkingSpot.address, fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BgGray, RoundedCornerShape(12.dp))
                    ) {
                        GoogleMap(
                            cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(LatLng(parkingSpot.latitude, parkingSpot.longitude), 15f)
                            },
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = false,
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false
                            )
                        ) {
                            Marker(
                                state = MarkerState(LatLng(parkingSpot.latitude, parkingSpot.longitude)),
                                title = parkingSpot.name
                            )
                        }
                        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent))
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ACTUALIZADOS ---

@Composable
fun BottomReserveBar(
    parkingSpot: ParkingSpot,
    selectedVehicle: Car?,
    primaryColor: Color,
    onSelectVehicle: () -> Unit,
    onReserve: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            // Sección de selección de vehículo
            if (selectedVehicle == null) {
                // Si no hay vehículo seleccionado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Seleccionar vehículo",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onSelectVehicle,
                        colors = ButtonDefaults.textButtonColors(contentColor = primaryColor)
                    ) {
                        Text("Seleccionar")
                    }
                }
            } else {
                // Si hay vehículo seleccionado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (selectedVehicle.type == com.example.smarparkinapp.data.model.VehicleType.AUTOMOVIL) {
                            Icons.Default.DirectionsCar
                        } else {
                            Icons.Default.TwoWheeler
                        },
                        contentDescription = null,
                        tint = primaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${selectedVehicle.brand} ${selectedVehicle.model}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = selectedVehicle.plate,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    TextButton(
                        onClick = onSelectVehicle,
                        colors = ButtonDefaults.textButtonColors(contentColor = primaryColor)
                    ) {
                        Text("Cambiar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Sección de precio y reserva
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = " ${parkingSpot.price}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "por día",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = onReserve,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .width(180.dp),
                    enabled = selectedVehicle != null // Solo habilitado si hay vehículo seleccionado
                ) {
                    Text(
                        text = if (selectedVehicle != null) "Reservar ahora" else "Selecciona un vehículo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SelectedVehicleSection(vehicle: Car, onChangeVehicle: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Vehículo seleccionado",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            TextButton(onClick = onChangeVehicle) {
                Text("Cambiar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tarjeta del vehículo seleccionado
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (vehicle.type == com.example.smarparkinapp.data.model.VehicleType.AUTOMOVIL) {
                        Icons.Default.DirectionsCar
                    } else {
                        Icons.Default.TwoWheeler
                    },
                    contentDescription = null,
                    tint = Color(0xFF5555FF),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehicle.brand,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = vehicle.plate,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${vehicle.model} • ${vehicle.color}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(tint.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(subtitle, fontSize = 11.sp, color = Color.Gray)
    }
}