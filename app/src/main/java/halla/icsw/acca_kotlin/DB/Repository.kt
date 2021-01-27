package halla.icsw.acca_kotlin.DB

import android.app.Application
import androidx.room.Room

class Repository : Application() {
    companion object {
        lateinit var mMySharedPreferences: MySharedPreferences
        lateinit var db: MyDataBase
    }

    override fun onCreate() {
        db = Room.databaseBuilder(
            this.applicationContext,
            MyDataBase::class.java,
            "DataBase.db"
        ).allowMainThreadQueries().build()
        mMySharedPreferences = MySharedPreferences(applicationContext)
        super.onCreate()
    }
}