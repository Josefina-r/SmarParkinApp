// ParkingDetailScreen.kt - VERSIÓN COMPLETAMENTE CORREGIDA
package com.example.smarparkinapp.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.example.smarparkinapp.ui.theme.viewmodel.ReviewViewModelFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.smarparkinapp.ui.theme.NavRoutes
import com.example.smarparkinapp.ui.theme.data.model.ParkingReview
import com.example.smarparkinapp.ui.theme.data.model.ParkingSpot
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.HomeViewModelFactory
import com.example.smarparkinapp.ui.theme.viewmodel.ReviewViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkingDetailScreen(
    navController: NavHostController,
    parkingId: Long
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context.applicationContext)
    )


    val reviewViewModel: ReviewViewModel = viewModel(
        factory = ReviewViewModelFactory(context)
    )

    // Estados
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showAllReviews by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(0f) }
    var userComment by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Observar estados
    val reviewsResponse by reviewViewModel.reviewsResponse.collectAsState()
    val isLoading by reviewViewModel.isLoading.collectAsState()
    val reviewCreated by reviewViewModel.reviewCreated.collectAsState()
    val errorState by reviewViewModel.error.collectAsState()

    // Cargar datos del parking
    val parkingSpots by homeViewModel.filteredParkingSpots.collectAsState()
    val parkingSpot = parkingSpots.find { it.id == parkingId.toInt() }

    // Cargar reseñas REALES cuando se monte el componente
    LaunchedEffect(parkingId) {
        reviewViewModel.loadParkingReviews(parkingId.toInt())
    }

    // Manejar creación exitosa de reseña
    LaunchedEffect(reviewCreated) {
        if (reviewCreated) {
            showReviewDialog = false
            userRating = 0f
            userComment = ""
            reviewViewModel.resetStates()
        }
    }


    // Colores locales (si no están en tu theme)
    val TextBlack = Color(0xFF1A1A1A)
    val TextGray = Color(0xFF666666)
    val BorderGray = Color(0xFFE0E0E0)
    val GrisClaro = Color(0xFFF5F5F5)
    val GrisMedio = Color(0xFF9E9E9E)
    val NaranjaPrincipal = Color(0xFFFF9800)

    if (parkingSpot == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AzulPrincipal)
        }
        LaunchedEffect(Unit) {
            homeViewModel.fetchParkingSpots()
        }
    } else {
        // Obtener reseñas del ViewModel
        val reviews = reviewsResponse?.reviews ?: emptyList()
        val totalReviews = reviewsResponse?.stats?.totalReviews ?: 0

        // Preparar lista de imágenes
        val allImages = buildList<Pair<String, String>> {
            parkingSpot.imagenPrincipal?.let { url ->
                if (url.isNotEmpty()) {
                    add(url to "Imagen principal")
                }
            }

            parkingSpot.imagenes.forEach { image ->
                if (image.imagenUrl.isNotEmpty()) {
                    add(image.imagenUrl to (image.descripcion ?: "Imagen del estacionamiento"))
                }
            }

            if (isEmpty() && parkingSpot.imagenUrl.isNotEmpty()) {
                add(parkingSpot.imagenUrl to "Imagen del estacionamiento")
            }

            if (isEmpty()) {
                add("" to "Sin imagen disponible")
            }
        }

        // Preparar amenidades
        val amenidades = buildList {
            if (parkingSpot.tieneCamaras) add("Cámaras de seguridad")
            if (parkingSpot.tieneVigilancia24h) add("Vigilancia 24h")
            add("Acceso controlado")
            when (parkingSpot.nivelSeguridad) {
                1 -> add("Seguridad básica")
                2 -> add("Seguridad media")
                3 -> add("Seguridad alta")
            }

            if (parkingSpot.servicios.isNotEmpty()) {
                addAll(parkingSpot.servicios.take(3))
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles del Estacionamiento") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AzulPrincipal,
                        titleContentColor = Blanco,
                        navigationIconContentColor = Blanco
                    )
                )
            },
            bottomBar = {
                SimpleReserveBar(
                    parkingSpot = parkingSpot,
                    onReserve = {
                        val route = NavRoutes.VehicleSelection.createRoute(parkingSpot.id.toLong())
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
                // 1. GALERÍA DE IMÁGENES
                if (allImages.isNotEmpty()) {
                    Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(allImages[selectedImageIndex].first)
                                .crossfade(true)
                                .build(),
                            contentDescription = allImages[selectedImageIndex].second,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(GrisClaro),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Image,
                                        contentDescription = "Cargando imagen",
                                        tint = GrisMedio,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(GrisClaro),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Error,
                                            contentDescription = "Error cargando imagen",
                                            tint = GrisMedio,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Text(
                                            "Imagen no disponible",
                                            color = GrisMedio,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        )

                        // Badge de Estado
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (parkingSpot.estaAbierto && parkingSpot.availableSpots > 0) VerdePrincipal
                            else if (!parkingSpot.estaAbierto) Color.Red
                            else NaranjaPrincipal
                        ) {
                            Text(
                                text = when {
                                    !parkingSpot.estaAbierto -> "Cerrado"
                                    parkingSpot.availableSpots <= 0 -> "Lleno"
                                    else -> "Disponible"
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Blanco
                            )
                        }

                        // Indicador de imágenes
                        if (allImages.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                allImages.forEachIndexed { index, _ ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (index == selectedImageIndex)
                                                    Blanco
                                                else
                                                    Blanco.copy(alpha = 0.5f)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // Miniaturas de imágenes
                    if (allImages.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allImages.forEachIndexed { index, (imageUrl, description) ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            2.dp,
                                            if (index == selectedImageIndex) AzulPrincipal else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedImageIndex = index }
                                ) {
                                    SubcomposeAsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Miniatura $description",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(6.dp)),
                                        contentScale = ContentScale.Crop,
                                        loading = {
                                            Box(modifier = Modifier.fillMaxSize().background(GrisClaro))
                                        }
                                    )
                                }
                            }
                        }
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

                    // 3. INFORMACIÓN RÁPIDA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoItem(
                            icon = Icons.Default.AccessTime,
                            title = "Horario",
                            subtitle = parkingSpot.horarioApertura?.let { apertura ->
                                parkingSpot.horarioCierre?.let { cierre ->
                                    "$apertura - $cierre"
                                } ?: "$apertura - 24h"
                            } ?: "24 horas",
                            tint = AzulPrincipal
                        )
                        InfoItem(
                            icon = Icons.Default.Security,
                            title = "Seguridad",
                            subtitle = parkingSpot.nivelSeguridadDesc ?: when (parkingSpot.nivelSeguridad) {
                                1 -> "Básica"
                                2 -> "Media"
                                3 -> "Alta"
                                else -> "Básica"
                            },
                            tint = AzulPrincipal
                        )
                        InfoItem(
                            icon = Icons.Default.LocalParking,
                            title = "Espacios",
                            subtitle = "${parkingSpot.availableSpots} de ${parkingSpot.totalPlazas}",
                            tint = VerdePrincipal
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. DESCRIPCIÓN
                    Text(
                        "Acerca de este estacionamiento",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextBlack
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = parkingSpot.descripcion ?: buildString {
                            append("Estacionamiento ubicado en ${parkingSpot.address}. ")
                            append("Cuenta con ${parkingSpot.availableSpots} espacios disponibles de ${parkingSpot.totalPlazas} totales. ")
                            if (parkingSpot.tieneCamaras) append("Disponible con cámaras de seguridad. ")
                            if (parkingSpot.tieneVigilancia24h) append("Vigilancia 24 horas. ")
                            append("Nivel de seguridad ${parkingSpot.nivelSeguridadDesc ?: when (parkingSpot.nivelSeguridad) {
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
                    AmenidadesGrid(
                        amenidades = amenidades,
                        textGray = TextGray,
                        iconColor = AzulPrincipal
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // ========== SECCIÓN DE RESEÑAS ==========
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Reseñas y Calificaciones",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextBlack
                            )

                            if (!isLoading) {
                                Text(
                                    "${reviewsResponse?.stats?.averageRating?.let { "%.1f".format(it) } ?: "0.0"} ★ • $totalReviews reseñas",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                        }

                        // Botón para agregar reseña
                        Button(
                            onClick = { showReviewDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AzulPrincipal,
                                contentColor = Blanco
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar Reseña", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AzulPrincipal)
                        }
                    } else if (reviews.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Comment,
                                contentDescription = "Sin reseñas",
                                tint = GrisMedio,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay reseñas aún",
                                color = TextGray,
                                fontSize = 14.sp
                            )
                            Text(
                                "¡Sé el primero en opinar!",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        // Lista de reseñas
                        val reviewsToShow = if (showAllReviews) reviews else reviews.take(3)

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.heightIn(max = if (showAllReviews) 400.dp else 200.dp)
                        ) {
                            items(reviewsToShow) { review ->
                                ReviewCard(review = review)
                            }
                        }

                        // Botón para ver más/menos
                        if (reviews.size > 3) {
                            TextButton(
                                onClick = { showAllReviews = !showAllReviews },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (showAllReviews) "Ver menos" else "Ver todas las reseñas (${reviews.size})",
                                    color = AzulPrincipal,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. UBICACIÓN
                    Text("Ubicación", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                    Text(parkingSpot.address, fontSize = 14.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mapa (si tienes las coordenadas)
                    if (parkingSpot.latitude != 0.0 && parkingSpot.longitude != 0.0) {
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
                                ),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Marker(
                                    state = MarkerState(LatLng(parkingSpot.latitude, parkingSpot.longitude)),
                                    title = parkingSpot.name
                                )
                            }
                        }
                    }

                    // Información de contacto
                    Spacer(modifier = Modifier.height(16.dp))
                    parkingSpot.telefono?.let { phone ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Teléfono: $phone", fontSize = 14.sp, color = TextGray)
                        }
                    }

                    // Estado de aprobación
                    if (!parkingSpot.aprobado) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = NaranjaPrincipal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pendiente de aprobación",
                                fontSize = 14.sp,
                                color = NaranjaPrincipal,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // ========== DIÁLOGO PARA AGREGAR RESEÑA ==========
        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = {
                    showReviewDialog = false
                    reviewViewModel.resetStates()
                },
                title = {
                    Text("Calificar Estacionamiento", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        // Mostrar error si hay
                        errorState?.let { error ->
                            Text(
                                error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Rating Stars
                        Text("¿Cómo calificarías este estacionamiento?",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                IconButton(
                                    onClick = { userRating = i.toFloat() },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (i <= userRating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = "$i estrellas",
                                        tint = if (i <= userRating.toInt()) NaranjaPrincipal else GrisMedio,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Text("Tu calificación: $userRating",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier.padding(bottom = 8.dp))

                        Spacer(modifier = Modifier.height(8.dp))

                        // Comentario
                        OutlinedTextField(
                            value = userComment,
                            onValueChange = { userComment = it },
                            label = { Text("Comentario (opcional)") },
                            placeholder = { Text("Comparte tu experiencia...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (userRating > 0) {
                                coroutineScope.launch {
                                    reviewViewModel.createReview(
                                        parkingId.toInt(),
                                        userRating,
                                        userComment
                                    )
                                }
                            }
                        },
                        enabled = userRating > 0 && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulPrincipal,
                            contentColor = Blanco
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Blanco,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Enviar Reseña")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showReviewDialog = false
                            reviewViewModel.resetStates()
                        }
                    ) {
                        Text("Cancelar", color = TextGray)
                    }
                }
            )
        }
    }
}

// Componente para mostrar una reseña
// Componente para mostrar una reseña - VERSIÓN CORREGIDA
@Composable
fun ReviewCard(review: ParkingReview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco,
            contentColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.usuarioNombre.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.usuarioNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= review.calificacion.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (i <= review.calificacion.toInt()) Color(0xFFFF9800) else Color(0xFF9E9E9E),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "%.1f".format(review.calificacion),
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                // Fecha
                Text(
                    text = formatDate(review.fecha),
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comentario
            if (review.comentario.isNotEmpty()) {
                Text(
                    text = review.comentario,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 18.sp
                )
            }

            // Si NO está aprobada (pendiente de moderación)
            if (!review.aprobado) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3CD), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Pendiente",
                        tint = Color(0xFF856404),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Reseña pendiente de aprobación",
                        fontSize = 12.sp,
                        color = Color(0xFF856404)
                    )
                }
            }
        }
    }
}

// Función para formatear fecha
fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun SimpleReserveBar(
    parkingSpot: ParkingSpot,
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
                        text = parkingSpot.price,
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
fun InfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(tint.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center
        )
        Text(
            subtitle,
            fontSize = 11.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AmenidadesGrid(
    amenidades: List<String>,
    textGray: Color,
    iconColor: Color
) {
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