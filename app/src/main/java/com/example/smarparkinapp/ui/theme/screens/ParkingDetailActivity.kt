package com.example.smarparkinapp.ui.theme.screens


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.smarparkinapp.ui.theme.theme.SmarParkinAppTheme

class ParkingDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parkingName = intent.getStringExtra("parkingName") ?: "Estacionamiento"
        val parkingAddress = intent.getStringExtra("parkingAddress") ?: ""
        val parkingPrice = intent.getDoubleExtra("parkingPrice", 0.0)
        val parkingPhone = intent.getStringExtra("parkingPhone") ?: ""

        setContent {
            SmarParkinAppTheme {
                EstacionamientoDetalleScreen(
                    estacionamiento = com.example.smarparkinapp.ui.theme.data.model.Estacionamiento(
                        nombre = parkingName,
                        direccion = parkingAddress,
                        precioHora = parkingPrice,
                        horario = "24 horas",
                        amenidades = listOf(
                            "Seguridad 24/7",
                            "Cámaras",
                            "Techado",
                            "Acceso rápido"
                        ),
                        telefono = parkingPhone
                    )
                )
            }
        }
    }
}
