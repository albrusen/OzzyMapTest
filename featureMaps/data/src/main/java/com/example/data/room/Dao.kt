package com.example.data.room

import androidx.room.Dao
import androidx.room.Query
import com.example.domain.api.CellCluster
import kotlinx.coroutines.flow.Flow


@Dao
interface CellDataDao {
    @Query("SELECT * FROM cell_data")
    fun getAllCellData(): Flow<List<CellData>>

    @Query("""
        SELECT
            COUNT(*) AS NumberOfCellsInCluster,
            AVG(LAT) AS CentroidLat,                
            AVG(LON) AS CentroidLon,
            :minLat AS minLat,
            :maxLat AS maxLat,
            :minLon AS minLon,
            :maxLon AS maxLon
        FROM
            cell_data
        WHERE LAT BETWEEN :minLat AND :maxLat AND LON BETWEEN :minLon AND :maxLon         
    """)
    fun getCellDataClusterInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): CellCluster


    @Query("SELECT * FROM cell_data WHERE LAT BETWEEN :minLat AND :maxLat AND LON BETWEEN :minLon AND :maxLon")
    fun getCellDataInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<CellData>>

    @Query("""
        SELECT COUNT(*)
        FROM cell_data
        WHERE LAT BETWEEN :minLat AND :maxLat
        AND LON BETWEEN :minLon AND :maxLon
    """)
    fun getCellCountInBounds(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Int


    @Query("""SELECT * 
            FROM cell_data 
            WHERE LAT BETWEEN :minLat AND :maxLat 
              AND ( 
                    (LON <= :minLon AND LON <= -180.0)
                    OR
                    (LON <= 180.0 AND LON >= :maxLon)
                  )""")
    fun getCellDataInBoundsAntimeridian(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Flow<List<CellData>>

    @Query("""
        SELECT COUNT(*)
        FROM cell_data 
            WHERE LAT BETWEEN :minLat AND :maxLat 
              AND ( 
                    (LON <= :minLon AND LON <= -180.0)
                    OR
                    (LON <= 180.0 AND LON >= :maxLon)
                  )
    """)
    fun getCellCountInBoundsAntimeridian(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double): Int

}