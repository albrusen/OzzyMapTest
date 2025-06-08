package com.example.presentation.mapscreen.utils

import com.example.presentation.mapscreen.MapBounds
import com.google.maps.android.compose.CameraPositionState

fun getMapBounds(cameraPositionState: CameraPositionState, screenWidthPx: Float, screenHeightPx: Float): MapBounds? {
    val projection = cameraPositionState.projection ?: return null
    val topLeftScreenPoint = android.graphics.Point(0, 0)
    val topRightScreenPoint = android.graphics.Point(screenWidthPx.toInt(), 0)
    val bottomLeftScreenPoint = android.graphics.Point(0, screenHeightPx.toInt())
    val bottomRightScreenPoint = android.graphics.Point(screenWidthPx.toInt(), screenHeightPx.toInt())

    val topLeftLatLng = projection.fromScreenLocation(topLeftScreenPoint)
    val topRightLatLng = projection.fromScreenLocation(topRightScreenPoint)
    val bottomLeftLatLng = projection.fromScreenLocation(bottomLeftScreenPoint)
    val bottomRightLatLng = projection.fromScreenLocation(bottomRightScreenPoint)

    val allLatitudes = listOf(
        topLeftLatLng.latitude,
        topRightLatLng.latitude,
        bottomLeftLatLng.latitude,
        bottomRightLatLng.latitude
    )
    val allLongitudes = listOf(
        topLeftLatLng.longitude,
        topRightLatLng.longitude,
        bottomLeftLatLng.longitude,
        bottomRightLatLng.longitude
    )
    // TODO !!! ВАЖНО: Учет пересечения антимеридиана для долготы !!!
    // Это более сложная логика, но крайне важна, если ваша карта может охватывать 180/-180 меридиан.
    // Если minLon = 170 и maxLon = -170, это значит, что карта пересекает 180 меридиан.
    // Тогда запрос "LON BETWEEN 170 AND -170" будет некорректен.

    val calculatedMinLon: Double
    val calculatedMaxLon: Double

    // Проверка на пересечение антимеридиана
    if (allLongitudes.max() - allLongitudes.min() > 180) { // Если диапазон > 180 градусов, скорее всего пересекаем
        calculatedMinLon = allLongitudes.max() // Берем максимальное значение как "начало" от 180
        calculatedMaxLon = allLongitudes.min() // Берем минимальное значение как "конец" от -180
    } else {
        calculatedMinLon = allLongitudes.min()
        calculatedMaxLon = allLongitudes.max()
    }

    // --- ДОБАВЛЕНИЕ БУФЕРА (PADDING) ---
    val bufferPercentage = 0.1 // 5% буфер. Можно попробовать 0.02 (2%) до 0.1 (10%)

    val latRange = allLatitudes.max() - allLatitudes.min()
    val lonRange = calculatedMaxLon - calculatedMinLon

    val latBuffer = latRange * bufferPercentage
    val lonBuffer = lonRange * bufferPercentage

    val bufferedMinLat = allLatitudes.min() - latBuffer
    val bufferedMaxLat = allLatitudes.max() + latBuffer
    var bufferedMinLon = calculatedMinLon - lonBuffer
    var bufferedMaxLon = calculatedMaxLon + lonBuffer

    return MapBounds(
        minLat = bufferedMinLat,
        maxLat = bufferedMaxLat,
        minLon = bufferedMinLon,
        maxLon = bufferedMaxLon
    )
}