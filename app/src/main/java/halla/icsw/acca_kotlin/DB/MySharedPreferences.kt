package halla.icsw.acca_kotlin.DB

import android.content.Context

class MySharedPreferences(context: Context) {
    private val mMySharedPreferences =
        context.getSharedPreferences("distance", Context.MODE_PRIVATE)

    fun getDistance(key: String, defValue: Float): Double {
        return mMySharedPreferences.getFloat(key, defValue).toDouble()
    }

    fun setDistance(key: String, value: Double) {
        mMySharedPreferences.edit().putFloat(key, value.toFloat()).apply()
    }

    fun setIsChecked(key: String, value: Boolean) {
        mMySharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getIsChecked(key: String, defValue: Boolean): Boolean {
        return mMySharedPreferences.getBoolean(key, defValue)
    }

    fun getLiter(key: String, defValue: Float): Double {
        return mMySharedPreferences.getFloat(key, defValue).toDouble()
    }

    fun setLiter(key: String, value: Double) {
        mMySharedPreferences.edit().putFloat(key, value.toFloat()).apply()
    }

    fun getAfterCheckDistance(key: String, defValue: Float): Double {
        return mMySharedPreferences.getFloat(key, defValue).toDouble()
    }

    fun setAfterCheckDistance(key: String, value: Double) {
        mMySharedPreferences.edit().putFloat(key, value.toFloat()).apply()
    }
}