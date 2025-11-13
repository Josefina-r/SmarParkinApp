package com.example.smarparkinapp.ui.theme.services
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // ✅ Obtener ubicación actual una vez
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Task<Location> {
        return fusedLocationClient.lastLocation
    }

    // ✅ Obtener actualizaciones continuas de ubicación
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 segundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // 5 segundos mínimo
            setMaxUpdateDelayMillis(15000L) // 15 segundos máximo
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    launch {
                        send(location)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // ✅ Detener actualizaciones de ubicación
    fun stopLocationUpdates() {
        // Se detiene automáticamente cuando el Flow se cierra
    }
}