package com.example.smarparkinapp.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.ProfileViewModel
import com.example.smarparkinapp.ui.theme.utils.SharedProfilePhotoManager
import kotlinx.coroutines.launch

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
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val scope = rememberCoroutineScope()

    var profileImageBitmap by remember {
        mutableStateOf(SharedProfilePhotoManager.profilePhotoBitmap)
    }
    var showImageMenu by remember { mutableStateOf(false) }

    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasStoragePermission = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?:
                permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: hasStoragePermission
    }

    LaunchedEffect(Unit) {
        SharedProfilePhotoManager.loadProfilePhoto(context)
        profileImageBitmap = SharedProfilePhotoManager.profilePhotoBitmap
        viewModel.forceReloadProfile(context)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                val success = SharedProfilePhotoManager.saveProfilePhoto(context, it)
                if (success) {
                    profileImageBitmap = SharedProfilePhotoManager.profilePhotoBitmap
                }
            }
            showImageMenu = false
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            viewModel.forceReloadProfile(context)
            viewModel.resetUpdateSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mi Perfil", color = Blanco, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Blanco)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = VerdePrincipal // CAMBIADO: de AzulPrincipal a VerdePrincipal
                )
            )
        }
    ) { padding ->
        if (isLoading && userProfile == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VerdePrincipal) // CAMBIADO: de AzulPrincipal a VerdePrincipal
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GrisClaro)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeader(
                    userProfile = userProfile,
                    profileImageBitmap = profileImageBitmap,
                    showImageMenu = showImageMenu,
                    onImageClick = { showImageMenu = true },
                    onDismissMenu = { showImageMenu = false },
                    onChooseFromGallery = {
                        if (hasStoragePermission) {
                            galleryLauncher.launch("image/*")
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_MEDIA_IMAGES
                                )
                            )
                        }
                    },
                    onRemovePhoto = {
                        scope.launch {
                            val success = SharedProfilePhotoManager.deleteProfilePhoto(context)
                            if (success) {
                                profileImageBitmap = null
                            }
                        }
                        showImageMenu = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    ActionCard(
                        title = "Cambiar Contraseña",
                        icon = Icons.Default.Lock,
                        iconBackground = Color(0xFFFFF2E6),
                        iconTint = Color(0xFFFF9800),
                        onClick = onChangePassword
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(12.dp))

                    ActionCard(
                        title = "Mis Vehículos",
                        icon = Icons.Default.DirectionsCar,
                        iconBackground = Color(0xFFE8F5E8),
                        iconTint = Color(0xFF4CAF50),
                        onClick = onMyVehicles
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // REMOVIDO: ProfileTabsSection que contenía la pestaña duplicada de Vehículos
                    // En su lugar, mostramos solo la información del perfil
                    ProfileInfoSection(userProfile = userProfile)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?,
    profileImageBitmap: ImageBitmap?,
    showImageMenu: Boolean,
    onImageClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.verticalGradient(
                    // CAMBIADO: de AzulPrincipal/AzulSecundario a VerdePrincipal/VerdeSecundario
                    colors = listOf(VerdePrincipal, VerdeSecundario)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(120.dp).clickable { onImageClick() }
            ) {
                if (profileImageBitmap != null) {
                    Image(
                        bitmap = profileImageBitmap,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(120.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(120.dp).clip(CircleShape).background(Blanco),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(VerdeClaro), // CAMBIADO: de AzulClaro a VerdeClaro
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = VerdePrincipal, // CAMBIADO: de AzulPrincipal a VerdePrincipal
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(VerdePrincipal)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Blanco,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = getUserDisplayName(userProfile),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Blanco
            )

            Spacer(modifier = Modifier.height(4.dp))

            userProfile?.email?.let { email ->
                Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Blanco.copy(alpha = 0.9f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                Text(text = "Activo", style = MaterialTheme.typography.bodySmall, color = Blanco.copy(alpha = 0.8f))
            }
        }

        if (showImageMenu) {
            DropdownMenu(
                expanded = showImageMenu,
                onDismissRequest = onDismissMenu,
                modifier = Modifier.background(Blanco)
            ) {
                DropdownMenuItem(
                    text = { Text("Elegir de galería") },
                    onClick = onChooseFromGallery,
                    leadingIcon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) }
                )
                if (profileImageBitmap != null) {
                    DropdownMenuItem(
                        text = { Text("Eliminar foto") },
                        onClick = onRemovePhoto,
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                    )
                }
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
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = GrisTexto
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GrisTexto.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
        }
    }
}

// NUEVO: Sección de información del perfil sin tabs
@Composable
private fun ProfileInfoSection(
    userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Información Personal",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = VerdePrincipal, // CAMBIADO: de AzulPrincipal a VerdePrincipal
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ProfileInfoItem(
                label = "Teléfono",
                value = userProfile?.phone ?: "No especificado",
                icon = Icons.Default.Phone
            )
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoItem(
                label = "Dirección",
                value = userProfile?.address ?: "No especificada",
                icon = Icons.Default.LocationOn
            )
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoItem(
                label = "Tipo documento",
                value = formatDocumentType(userProfile?.tipoDocumento) ?: "No especificado",
                icon = Icons.Default.Badge
            )
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoItem(
                label = "Número documento",
                value = userProfile?.numeroDocumento ?: "No especificado",
                icon = Icons.Default.Numbers
            )
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoItem(
                label = "Fecha de nacimiento",
                value = userProfile?.fechaNacimiento ?: "No especificada",
                icon = Icons.Default.Cake
            )
            Spacer(modifier = Modifier.height(12.dp))

            ProfileInfoItem(
                label = "País",
                value = userProfile?.pais ?: "Perú",
                icon = Icons.Default.Flag
            )
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = VerdePrincipal.copy(alpha = 0.7f), // CAMBIADO: de AzulPrincipal a VerdePrincipal
                modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = GrisTexto.copy(alpha = 0.7f), fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = GrisTexto, fontWeight = FontWeight.Normal)
        }
    }
}

private fun getUserDisplayName(userProfile: com.example.smarparkinapp.ui.theme.data.model.UserProfile?): String {
    return userProfile?.let { profile ->
        val fullName = "${profile.firstName} ${profile.lastName}".trim()
        if (fullName.isNotEmpty()) fullName else profile.username
    } ?: "Usuario"
}

private fun formatDocumentType(documentType: String?): String? {
    return when (documentType?.lowercase()) {
        "dni" -> "DNI"
        "pasaporte" -> "Pasaporte"
        "carnet_extranjeria" -> "Carnet de Extranjería"
        else -> documentType
    }
}