package halla.icsw.acca_kotlin.DB

import android.app.Application

class Repository : Application() {
    companion object {
        lateinit var mMySharedPreferences: MySharedPreferences
    }
    override fun onCreate() {
        mMySharedPreferences = MySharedPreferences(applicationContext)
        super.onCreate()
    }
}