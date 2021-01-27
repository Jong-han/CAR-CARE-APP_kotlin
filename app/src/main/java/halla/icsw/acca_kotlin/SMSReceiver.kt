package halla.icsw.acca_kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Toast.makeText(context, "메시지 수신", Toast.LENGTH_SHORT).show()
        }
    }
}