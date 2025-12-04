package com.example.smarparkinapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuessScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun whereAmI() {
        println("📍 ¿DÓNDE ESTOY?")
        Thread.sleep(5000)
        println("✅ App abierta - Verifica manualmente:")
        println("   ¿Ves 'Usuario' y 'Contraseña'?")
        println("   ¿O ves 'Home', 'Mapa'?")
    }
}