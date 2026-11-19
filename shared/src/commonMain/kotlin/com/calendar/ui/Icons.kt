package com.calendar.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/** Iconos de atrezo dibujados con Canvas (sin `material-icons-extended`, que no compila en iOS). */
enum class Ico { MENU, SEARCH, TASKS, PLUS, CHEVRON_LEFT, DROPDOWN, PENCIL, GRID, TRAY, LIST, MORE, MORE_GRID, CLOSE, CALENDAR }

@Composable
fun Icon(kind: Ico, color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val w = this.size.width
        val sw = w * 0.085f
        val st = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (kind) {
            Ico.MENU -> for (y in listOf(0.28f, 0.5f, 0.72f))
                drawLine(color, Offset(w * 0.15f, w * y), Offset(w * 0.85f, w * y), strokeWidth = sw, cap = StrokeCap.Round)
            Ico.SEARCH -> {
                drawCircle(color, radius = w * 0.26f, center = Offset(w * 0.43f, w * 0.43f), style = st)
                drawLine(color, Offset(w * 0.62f, w * 0.62f), Offset(w * 0.85f, w * 0.85f), strokeWidth = sw, cap = StrokeCap.Round)
            }
            Ico.TASKS -> {
                drawCircle(color, radius = w * 0.38f, center = Offset(w * 0.5f, w * 0.5f), style = st)
                val p = Path().apply {
                    moveTo(w * 0.33f, w * 0.52f); lineTo(w * 0.45f, w * 0.64f); lineTo(w * 0.69f, w * 0.38f)
                }
                drawPath(p, color, style = st)
            }
            Ico.PLUS -> {
                drawLine(color, Offset(w * 0.5f, w * 0.18f), Offset(w * 0.5f, w * 0.82f), strokeWidth = sw * 1.2f, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.18f, w * 0.5f), Offset(w * 0.82f, w * 0.5f), strokeWidth = sw * 1.2f, cap = StrokeCap.Round)
            }
            Ico.CHEVRON_LEFT -> {
                val p = Path().apply { moveTo(w * 0.62f, w * 0.2f); lineTo(w * 0.32f, w * 0.5f); lineTo(w * 0.62f, w * 0.8f) }
                drawPath(p, color, style = Stroke(width = sw * 1.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            Ico.DROPDOWN -> {
                val p = Path().apply { moveTo(w * 0.25f, w * 0.38f); lineTo(w * 0.75f, w * 0.38f); lineTo(w * 0.5f, w * 0.66f); close() }
                drawPath(p, color, style = Fill)
            }
            Ico.PENCIL -> {
                drawLine(color, Offset(w * 0.28f, w * 0.72f), Offset(w * 0.72f, w * 0.28f), strokeWidth = sw * 1.8f, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.15f, w * 0.88f), Offset(w * 0.85f, w * 0.88f), strokeWidth = sw, cap = StrokeCap.Round)
            }
            Ico.GRID -> {
                drawRoundRect(color, Offset(w * 0.15f, w * 0.2f), Size(w * 0.7f, w * 0.65f), CornerRadius(w * 0.1f), style = st)
                drawLine(color, Offset(w * 0.15f, w * 0.4f), Offset(w * 0.85f, w * 0.4f), strokeWidth = sw)
                for (x in listOf(0.32f, 0.5f, 0.68f)) for (y in listOf(0.55f, 0.72f))
                    drawCircle(color, radius = w * 0.025f, center = Offset(w * x, w * y))
            }
            Ico.TRAY -> {
                drawRoundRect(color, Offset(w * 0.15f, w * 0.22f), Size(w * 0.7f, w * 0.56f), CornerRadius(w * 0.1f), style = st)
                val p = Path().apply { moveTo(w * 0.15f, w * 0.52f); lineTo(w * 0.36f, w * 0.52f); lineTo(w * 0.42f, w * 0.62f); lineTo(w * 0.58f, w * 0.62f); lineTo(w * 0.64f, w * 0.52f); lineTo(w * 0.85f, w * 0.52f) }
                drawPath(p, color, style = st)
            }
            Ico.MORE -> for (y in listOf(0.2f, 0.5f, 0.8f)) drawCircle(color, radius = w * 0.07f, center = Offset(w * 0.5f, w * y))
            Ico.MORE_GRID -> for (x in listOf(0.3f, 0.7f)) for (y in listOf(0.3f, 0.7f)) drawCircle(color, radius = w * 0.07f, center = Offset(w * x, w * y))
            Ico.CLOSE -> {
                drawLine(color, Offset(w * 0.22f, w * 0.22f), Offset(w * 0.78f, w * 0.78f), strokeWidth = sw * 1.2f, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.78f, w * 0.22f), Offset(w * 0.22f, w * 0.78f), strokeWidth = sw * 1.2f, cap = StrokeCap.Round)
            }
            Ico.CALENDAR -> {
                drawRoundRect(color, Offset(w * 0.17f, w * 0.22f), Size(w * 0.66f, w * 0.62f), CornerRadius(w * 0.08f), style = st)
                drawLine(color, Offset(w * 0.17f, w * 0.4f), Offset(w * 0.83f, w * 0.4f), strokeWidth = sw)
                drawLine(color, Offset(w * 0.34f, w * 0.14f), Offset(w * 0.34f, w * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
                drawLine(color, Offset(w * 0.66f, w * 0.14f), Offset(w * 0.66f, w * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
                for (x in listOf(0.36f, 0.5f, 0.64f)) for (y in listOf(0.52f, 0.67f))
                    drawCircle(color, radius = w * 0.022f, center = Offset(w * x, w * y))
            }
            Ico.LIST -> {
                drawRoundRect(color, Offset(w * 0.15f, w * 0.18f), Size(w * 0.7f, w * 0.26f), CornerRadius(w * 0.07f), style = st)
                drawRoundRect(color, Offset(w * 0.15f, w * 0.56f), Size(w * 0.7f, w * 0.26f), CornerRadius(w * 0.07f), style = st)
            }
        }
    }
}

/** Icono «hoy» de Google/Samsung: rectángulo redondeado con el número del día dentro. */
@Composable
fun DayIcon(day: Int, color: Color, size: Dp = 26.dp) {
    Box(
        Modifier.size(size).border(2.dp, color, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) { Text(day.toString(), color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false) }
}
