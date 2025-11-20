package com.example.smarparkinapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.smarparkinapp.data.model.VehicleType
import com.example.smarparkinapp.ui.theme.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleDialog(
    viewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Agregar Vehículo",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TIPO DE VEHÍCULO (Solo automóvil)
                Text(
                    text = "Tipo de Vehículo",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Forzar automóvil como único tipo
                LaunchedEffect(Unit) {
                    viewModel.updateVehicleType(VehicleType.AUTOMOVIL)
                }
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Automóvil") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MARCA (Solo marcas de carros)
                Text(
                    text = "Marca *",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                var marcaExpanded by remember { mutableStateOf(false) }

                val marcasAutos = listOf(
                    "Toyota", "Honda", "Ford", "Chevrolet", "Nissan",
                    "Hyundai", "Kia", "Volkswagen", "BMW", "Mercedes-Benz",
                    "Audi", "Mazda", "Subaru", "Lexus", "Volvo",
                    "Mitsubishi", "Jeep", "Renault", "Peugeot", "Citroën",
                    "Fiat", "Suzuki", "Isuzu", "Chrysler", "Dodge", "Otro"
                )

                ExposedDropdownMenuBox(
                    expanded = marcaExpanded,
                    onExpandedChange = { marcaExpanded = !marcaExpanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.vehicleBrand,
                        onValueChange = { viewModel.updateVehicleBrand(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Selecciona la marca") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = marcaExpanded,
                        onDismissRequest = { marcaExpanded = false }
                    ) {
                        marcasAutos.forEach { marca ->
                            DropdownMenuItem(
                                text = { Text(marca) },
                                onClick = {
                                    viewModel.updateVehicleBrand(marca)
                                    marcaExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // MODELO (Solo modelos de carros)
                Text(
                    text = "Modelo *",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                var modeloExpanded by remember { mutableStateOf(false) }

                // Modelos específicos para cada marca de autos
                val modelosAutos = when (viewModel.vehicleBrand) {
                    "Toyota" -> listOf("Corolla", "Camry", "RAV4", "Hilux", "Yaris", "Prius", "4Runner", "Highlander", "Tacoma", "Sienna", "Otro")
                    "Honda" -> listOf("Civic", "Accord", "CR-V", "HR-V", "Pilot", "City", "Fit", "Odyssey", "Ridgeline", "Passport", "Otro")
                    "Ford" -> listOf("F-150", "Focus", "Escape", "Explorer", "Mustang", "Ranger", "Fusion", "Edge", "Expedition", "Bronco", "Otro")
                    "Chevrolet" -> listOf("Spark", "Aveo", "Cruze", "Malibu", "Trax", "Equinox", "Tracker", "Blazer", "Tahoe", "Silverado", "Otro")
                    "Nissan" -> listOf("Sentra", "Versa", "Altima", "Kicks", "X-Trail", "Frontier", "Murano", "Pathfinder", "Rogue", "Maxima", "Otro")
                    "Hyundai" -> listOf("Accent", "Elantra", "Tucson", "Santa Fe", "Creta", "i10", "Sonata", "Kona", "Palisade", "Venue", "Otro")
                    "Kia" -> listOf("Rio", "Forte", "Seltos", "Sportage", "Sorento", "Picanto", "Cerato", "Carnival", "Stonic", "Niro", "Otro")
                    "Volkswagen" -> listOf("Golf", "Jetta", "Tiguan", "Polo", "Virtus", "Taos", "Passat", "T-Cross", "Arteon", "Atlas", "Otro")
                    "BMW" -> listOf("Serie 3", "Serie 5", "X1", "X3", "X5", "Serie 1", "X7", "Serie 7", "X2", "X4", "Otro")
                    "Mercedes-Benz" -> listOf("Clase A", "Clase C", "Clase E", "GLA", "GLC", "GLE", "Clase S", "GLS", "CLA", "GLB", "Otro")
                    "Audi" -> listOf("A3", "A4", "A6", "Q3", "Q5", "Q7", "A5", "Q8", "A7", "A8", "Otro")
                    "Mazda" -> listOf("Mazda 2", "Mazda 3", "CX-3", "CX-5", "CX-9", "Mazda 6", "CX-30", "MX-5", "CX-50", "BT-50", "Otro")
                    "Subaru" -> listOf("Impreza", "Legacy", "Forester", "Outback", "Crosstrek", "WRX", "Ascent", "BRZ", "Otro")
                    "Lexus" -> listOf("ES", "RX", "NX", "IS", "UX", "LX", "LS", "GX", "RC", "LC", "Otro")
                    "Volvo" -> listOf("S60", "S90", "XC40", "XC60", "XC90", "V60", "V90", "C40", "XC70", "Otro")
                    "Mitsubishi" -> listOf("Lancer", "Outlander", "Eclipse Cross", "ASX", "Montero", "Mirage", "Pajero", "Triton", "Otro")
                    "Jeep" -> listOf("Wrangler", "Grand Cherokee", "Cherokee", "Compass", "Renegade", "Gladiator", "Wagoneer", "Otro")
                    "Renault" -> listOf("Duster", "Sandero", "Logan", "Kwid", "Captur", "Koleos", "Clio", "Megane", "Otro")
                    "Peugeot" -> listOf("208", "308", "2008", "3008", "5008", "508", "Partner", "Rifter", "Otro")
                    "Citroën" -> listOf("C3", "C4", "C5", "Berlingo", "C-Elysee", "C4 Cactus", "Otro")
                    "Fiat" -> listOf("500", "Panda", "Tipo", "Punto", "Cronos", "Argo", "Mobi", "Otro")
                    "Suzuki" -> listOf("Swift", "Vitara", "S-Cross", "Jimny", "Ciaz", "Ertiga", "Baleno", "Otro")
                    "Isuzu" -> listOf("D-Max", "MU-X", "Otro")
                    "Chrysler" -> listOf("300", "Pacifica", "Voyager", "Otro")
                    "Dodge" -> listOf("Charger", "Challenger", "Durango", "Journey", "Otro")
                    else -> listOf("Selecciona primero la marca")
                }

                ExposedDropdownMenuBox(
                    expanded = modeloExpanded,
                    onExpandedChange = { modeloExpanded = !modeloExpanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.vehicleModel,
                        onValueChange = { viewModel.updateVehicleModel(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Selecciona el modelo") },
                        readOnly = true,
                        enabled = viewModel.vehicleBrand.isNotEmpty() && viewModel.vehicleBrand != "Otro",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeloExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = modeloExpanded,
                        onDismissRequest = { modeloExpanded = false }
                    ) {
                        modelosAutos.forEach { modelo ->
                            DropdownMenuItem(
                                text = { Text(modelo) },
                                onClick = {
                                    viewModel.updateVehicleModel(modelo)
                                    modeloExpanded = false
                                },
                                enabled = viewModel.vehicleBrand.isNotEmpty() && viewModel.vehicleBrand != "Otro"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // COLOR (Dropdown)
                Text(
                    text = "Color *",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                var colorExpanded by remember { mutableStateOf(false) }
                val colores = listOf(
                    "Blanco", "Negro", "Plata", "Gris", "Azul", "Rojo",
                    "Verde", "Amarillo", "Naranja", "Marrón", "Beige",
                    "Vino", "Dorado", "Azul Marino", "Verde Oscuro", "Otro"
                )

                ExposedDropdownMenuBox(
                    expanded = colorExpanded,
                    onExpandedChange = { colorExpanded = !colorExpanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.vehicleColor,
                        onValueChange = { viewModel.updateVehicleColor(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Selecciona el color") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) }
                    )
                    ExposedDropdownMenu(
                        expanded = colorExpanded,
                        onDismissRequest = { colorExpanded = false }
                    ) {
                        colores.forEach { color ->
                            DropdownMenuItem(
                                text = { Text(color) },
                                onClick = {
                                    viewModel.updateVehicleColor(color)
                                    colorExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PLACA (Campo de texto libre)
                OutlinedTextField(
                    value = viewModel.vehiclePlate,
                    onValueChange = { viewModel.updateVehiclePlate(it.uppercase()) },
                    label = { Text("Placa del Vehículo *") },
                    placeholder = { Text("Ej: ABC123") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.vehiclePlate.isNotEmpty() && !isValidPlate(viewModel.vehiclePlate)
                )

                if (viewModel.vehiclePlate.isNotEmpty() && !isValidPlate(viewModel.vehiclePlate)) {
                    Text(
                        text = "Formato: 3 letras + 3 números (Ej: ABC123)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Guardar
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid(viewModel)
                ) {
                    Text("Guardar Vehículo")
                }
            }
        }
    }
}

// Función para validar placa de carros
private fun isValidPlate(plate: String): Boolean {
    // Formato para carros: 3 letras + 3 números o variaciones comunes
    val standardPlateRegex = Regex("^[A-Z]{3}[0-9]{3}\$|^[A-Z]{3}[0-9]{2}[A-Z]\$|^[A-Z]{2}[0-9]{3}[A-Z]\$")
    return plate.matches(standardPlateRegex)
}

// Función para validar el formulario completo
private fun isFormValid(viewModel: ReservationViewModel): Boolean {
    return viewModel.vehicleBrand.isNotEmpty() &&
            viewModel.vehicleModel.isNotEmpty() &&
            viewModel.vehicleColor.isNotEmpty() &&
            viewModel.vehiclePlate.isNotEmpty() &&
            isValidPlate(viewModel.vehiclePlate)
}