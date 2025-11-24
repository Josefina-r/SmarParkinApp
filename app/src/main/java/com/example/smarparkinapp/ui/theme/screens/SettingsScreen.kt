package com.example.smarparkinapp.ui.theme.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.smarparkinapp.ui.theme.data.LocalStrings
import com.example.smarparkinapp.ui.theme.theme.*
import com.example.smarparkinapp.ui.theme.viewmodel.SettingsViewModel
import com.example.smarparkinapp.ui.theme.viewmodel.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(context))
    val strings = LocalStrings.current

    // Observar los estados del ViewModel usando .value
    val currentThemeState = settingsViewModel.currentTheme.collectAsState()
    val currentTheme = currentThemeState.value

    val currentLanguageState = settingsViewModel.currentLanguage.collectAsState()
    val currentLanguage = currentLanguageState.value

    val notificationsEnabledState = settingsViewModel.notificationsEnabled.collectAsState()
    val notificationsEnabled = notificationsEnabledState.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        strings.settings,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = strings.back, tint = AzulPrincipal)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8F9FA))
        ) {
            // Sección: Cuenta
            SettingsSection(title = strings.account) {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = strings.profile,
                    subtitle = strings.profileSubtitle
                ) {
                    navController.navigate("perfil")
                }

                SettingsItem(
                    icon = Icons.Default.Security,
                    title = strings.privacy,
                    subtitle = strings.privacySubtitle
                ) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }

                SettingsItemWithSwitch(
                    icon = Icons.Default.Notifications,
                    title = strings.notifications,
                    subtitle = strings.notificationsSubtitle,
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        settingsViewModel.updateNotifications(enabled)
                    }
                )
            }

            // Sección: App
            SettingsSection(title = strings.app) {
                SettingsItemWithOptions(
                    icon = Icons.Default.Language,
                    title = strings.language,
                    subtitle = settingsViewModel.getLanguageDisplayName(currentLanguage),
                    options = listOf(
                        "Español" to "es",
                        "English" to "en",
                        "Português" to "pt"
                    ),
                    selectedOption = currentLanguage,
                    onOptionSelected = { languageCode ->
                        settingsViewModel.updateLanguage(languageCode)
                    }
                )

                SettingsItemWithOptions(
                    icon = Icons.Default.DarkMode,
                    title = strings.theme,
                    subtitle = settingsViewModel.getThemeDisplayName(currentTheme),
                    options = listOf(
                        settingsViewModel.getThemeDisplayName("light") to "light",
                        settingsViewModel.getThemeDisplayName("dark") to "dark",
                        settingsViewModel.getThemeDisplayName("system") to "system"
                    ),
                    selectedOption = currentTheme,
                    onOptionSelected = { theme ->
                        settingsViewModel.updateTheme(theme)
                    }
                )

                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = strings.storage,
                    subtitle = strings.storageSubtitle
                ) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }

                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = strings.location,
                    subtitle = strings.locationSubtitle
                ) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }
            }

            // Sección: Ayuda y Soporte
            SettingsSection(title = strings.help) {
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = strings.helpCenter,
                    subtitle = strings.helpCenterSubtitle
                ) {
                    navController.navigate("help_center")
                }

                SettingsItem(
                    icon = Icons.Default.Chat,
                    title = strings.chatbot,
                    subtitle = strings.chatbotSubtitle
                ) {
                    navController.navigate("chatbot")
                }

                SettingsItem(
                    icon = Icons.Default.ContactSupport,
                    title = strings.contact,
                    subtitle = strings.contactSubtitle
                ) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("soporte@parkeaya.com"))
                        putExtra(Intent.EXTRA_SUBJECT, "Soporte ParkeaYa")
                        putExtra(Intent.EXTRA_TEXT, "Hola equipo de ParkeaYa,\n\nNecesito ayuda con:")
                    }
                    context.startActivity(Intent.createChooser(intent, "Enviar email"))
                }

                SettingsItem(
                    icon = Icons.Default.Info,
                    title = strings.about,
                    subtitle = strings.aboutSubtitle
                ) {
                    navController.navigate("about")
                }
            }

            // Sección: Legal
            SettingsSection(title = strings.legal) {
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = strings.terms,
                    subtitle = strings.termsSubtitle
                ) {
                    navController.navigate("terms")
                }

                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = strings.privacyPolicy,
                    subtitle = strings.privacyPolicySubtitle
                ) {
                    navController.navigate("privacy")
                }
            }

            // Información de versión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ParkeaYa v1.0.0",
                    color = GrisTexto,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = AzulPrincipal,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdePrincipal,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Text(
                    text = subtitle,
                    color = GrisTexto,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = GrisMedio,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdePrincipal,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Text(
                    text = subtitle,
                    color = GrisTexto,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Blanco,
                    checkedTrackColor = VerdePrincipal,
                    uncheckedThumbColor = Blanco,
                    uncheckedTrackColor = GrisMedio
                )
            )
        }
    }
}

@Composable
fun SettingsItemWithOptions(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<Pair<String, String>>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val strings = LocalStrings.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdePrincipal,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = AzulPrincipal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )

                Text(
                    text = subtitle,
                    color = GrisTexto,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = strings.select,
                tint = GrisMedio,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    options.forEach { (displayName, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOptionSelected(value)
                                    showDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = value == selectedOption,
                                onClick = {
                                    onOptionSelected(value)
                                    showDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(displayName, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}