package com.example.smarparkinapp.ui.theme.screens

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlin.math.*

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
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val parkingSpots by viewModel.filteredParkingSpots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Ubicación del usuario
    val userLatLng = remember { mutableStateOf(LatLng(-8.111667, -79.028889)) }

    // Estados para el panel
    var isPanelExpanded by remember { mutableStateOf(false) }
    var selectedDistance by remember { mutableStateOf(5.0) }
    var selectedParkingSpot by remember { mutableStateOf<ParkingSpot?>(null) }

    // Animación para la altura del panel
    val panelHeight by animateDpAsState(
        targetValue = if (isPanelExpanded) 700.dp else 120.dp, // Panel más alto para ver mejor las cards
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(searchQuery) {
        viewModel.searchParking(searchQuery)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchParkingSpots()
        viewModel.updateUserLocation(userLatLng.value.latitude, userLatLng.value.longitude)
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)
                    .background(Blanco)
            ) {
                // ENCABEZADO DEL DRAWER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulPrincipal)
                        .padding(20.dp)
                ) {
                    Text(
                        "Hola",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Blanco,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Usuario", // Nombre estático o del user
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Blanco,
                            fontSize = 18.sp
                        )
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerMenuItem("Perfil", onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(NavRoutes.Perfil.route)
                })
                DrawerMenuItem("Historial", onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(NavRoutes.Historial.route)
                })
                DrawerMenuItem("Configuración", onClick = {})
                DrawerMenuItem("Cerrar Sesión", onClick = {})
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLatLng.value, 14f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(userLatLng.value),
                    title = "Tu ubicación",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )

                parkingSpots.forEach { spot ->
                    val distance = calculateDistance(userLatLng.value, LatLng(spot.latitude, spot.longitude))
                    if (distance <= selectedDistance) {
                        Marker(
                            state = MarkerState(LatLng(spot.latitude, spot.longitude)),
                            title = spot.name,
                            snippet = buildMarkerSnippet(spot, distance),
                            icon = BitmapDescriptorFactory.defaultMarker(if (spot.estaAbierto) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED),
                            onClick = {
                                selectedParkingSpot = spot
                                true
                            }
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VerdePrincipal)
                }
            }

            // HEADER SUPERIOR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Blanco, CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }

                    FloatingActionButton(
                        onClick = {
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(userLatLng.value, 14f)
                            )
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = Blanco
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "Ubicación actual",
                            tint = VerdePrincipal
                        )
                    }
                }
            }

            // PANEL INFERIOR DESLIZANTE
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(panelHeight)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(
                            topStart = if (isPanelExpanded) 24.dp else 0.dp,
                            topEnd = if (isPanelExpanded) 24.dp else 0.dp
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Indicador de arrastre visual
                    if (isPanelExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(
                                        Color.Gray.copy(alpha = 0.3f),
                                        RoundedCornerShape(2.dp)
                                    )
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    if (!isPanelExpanded) {
                        // VISTA COLAPSADA (Solo título)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { isPanelExpanded = true }
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Mostrar ${parkingSpots.size} cocheras",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AzulPrincipal,
                                    fontSize = 20.sp
                                )
                            )
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "Expandir",
                                tint = AzulPrincipal,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else {
                        // VISTA EXPANDIDA (Lista completa)
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Header del panel expandido
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Buscar",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = AzulPrincipal,
                                        fontSize = 24.sp
                                    )
                                )
                                IconButton(
                                    onClick = { isPanelExpanded = false },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Colapsar",
                                        tint = AzulPrincipal
                                    )
                                }
                            }

                            // Buscador
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(50.dp)
                                    .background(
                                        GrisClaro.copy(alpha = 0.3f),
                                        RoundedCornerShape(25.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = { Text("Buscar...") },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                            }

                            // Filtros tipo Tabs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TabText("Todos", true)
                                TabText("Edificio", false)
                                TabText("Casa", false)
                                TabText("Playa", false)
                            }

                            Divider(color = Color.LightGray.copy(alpha = 0.5f))

                            // LISTA DE ESTACIONAMIENTOS (Nuevo Diseño)
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xFFF8F9FA)) // Fondo gris muy claro
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                contentPadding = PaddingValues(bottom = 20.dp)
                            ) {
                                items(parkingSpots.filter { spot ->
                                    val distance = calculateDistance(userLatLng.value, LatLng(spot.latitude, spot.longitude))
                                    distance <= selectedDistance
                                }) { parking ->
                                    ModernParkingCard(
                                        parkingSpot = parking,
                                        distance = calculateDistance(userLatLng.value, LatLng(parking.latitude, parking.longitude)),
                                        // ✅ CORREGIDO: Solo usar onDetailClick para navegar al detalle
                                        onDetailClick = {
                                            onParkingClick(parking.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// COMPONENTE: Tarjeta estilo Airbnb (Diseño solicitado) - CORREGIDO
@Composable
fun ModernParkingCard(
    parkingSpot: ParkingSpot,
    distance: Double,
    onDetailClick: () -> Unit // ✅ CORREGIDO: Solo un parámetro para navegación
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailClick() }
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // 1. ZONA DE IMAGEN (Grande)
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = parkingSpot.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    // Placeholder en caso de error de imagen
                    error = rememberVectorPainter(Icons.Default.ImageNotSupported)
                )

                // Etiqueta Superior Derecha
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "Edificio", // Puedes personalizar esto según el tipo real
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Etiqueta Inferior Izquierda (ID/Info)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 40.dp),
                    shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = "${parkingSpot.availableSpots} disp.",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }

            // 2. ZONA DE DATOS
            Column(modifier = Modifier.padding(16.dp)) {

                // Nombre y Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = parkingSpot.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF5555FF),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.2f".format(parkingSpot.ratingPromedio),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }

                // Ciudad
                Text(
                    text = "Trujillo, Trujillo",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Dirección y Distancia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = parkingSpot.address,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${"%.2f".format(distance)} km",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // 3. PRECIOS Y BOTÓN DE ACCIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Precio Auto
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = " ${parkingSpot.price} /día",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }

                    // Botón Ver Cochera
                    Button(
                        onClick = onDetailClick, // ✅ CORREGIDO: Usar onDetailClick aquí también
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5555FF) // Azul exacto
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Ver cochera", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// COMPONENTE: Texto Tab
@Composable
fun TabText(text: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color(0xFF0055FF) else Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(Color(0xFF0055FF))
            )
        }
    }
}

// COMPONENTE: Item del Drawer
@Composable
fun DrawerMenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AzulPrincipal,
                fontSize = 16.sp
            )
        )
    }
}

// FUNCIONES AUXILIARES
private fun calculateDistance(start: LatLng, end: LatLng): Double {
    val earthRadius = 6371.0
    val dLat = Math.toRadians(end.latitude - start.latitude)
    val dLng = Math.toRadians(end.longitude - start.longitude)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) * sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

private fun buildMarkerSnippet(spot: ParkingSpot, distance: Double): String {
    return "${spot.price} - ${spot.availableSpots} disp. - ${"%.1f".format(distance)} km"
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