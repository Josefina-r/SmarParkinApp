package com.example.smarparkinapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.smarparkinapp.ui.theme.screens.LoginScreen
import org.junit.Rule
import org.junit.Test

class LoginScreenInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginScreen_displaysAndAllowsInput() {

        composeRule.setContent {
            val navController = rememberNavController()

            LoginScreen(
                onLoginSuccess = {},
                onRegisterClick = {},
                navController = navController,
                onForgotPasswordClick = {}
            )
        }
        //Thread.sleep(2000)
        // Verifica que el título existe
        composeRule.onNodeWithText("Bienvenido a ParkeaYa", ignoreCase = true)
            .assertIsDisplayed()

        // Escribir usuario
        composeRule.onNodeWithText("Usuario", ignoreCase = true)
            .performTextInput("admin")

        // Escribir contraseña
        composeRule.onNodeWithText("Contraseña", ignoreCase = true)
            .performTextInput("123456")

        // Clic en ingresar
        composeRule.onNodeWithText("Ingresar")
            .performClick()
    }
}
