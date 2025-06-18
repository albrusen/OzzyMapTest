package com.example.presentation.mapscreen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.mapscreen.utils.CellCluster
import com.example.presentation.mapscreen.utils.CellData
import com.example.presentation.mapscreen.utils.ClusterBitmapDescriptor
import com.example.presentation.mapscreen.utils.createSingleMarkerBitmapDescriptor
import com.example.presentation.mapscreen.utils.getMapBounds
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.maps.presentation.R

@Composable
fun MapScreen(
    viewModel: MapScreenModel = hiltViewModel(),
    lat: Double = -38.1819,
    lon: Double = 176.2591,
    initialZoom: Float = 15f
) {
    val initialLocation = remember { LatLng(lat, lon) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, initialZoom)
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val visibleCellStations by viewModel.visibleCellClusterStations.collectAsState()
    var currentMarker by remember { mutableStateOf<Marker?>(null) }
    val stations by viewModel.loadedStations.collectAsState()
    val stationsCount by viewModel.currentStationsCount.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()

    SideEffect {
        Log.d("ComposeDebug", "Recompose")
    }

    fun updateMapCameraState() {
        val mapBounds = getMapBounds(cameraPositionState, screenWidthPx, screenHeightPx)
        mapBounds?.let {
            viewModel.updateMapCameraState(it, cameraPositionState.position.zoom)
        }
    }

    fun onMarkerClick(marker: Marker, station: CellCluster) {
        if (marker == currentMarker) {
            currentMarker = null
            marker.hideInfoWindow()
            viewModel.clearSelection()
        } else {
            viewModel.loadStations(station)
            currentMarker?.hideInfoWindow()
            currentMarker = marker
            marker.showInfoWindow()
        }
    }

    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) currentMarker?.hideInfoWindow()
        if (!cameraPositionState.isMoving) updateMapCameraState()
    }

    LaunchedEffect(key1 = selectedCell) {
        selectedCell?.let {
            val newPos = CameraUpdateFactory.newLatLngZoom(it.position, cameraPositionState.position.zoom)
            cameraPositionState.animate(newPos)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { updateMapCameraState() },
    ) {
        if (selectedCell != null) {
            SingleCellMarker(selectedCell!!, {
//                viewModel.dismissCell()
            })
        } else {
            visibleCellStations.forEach { station ->
                CellStationMarker(station,
                    onMarkerClick = {
                        mark -> onMarkerClick(mark, station)
                    }
                )
            }
        }
    }
    ClusterDetails(stations, stationsCount)
}

@Composable
fun SingleCellMarker(station: CellData,
                     onMarkerClick: (Marker) -> Unit
) {
    Marker(
        state = rememberUpdatedMarkerState(position = station.position),
        title = "Station id ${station.CELLID}",
        snippet = station.snippet,
        // You can add a custom icon if you want later:
        icon = createSingleMarkerBitmapDescriptor(markerDrawableResId = R.drawable.marker_map_icon,
            title = station.title),
        onClick = { marker ->
            onMarkerClick(marker) // Pass the clicked marker to the parent
            true // Return true to indicate that the event has been consumed
        }
    )
}

@Composable
fun CellStationMarker(
    station: CellCluster,
    onMarkerClick: (Marker) -> Unit
) {
    SideEffect {
        Log.d("ComposeDebug", "Recompose CellStationMarker${station.CentroidLat}")
    }
    Marker(
        state = rememberUpdatedMarkerState(position = station.position),
        title = station.title,
        snippet = station.snippet,
        // You can add a custom icon if you want later:
        icon = ClusterBitmapDescriptor(station.NumberOfCellsInCluster),
        onClick = { marker ->
            onMarkerClick(marker) // Pass the clicked marker to the parent
            true // Return true to indicate that the event has been consumed
        }
    )
}