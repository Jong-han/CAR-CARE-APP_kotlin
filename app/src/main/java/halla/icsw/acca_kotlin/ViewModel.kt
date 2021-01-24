package halla.icsw.acca_kotlin

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.math.round

class MyViewModel(application: Application) : AndroidViewModel(application), LocationListener {

    var curDistance = MutableLiveData<Double>()
    var totalDistance = MutableLiveData<Double>()
    var location: Location? = null
    var isStart: Boolean = false

    val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        totalDistance.value = 0.0
        curDistance.value = 0.0
    }

    fun startDrive() {
        val isGPSEnable: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnable: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (ActivityCompat.checkSelfPermission(getApplication(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplication(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            when {
                isGPSEnable -> location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                isNetworkEnable -> location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
        isStart = true
    }

    fun endDrive() {
        totalDistance.value = curDistance.value?.let { totalDistance.value?.plus(it) }
        locationManager.removeUpdates(this)
        curDistance.value = 0.0
        isStart = false
    }

    override fun onLocationChanged(newlocation: Location) {
        var tempDistance = round(newlocation.distanceTo(location).toDouble())
        curDistance.value = curDistance.value?.plus(tempDistance)
    }

}