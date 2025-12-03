package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverviewScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onPaymentMethods: () -> Unit,
    onMyVehicles: () -> Unit,
    onChangePassword: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Estado para controlar cuando recargar
    var needsRefresh by remember { mutableStateOf(true) }

    // Recargar cuando esta pantalla se vuelve activa
    LaunchedEffect(needsRefresh) {
        if (needsRefresh) {
            println("🔄 ProfileOverviewScreen - Recargando datos del perfil")
            viewModel.loadUserProfile(context)
            needsRefresh = false
        }
    }

    // Forzar recarga cuando regresamos de editar
    DisposableEffect(Unit) {
        onDispose {
            // Esta es la clave: cuando salimos de esta pantalla,
            // marcamos que necesitamos recargar la próxima vez
            needsRefresh = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Información de Perfil",
                        color = Blanco,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Solo navegar, no limpiar datos
                        onBack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulPrincipal
                )
            )
        }
    ) { padding ->
        if (isLoading && userProfile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AzulPrincipal)
            }
        } else {
            ProfileContent(
                userProfile = userProfile,
                onEditProfile = {
                    // Solo navegar, NO limpiar datos aquí
                    onEditProfile()
                },
                onPaymentMethods = onPaymentMethods,
                onMyVehicles = onMyVehicles,
                onChangePassword = onChangePassword,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ProfileContent(
    userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?,
    onEditProfile: () -> Unit,
    onPaymentMethods: () -> Unit,
    onMyVehicles: () -> Unit,

    onChangePassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GrisClaro)
    ) {
        // Header con gradiente azul
        ProfileHeader(userProfile = userProfile)

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            ActionCard(
                title = "Cambiar Contraseña",
                icon = Icons.Default.Lock,
                iconBackground = Color(0xFFFFF2E6), // Naranja claro
                iconTint = Color(0xFFFF9800), // Naranja principal
                onClick = onChangePassword
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjetas de acción existentes
            ActionCard(
                title = "Actualizar mis datos",
                icon = Icons.Default.Edit,
                iconBackground = VerdeClaro,
                iconTint = VerdePrincipal,
                onClick = onEditProfile
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionCard(
                title = "Métodos de Pago",
                icon = Icons.Default.CreditCard,
                iconBackground = AzulClaro.copy(alpha = 0.3f),
                iconTint = AzulPrincipal,
                onClick = onPaymentMethods
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs con información
            ProfileTabsSection(
                userProfile = userProfile,
                onMyVehicles = onMyVehicles
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AzulPrincipal, AzulSecundario)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Blanco)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(AzulClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = AzulPrincipal,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = getUserDisplayName(userProfile),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Blanco
            )

            Spacer(modifier = Modifier.height(4.dp))

            userProfile?.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Blanco.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = GrisTexto
            )
            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = GrisTexto.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ProfileTabsSection(
    userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?,
    onMyVehicles: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Mi perfil", "Mis vehículos")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Blanco,
                contentColor = AzulPrincipal,
                divider = {},
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(3.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(VerdePrincipal, VerdeSecundario)
                                ),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (selectedTabIndex == index) AzulPrincipal else GrisTexto
                            )
                        }
                    )
                }
            }

            // Contenido del tab seleccionado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> ProfileInfoTab(userProfile = userProfile)
                    1 -> VehiclesTab(onMyVehicles = onMyVehicles)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoTab(userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?) {
    Column {
        ProfileInfoItem(
            label = "Teléfono",
            value = userProfile?.phone ?: "No especificado"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileInfoItem(
            label = "Dirección",
            value = userProfile?.address ?: "No especificada"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileInfoItem(
            label = "Tipo documento",
            value = formatDocumentType(userProfile?.tipoDocumento) ?: "No especificado"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileInfoItem(
            label = "Número documento",
            value = userProfile?.numeroDocumento ?: "No especificado"
        )
    }
}

@Composable
private fun VehiclesTab(onMyVehicles: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gestión de Vehículos",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = GrisTexto
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Administra y configura tus vehículos registrados",
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onMyVehicles,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal
            )
        ) {
            Text(
                text = "Ver mis vehículos",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GrisTexto.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto,
            fontWeight = FontWeight.Normal
        )
    }
}

// Funciones helper
private fun getUserDisplayName(userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?): String {
    return userProfile?.let { profile ->
        "${profile.firstName} ${profile.lastName}".trim().takeIf { it.isNotEmpty() }
            ?: profile.username
    } ?: "Usuario"
}

private fun formatDocumentType(documentType: String?): String? {
    return when (documentType) {
        "dni" -> "DNI"
        "pasaporte" -> "Pasaporte"
        "carnet_extranjeria" -> "Carnet de Extranjería"
        else -> documentType
    }
}