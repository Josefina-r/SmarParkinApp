package com.example.smarparkinapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.smarparkinapp.ui.screens.TicketScreen

class TicketActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val parkingName = intent.getStringExtra("parkingName") ?: "Estacionamiento"
        val plate = intent.getStringExtra("plate") ?: ""
        val duration = intent.getIntExtra("duration", 1)
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)

        setContent {
            TicketScreen(
                parkingName = parkingName,
                plate = plate,
                duration = duration,
                totalPrice = totalPrice
            )
        }
    }
}
