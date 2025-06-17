package com.example.presentation.mapscreen.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator
import kotlin.math.max

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
fun createSingleMarkerBitmapDescriptor(
    title: String,
    @DrawableRes markerDrawableResId: Int, // ID ресурса Drawable
): BitmapDescriptor {
    val context = LocalContext.current
    val density = LocalDensity.current

    return remember(title, markerDrawableResId, density, context) {
        val lines = title.split("\n") // Разбиваем текст на строки по символу новой строки

        val baseMarkerBitmap: Bitmap? = try {
            val drawable: Drawable? = context.resources.getDrawable(markerDrawableResId, context.theme)
            if (drawable == null) {
                Log.e("MarkerIcon", "Drawable not found for ID: $markerDrawableResId")
                null
            } else {
                val bitmapWidth = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else (32.dp.value * density.density).toInt()
                val bitmapHeight = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else (48.dp.value * density.density).toInt()

                val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        } catch (e: Exception) {
            Log.e("MarkerIcon", "Error loading or converting drawable to bitmap: ${e.message}", e)
            null
        }

        if (baseMarkerBitmap == null || baseMarkerBitmap.width <= 0 || baseMarkerBitmap.height <= 0) {
            Log.e("MarkerIcon", "Base marker bitmap is invalid or null. Returning default marker.")
            return@remember BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        }

        // --- Расчеты для многострочного текста ---
        val textPaddingVerticalDp = 4.dp
        val textPaddingHorizontalDp = 6.dp
        val textSizeSp = 12.sp

        val textSizePx = with(density) { textSizeSp.toPx() }
        val textPaddingVerticalPx = with(density) { textPaddingVerticalDp.toPx() }
        val textPaddingHorizontalPx = with(density) { textPaddingHorizontalDp.toPx() }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = textSizePx
            textAlign = Paint.Align.CENTER // Центрируем каждую строку
        }

        var maxTextWidthPx = 0f
        val lineHeightPx = textPaint.fontSpacing // Высота одной строки текста, включая межстрочный интервал

        // Вычисляем ширину каждой строки и находим максимальную
        lines.forEach { line ->
            val textBounds = Rect()
            textPaint.getTextBounds(line, 0, line.length, textBounds)
            maxTextWidthPx = max(maxTextWidthPx, textBounds.width().toFloat())
        }

        // Высота таблички: (количество строк * высота строки) + 2 * вертикальный отступ
        val labelHeightPx = (lines.size * lineHeightPx) + 2 * textPaddingVerticalPx
        // Ширина таблички: максимальная ширина текста + 2 * горизонтальный отступ
        val labelWidthPx = maxTextWidthPx + 2 * textPaddingHorizontalPx

        // Общие размеры Bitmap
        val finalWidthPx = max(baseMarkerBitmap.width.toFloat(), labelWidthPx).toInt()
        val finalHeightPx = (labelHeightPx + baseMarkerBitmap.height).toInt()

        if (finalWidthPx <= 0 || finalHeightPx <= 0) {
            Log.e("MarkerIcon", "Calculated final dimensions are invalid: width=$finalWidthPx, height=$finalHeightPx. Title='$title'")
            return@remember com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED)
        }

        val finalBitmap = Bitmap.createBitmap(finalWidthPx, finalHeightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(finalBitmap)

        // --- Рисуем белую табличку для текста сверху ---
        val labelRectF = RectF(
            (finalWidthPx - labelWidthPx) / 2f,
            0f,
            (finalWidthPx + labelWidthPx) / 2f,
            labelHeightPx
        )
        val labelBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        val labelBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            strokeWidth = with(density) { 2.dp.toPx() }
        }
        val labelCornerRadius = with(density) { 5.dp.toPx() }
        val labelCornerRadiusInner = with(density) { 2.dp.toPx() }

        canvas.drawRoundRect(labelRectF, labelCornerRadius, labelCornerRadius, labelBackgroundPaint)
        canvas.drawRoundRect(labelRectF, labelCornerRadiusInner, labelCornerRadiusInner, labelBorderPaint)

        // --- Рисуем каждую строку текста в центре таблички ---
        var currentY = textPaddingVerticalPx + textPaint.fontMetrics.top * -1 // Начальная Y-позиция для первой строки
        lines.forEach { line ->
            val textX = finalWidthPx / 2f // Всегда по центру
            canvas.drawText(line, textX, currentY, textPaint)
            currentY += lineHeightPx // Переходим к следующей строке
        }

        // --- Рисуем загруженный маркер под табличкой ---
        canvas.drawBitmap(
            baseMarkerBitmap,
            (finalWidthPx - baseMarkerBitmap.width) / 2f,
            labelHeightPx,
            null
        )

        return@remember BitmapDescriptorFactory.fromBitmap(finalBitmap)
    }
}
