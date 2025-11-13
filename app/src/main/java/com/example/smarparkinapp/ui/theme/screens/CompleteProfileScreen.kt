package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.CompleteProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    userId: Int, // Recibes el parámetro pero no lo usas
    onProfileCompleted: () -> Unit,
    viewModel: CompleteProfileViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Completa tu Perfil", color = Blanco) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Agrega la información de tu vehículo para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisClaro,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Sección de vehículo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Información del Vehículo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )

                    OutlinedTextField(
                        value = viewModel.placa,
                        onValueChange = { viewModel.placa = it },
                        label = { Text("Placa del vehículo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = viewModel.modelo,
                        onValueChange = { viewModel.modelo = it },
                        label = { Text("Modelo del vehículo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = viewModel.color,
                        onValueChange = { viewModel.color = it },
                        label = { Text("Color") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Selector de tipo de vehículo
                    var expanded by remember { mutableStateOf(false) }
                    val tiposVehiculo = listOf("auto", "moto", "camioneta", "otros")

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = viewModel.tipoVehiculo,
                            onValueChange = { },
                            label = { Text("Tipo de vehículo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                    contentDescription = "Seleccionar tipo"
                                )
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            tiposVehiculo.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.tipoVehiculo = tipo
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveProfile()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Blanco,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        "Guardar y continuar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Manejar éxito
            LaunchedEffect(viewModel.isSuccess) {
                if (viewModel.isSuccess) {
                    onProfileCompleted()
                }
            }

            // Mostrar errores
            uiState.errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2F2))
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}