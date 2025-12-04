package com.example.smarparkinapp.ui.theme.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.utils.SharedProfilePhotoManager
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.example.smarparkinapp.ui.theme.viewmodel.UserViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.UserViewModelFactory
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
    onReservationClick: (Long) -> Unit
) {
    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context.applicationContext)
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(context)
    )
    var activeFilter by remember { mutableStateOf("Todos") }
    var searchQuery by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val parkingSpots by viewModel.filteredParkingSpots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val currentUser by userViewModel.currentUser.collectAsState()
    val isLoggedIn by userViewModel.isLoggedIn.collectAsState()

    var profilePhotoBitmap by remember { mutableStateOf(SharedProfilePhotoManager.profilePhotoBitmap) }

    val userLatLng = remember { mutableStateOf(LatLng(-8.111667, -79.028889)) }

    var isPanelExpanded by remember { mutableStateOf(false) }
    var selectedDistance by remember { mutableStateOf(5.0) }

    val panelHeight by animateDpAsState(
        targetValue = if (isPanelExpanded) 700.dp else 120.dp,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(Unit) {
        SharedProfilePhotoManager.loadProfilePhoto(context)
        profilePhotoBitmap = SharedProfilePhotoManager.profilePhotoBitmap
    }

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.isOpen) {
            SharedProfilePhotoManager.loadProfilePhoto(context)
            profilePhotoBitmap = SharedProfilePhotoManager.profilePhotoBitmap
        }
    }

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
                modifier = Modifier.width(300.dp),
                drawerContainerColor = Blanco,
                drawerTonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    // CAMBIADO: de AzulPrincipal/AzulSecundario a VerdePrincipal/VerdeSecundario
                                    colors = listOf(VerdePrincipal, VerdeSecundario)
                                )
                            )
                            .padding(vertical = 32.dp, horizontal = 20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Blanco)
                                    .clickable {
                                        if (isLoggedIn) {
                                            navController.navigate("perfil")
                                            scope.launch { drawerState.close() }
                                        }
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (profilePhotoBitmap != null) {
                                    Image(
                                        bitmap = profilePhotoBitmap!!,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            // CAMBIADO: de AzulClaro a VerdeClaro
                                            .background(VerdeClaro),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "Avatar",
                                            // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                            tint = VerdePrincipal,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                if (isLoggedIn) {
                                    currentUser?.let { user ->
                                        val fullName = "${user.first_name ?: ""} ${user.last_name ?: ""}".trim()
                                        if (fullName.isNotEmpty()) fullName else user.username
                                    } ?: "Usuario"
                                } else {
                                    "Usuario Invitado"
                                },
                                color = Blanco,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            if (isLoggedIn) {
                                currentUser?.email?.let { email ->
                                    Text(
                                        email,
                                        color = Blanco.copy(alpha = 0.9f),
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.width(200.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (isLoggedIn) {
                                Button(
                                    onClick = {
                                        navController.navigate("perfil")
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier
                                        .height(36.dp)
                                        .padding(horizontal = 20.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Blanco,
                                        // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                        contentColor = VerdePrincipal
                                    )
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar perfil", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Ver Perfil", fontSize = 13.sp)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        navController.navigate("login") { popUpTo("home") { inclusive = false } }
                                        scope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier
                                        .height(36.dp)
                                        .padding(horizontal = 20.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Blanco,
                                        // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                        contentColor = VerdePrincipal
                                    )
                                ) {
                                    Icon(Icons.Default.Login, contentDescription = "Iniciar sesión", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Iniciar Sesión", fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            "Principal",
                            color = GrisTexto.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )

                        DrawerMenuItem("Inicio", Icons.Default.Home) {
                            navController.navigate("home") { popUpTo("home") { inclusive = false } }
                            scope.launch { drawerState.close() }
                        }

                        if (isLoggedIn) {
                            DrawerMenuItem("Mi Perfil", Icons.Default.Person) {
                                navController.navigate("perfil")
                                scope.launch { drawerState.close() }
                            }
                        }

                        DrawerMenuItem("Reservas", Icons.Default.DateRange) {
                            navController.navigate("myReservations")
                            scope.launch { drawerState.close() }
                        }

                        DrawerMenuItem("Mis Tickets", Icons.Default.ConfirmationNumber) {
                            navController.navigate("tickets")
                            scope.launch { drawerState.close() }
                        }

                        DrawerMenuItem("Mis Vehículos", Icons.Default.DirectionsCar) {
                            navController.navigate("myVehicles")
                            scope.launch { drawerState.close() }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Soporte",
                            color = GrisTexto.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )

                        DrawerMenuItem("Ayuda", Icons.Default.Help) {
                            navController.navigate("help")
                            scope.launch { drawerState.close() }
                        }

                        DrawerMenuItem("Soporte", Icons.Default.ChatBubble) {
                            navController.navigate("chatbot")
                            scope.launch { drawerState.close() }
                        }

                        DrawerMenuItem("Ajustes", Icons.Default.Settings) {
                            navController.navigate(NavRoutes.Settings.route)
                            scope.launch { drawerState.close() }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Cuenta",
                            color = GrisTexto.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )

                        if (isLoggedIn) {
                            DrawerMenuItem("Cerrar Sesión", Icons.Default.Logout) {
                                userViewModel.logout()
                                navController.navigate("login") { popUpTo("home") { inclusive = true } }
                                scope.launch { drawerState.close() }
                            }
                        } else {
                            DrawerMenuItem("Iniciar Sesión", Icons.Default.Login) {
                                navController.navigate("login") { popUpTo("home") { inclusive = false } }
                                scope.launch { drawerState.close() }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(50.dp)
                                .background(color = VerdePrincipal, shape = RoundedCornerShape(12.dp))
                                .clickable {
                                    compartirApp(context)
                                    scope.launch { drawerState.close() }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = Blanco, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compartir la App", color = Blanco, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Versión 1.0.0", color = GrisTexto.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(userLatLng.value, 14f)
            }

            GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
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
                                onParkingClick(spot.id)
                                true
                            }
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VerdePrincipal)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
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
                        modifier = Modifier.size(48.dp).background(Blanco, CircleShape)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }

                    if (isLoggedIn) {
                        Text(
                            "Hola, ${currentUser?.username ?: "Usuario"}",
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng.value, 14f)) },
                        modifier = Modifier.size(48.dp),
                        containerColor = Blanco
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Ubicación actual", tint = VerdePrincipal)
                    }
                }
            }

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
                Column(modifier = Modifier.fillMaxSize()) {
                    if (isPanelExpanded) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).align(Alignment.CenterHorizontally)) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    if (!isPanelExpanded) {
                        Column(
                            modifier = Modifier.fillMaxSize().clickable { isPanelExpanded = true }.padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Mostrar ${parkingSpots.size} cocheras",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                    color = VerdePrincipal,
                                    fontSize = 20.sp
                                )
                            )
                            // CAMBIADO: de AzulPrincipal a VerdePrincipal
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Expandir", tint = VerdePrincipal, modifier = Modifier.size(32.dp))
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Buscar",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                        color = VerdePrincipal,
                                        fontSize = 24.sp
                                    )
                                )
                                IconButton(onClick = { isPanelExpanded = false }, modifier = Modifier.size(32.dp)) {
                                    // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Colapsar", tint = VerdePrincipal)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .height(50.dp)
                                    .background(GrisClaro.copy(alpha = 0.3f), RoundedCornerShape(25.dp))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
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

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                FilterTab(text = "Todos", isSelected = activeFilter == "Todos") {
                                    activeFilter = "Todos"
                                    viewModel.fetchParkingSpots()
                                }
                                FilterTab(text = "Más Económicos", isSelected = activeFilter == "Económicos") {
                                    activeFilter = "Económicos"
                                    viewModel.fetchMasEconomicos()
                                }
                                FilterTab(text = "Mejor Rating", isSelected = activeFilter == "Rating") {
                                    activeFilter = "Rating"
                                    viewModel.fetchMejoresCalificados()
                                }
                                FilterTab(text = "Seguridad Alta", isSelected = activeFilter == "Seguridad") {
                                    activeFilter = "Seguridad"
                                    viewModel.filterBySecurity(4)
                                }
                            }

                            Divider(color = Color.LightGray.copy(alpha = 0.5f))

                            LazyColumn(
                                modifier = Modifier.weight(1f).background(Color(0xFFF8F9FA)).padding(horizontal = 16.dp, vertical = 12.dp),
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
                                        onDetailClick = { onParkingClick(parking.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun FilterTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            // CAMBIADO: de AzulPrincipal a VerdePrincipal
            color = if (isSelected) VerdePrincipal else GrisTexto,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 12.sp
        )
        if (isSelected) {
            // CAMBIADO: de AzulPrincipal a VerdePrincipal
            Box(modifier = Modifier.width(40.dp).height(3.dp).background(VerdePrincipal))
        }
    }
}

@Composable
fun ModernParkingCard(parkingSpot: ParkingSpot, distance: Double, onDetailClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onDetailClick() }.shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = parkingSpot.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.ImageNotSupported)
                )

                Surface(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp), shape = RoundedCornerShape(20.dp), color = Color.White) {
                    Text("Edificio", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Surface(modifier = Modifier.align(Alignment.TopStart).padding(top = 40.dp), shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp), color = Color.White.copy(alpha = 0.9f)) {
                    Text("${parkingSpot.availableSpots} disp.", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, color = Color.Black)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(parkingSpot.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF5555FF), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("%.2f".format(parkingSpot.ratingPromedio), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    }
                }

                Text("Trujillo, Trujillo", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold), modifier = Modifier.padding(vertical = 4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(parkingSpot.address, style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray, fontSize = 14.sp), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Text("%.2f".format(distance) + " km", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(" ${parkingSpot.price} /día", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        }
                    }

                    Button(
                        onClick = onDetailClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5555FF)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Ver cochera", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(
                // CAMBIADO: de AzulPrincipal a VerdePrincipal
                VerdePrincipal.copy(alpha = 0.1f),
                CircleShape
            ).padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null,
                // CAMBIADO: de AzulPrincipal a VerdePrincipal
                tint = VerdePrincipal,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = GrisTexto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

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

private fun compartirApp(context: Context) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Descarga ParkeaYa")
            putExtra(Intent.EXTRA_TEXT, "¡Descarga ParkeaYa! 🚗\n\nLa mejor app para encontrar estacionamiento fácilmente.\n\n• Encuentra cocheras disponibles en tiempo real\n• Reserva desde tu celular\n• Precios competitivos\n• Fácil de usar\n\nDescárgala ahora: [https://tudominio.com/parkea-ya]")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartir ParkeaYa"))
    } catch (e: Exception) {
        println("Error al compartir la app: ${e.message}")
    }
}