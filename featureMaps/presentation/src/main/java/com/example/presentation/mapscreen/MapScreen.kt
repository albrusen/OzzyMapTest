package com.example.presentation.mapscreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MyMapApp() {
    // Создаем начальную позицию камеры.
    // Например, координаты центра Киева: LatLng(50.4501, 30.5234)
    val initialLocation = remember { LatLng(50.4501, 30.5234) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 10f) // 10f - начальный уровень зума
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(), // Карта займет всю доступную площадь
        cameraPositionState = cameraPositionState
    ) {
        // Здесь пока ничего нет, но скоро появятся маркеры!
    }
}