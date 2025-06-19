package com.example.presentation.mapscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.CellProvider
import com.example.presentation.mapscreen.utils.CellCluster
import com.example.presentation.mapscreen.utils.CellData
import com.example.presentation.mapscreen.utils.toUI
import com.example.presentation.mapscreen.utils.toUI1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapScreenModel @Inject constructor(
    val cellProvider: CellProvider
): ViewModel() {

    private val DEGREES_PIXEL = 125
    val MAX_ALLOWED_STATIONS_IN_MEMORY = 8000
    private val _currentMapCameraState = MutableStateFlow<MapCameraState?>(null)

    val currentStationsCount = MutableStateFlow(0)
    private val _selectedCluster = MutableStateFlow<CellCluster?>(null)
    val selectedCell = MutableStateFlow<CellData?>(null)

    val loadedStations : StateFlow<List<CellData>?> = _selectedCluster
        .flatMapLatest { cluster ->
            if (null == cluster) {
                currentStationsCount.value = 0
                return@flatMapLatest flowOf(null)
            }
            loadCellsInCluster(cluster)
        }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val visibleCellClusterStations: StateFlow<List<CellCluster>> = _currentMapCameraState
        .filterNotNull()
        .flatMapLatest { cameraState ->
            val bounds = cameraState.bounds
            Log.d("MapScreenModel", "Loading stations in bounds. begin")
            val count = cellProvider.getCountDataInBounds(
                bounds.minLat, bounds.maxLat, bounds.minLon, bounds.maxLon
            )
            val distanceLat = (bounds.maxLat - bounds.minLat)
            val distanceLon = (bounds.maxLon - bounds.minLon)
            val time = System.currentTimeMillis()
            val gridLat = 8
            val gridLon = 8
            val counts = mutableListOf<CellCluster>()
            val k = mutableListOf<Deferred<CellCluster>>()
            for (i in 0..< gridLat) {
                for (j in 0..< gridLon) {
                    k.add(viewModelScope.async(Dispatchers.IO) {
                        return@async cellProvider.getCellDataClusterInBounds(
                            bounds.minLat + distanceLat / gridLat * i,
                            bounds.minLat + distanceLat / gridLat * (i + 1),
                            bounds.minLon + distanceLon / gridLon * j,
                            bounds.minLon + distanceLon / gridLon * (j + 1)

                        ).toUI1()
                    })
                    }
                }
            k.awaitAll()
            k.forEach {
                counts.add(it.getCompleted())
            }
            Log.d("MapScreenModel", "Loading stations wewe $count in bounds. end = ${System.currentTimeMillis()-time}")
            flowOf(counts)
        }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateMapCameraState(bounds: MapBounds, zoom: Float) {
        viewModelScope.launch {
            _currentMapCameraState.value = MapCameraState(bounds, zoom)
        }
    }

    private fun loadCellsInCluster(cluster: CellCluster): Flow<List<CellData>?> {
        val minLat = cluster.minLat
        val maxLat = cluster.maxLat
        val minLon = cluster.minLon
        val maxLon = cluster.maxLon
        val count = cellProvider.getCountDataInBounds(minLat, maxLat, minLon, maxLon)
        currentStationsCount.value = count
        if (count > MAX_ALLOWED_STATIONS_IN_MEMORY) {
            return flowOf(null)
        }

        return cellProvider.getCellDataInBounds(minLat, maxLat, minLon, maxLon).map { list->list.map { it.toUI() } }
    }

    fun loadStations(cluster: CellCluster) {
        _selectedCluster.value = cluster
    }

    fun clearSelection() {
        _selectedCluster.value = null
    }

    fun dismissCell() {
        selectedCell.value = null
    }

    fun onSingleCellMarkerClick(cellData: CellData) {
        selectedCell.value = cellData
    }

}

data class MapBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)

data class MapCameraState(
    val bounds: MapBounds,
    val zoom: Float
)