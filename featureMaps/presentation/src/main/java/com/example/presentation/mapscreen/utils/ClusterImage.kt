package com.example.presentation.mapscreen.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberUpdatedMarkerState

@Composable
fun ClusterBitmapDescriptor(count: Int): BitmapDescriptor {
    val density = LocalDensity.current
    val textPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE // Цвет текста
            textSize = with(density) { 12.sp.toPx() }
            textAlign = Paint.Align.CENTER // Выравнивание текста по центру
            isFakeBoldText = true      // Опционально: сделать текст жирнее
        }
    }
    val circlePaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF6200EE") // Пурпурный цвет, пример из Material Design Primary 500
            style = Paint.Style.FILL // Заливка круга
        }
    }
    val strokePaint = remember { // Опционально: для обводки круга
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK // Цвет обводки
            style = Paint.Style.STROKE // Только обводка
            strokeWidth = with(density) { 2.dp.toPx() }           // Толщина обводки
        }
    }

    // Возвращаем BitmapDescriptor, обернутый в remember(count)
    // Это гарантирует, что Bitmap будет пересоздан только при изменении count
    return remember(count) {
        // Определяем размер Bitmap в зависимости от количества итемов
        // Для больших чисел делаем кружок больше
        val size = when {
            count < 10 -> with(density) { 30.dp.toPx() } // px
            count < 100 -> with(density){ 32.dp.toPx() } // px
            else -> with(density) { 35.dp.toPx() } // px
        }

        val bitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val centerX = (size / 2f)
        val centerY = (size / 2f)
        val radius = size / 2f - with(density) { 2.dp.toPx() } // Учитываем обводку

        // Рисуем круг
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
        // Рисуем обводку (если нужна)
        canvas.drawCircle(centerX, centerY, radius, strokePaint)

        // Рисуем текст (количество)
        val text = if (count > 999) "1k+" else count.toString() // Ограничиваем текст для очень больших чисел
        val textBounds = Rect() // Для точного измерения размеров текста
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        // Рассчитываем позицию текста, чтобы он был точно по центру
        val x = centerX
        val y = centerY - textBounds.exactCenterY() // exactCenterY() для вертикального центрирования

        canvas.drawText(text, x, y, textPaint)

        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

@Composable
fun SingleCellMarker(station: CellData,
                     onMarkerClick: (Marker) -> Unit
) {
    val state = rememberUpdatedMarkerState(position = station.position)
    MarkerInfoWindow(
        state = state,
    ) {
        Card(
            modifier = Modifier.width(150.dp),
            shape = MaterialTheme.shapes.medium // Закругленные углы

        ) {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center) {
                Text(
                    text = station.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
    LaunchedEffect(station) {
        state.showInfoWindow()
    }
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