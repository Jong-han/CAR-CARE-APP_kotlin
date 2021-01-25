package halla.icsw.acca_kotlin.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OilDAO {
    @Insert
    fun insert(entity: OilEntity)

    @Query("SELECT * FROM OilRecord")
    fun getAll() : List<OilEntity>

//    @Query("SELECT SUM(Price) FROM OilRecord")
//    fun getTotalPrice() : OilEntity
}