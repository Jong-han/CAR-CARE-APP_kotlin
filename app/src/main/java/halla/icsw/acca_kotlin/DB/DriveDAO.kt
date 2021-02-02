package halla.icsw.acca_kotlin.DB

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DriveDAO {

    @Insert
    fun insert(entity: DriveEntity)

    @Query("SELECT * FROM DriveRecord")
    fun getAll(): LiveData<List<DriveEntity>>

    @Query("SELECT SUM(distance) FROM DriveRecord")
    fun getTotalDistance(): Double

    @Query("SELECT driveDate FROM DriveRecord")
    fun getdate(): String

    @Query("SELECT distance FROM DriveRecord")
    fun getdistance(): Double

}