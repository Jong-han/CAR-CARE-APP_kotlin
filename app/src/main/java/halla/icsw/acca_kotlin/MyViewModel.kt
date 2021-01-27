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
import halla.icsw.acca_kotlin.DB.Repository
import halla.icsw.acca_kotlin.Maintenance.Cycle
import halla.icsw.acca_kotlin.Maintenance.PartData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class MyViewModel(application: Application) : AndroidViewModel(application), LocationListener {

    // **** 주행에 대한 라이브데이터 **** //
    var curDistance = MutableLiveData<Double>() // 실시간으로 표시되는 현재 주행거리
    var totalDistance = MutableLiveData<Double>() // 누적 주행거리
    var driveDate = MutableLiveData<String>() // 모든 주행 날짜
    var driveDistance = MutableLiveData<String>() // 모든 주행 기록

    // **** 부품교체까지 남은 거리에 대한 라이브데이터 **** //
    var engineOil = MutableLiveData<PartData>()
    var autoOil = MutableLiveData<PartData>()
    var powerOil = MutableLiveData<PartData>()
    var brakeOil = MutableLiveData<PartData>()
    var brakePad = MutableLiveData<PartData>()
    var timingBelt = MutableLiveData<PartData>()

    var engineOilDistance = MutableLiveData<Double>()
    var autoOilDistance = MutableLiveData<Double>()
    var powerOilDistance = MutableLiveData<Double>()
    var brakeOilDistance = MutableLiveData<Double>()
    var brakePadDistance = MutableLiveData<Double>()
    var timingBeltDistance = MutableLiveData<Double>()


    // **** 각 부품들에 대한 Cycle 객체 **** //
    private val engineOilCycle = Cycle(40000.0)
    private val autoOilCycle = Cycle(40000.0)
    private val powerOilCycle = Cycle(40000.0)
    private val brakeOilCycle = Cycle(40000.0)
    private val brakePadCycle = Cycle(20000.0)
    private val timingBeltCycle = Cycle(80000.0)

    // **** 거리측정을 위한 데이터 **** //
    var location: Location? = null // 거리측정에 사용되는 위치정보
    var isStart: Boolean = false // 주행시작 버튼이 눌렸는지에 대한 플래그

    private val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        totalDistance.value = Repository.db.driveDAO().getTotalDistance()
        curDistance.value = 0.0
        refreshParts()
        setDriveInfo()
    }

    // **** 주행 시작 버튼 **** //
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

    // **** 주행 종료 버튼 **** //
    fun endDrive() {
        totalDistance.value = curDistance.value?.let { totalDistance.value?.plus(it) }
        locationManager.removeUpdates(this)
        var timeNow = System.currentTimeMillis()
        var date = Date(timeNow)
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var now = sdf.format(date)
        curDistance.value?.let { DriveEntity(now, it) }?.let { Repository.db.driveDAO().insert(it) }
        totalDistance.value = Repository.db.driveDAO().getTotalDistance()
        curDistance.value = 0.0
        location = null
        isStart = false

    }

    private fun calculatePart2(partName: String, partCycle: Cycle): PartData? { // 남은 거리를 계산하는 메소드
        var lastDistance = totalDistance.value?.minus(Repository.mMySharedPreferences.getDistance(partName, 0f))
        return lastDistance?.let { partCycle.getPartData(it) }
    }

    fun refreshParts(){
        engineOil.value = calculatePart2("EngineOil", engineOilCycle)
        engineOilDistance.value = engineOil.value!!.remainingDistance
        autoOil.value = calculatePart2("AutoOil", autoOilCycle)
        autoOilDistance.value = autoOil.value!!.remainingDistance
        powerOil.value = calculatePart2("PowerOil", powerOilCycle)
        powerOilDistance.value = powerOil.value!!.remainingDistance
        brakeOil.value = calculatePart2("BrakeOil", brakeOilCycle)
        brakeOilDistance.value = brakeOil.value!!.remainingDistance
        brakePad.value = calculatePart2("BrakePad", brakePadCycle)
        brakePadDistance.value = brakePad.value!!.remainingDistance
        timingBelt.value = calculatePart2("TimingBelt", timingBeltCycle)
        timingBeltDistance.value = timingBelt.value!!.remainingDistance
    }

    fun calculateCycle(partName: String) {  // 교체버튼이 눌렸을 때,
        when (partName) {
            "EngineOil" -> {
                // 총 주행거리를 sp에 저장
                totalDistance.value?.let { Repository.mMySharedPreferences.setDistance(partName, it) }
                // 저장한 거리를 기반으로 남은 거리와 상태를 계산 -> 무조건 PartData(0,2) 이 반환됨
                // 반환된 PartData를 LivaData에 적용
                engineOil.value = calculatePart2(partName, engineOilCycle)
                engineOilDistance.value = engineOil.value!!.remainingDistance
            }
            "AutoOil" -> {
                totalDistance.value?.let { Repository.mMySharedPreferences.setDistance(partName, it) }
                autoOil.value = calculatePart2(partName, autoOilCycle)
                autoOilDistance.value = autoOil.value!!.remainingDistance
            }
            "PowerOil" -> {
                totalDistance.value?.let { Repository.mMySharedPreferences.setDistance(partName, it) }
                powerOil.value = calculatePart2(partName, powerOilCycle)
                powerOilDistance.value = powerOil.value!!.remainingDistance
            }
            "BrakeOil" -> {
                totalDistance.value?.let { Repository.mMySharedPreferences.setDistance(partName, it) }
                brakeOil.value = calculatePart2(partName, brakeOilCycle)
                brakeOilDistance.value = brakeOil.value!!.remainingDistance
            }
            "BrakePad" -> {
                totalDistance.value?.let { Repository.mMySharedPreferences.setDistance(partName, it) }
                brakePad.value = calculatePart2(partName, brakePadCycle)
                brakePadDistance.value = brakePad.value!!.remainingDistance
            }
            "TimingBelt" -> {
                totalDistance.value?.let { Repository.mMySharedPreferences.setDistance(partName, it) }
                timingBelt.value = calculatePart2(partName, timingBeltCycle)
                timingBeltDistance.value = timingBelt.value!!.remainingDistance
            }
        }
    }

    // **** 주행 정보 불러오기 **** //
    fun setDriveInfo() {
        var dataList = Repository.db.driveDAO().getAll()
        var cnt = 1
        var str_date = ""
        var str_distance = ""
        for (i in dataList) {
            if (cnt != dataList.size) {
                str_date += (i.driveDate + "\n")
                str_distance += (i.distance.toString() + " Km\n")
                cnt++
            } else {
                str_date += i.driveDate
                str_distance += (i.distance.toString() + " Km")
                cnt = 0
            }
        }
        driveDate.value = str_date
        driveDistance.value = str_distance
    }

    // **** 위치 정보가 변경될 때마다 실행됨 **** //
    override fun onLocationChanged(newlocation: Location) {
        if (location != null) {
            var tempDistance = round(newlocation.distanceTo(location).toDouble())
            curDistance.value = curDistance.value?.plus(tempDistance)
        }
        location = newlocation
    }

}