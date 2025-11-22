package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import com.example.smarparkinapp.ui.theme.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, onMenuClick: () -> Unit = {}) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú", modifier = Modifier.padding(16.dp))
                Divider()

                val drawerItems = listOf(
                    NavRoutes.Perfil to "Perfil",
                    NavRoutes.Historial to "Historial",
                    NavRoutes.Reservar to "Reservar",
                    NavRoutes.Notificaciones to "Notificaciones"
                )

                drawerItems.forEach { (route, label) ->
                    NavigationDrawerItem(
                        label = { Text(label) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(route.route)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ParkeaYa") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home.route,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ){
                composable(NavRoutes.Perfil.route) {
                    Text("Pantalla Perfil")
                }
                composable(NavRoutes.Historial.route) {
                    Text("Pantalla Historial")
                }
                composable(NavRoutes.Reservar.route) {
                    Text("Pantalla Reservar")
                }
                composable(NavRoutes.Notificaciones.route) {
                    Text("Pantalla Notificaciones")
                }
            }
        }
    }
}
