package halla.icsw.acca_kotlin.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OilRecord")
data class OilEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var OilStation: String = "",
    var Price: Int = 0
)
