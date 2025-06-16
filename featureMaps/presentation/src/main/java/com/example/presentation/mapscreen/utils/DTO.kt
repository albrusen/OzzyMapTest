package com.example.presentation.mapscreen.utils

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.Locale
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
)  : ClusterItem {
    val formattedLat = String.format(Locale.US,"%.4f", LAT)
    val formattedLon = String.format(Locale.US,"%.4f", LON)
    override fun getPosition(): LatLng = LatLng(LAT, LON)
    override fun getTitle(): String = "Станция ${CELLID}, локация: $formattedLat / $formattedLon"
    override fun getSnippet(): String = "MCC: ${MCC}, MNC: ${MNC}, LAC: ${LAC}, RAT: $RAT"
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
    )
}