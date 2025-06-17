package com.example.presentation.mapscreen.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@Composable
fun ClusterBitmapDescriptor(count: Int): BitmapDescriptor {
    // Используем remember для создания Paint-объектов только один раз
    // и переиспользования их для оптимизации производительности
    val textPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE // Цвет текста
            textSize = 30f             // Размер текста
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
            color = Color.WHITE // Цвет обводки
            style = Paint.Style.STROKE // Только обводка
            strokeWidth = 2f           // Толщина обводки
        }
    }

    // Возвращаем BitmapDescriptor, обернутый в remember(count)
    // Это гарантирует, что Bitmap будет пересоздан только при изменении count
    return remember(count) {
        // Определяем размер Bitmap в зависимости от количества итемов
        // Для больших чисел делаем кружок больше
        val size = when {
            count < 10 -> 40.dp // px
            count < 100 -> 100.dp // px
            count < 1000 -> 120.dp // px
            else -> 140.dp // px
        }

        val bitmap = Bitmap.createBitmap(size.value.toInt(), size.value.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val centerX = (size / 2f).value
        val centerY = (size / 2f).value
        val radius = size.value / 2f - strokePaint.strokeWidth / 2 // Учитываем обводку

        // Рисуем круг
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
        // Рисуем обводку (если нужна)
        canvas.drawCircle(centerX, centerY, radius, strokePaint)

        // Рисуем текст (количество)
        val text = if (count > 999) "999+" else count.toString() // Ограничиваем текст для очень больших чисел
        val textBounds = Rect() // Для точного измерения размеров текста
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        // Рассчитываем позицию текста, чтобы он был точно по центру
        val x = centerX
        val y = centerY - textBounds.exactCenterY() // exactCenterY() для вертикального центрирования

        canvas.drawText(text, x, y, textPaint)

        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}