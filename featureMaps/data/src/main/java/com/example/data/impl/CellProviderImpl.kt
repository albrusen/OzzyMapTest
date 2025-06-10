package com.example.data.impl

import com.example.data.room.AppDatabase
import com.example.data.room.toDomain
import com.example.domain.api.CellData
import com.example.domain.api.CellProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CellProviderImpl(
    val db: AppDatabase
): CellProvider {

    override fun getCountDataInBounds(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Int {
        if (maxLon - minLon > 180) {
            return db.cellDataDao().getCellCountInBoundsAntimeridian(minLat, maxLat, minLon, maxLon)
        }
        return db.cellDataDao().getCellCountInBounds(minLat, maxLat, minLon, maxLon)
    }

    override fun getCellDataInBounds(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<CellData>> {
        if (maxLon - minLon > 180) {
            return db.cellDataDao()
                .getCellDataInBoundsAntimeridian(minLat, maxLat, minLon, maxLon)
                .map { list ->
                    list.map {
                        it.toDomain()
                    }
                }
        }
        return db.cellDataDao()
            .getCellDataInBounds(minLat, maxLat, minLon, maxLon)
            .map { list ->
                list.map {
                    it.toDomain()
                }
            }
    }

}