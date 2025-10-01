package com.example.smarparkinapp.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.smarparkinapp.ui.theme.theme.*

@Composable
fun SearchScreen(
    onApplyFilters: (
        tipoVehiculo: String,
        disponible: Boolean,
        precioMax: Float,
        seguridad: List<String>,
        amenidades: List<String>
    ) -> Unit = { _, _, _, _, _ -> }
) {
    var tipoVehiculo by remember { mutableStateOf("Auto") }
    var disponible by remember { mutableStateOf(false) }
    var precio by remember { mutableStateOf(50f) }

    // Seguridad
    var camaras by remember { mutableStateOf(false) }
    var vigilante by remember { mutableStateOf(false) }

    // Amenidades
    var techado by remember { mutableStateOf(false) }
    var lavado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Filtros de búsqueda",
            style = MaterialTheme.typography.headlineSmall,
            color = AzulPrincipal
        )

        Spacer(Modifier.height(20.dp))

        // Tipo de vehículo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AzulClaro),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tipo de vehículo", style = MaterialTheme.typography.titleMedium, color = AzulPrincipal)

                var expanded by remember { mutableStateOf(false) }
                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal)
                ) {
                    Text(tipoVehiculo)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Auto", "Moto", "Camioneta").forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                tipoVehiculo = tipo
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Disponibilidad
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GrisClaro),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Checkbox(checked = disponible, onCheckedChange = { disponible = it })
                Text("Solo espacios disponibles", color = VerdePrincipal)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Precio máximo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AzulClaro),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Precio máximo: S/.${precio.toInt()}", style = MaterialTheme.typography.titleMedium, color = AzulPrincipal)

                Slider(
                    value = precio,
                    onValueChange = { precio = it },
                    valueRange = 10f..200f,
                    colors = SliderDefaults.colors(
                        thumbColor = VerdePrincipal,
                        activeTrackColor = VerdeSecundario
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Seguridad
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GrisClaro),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Seguridad", style = MaterialTheme.typography.titleMedium, color = VerdePrincipal)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = camaras, onCheckedChange = { camaras = it })
                    Text("Cámaras de vigilancia")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = vigilante, onCheckedChange = { vigilante = it })
                    Text("Personal de seguridad")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Amenidades
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AzulClaro),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Amenidades", style = MaterialTheme.typography.titleMedium, color = AzulPrincipal)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = techado, onCheckedChange = { techado = it })
                    Text("Espacio techado")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = lavado, onCheckedChange = { lavado = it })
                    Text("Servicio de lavado")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Botón Aplicar
        Button(
            onClick = {
                val seguridadSeleccionada = mutableListOf<String>()
                if (camaras) seguridadSeleccionada.add("Cámaras de vigilancia")
                if (vigilante) seguridadSeleccionada.add("Personal de seguridad")

                val amenidadesSeleccionadas = mutableListOf<String>()
                if (techado) amenidadesSeleccionadas.add("Espacio techado")
                if (lavado) amenidadesSeleccionadas.add("Servicio de lavado")

                onApplyFilters(tipoVehiculo, disponible, precio, seguridadSeleccionada, amenidadesSeleccionadas)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal,
                contentColor = Blanco
            )
        ) {
            Text("Aplicar búsqueda", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SmarParkinAppTheme {
        SearchScreen()
    }
}
