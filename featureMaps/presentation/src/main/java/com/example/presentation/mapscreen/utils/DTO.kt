package com.example.presentation.mapscreen.utils

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.example.domain.api.CellData as DomainCellData

@Immutable
data class CellData(
    val LAT: Double,
    val LON: Double,
    val MCC: Int,
    val MNC: Int,
    val LAC: Int,
    val CELLID: Int,
    val RAT: String,
    val NET: Int
)  : ClusterItem {
    override fun getPosition(): LatLng = LatLng(LAT, LON)
    override fun getTitle(): String = "Станция ${CELLID}"
    override fun getSnippet(): String = "MCC: ${MCC}, MNC: ${MNC}, LAC: ${LAC}, RAT: $RAT"
    // Если хочешь кастомный ID для кластера, можно добавить getZIndex и getTag
    override fun getZIndex(): Float? = null
}

fun DomainCellData.toUI(): CellData {
    return CellData(
        LAT = this.LAT,
        LON = this.LON,
        MCC = this.MCC,
        MNC = this.MNC,
        LAC = this.LAC,
        CELLID = this.CELLID,
        RAT = this.RAT,
        NET = this.NET,
    )
}