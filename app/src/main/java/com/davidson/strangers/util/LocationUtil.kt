package com.davidson.strangers.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


private const val REQUEST_INTERVAL = 500L
private const val PERMISSION_ID = 2222

class LocationUtil private constructor(private val activity: AppCompatActivity) {


    private val locationManger =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    private var locationRequest: LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        REQUEST_INTERVAL
    ).build()

    private var isLocationRequesting = false
    private lateinit var locationCallback: LocationCallback

    val isLocationUsable: Boolean
        get() = checkLocationPermission() && checkLocationEnabled()

    init {
        Log.d("LocationUtil", "Using Location Util")
    }

    fun checkLocationPermission(): Boolean = (
            ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ==
                    PackageManager.PERMISSION_GRANTED)

    fun checkLocationEnabled(): Boolean =
        (locationManger.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManger.isProviderEnabled(LocationManager.NETWORK_PROVIDER))

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    fun requestLastLocation(onSucess: (location: Location) -> Unit) {
        if (checkLocationPermission()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(activity) {
                onSucess(it)
            }
        }
    }

    fun requestCurrentLocation(onSuccess: (location: Location) -> Unit) {
        isLocationRequesting = true
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                onSuccess(result.locations[0])
                stopRequestingLocation()
            }
        }
        if (checkLocationPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }


    fun stopRequestingLocation() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            isLocationRequesting = false
        }
    }

    fun geoCoderConverter(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(activity)
        var address: Address? = null
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) {
                    address = it[0]
                }
            } else {
                address = geocoder.getFromLocation(latitude, longitude, 1)?.get(0)
            }
        } catch (e: Exception) {
            Log.i("LocationUtil", e.message.toString() ?: "ERROR")
        }
        return address
    }

    fun geoCoderConverter(city: String): Address? {
        val geocoder = Geocoder(activity)
        var address: Address? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(city, 1) {
                    address = it[0]
                }
            } else {
                address = geocoder.getFromLocationName(city, 1)?.get(0)
            }
        } catch (e: Exception) {
            Log.i("LocationUtil", e.message.toString() ?: "ERROR")
        }
        return address
    }

    companion object {
        private lateinit var INSTANCE: LocationUtil
        fun getInstance(_activity: AppCompatActivity): LocationUtil {
            if (!(::INSTANCE.isInitialized)) {
                INSTANCE = LocationUtil(_activity)
            }
            return INSTANCE
        }
    }
}