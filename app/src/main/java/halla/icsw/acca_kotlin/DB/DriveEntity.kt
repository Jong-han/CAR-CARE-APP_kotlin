package halla.icsw.acca_kotlin.DB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DriveRecord")
data class DriveEntity(
        var driveDate: String = "",
        var distance: Double = 0.0
){
        @PrimaryKey(autoGenerate = true) var id: Int = 0
}
