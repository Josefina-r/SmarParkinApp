// ParkingDetailScreen.kt - VERSIÓN CON NUEVA PALETA DE COLORES
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.data.model.Car
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModelFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel
@Composable
fun ParkingDetailScreen(
    navController: NavHostController,
    parkingId: Long
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context.applicationContext)
    )


    val parkingSpots by homeViewModel.filteredParkingSpots.collectAsState()
    val parkingSpot = parkingSpots.find { it.id == parkingId.toInt() }

    // Usar colores de tu nueva paleta
    val TextBlack = Color(0xFF1A1A1A)
    val TextGray = GrisTexto
    val BorderGray = GrisMedio

    // Generar amenidades basadas en las características del parking
    val amenidades = buildList {
        if (parkingSpot?.tieneCamaras == true) add("Cámaras de seguridad")
        if (parkingSpot?.tieneVigilancia24h == true) add("Vigilancia 24h")
        add("Acceso controlado")
        when (parkingSpot?.nivelSeguridad ?: 1) {
            1 -> add("Seguridad básica")
            2 -> add("Seguridad media")
            3 -> add("Seguridad alta")
        }
    }

    if (parkingSpot == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AzulPrincipal)
        }
        LaunchedEffect(Unit) {
            homeViewModel.fetchParkingSpots()
        }
    } else {
        Scaffold(
            bottomBar = {
                SimpleReserveBar(
                    parkingSpot = parkingSpot,
                    primaryColor = AzulPrincipal,
                    secondaryColor = VerdePrincipal,
                    onReserve = {
                        println("🔍 [ParkingDetail] Navegando a selección de vehículo para reserva")
                        println("🔍 [ParkingDetail] Parking ID: ${parkingSpot.id}")

                        val route = NavRoutes.VehicleSelection.createRoute(parkingSpot.id.toLong())
                        println("🔍 [ParkingDetail] Ruta completa: $route")
                        navController.navigate(route)
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Blanco)
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
                            .background(Blanco)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextBlack)
                    }

                    // Badge de Estado
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (parkingSpot.estaAbierto) VerdePrincipal else Color.Red
                    ) {
                        Text(
                            text = if (parkingSpot.estaAbierto) "Abierto" else "Cerrado",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Blanco
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
                                text = parkingSpot.address,
                                fontSize = 14.sp,
                                color = TextGray,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Rating Box
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "%.1f".format(parkingSpot.ratingPromedio),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextBlack
                                )
                            }
                            Text(
                                text = "(${parkingSpot.totalResenas} reseñas)",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. INFORMACIÓN RÁPIDA - CORREGIDO (SOLO PROPIEDADES DE PARKINGSPOT)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            Icons.Default.AccessTime,
                            "Horario",
                            // ✅ USAR SOLO PROPIEDADES DE PARKINGSPOT
                            if (parkingSpot.estaAbierto) "07:00 - 23:00" else "Cerrado",
                            AzulPrincipal
                        )
                        InfoItem(
                            Icons.Default.Security,
                            "Seguridad",
                            when (parkingSpot.nivelSeguridad) {
                                1 -> "Básica"
                                2 -> "Media"
                                3 -> "Alta"
                                else -> "Básica"
                            },
                            AzulPrincipal
                        )
                        InfoItem(
                            Icons.Default.LocalParking,
                            "Espacios",
                            "${parkingSpot.availableSpots} libres",
                            VerdePrincipal
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. DESCRIPCIÓN
                    Text("Acerca de este estacionamiento", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildString {
                            append("Estacionamiento ubicado en ${parkingSpot.address}. ")
                            append("Cuenta con ${parkingSpot.availableSpots} espacios disponibles. ")
                            if (parkingSpot.tieneCamaras) append("Disponible con cámaras de seguridad. ")
                            if (parkingSpot.tieneVigilancia24h) append("Vigilancia 24 horas. ")
                            append("Nivel de seguridad ${when (parkingSpot.nivelSeguridad) {
                                1 -> "básico"
                                2 -> "medio"
                                3 -> "alto"
                                else -> "básico"
                            }}.")
                        },
                        fontSize = 14.sp,
                        color = TextGray,
                        lineHeight = 20.sp
                    )

                    // Amenidades
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Servicios:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
                    Spacer(modifier = Modifier.height(8.dp))
                    AmenidadesGrid(amenidades = amenidades, textGray = TextGray, iconColor = AzulPrincipal)

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. UBICACIÓN
                    Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                    Text(parkingSpot.address, fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
                    ) {
                        GoogleMap(
                            cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(
                                    LatLng(parkingSpot.latitude, parkingSpot.longitude), 15f
                                )
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
                    }

                    // Información de precio - CORREGIDO (INTERPOLACIÓN DE STRING)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Precio:  ${parkingSpot.price} por hora", fontSize = 14.sp, color = TextGray) // ✅ CORREGIDO
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}


@Composable
fun SimpleReserveBar(
    parkingSpot: ParkingSpot,
    primaryColor: Color,
    secondaryColor: Color,
    onReserve: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Blanco,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            // Sección de precio y reserva simplificada
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
                        color = AzulPrincipal
                    )
                    Text(
                        text = "por hora",
                        fontSize = 12.sp,
                        color = AzulSecundario
                    )
                }

                Button(
                    onClick = onReserve,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeSecundario),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .width(180.dp),
                    enabled = parkingSpot.availableSpots > 0 && parkingSpot.estaAbierto
                ) {
                    Text(
                        text = when {
                            !parkingSpot.estaAbierto -> "Cerrado"
                            parkingSpot.availableSpots <= 0 -> "Sin espacios"
                            else -> "Reservar ahora"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Blanco
                    )
                }
            }

            // Indicador de estado
            Text(
                text = when {
                    !parkingSpot.estaAbierto -> "Estacionamiento cerrado"
                    parkingSpot.availableSpots <= 0 -> "Sin espacios disponibles"
                    else -> "${parkingSpot.availableSpots} espacios disponibles"
                },
                fontSize = 12.sp,
                color = when {
                    !parkingSpot.estaAbierto -> Color.Red
                    parkingSpot.availableSpots <= 0 -> Color.Red
                    else -> VerdePrincipal
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
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
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Black)
        Text(subtitle, fontSize = 11.sp, color = Gray)
    }
}

@Composable
fun AmenidadesGrid(amenidades: List<String>, textGray: Color, iconColor: Color) {
    Column {
        amenidades.forEach { amenidad ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(amenidad, fontSize = 14.sp, color = textGray)
            }
        }
    }
}