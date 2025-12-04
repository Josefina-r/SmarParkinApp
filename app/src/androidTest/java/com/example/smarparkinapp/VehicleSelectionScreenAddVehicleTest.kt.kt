package com.example.smarparkinapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.smarparkinapp.fakes.FakeReservationViewModel
import com.example.smarparkinapp.ui.theme.screens.VehicleSelectionScreenFake
import org.junit.Rule
import org.junit.Test

class VehicleSelectionScreenAddVehicleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun addVehicle_createsNewVehicleInList() {

        val fakeVm = FakeReservationViewModel()

        composeTestRule.setContent {
            VehicleSelectionScreenFake(fakeVm)
        }

        // Verifica que aparece un vehículo inicial
        composeTestRule.onNodeWithText("Toyota Corolla")
            .assertIsDisplayed()

        // Presiona el botón
        composeTestRule.onNodeWithText("Agregar Vehículo")
            .performClick()

        // Verifica que el nuevo vehículo aparece
        composeTestRule.waitUntilNodeCount(
            hasText("Nuevo Auto"),
            1
        )
    }
}
