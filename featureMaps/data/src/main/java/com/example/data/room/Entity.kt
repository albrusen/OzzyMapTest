package com.example.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.api.CellData as DomainCellData

@Entity(tableName = "cell_data")
data class CellData(
    @PrimaryKey(autoGenerate = true)
    val ID: Int = 0,

    @ColumnInfo(name = "MCC") val mcc: Int?,
    @ColumnInfo(name = "MNC") val mnc: Int?,
    @ColumnInfo(name = "LAC") val lac: Int?,
    @ColumnInfo(name = "CELLID") val cellId: Int?,
    @ColumnInfo(name = "PSC") val psc: Int?,
    @ColumnInfo(name = "RAT") val rat: String?,
    @ColumnInfo(name = "LAT") val lat: Double?,
    @ColumnInfo(name = "LON") val lon: Double?,
)

fun CellData.toDomain(): DomainCellData {
    return DomainCellData(
        LAT = this.lat!!,
        LON = this.lon!!,
        MCC = this.mcc!!,
        MNC = this.mnc!!,
        LAC = this.lac!!,
        CELLID = this.cellId!!,
        RAT = this.rat!!,
    )
}



