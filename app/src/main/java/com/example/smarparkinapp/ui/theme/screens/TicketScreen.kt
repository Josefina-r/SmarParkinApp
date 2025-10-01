package com.example.smarparkinapp.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import com.example.smarparkinapp.ui.theme.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    parkingName: String,
    plate: String,
    duration: Int,
    totalPrice: Double,
    navController: androidx.navigation.NavController? = null
) {
    val context = LocalContext.current

    // Se usa un ID único para la caja que se convertirá a imagen
    val ticketModifier = remember { Modifier.fillMaxWidth() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu Ticket", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulPrincipal,
                    titleContentColor = Blanco
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Ticket + QR
            Card(
                modifier = ticketModifier,
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(parkingName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Placa: $plate", fontWeight = FontWeight.SemiBold)
                    Text("Duración: $duration horas")
                    Text(
                        "Total: S/ $totalPrice",
                        fontWeight = FontWeight.Bold,
                        color = VerdePrincipal
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color.Gray, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = "QR", tint = Color.White, modifier = Modifier.size(80.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Muestra este ticket digital en la entrada del estacionamiento",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Compartir (solo ticket)
            Button(
                onClick = {
                    captureAndShareTicket(context, ticketModifier)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Text("Compartir Ticket", color = Blanco, fontWeight = FontWeight.Bold)
            }

            // Botón Volver
            Button(
                onClick = {
                    if (navController != null) {
                        navController.navigate("map")
                    } else {
                        (context as? android.app.Activity)?.finish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
            ) {
                Text("Volver", color = Blanco, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Compartir solo el ticket como imagen
fun captureAndShareTicket(context: Context, modifier: Modifier) {
    val activity = context as? android.app.Activity ?: return
    val rootView = activity.window.decorView.rootView

    val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    rootView.draw(canvas)

    try {
        val file = File(context.cacheDir, "ticket.png")
        val fOut = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.flush()
        fOut.close()
        file.setReadable(true, false)

        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/png"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "Compartir Ticket"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
