package halla.icsw.acca_kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.widget.Toast

class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Toast.makeText(context, "메시지 수신", Toast.LENGTH_SHORT).show()
//            val bundle = intent.extras
//            var str = "" // 출력할 문자열 저장
//
//            if (bundle != null) { // 수신된 내용이 있으면
//                // 실제 메세지는 Object타입의 배열에 PDU 형식으로 저장됨
//                // 수신된 내용이 있으면
//                // 실제 메세지는 Object타입의 배열에 PDU 형식으로 저장됨
//                val pdus = bundle["pdus"] as Array<Any>?
//                val msgs = arrayOfNulls<SmsMessage>(
//                    pdus!!.size
//                )
//                for (i in msgs.indices) {
//                    // PDU 포맷으로 되어 있는 메시지를 복원합니다.
//                    msgs[i] = SmsMessage.createFromPdu(pdus!![i] as ByteArray)
//                    str += msgs[i]?.getMessageBody().toString()
//                }
//            }
        }
    }
}