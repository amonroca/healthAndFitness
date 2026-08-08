package com.cse310.healthandfitness.util

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

// Wraps location access used by the app and performs location-related operations.
class LocationManager(context: Context) {
    private val appContext = context.applicationContext
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return withTimeoutOrNull(timeMillis = 15000L) {
            try {
                val cancellationTokenSource = CancellationTokenSource()
                @Suppress("MissingPermission")
                val currentLocation = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).await()

                currentLocation
                    ?: requestSingleLocationUpdate()
                    ?: getLastKnownLocation()
            } catch (_: Exception) {
                requestSingleLocationUpdate()
            }
        }
    }

    suspend fun getAddress(latitude: Double, longitude: Double): String? {
        if (!Geocoder.isPresent()) {
            return null
        }

        val geocoder = Geocoder(appContext, Locale.getDefault())

        return try {
            formatAddress(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(
                            latitude,
                            longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    continuation.resume(addresses.firstOrNull())
                                }

                                override fun onError(errorMessage: String?) {
                                    continuation.resume(null)
                                }
                            }
                        )
                    }
                } else {
                    getAddressCompat(geocoder, latitude, longitude)
                }
            )
        } catch (_: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun getAddressCompat(
        geocoder: Geocoder,
        latitude: Double,
        longitude: Double
    ): Address? {
        return geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
    }

    private fun formatAddress(address: Address?): String? {
        if (address == null) {
            return null
        }

        val addressLine = address.getAddressLine(0)
        if (!addressLine.isNullOrBlank()) {
            return addressLine
        }

        val parts = listOfNotNull(
            address.subAdminArea,
            address.locality,
            address.adminArea,
            address.countryName
        ).distinct()

        return parts.takeIf { it.isNotEmpty() }?.joinToString(", ")
    }

    private suspend fun requestSingleLocationUpdate(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(500L)
                .setMaxUpdates(1)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    if (continuation.isActive) {
                        continuation.resume(locationResult.lastLocation)
                    }
                }
            }

            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(callback)
            }

            @Suppress("MissingPermission")
            fusedLocationClient
                .requestLocationUpdates(request, callback, Looper.getMainLooper())
                .addOnFailureListener {
                    fusedLocationClient.removeLocationUpdates(callback)
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return fusedLocationClient.lastLocation.await()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }
}
