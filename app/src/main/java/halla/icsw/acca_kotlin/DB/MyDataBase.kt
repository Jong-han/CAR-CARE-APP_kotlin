package halla.icsw.acca_kotlin.DB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DriveEntity::class, OilEntity::class), version = 1)
abstract class MyDataBase : RoomDatabase() {
    abstract fun driveDAO(): DriveDAO
    abstract fun oilDAO(): OilDAO
}