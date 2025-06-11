package com.example.presentation.mapscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.CellProvider
import com.example.presentation.mapscreen.utils.CellData
import com.example.presentation.mapscreen.utils.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val MAX_ALLOWED_STATIONS_IN_MEMORY = 5000
    private val _currentMapCameraState = MutableStateFlow<MapCameraState?>(null)

    private val _currentStationsCount = MutableStateFlow(0)

    val showZoomInWarning: StateFlow<Boolean> = _currentStationsCount
        .map { count ->
            count > MAX_ALLOWED_STATIONS_IN_MEMORY
        }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)



    val visibleCellStations: StateFlow<List<CellData>> = _currentMapCameraState
        .filterNotNull()
        .flatMapLatest { cameraState ->
                val bounds = cameraState.bounds
                val count = cellProvider.getCountDataInBounds(
                    bounds.minLat, bounds.maxLat, bounds.minLon, bounds.maxLon
                )
            _currentStationsCount.value = count
            if (count > MAX_ALLOWED_STATIONS_IN_MEMORY) {
                    Log.d("MapScreenModel", "Too many stations ($count) in bounds, max allowed is $MAX_ALLOWED_STATIONS_IN_MEMORY. Zoom in more!")
                    flowOf(emptyList()) // Возвращаем пустой список, если станций слишком много
                } else {
                    Log.d("MapScreenModel", "Loading $count stations in bounds.")
                    cellProvider.getCellDataInBounds(bounds.minLat, bounds.maxLat, bounds.minLon, bounds.maxLon)
                        .map {
                            list -> list.map { it.toUI() }
                        }
                }
        }
        .flowOn(Dispatchers.IO)
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