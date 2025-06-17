package com.example.presentation.mapscreen.utils

import com.example.presentation.mapscreen.MapBounds
import com.google.maps.android.compose.CameraPositionState
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.cos

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
    // !!! ВАЖНО: Учет пересечения антимеридиана для долготы !!!
    // Это более сложная логика, но крайне важна, если карта может охватывать 180/-180 меридиан.
    // Если minLon = 170 и maxLon = -170, это значит, что карта пересекает 180 меридиан.
    // Тогда запрос "LON BETWEEN 170 AND -170" будет некорректен.
    var lonRange = allLongitudes.max() - allLongitudes.min()
    var bufferedMinLon: Double
    var bufferedMaxLon: Double
    val bufferPercentage = 0.0
    // Проверка на пересечение антимеридиана
    if (lonRange > 180) {
        lonRange = getLonRangeForAntimeridian(allLongitudes)
        val lonBuffer = lonRange * bufferPercentage
        bufferedMaxLon = allLongitudes.maxWestPoint() + lonBuffer
        bufferedMinLon = allLongitudes.maxEastPoint() - lonBuffer
    } else {
        val lonBuffer = lonRange * bufferPercentage
        bufferedMinLon = allLongitudes.min() - lonBuffer
        bufferedMaxLon = allLongitudes.max() + lonBuffer
    }

    val latRange = allLatitudes.max() - allLatitudes.min()
    val latBuffer = latRange * bufferPercentage

    val bufferedMinLat = allLatitudes.min() - latBuffer
    val bufferedMaxLat = allLatitudes.max() + latBuffer

    return MapBounds(
        minLat = bufferedMinLat,
        maxLat = bufferedMaxLat,
        minLon = bufferedMinLon,
        maxLon = bufferedMaxLon
    )
}

/**
 * Рассчитывает общую протяженность (диапазон) долгот в градусах,
 * учитывая случай, когда область карты пересекает 180-й меридиан.
 *
 * Если диапазон долгот пересекает 180-й меридиан, то minLon (maxWestPoint) > maxLon (maxEastPoint).
 * В этом случае диапазон состоит из двух частей:
 * 1. От 'maxWestPoint' (например, 170°) до +180°. Протяженность: (180 - maxWestPoint).
 * 2. От -180° до 'maxEastPoint' (например, -170°). Протяженность: (maxEastPoint + 180).
 *
 * Формула складывает протяженности этих двух сегментов, чтобы получить общую ширину
 * видимого диапазона долгот.
 *
 * @return Общая протяженность долгот в градусах.
 */
fun getLonRangeForAntimeridian(allLongitudes: List<Double>): Double {
 return 180 - allLongitudes.maxWestPoint() + allLongitudes.maxEastPoint() + 180
}

/**
 * Вычисляет "самую западную" долготу (ближайшую к -180° при движении с востока на запад)
 * среди положительных значений долготы в списке.
 *
 * Этот метод предназначен для специфического случая, когда отображаемая область карты
 * пересекает 180-й меридиан (линию перемены даты). В такой ситуации диапазон долгот
 * разбивается на две части: от minLon до 180° и от -180° до maxLon.
 *
 * Данный метод ищет наименьшее положительное значение долготы,
 * которое представляет собой восточную границу "западного" сегмента (от -180° до maxLon),
 * когда карта охватывает область, простирающуюся на запад от 180° меридиана.
 *
 * или 180.0, если положительных значений нет или все они больше 180.0.
 */
fun List<Double>.maxWestPoint(): Double {
    var west = 180.0
    this.forEach {
        if (it > 0 && it < west) west = it
    }
    return west
}

fun List<Double>.maxEastPoint(): Double {
    var east = -180.0
    this.forEach {
        if (it < 0 && it > east) east = it
    }
    return east
}

fun getDegreesPerPixel(lat: Double, zoom: Double): Double {
    val metersPerPixel = 156543.03392 * cos(lat * PI / 180) / pow(2.0, zoom)
    return 1 / (111000 / metersPerPixel)
}
