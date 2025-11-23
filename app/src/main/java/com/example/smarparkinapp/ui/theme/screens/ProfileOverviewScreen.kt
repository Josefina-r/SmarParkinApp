package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.R
import com.example.smarparkinapp.ui.theme.viewmodel.ProfileViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.smarparkinapp.ui.theme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverviewScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onPaymentMethods: () -> Unit,
    onMyVehicles: () -> Unit,
    onMiPerfilTab: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var hasLoaded by remember { mutableStateOf(false) }

    // Cargar el perfil solo una vez
    LaunchedEffect(Unit) {
        if (!hasLoaded && userProfile == null) {
            viewModel.loadUserProfile(context)
            hasLoaded = true
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
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.Person,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GrisClaro)
                .padding(padding)
        ) {
            // Header con gradiente azul
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
                    // Avatar con borde blanco
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
                        text = userProfile?.firstName?.let { fn ->
                            "${fn} ${userProfile?.lastName ?: ""}".trim()
                        } ?: (userProfile?.username ?: "Usuario"),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Tarjetas de acción
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditProfile() },
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
                                .background(VerdeClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = VerdePrincipal,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Actualizar mis datos",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = GrisTexto
                        )
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = AzulPrincipal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPaymentMethods() },
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
                                .background(AzulClaro.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Métodos de Pago",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = GrisTexto
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tabs con diseño mejorado
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
                                    onClick = {
                                        selectedTabIndex = index
                                        if (index == 0) onMiPerfilTab()
                                        else onMyVehicles()
                                    },
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
                                0 -> {
                                    // Contenido "Mi perfil"
                                    Column {
                                        ProfileInfoItem(
                                            label = "Teléfono",
                                            value = userProfile?.phone ?: "-"
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        ProfileInfoItem(
                                            label = "Dirección",
                                            value = userProfile?.address ?: "-"
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        ProfileInfoItem(
                                            label = "Tipo documento",
                                            value = userProfile?.tipoDocumento ?: "-"
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        ProfileInfoItem(
                                            label = "Número documento",
                                            value = userProfile?.numeroDocumento ?: "-"
                                        )
                                    }
                                }
                                1 -> {
                                    // Contenido "Mis vehículos"
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
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = onMyVehicles,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
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
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
