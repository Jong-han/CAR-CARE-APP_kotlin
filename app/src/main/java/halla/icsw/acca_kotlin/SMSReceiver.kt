package halla.icsw.acca_kotlin

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import halla.icsw.acca_kotlin.DB.OilEntity
import halla.icsw.acca_kotlin.DB.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SMSReceiver : BroadcastReceiver() {

    var location: Location? = null

    override fun onReceive(context: Context, intent: Intent) {

        /// ***** LOCATION PERMISSION CHECK ***** ///
        if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

//        val isGPSEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        val isNetworkEnabled: Boolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
//            when{
//                isGPSEnabled -> location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//                isNetworkEnabled -> location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                else -> Toast.makeText(context, "Provider 비활성", Toast.LENGTH_SHORT).show()
//            }

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)


            if (location != null) {
                Toast.makeText(context, "메시지 수신", Toast.LENGTH_SHORT).show()
                val bundle = intent.extras
                var message = "" // SMS 저장
                var price = 0 // 유가
                var totalPrice = 0 // 주유 가격
                var today = "" // 주유 날짜
                val x = location!!.longitude
                val y = location!!.latitude
                val katec = GeoTrans.convert(GeoTrans.GEO, GeoTrans.KATEC, GeoTransPoint(x, y))
                val katec_x = katec.x
                val katec_y = katec.y

                val api = Retrofit_Interface.create()

                if (bundle != null) { // 수신된 내용이 있으면
                    // 실제 메세지는 Object타입의 배열에 PDU 형식으로 저장됨
                    val pdus = bundle["pdus"] as Array<*>?
                    val msgs = arrayOfNulls<SmsMessage>(
                            pdus!!.size
                    )
                    for (i in msgs.indices) {
                        // PDU 포맷으로 되어 있는 메시지를 복원합니다.
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, bundle.getString("format"))
                        } else {
                            @Suppress("DEPRECATION")
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        }
                        message += msgs[i]?.messageBody.toString()
                    }

                    val isStation = message.contains("주유소") || message.contains("지에스칼텍스")
                    val isCancel = message.contains("취소") || message.contains("거절")
                    if (isStation && !isCancel) {

                        val timeNow = System.currentTimeMillis()
                        val date = Date(timeNow)
                        val sdf = SimpleDateFormat("yyyy-MM-dd")
                        today = sdf.format(date)

                        val messageArray = message.split("\n", " ")
                        for (index in messageArray) {
                            if (index.contains(",")) {
                                val temp_valueindex = index.slice(IntRange(0, index.indexOf("원")))
                                totalPrice = temp_valueindex.replace(",", "").replace("원", "").toInt()
                                break
                            }
                        }
                    }
                    /// ***** OPINET API 호출 ***** ///
                    val oilKind = Repository.mMySharedPreferences.getUserOilKind()
                    api.getOilStationInfo("F792200616", katec_x, katec_y, 100, 2, oilKind, "json")
                            .enqueue(object : Callback<Result> {
                                override fun onResponse(call: Call<Result>, response: Response<Result>) {
                                    if (response.isSuccessful) {
                                        response.body()?.result?.oil.let {
                                            for (i in it!!) {
                                                price = i.oilPrice
                                                var isChecked = 0
                                                if (Repository.mMySharedPreferences.getIsChecked("Check", false))
                                                    isChecked = 1
                                                Repository.db.oilDAO().insert(OilEntity(today, price, totalPrice, isChecked))
                                                location = null
                                                break
                                            }
                                        }
                                    }
                                }
                                override fun onFailure(call: Call<Result>, t: Throwable) {
                                }
                            })
                }
            }else
                Toast.makeText(context, "위치정보 수신 안됨!!", Toast.LENGTH_SHORT).show()
        }
    }
}