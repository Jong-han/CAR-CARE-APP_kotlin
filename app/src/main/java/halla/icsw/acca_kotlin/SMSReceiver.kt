package halla.icsw.acca_kotlin

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
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

        var notificationId = 1000
        val CHANNEL_ID = "Maintenance_noti"
        val channel_name = "ACCA"

        //*********** NOTIFICATION ***********//
        fun createNotificationChannel(
                builder: NotificationCompat.Builder,
                notificationId: Int,
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val descriptionText = "1번 채널입니다. "
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, channel_name, importance).apply {
                    description = descriptionText
                }

                channel.lightColor = Color.BLUE
                channel.enableVibration(true)
                // Register the channel with the system
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                notificationManager.notify(notificationId, builder.build())

            } else {
                val notificationManager: NotificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, builder.build())
            }
        }

        fun showNotification() {
            val intent = Intent(context, MainActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = context?.let { NotificationCompat.Builder(it, CHANNEL_ID) }
                    ?.setSmallIcon(R.mipmap.ic_acca)
                    ?.setContentTitle("주유정보 저장 실패")
                    ?.setContentText("위치를 찾을 수 없어 주유정보를 저장하지 못했습니다.  수동으로 입력해주세요.")
                    ?.setAutoCancel(true)
                    ?.setPriority(NotificationCompat.PRIORITY_HIGH)
                    ?.setFullScreenIntent(pendingIntent, true)
            if (builder != null) {
                createNotificationChannel(builder, notificationId)
            }
        }
        //*********** NOTIFICATION ***********//

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

            val bundle = intent.extras
            var message = "" // SMS 저장
            var price = 0 // 유가
            var totalPrice = 0 // 주유 가격
            var today = "" // 주유 날짜

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
                    if (location != null) {  // 위치정보를 받아왔는지?
                        val x = location!!.longitude
                        val y = location!!.latitude
                        val katec = GeoTrans.convert(GeoTrans.GEO, GeoTrans.KATEC, GeoTransPoint(x, y))
                        val katec_x = katec.x
                        val katec_y = katec.y
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
                    } else  // 주유소 결제 문자를 수신했으나, 위치를 받아오지 못했을 때 알림을 보냄
                        showNotification()
                }
            }
        }
    }
}