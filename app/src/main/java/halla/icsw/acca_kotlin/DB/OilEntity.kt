package halla.icsw.acca_kotlin.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OilRecord")
data class OilEntity(
    var OilStation: String = "",   // 날짜  OilStation이지만..하여튼 date임..
    var Price: Int = 0,            // 유가
    var totalPrice: Int = 0                              //주유 가격
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
