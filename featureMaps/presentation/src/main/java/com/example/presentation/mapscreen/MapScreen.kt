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
import com.example.presentation.mapscreen.utils.CellData
import com.example.presentation.mapscreen.utils.getMapBounds
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(viewModel: MapScreenModel = hiltViewModel()) {
    val initialLocation = remember { LatLng(-38.1819, 176.2591) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val visibleCellStations by viewModel.visibleCellStations.collectAsState()
    var currentMarker by remember { mutableStateOf<Marker?>(null) }

    SideEffect {
        Log.d("ComposeDebug", "Recompose")
    }

    fun updateMapCameraState() {
        val mapBounds = getMapBounds(cameraPositionState, screenWidthPx, screenHeightPx)
        mapBounds?.let {
            viewModel.updateMapCameraState(it, cameraPositionState.position.zoom)
        }
    }

    fun onMarkerClick(marker: Marker) {
        if (marker == currentMarker) {
            currentMarker = null
            marker.hideInfoWindow()
        } else {
            currentMarker?.hideInfoWindow()
            currentMarker = marker
            marker.showInfoWindow()
        }
    }

    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) currentMarker?.hideInfoWindow()
        if (!cameraPositionState.isMoving) updateMapCameraState()
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(), // Карта займет всю доступную площадь
        cameraPositionState = cameraPositionState,
        onMapLoaded = { updateMapCameraState() },
    ) {
        visibleCellStations.forEach { station ->
            CellStationMarker(station,
                onMarkerClick = { mark -> onMarkerClick(mark) }
            )
        }
    }

}

@Composable
fun CellStationMarker(
    station: CellData,
    onMarkerClick: (Marker) -> Unit
) {
    val position = LatLng(station.LAT!!, station.LON!!)
    SideEffect {
        Log.d("ComposeDebug", "Recompose CellStationMarker")
    }
    Marker(
        state = MarkerState(position = position),
        title = "Station id ${station.CELLID}",
        snippet = "MCC: ${station.MCC}, MNC: ${station.MNC}, LAC: ${station.LAC}, RAT: ${station.RAT}",
        // You can add a custom icon if you want later:
        // icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
        onClick = { marker ->
            onMarkerClick(marker) // Pass the clicked marker to the parent
            true // Return true to indicate that the event has been consumed
        }
    )
}
