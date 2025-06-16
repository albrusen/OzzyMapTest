package com.example.domain.api

import kotlinx.coroutines.flow.Flow

interface CellProvider {
    fun getCountDataInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Int
    fun getCellDataInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<CellData>>
}

data class CellData(
    val LAT: Double,
    val LON: Double,
    val MCC: Int,
    val MNC: Int,
    val LAC: Int,
    val CELLID: Int,
    val RAT: String,
)