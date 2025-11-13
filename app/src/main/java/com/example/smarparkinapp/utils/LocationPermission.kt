package com.example.smarparkinapp.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionState(): LocationPermissionState {
    val fineLocationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val coarseLocationPermissionState = rememberPermissionState(
        permission = android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    return remember(fineLocationPermissionState, coarseLocationPermissionState) {
        LocationPermissionState(
            fineLocationPermissionState = fineLocationPermissionState,
            coarseLocationPermissionState = coarseLocationPermissionState
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
data class LocationPermissionState(
    val fineLocationPermissionState: PermissionState,
    val coarseLocationPermissionState: PermissionState
) {
    val hasFineLocationPermission: Boolean
        get() = fineLocationPermissionState.status.isGranted

    val hasCoarseLocationPermission: Boolean
        get() = coarseLocationPermissionState.status.isGranted

    val hasLocationPermission: Boolean
        get() = hasFineLocationPermission || hasCoarseLocationPermission

    fun requestPermission() {
        fineLocationPermissionState.launchPermissionRequest()
        coarseLocationPermissionState.launchPermissionRequest()
    }
}

// ✅ Alternativa sin Accompanist
@Composable
fun rememberLocationPermissionLauncher(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
): LocationPermissionLauncher {
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                onPermissionGranted()
            }

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                onPermissionGranted()
            }

            else -> {
                onPermissionDenied()
            }
        }
    }

    return remember(locationPermissionLauncher) {
        LocationPermissionLauncher(
            launcher = locationPermissionLauncher,
            hasPermission = {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
}

data class LocationPermissionLauncher(
    val launcher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    val hasPermission: () -> Boolean
) {
    fun requestPermission() {
        launcher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
