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
import androidx.room.Room
import halla.icsw.acca_kotlin.DB.DriveEntity
import halla.icsw.acca_kotlin.DB.MyDataBase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class MyViewModel(application: Application) : AndroidViewModel(application), LocationListener {

    var curDistance = MutableLiveData<Double>()
    var totalDistance = MutableLiveData<Double>()
    var driveDate = MutableLiveData<String>()
    var driveDistance = MutableLiveData<Double>()
    var location: Location? = null
    var isStart: Boolean = false

    var db = Room.databaseBuilder(
        application.applicationContext,
        MyDataBase::class.java,
        "DataBase.db"
    ).allowMainThreadQueries().build()

    val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        totalDistance.value = db.driveDAO().getTotalDistance()
        curDistance.value = 0.0
        driveDate.value = db.driveDAO().getAll().toString()
        driveDistance.value = db.driveDAO().getdistance()
    }

    fun startDrive() {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
        isStart = true
    }

    fun endDrive() {
        totalDistance.value = curDistance.value?.let { totalDistance.value?.plus(it) }
        locationManager.removeUpdates(this)
        var timeNow = System.currentTimeMillis()
        var date = Date(timeNow)
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var now = sdf.format(date)
        curDistance.value?.let { DriveEntity(now, it) }?.let { db.driveDAO().insert(it) }
        totalDistance.value = db.driveDAO().getTotalDistance()
        curDistance.value = 0.0
        location = null
        isStart = false

    }

    fun refreshDriveRecord(){
        driveDate.value = db.driveDAO().getAll().toString()
        driveDistance.value = db.driveDAO().getTotalDistance()
    }

    override fun onLocationChanged(newlocation: Location) {
        if(location != null) {
            var tempDistance = round(newlocation.distanceTo(location).toDouble())
            curDistance.value = curDistance.value?.plus(tempDistance)
        }
        location = newlocation
    }

}