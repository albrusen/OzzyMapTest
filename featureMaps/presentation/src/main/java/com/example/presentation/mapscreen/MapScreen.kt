package com.example.presentation.mapscreen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.presentation.mapscreen.utils.CellData
import com.example.presentation.mapscreen.utils.getMapBounds
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.clustering.rememberClusterRenderer
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(MapsComposeExperimentalApi::class)
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

    SideEffect {
        Log.d("ComposeDebug", "Recompose")
    }

    fun updateMapCameraState() {
        val mapBounds = getMapBounds(cameraPositionState, screenWidthPx, screenHeightPx)
        mapBounds?.let {
            viewModel.updateMapCameraState(it, cameraPositionState.position.zoom)
        }
    }

    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) updateMapCameraState()
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { updateMapCameraState() },
    ) {
        var clusterManager = rememberClusterManager<CellData>()
        clusterManager?.let { manager ->
            val renderer = rememberClusterRenderer(
                clusterManager = manager,
                clusterContent = null,
                clusterItemContent = null,
            )

            //При рекомпозиции rememberClusterRenderer() может (хотя и не всегда)
            //создать новый экземпляр рендерера, если его зависимости изменились.
            //SideEffect гарантирует, что каждый раз, когда Compose "перерисовывает" эту часть UI,
            //он проверит, является ли текущий рендерер у manager тем же самым, что и renderer,
            //созданный в Compose. Если нет (то есть, если rememberClusterRenderer вернул новый экземпляр,
            //или если это первое присвоение), SideEffect обновит его.
            SideEffect {
                if (manager.renderer != renderer) {
                    manager.renderer = renderer ?: return@SideEffect
                }
            }

            Clustering(
                items = visibleCellStations,
                clusterManager = manager,
            )
        }
    }

}

@Composable
fun ClusterItemContent(item: CellData) {
    Text("item = ${item.CELLID}")
}

@Composable
fun ClusterContent(size: Int) {
    Text("Size = $size")
}
