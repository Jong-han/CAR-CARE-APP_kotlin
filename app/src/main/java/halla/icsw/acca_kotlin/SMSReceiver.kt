package halla.icsw.acca_kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import halla.icsw.acca_kotlin.DB.OilEntity
import halla.icsw.acca_kotlin.DB.Repository
import java.text.SimpleDateFormat
import java.util.*

class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Toast.makeText(context, "메시지 수신", Toast.LENGTH_SHORT).show()
            val bundle = intent.extras
            var message = "" // SMS 저장
            var price = 0 // 유가
            var totalPrice = 0 // 주유 가격
            var today = "" // 주유 날짜


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
                            totalPrice = temp_valueindex.replace(",","").replace("원","").toInt()
                            break
                        }
                    }
                }
                Repository.db.oilDAO().insert(OilEntity(today,price,totalPrice))
                Log.d("메시지 처리", today + " " + totalPrice)
            }
        }
    }
}