package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.data.model.CarResponse
import com.example.smarparkinapp.ui.theme.data.model.UserProfile
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavHostController? = null,
    onCerrarSesion: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAddVehicleDialog by remember { mutableStateOf(false) }

    // ✅ Estados del ViewModel
    val userState = viewModel.userState.value
    val vehiclesState = viewModel.vehiclesState.value

    // ✅ Cargar datos al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }

    // ✅ Mostrar errores del perfil
    LaunchedEffect(userState.error) {
        userState.error?.let { error ->
            println("Error en perfil: $error")
        }
    }

    // ✅ Mostrar errores de vehículos
    LaunchedEffect(vehiclesState.error) {
        vehiclesState.error?.let { error ->
            println("Error en vehículos: $error")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            if (userState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulPrincipal)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del usuario
                    item { UserInfoHeader(user = userState.user) }
                    item { PersonalInfoSection(user = userState.user) }

                    // Sección vehículos
                    item {
                        VehiclesSection(
                            vehicles = vehiclesState.vehicles,
                            isLoading = vehiclesState.isLoading,
                            error = vehiclesState.error,
                            onAddVehicle = { showAddVehicleDialog = true },
                            onEditVehicle = { /* Implementar edición si deseas */ },
                            onDeleteVehicle = { id -> viewModel.deleteVehicle(id) }
                        )
                    }

                    // Botones de acción
                    item {
                        ActionButtons(
                            onCerrarSesion = { showLogoutDialog = true },
                            onEditarPerfil = { navController?.navigate("editProfile") }
                        )
                    }
                }
            }
        }
    }

    // 🔹 Dialog para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onCerrarSesion()
                }) { Text("Sí, cerrar sesión", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // 🔹 Dialog para agregar vehículo
    if (showAddVehicleDialog) {
        AddVehicleDialog(
            onDismiss = { showAddVehicleDialog = false },
            onSave = { placa, color, modelo, tipo ->
                viewModel.addVehicle(placa, color, modelo, tipo)
                showAddVehicleDialog = false
            }
        )
    }
}

@Composable
fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var placa by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Vehículo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") })
                OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
                OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (placa.isNotBlank() && modelo.isNotBlank()) {
                    onSave(placa, color, modelo, tipo)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UserInfoHeader(user: UserProfile?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AzulPrincipal),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                color = VerdeSecundario
            ) {
                Icon(Icons.Default.Person, contentDescription = "Usuario", tint = Blanco, modifier = Modifier.padding(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(user?.username ?: "Usuario", style = MaterialTheme.typography.titleLarge, color = Blanco, fontWeight = FontWeight.Bold)
            Text(user?.email ?: "correo@ejemplo.com", style = MaterialTheme.typography.bodyMedium, color = Blanco.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun PersonalInfoSection(user: UserProfile?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Información Personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulPrincipal)
            Spacer(modifier = Modifier.height(12.dp))
            InfoRow("Correo electrónico", user?.email ?: "No disponible")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow("Teléfono", user?.phone ?: "No registrado")
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            InfoRow("Fecha de registro", user?.dateJoined ?: "No disponible")
        }
    }
}

@Composable
fun VehiclesSection(
    vehicles: List<CarResponse>,
    isLoading: Boolean,
    error: String?,
    onAddVehicle: () -> Unit,
    onEditVehicle: (Int) -> Unit,
    onDeleteVehicle: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mis Vehículos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulPrincipal)
                Text("${vehicles.size} vehículos", style = MaterialTheme.typography.bodySmall, color = GrisClaro)
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AzulPrincipal)
                }
                error != null -> Text("Error al cargar vehículos: $error", color = Color.Red)
                vehicles.isEmpty() -> Text("No tienes vehículos registrados", color = GrisClaro)
                else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    vehicles.forEach { vehicle ->
                        VehicleItem(vehicle, { onEditVehicle(vehicle.id) }, { onDeleteVehicle(vehicle.id) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onAddVehicle, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Vehículo")
            }
        }
    }
}

@Composable
fun VehicleItem(vehicle: CarResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        colors = CardDefaults.cardColors(containerColor = GrisClaro.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DirectionsCar, contentDescription = "Vehículo", tint = AzulPrincipal)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${vehicle.modelo} ${vehicle.tipo}", fontWeight = FontWeight.Medium)
                Text("Placa: ${vehicle.placa} • Color: ${vehicle.color ?: "No especificado"}", color = GrisClaro)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = GrisClaro)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ActionButtons(onCerrarSesion: () -> Unit, onEditarPerfil: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onEditarPerfil, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Perfil")
        }
        OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)) {
            Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }
}
