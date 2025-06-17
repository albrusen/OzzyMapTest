package com.example.presentation.mapscreen.utils

import androidx.compose.runtime.Immutable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.Locale
import com.example.domain.api.CellData as DomainCellData
import com.example.domain.api.CellCluster as DomainCellCluster

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


@Immutable
data class CellCluster(
    val lat_bucket: Int,
    val lon_bucket: Int,
    val NumberOfCellsInCluster: Int,
    val minLat: Double,
    val minLon: Double,
    val maxLat: Double,
    val maxLon: Double,
    val CentroidLat: Double,
    val CentroidLon: Double,
    val RepresentativeCellId: Int
)  : ClusterItem {
    fun formattedPos(pos: Double) = String.format(Locale.US,"%.4f", pos)
    fun centerLat() = CentroidLat
    fun centerLon() = CentroidLon
    override fun getPosition(): LatLng = LatLng(centerLat(), centerLon())
    override fun getTitle(): String = "$NumberOfCellsInCluster Станций"
    override fun getSnippet(): String = ""
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


fun DomainCellCluster.toUI1(): CellCluster {
    return CellCluster(
        lat_bucket = this.lat_bucket,
        lon_bucket = this.lon_bucket,
        NumberOfCellsInCluster = this.NumberOfCellsInCluster,
        minLon = this.minLon,
        maxLon = this.maxLon,
        minLat = this.minLat,
        maxLat = this.maxLat,
        CentroidLat = this.CentroidLat,
        CentroidLon = this.CentroidLon,
        RepresentativeCellId = this.RepresentativeCellId,
    )
}