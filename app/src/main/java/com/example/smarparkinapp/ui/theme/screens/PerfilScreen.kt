package com.example.smarparkinapp.ui.theme.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavHostController? = null,
    onCerrarSesion: () -> Unit = {}
) {
    // Estados para los menús
    var showLogoutDialog by remember { mutableStateOf(false) }
    var topBarMenuExpanded by remember { mutableStateOf(false) }
    var selectedVehicleIndex by remember { mutableStateOf(-1) }

    // Estado para la imagen de perfil
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para seleccionar imagen de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileImageUri = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Mi Perfil",
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Gestiona tu información personal",
                            color = Blanco.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Blanco,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    // Menú de opciones en TopBar
                    Box {
                        IconButton(onClick = { topBarMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = Blanco,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = topBarMenuExpanded,
                            onDismissRequest = { topBarMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Editar perfil", fontWeight = FontWeight.Medium)
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    // Navegar a edición de perfil
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Configuración", fontWeight = FontWeight.Medium)
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    // Navegar a configuración
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Ayuda y Soporte", fontWeight = FontWeight.Medium)
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    // Navegar a ayuda
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Help, contentDescription = null)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(GrisClaro, Color.White)
                    )
                )
                .padding(padding)
        ) {
            // Header con foto de perfil
            ProfileHeader(
                profileImageUri = profileImageUri,
                onProfileImageClick = { galleryLauncher.launch("image/*") }
            )

            // Contenido principal con scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información personal
                ProfileSection(
                    title = "Información Personal",
                    icon = Icons.Default.Person,
                    items = listOf(
                        ProfileItem("Correo electrónico", "juan.perez@email.com"),
                        ProfileItem("Teléfono", "+52 55 1234 5678"),
                        ProfileItem("Fecha de registro", "15/03/2024")
                    )
                )

                // Vehículos registrados
                ProfileSection(
                    title = "Vehículos Registrados",
                    icon = Icons.Default.DirectionsCar,
                    items = listOf(
                        ProfileItem("Toyota Corolla 2020", "Placa: ABC-123", true),
                        ProfileItem("", "", false, isAction = true, isAddButton = true) // Solo icono +
                    ),
                    onVehicleLongPress = { index -> selectedVehicleIndex = index }
                )

                // Métodos de pago
                ProfileSection(
                    title = "Métodos de Pago",
                    icon = Icons.Default.CreditCard,
                    items = listOf(
                        ProfileItem("Visa terminada en 1234", "Principal", false),
                        ProfileItem("", "", false, isAction = true, isAddButton = true) // Solo icono +
                    )
                )


                // Botones de acción
                ActionButtons(
                    onCerrarSesion = { showLogoutDialog = true },
                    onEditarPerfil = { /* Navegar a edición */ },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Menú contextual
        VehicleContextMenu(
            selectedIndex = selectedVehicleIndex,
            onDismiss = { selectedVehicleIndex = -1 },
            onEditar = {
                selectedVehicleIndex = -1

            },
            onEliminar = {
                selectedVehicleIndex = -1

            }
        )


        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    showLogoutDialog = false
                    onCerrarSesion()
                },
                onDismiss = { showLogoutDialog = false }
            )
        }
    }
}

@Composable
fun ProfileHeader(
    profileImageUri: Uri?,
    onProfileImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(AzulPrincipal, AzulPrincipal.copy(alpha = 0.9f))
                )
            )
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Foto de perfil
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape),
                color = VerdeSecundario,
                border = BorderStroke(2.dp, Blanco),
                onClick = onProfileImageClick
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = Blanco,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Badge para cambiar foto - SIN SOMBRA
            Surface(
                modifier = Modifier
                    .size(24.dp),
                shape = CircleShape,
                color = VerdePrincipal,
                onClick = onProfileImageClick
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint = Blanco,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Información del usuario
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Juan Carlos Pérez García",
                style = MaterialTheme.typography.titleMedium,
                color = Blanco,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ProfileSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<ProfileItem>,
    onVehicleLongPress: (Int) -> Unit = { _ -> }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                clip = true
            ),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            // Header de la sección
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal
                )
            }


            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEachIndexed { index, item ->
                    ProfileItemRow(
                        item = item,
                        onLongPress = {
                            if (item.isVehicle) onVehicleLongPress(index)
                        }
                    )
                    if (index < items.lastIndex) {
                        Divider(color = GrisClaro.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItemRow(
    item: ProfileItem,
    onLongPress: () -> Unit = {}
) {
    val backgroundColor = if (item.isAction) AzulPrincipal.copy(alpha = 0.1f) else Color.Transparent
    val textColor = if (item.isAction) AzulPrincipal else Color.Gray

    val clickModifier = when {
        item.isAction -> {
            Modifier.clickable {
                // Acción para "Agregar nuevo vehículo" o "Agregar método de pago"
            }
        }
        item.isVehicle -> {
            Modifier.combinedClickable(
                onClick = { /* No hacer nada en click normal */ },
                onLongClick = onLongPress
            )
        }
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .then(clickModifier)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        if (item.isAddButton) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar",
                    tint = AzulPrincipal,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Column {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (item.isAction) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (item.isAction) AzulPrincipal else Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        item.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onCerrarSesion: () -> Unit,
    onEditarPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Editar perfil
        OutlinedButton(
            onClick = onEditarPerfil,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, AzulPrincipal),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AzulPrincipal
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar perfil",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Editar Perfil", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
        }


        Button(
            onClick = onCerrarSesion,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B6C),
                contentColor = Blanco
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Cerrar sesión",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun VehicleContextMenu(
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    if (selectedIndex != -1) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    "Opciones del Vehículo",
                    fontWeight = FontWeight.Bold,
                    color = AzulPrincipal
                )
            },
            text = {
                Text("¿Qué acción deseas realizar con este vehículo?")
            },
            confirmButton = {
                TextButton(onClick = onEditar) {
                    Text("Editar", fontWeight = FontWeight.Medium)
                }
                TextButton(onClick = onEliminar) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Advertencia",
                tint = Color(0xFFFF6B6B)
            )
        },
        title = {
            Text(
                "Cerrar Sesión",
                fontWeight = FontWeight.Bold,
                color = AzulPrincipal
            )
        },
        text = {
            Text("¿Estás seguro de que quieres cerrar sesión? Tendrás que volver a iniciar sesión para usar la aplicación.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Sí, cerrar sesión", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontWeight = FontWeight.Medium)
            }
        }
    )
}

data class ProfileItem(
    val title: String,
    val subtitle: String = "",
    val isVehicle: Boolean = false,
    val isAction: Boolean = false,
    val isAddButton: Boolean = false
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen()
}