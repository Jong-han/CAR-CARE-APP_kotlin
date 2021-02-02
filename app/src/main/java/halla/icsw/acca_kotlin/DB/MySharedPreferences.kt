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

    fun setExistUserData(value: Boolean) {
        mMySharedPreferences.edit().putBoolean("userData", value).apply()
    }

    fun getExistUserData(): Boolean {
        return mMySharedPreferences.getBoolean("userData", false)
    }

    fun setUserOilKind(value: String) {
        mMySharedPreferences.edit().putString("oilKind", value).apply()
    }

    fun getUserOilKind(): String {
        return mMySharedPreferences.getString("oilKind", "B027")!!
    }

    fun setUserDistance(value: Double) {
        mMySharedPreferences.edit().putFloat("userDistance",value.toFloat()).apply()
    }

    fun getUserDistance(): Double {
        return mMySharedPreferences.getFloat("userDistance",0f).toDouble()
    }
}