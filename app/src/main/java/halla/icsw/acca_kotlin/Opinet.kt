package halla.icsw.acca_kotlin

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("RESULT")
    val result: Oil
)

data class Oil(
    @SerializedName("OIL")
    val  oil: List<info> = listOf()
)

data class info(
    @SerializedName("UNI_ID")
    val oilStation_id: String,
    @SerializedName("POLL_DIV_CD")
    val brand: String,
    @SerializedName("OS_NM")
    val oilStation_name: String,
    @SerializedName("PRICE")
    val oilPrice : Int,
    @SerializedName("DISTANCE")
    val hereToDistance : Double,
    @SerializedName("GIS_X_COOR")
    val xkatec: Double,
    @SerializedName("GIS_Y_COOR")
    val ykatec: Double
)

