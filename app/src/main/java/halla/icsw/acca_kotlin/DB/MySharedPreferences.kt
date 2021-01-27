package halla.icsw.acca_kotlin.DB

import android.content.Context

class MySharedPreferences (context: Context){
    private val mMySharedPreferences = context.getSharedPreferences("distance", Context.MODE_PRIVATE)

    fun getDistance(key: String, defValue: Float = 0f) : Double{
        return mMySharedPreferences.getFloat(key,defValue).toDouble()
    }

    fun setDistance(key: String, Value: Double){
        mMySharedPreferences.edit().putFloat(key,Value.toFloat()).apply()
    }
}