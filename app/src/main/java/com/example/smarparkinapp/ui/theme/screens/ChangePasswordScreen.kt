package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.ui.theme.theme.AzulPrincipal
import com.example.smarparkinapp.ui.theme.theme.Blanco
import com.example.smarparkinapp.ui.theme.theme.VerdePrincipal
import com.example.smarparkinapp.ui.theme.viewmodel.ChangePasswordViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onPasswordChanged: () -> Unit = {},
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ChangePasswordViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ChangePasswordViewModel(context) as T
            }
        }
    )

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val passwordChanged by viewModel.passwordChanged.collectAsState()

    // Efecto para navegar cuando la contraseña se cambia exitosamente
    LaunchedEffect(passwordChanged) {
        if (passwordChanged) {
            delay(2000) // Esperar 2 segundos para mostrar mensaje de éxito
            onPasswordChanged()
            viewModel.resetPasswordChanged()
        }
    }

    // Limpiar mensajes cuando se cambien los campos
    LaunchedEffect(oldPassword, newPassword, confirmPassword) {
        if (errorMessage != null || successMessage != null) {
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Cambiar Contraseña",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = AzulPrincipal
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = AzulPrincipal
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Actualiza tu contraseña",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AzulPrincipal
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Ingresa tu contraseña actual y la nueva contraseña que deseas usar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de contraseña
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Contraseña actual
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = {
                        Text(
                            "Contraseña actual",
                            color = AzulPrincipal.copy(alpha = 0.8f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Contraseña actual",
                            tint = AzulPrincipal
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { oldPasswordVisible = !oldPasswordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (oldPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (oldPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = AzulPrincipal.copy(alpha = 0.6f)
                            )
                        }
                    },
                    visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage?.contains("actual", ignoreCase = true) == true,
                    singleLine = true
                )

                // Nueva contraseña
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = {
                        Text(
                            "Nueva contraseña",
                            color = VerdePrincipal.copy(alpha = 0.8f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Nueva contraseña",
                            tint = VerdePrincipal
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { newPasswordVisible = !newPasswordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (newPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = VerdePrincipal.copy(alpha = 0.6f)
                            )
                        }
                    },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage?.contains("nueva", ignoreCase = true) == true ||
                            errorMessage?.contains("6", ignoreCase = true) == true ||
                            errorMessage?.contains("diferente", ignoreCase = true) == true,
                    singleLine = true
                )

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(
                            "Confirmar nueva contraseña",
                            color = VerdePrincipal.copy(alpha = 0.8f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Confirmar contraseña",
                            tint = VerdePrincipal
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = VerdePrincipal.copy(alpha = 0.6f)
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default,
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage?.contains("coinciden", ignoreCase = true) == true,
                    singleLine = true
                )
            }

            // Mensajes de estado
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = message,
                                color = Color(0xFFD32F2F),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                successMessage?.let { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E8)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = message,
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón cambiar contraseña
            Button(
                onClick = {
                    // Validación local antes de enviar
                    val validation = viewModel.validatePasswords(oldPassword, newPassword, confirmPassword)
                    if (validation is ChangePasswordViewModel.ValidationResult.Valid) {
                        viewModel.changePassword(oldPassword, newPassword, confirmPassword)
                    } else {
                        viewModel.clearMessages()
                        // CORRECCIÓN: No podemos reasignar directamente, usamos el método del ViewModel
                        when (validation) {
                            is ChangePasswordViewModel.ValidationResult.Error -> {
                                // El ViewModel debería tener un método para setear el error
                                // Por ahora, usamos una solución temporal
                                viewModel.setErrorMessage((validation as ChangePasswordViewModel.ValidationResult.Error).message)
                            }
                            else -> {}
                        }
                    }
                },
                enabled = !isLoading && oldPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdePrincipal,
                    disabledContainerColor = VerdePrincipal.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Blanco,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Cambiar Contraseña",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Blanco
                    )
                }
            }

            // Información adicional
            if (!isLoading) {
                Text(
                    "Asegúrate de que tu nueva contraseña tenga al menos 6 caracteres",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Preview para desarrollo
@Composable
fun ChangePasswordScreenPreview() {
    MaterialTheme {
        ChangePasswordScreen(
            onPasswordChanged = {},
            onBack = {}
        )
    }
}