package halla.icsw.acca_kotlin.DB

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Repository : Application() {
    companion object {
        lateinit var mMySharedPreferences: MySharedPreferences
        lateinit var db: MyDataBase
    }

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE 'OilRecord' ADD COLUMN 'totalPrice' INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE 'OilRecord' ADD COLUMN 'isChecked' INTEGER NOT NULL DEFAULT 0")
        }
    }

    override fun onCreate() {
        db = Room.databaseBuilder(
            this.applicationContext,
            MyDataBase::class.java,
            "DataBase.db"
        ).allowMainThreadQueries().addMigrations(MIGRATION_2_3).build()
        mMySharedPreferences = MySharedPreferences(applicationContext)
        super.onCreate()
    }
}