package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarparkinapp.R
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.data.model.RegisterRequest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import com.example.smarparkinapp.ui.theme.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: (Int) -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val registeredUserId by viewModel.registeredUserId.collectAsState() // ✅ lo traemos del viewmodel

    // ✅ Cuando el registro es exitoso, navega al CompleteProfileScreen
    LaunchedEffect(registeredUserId) {
        registeredUserId?.let { id ->
            onRegisterSuccess(id)
            viewModel.clearRegisteredUserId()
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(AzulClaro, VerdeClaro),
        startY = 0f,
        endY = 1000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
    ) {
        Image(
            painter = painterResource(id = R.drawable.parkin_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.05f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AzulPrincipal, VerdePrincipal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card de registro
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AzulPrincipal
                        ),
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Regístrate para empezar a usar ParkeaYa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color.DarkGray.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 32.dp),
                        textAlign = TextAlign.Center
                    )

                    // Campos
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre completo", color = AzulPrincipal.copy(alpha = 0.8f)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre", tint = AzulPrincipal) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico", color = AzulPrincipal.copy(alpha = 0.8f)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = AzulPrincipal) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = VerdePrincipal.copy(alpha = 0.8f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña", tint = VerdePrincipal) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña", color = VerdePrincipal.copy(alpha = 0.8f)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirmar contraseña", tint = VerdePrincipal) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(28.dp))

                    // Botón Registrarse
                    Button(
                        onClick = {
                            if (password == confirmPassword &&
                                name.isNotEmpty() &&
                                email.isNotEmpty()
                            ) {
                                val request = RegisterRequest(
                                    username = name,
                                    email = email,
                                    password = password
                                )
                                viewModel.register(request)
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Blanco)
                        } else {
                            Text("Registrarse", fontSize = 17.sp, color = Blanco, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Mostrar error si existe
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = androidx.compose.ui.graphics.Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Ir al login
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("¿Ya tienes cuenta? ", color = androidx.compose.ui.graphics.Color.DarkGray)
                        Text(
                            "Inicia sesión",
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onLoginClick)
                        )
                    }
                }
            }
        }
    }
}
