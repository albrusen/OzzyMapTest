package com.example.data.room

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CellDataDao {
    @Query("SELECT * FROM cell_data")
    fun getAllCellData(): Flow<List<CellData>>


    @Query("SELECT * FROM cell_data WHERE LAT BETWEEN :minLat AND :maxLat AND LON BETWEEN :minLon AND :maxLon")
    fun getCellDataInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<CellData>>

    @Query("""
        SELECT COUNT(*)
        FROM cell_data
        WHERE LAT BETWEEN :minLat AND :maxLat
        AND LON BETWEEN :minLon AND :maxLon
    """)
    fun getCellCountInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Int

}