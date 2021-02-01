package halla.icsw.acca_kotlin.DB

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(DriveEntity::class, OilEntity::class), version = 3)
abstract class MyDataBase : RoomDatabase() {
    abstract fun driveDAO(): DriveDAO
    abstract fun oilDAO(): OilDAO
}