package com.example.smarparkinapp.ui.theme.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smarparkinapp.R
import com.example.smarparkinapp.ui.theme.data.AuthManager
import com.example.smarparkinapp.ui.theme.data.model.User
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.LoginViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.UserViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.UserViewModelFactory

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(context) as T
            }
        }
    )

    // ✅ NUEVO: UserViewModel para manejar la sesión del usuario
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(context)
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by loginViewModel.isLoading.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()
    val loginSuccess by loginViewModel.loginSuccess.collectAsState()

    // ✅ MEJORADO: Manejo del login exitoso con UserViewModel
    LaunchedEffect(loginSuccess) {
        println("🔄 [SCREEN] LoginScreen - loginSuccess: $loginSuccess")
        if (loginSuccess) {
            println("🚀 [SCREEN] Login exitoso, configurando usuario...")

            // Obtener información del usuario desde AuthManager
            val authManager = AuthManager(context)
            val userId = authManager.getUserId()
            val username = authManager.getUsername()
            val token = authManager.getAuthToken()

            println("👤 [SCREEN] Usuario recuperado - ID: $userId, Username: $username")

            if (userId != -1 && !username.isNullOrEmpty() && !token.isNullOrEmpty()) {
                // Crear objeto User y configurar en UserViewModel
                val user = User(
                    id = userId,
                    username = username,
                    email = "", // Puedes obtener el email si lo guardas en AuthManager
                    first_name = null,
                    last_name = null
                )

                userViewModel.login(user, token)
                println("✅ [SCREEN] Usuario configurado en UserViewModel")
            } else {
                println("❌ [SCREEN] No se pudo recuperar información completa del usuario")
            }

            // Navegar al home
            println("🏠 [SCREEN] Navegando al Home...")
            onLoginSuccess()
            loginViewModel.clearLoginSuccess()
            println("🧹 [SCREEN] Estado limpiado")
        }
    }

    // Fondo degradado
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
        // Imagen de fondo tenue
        Image(
            painter = painterResource(id = R.drawable.parkin_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.05f,
            modifier = Modifier.fillMaxSize()
        )

        // Contenido principal
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

            // Card de login
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
                        text = "Bienvenido a ParkeaYa",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AzulPrincipal
                        ),
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Encuentra estacionamiento fácilmente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 32.dp),
                        textAlign = TextAlign.Center
                    )

                    // Campo de usuario
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario", color = AzulPrincipal.copy(alpha = 0.8f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Usuario",
                                tint = AzulPrincipal
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(20.dp))

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = VerdePrincipal.copy(alpha = 0.8f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Contraseña",
                                tint = VerdePrincipal
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password",
                                    tint = VerdePrincipal
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Olvidé contraseña
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = AzulSecundario,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                if (username.isNotEmpty()) {
                                    loginViewModel.resetPassword(username)
                                } else {
                                    // Mostrar diálogo o mensaje para ingresar email
                                    println("⚠ [SCREEN] Ingresa tu usuario/email primero")
                                }
                            }
                            .padding(top = 12.dp, bottom = 28.dp),
                        fontWeight = FontWeight.Medium
                    )

                    // Mostrar mensaje de reset de contraseña
                    loginViewModel.resetMessage?.let { message ->
                        if (message.isNotEmpty()) {
                            Text(
                                text = message,
                                color = if (message.contains("Error")) Color.Red else VerdePrincipal,
                                modifier = Modifier.padding(top = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Botón Login
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(VerdeSecundario, VerdePrincipal)
                                )
                            )
                            .clickable(enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty()) {
                                println("🖱 [SCREEN] Botón login presionado - Usuario: $username")
                                loginViewModel.login(username, password)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Blanco)
                        } else {
                            Text(
                                "Ingresar",
                                fontSize = 17.sp,
                                color = Blanco,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Mostrar error si existe
                    errorMessage?.let { error ->
                        if (error.isNotEmpty()) {
                            Text(
                                text = error,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Ir a registro
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("¿No tienes cuenta? ", color = Color.DarkGray)
                        Text(
                            "Regístrate",
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onRegisterClick)
                        )
                    }
                }
            }
        }
    }
}