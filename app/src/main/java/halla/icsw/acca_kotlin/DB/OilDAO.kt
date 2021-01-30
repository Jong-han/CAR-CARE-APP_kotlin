package halla.icsw.acca_kotlin.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OilDAO {
    @Insert
    fun insert(entity: OilEntity)

    @Query("SELECT * FROM OilRecord")
    fun getAll() : LiveData<List<OilEntity>>

    @Query("SELECT SUM(totalPrice) FROM OilRecord")
    fun getTotalPrice() : LiveData<Int>
}