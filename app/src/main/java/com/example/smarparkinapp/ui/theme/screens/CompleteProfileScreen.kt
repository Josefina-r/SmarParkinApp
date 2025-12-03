package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.data.model.*
import com.example.smarparkinapp.ui.theme.viewmodel.CompleteProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    userId: Int,
    onProfileCompleted: () -> Unit,
    viewModel: CompleteProfileViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    OutlinedTextField(
        value = viewModel.placa,
        onValueChange = { viewModel.placa = it },
        label = { Text("Placa del vehículo") },
        modifier = Modifier.fillMaxWidth()
    )

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
                "Agrega la información de tu vehículo y método de pago para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisClaro
            )

            // Vehículo
            OutlinedTextField(
                value = viewModel.placa,
                onValueChange = { viewModel.placa = it },
                label = { Text("Placa del vehículo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.modelo,
                onValueChange = { viewModel.modelo = it },
                label = { Text("Modelo y año") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.color,
                onValueChange = { viewModel.color = it },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )


            OutlinedTextField(
                value = viewModel.metodoPago,
                onValueChange = { viewModel.metodoPago = it },
                label = { Text("Método de pago (Yape, Plin, Tarjeta)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))


            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveProfile(userId)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Blanco, strokeWidth = 2.dp)
                } else {
                    Text("Guardar y continuar")
                }
            }
            LaunchedEffect(viewModel.isSuccess) {
                if (viewModel.isSuccess) {
                    onProfileCompleted()
                }
            }

            uiState.errorMessage?.let {
                Text(it, color = Color.Red)
            }

        }
    }
}
