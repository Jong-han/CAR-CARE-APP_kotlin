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


        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {

                Toast.makeText(context, "메시지 수신", Toast.LENGTH_SHORT).show()
                val bundle = intent.extras
                var message = "" // SMS 저장
                var price = 0 // 유가
                var totalPrice = 0 // 주유 가격
                var today = "" // 주유 날짜
                var x = location!!.longitude
                var y = location!!.latitude
                var katec = GeoTrans.convert(GeoTrans.GEO, GeoTrans.KATEC, GeoTransPoint(x, y))
                var katec_x = katec.x
                var katec_y = katec.y

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

                    var isStation = message.contains("주유소") || message.contains("지에스칼텍스")
                    var isCancel = message.contains("취소") || message.contains("거절")
                    if (isStation && !isCancel) {

                        var timeNow = System.currentTimeMillis()
                        var date = Date(timeNow)
                        var sdf = SimpleDateFormat("yyyy-MM-dd")
                        today = sdf.format(date)

                        var messageArray = message.split("\n", " ")
                        for (index in messageArray) {
                            if (index.contains(",")) {
                                var temp_valueindex = index.slice(IntRange(0, index.indexOf("원")))
                                totalPrice = temp_valueindex.replace(",", "").replace("원", "").toInt()
                                break
                            }
                        }
                    }
                    /// ***** OPINET API 호출 ***** ///
                    api.getOilStationInfo("F792200616", katec_x, katec_y, 100, 2, "B027", "json")
                            .enqueue(object : Callback<Result> {
                                override fun onResponse(call: Call<Result>, response: Response<Result>) {
                                    var success = response.isSuccessful
                                    if (success) {
                                        for (i in response.body()?.result?.oil!!) {
                                            price = i.oilPrice
                                            Repository.db.oilDAO().insert(OilEntity(today, price, totalPrice))
                                            location = null
                                            break;
                                        }
                                    }
                                }
                                override fun onFailure(call: Call<Result>, t: Throwable) {
                                }
                            })
                }
            }
        }
    }
}