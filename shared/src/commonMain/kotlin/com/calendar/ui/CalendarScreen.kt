package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendar.domain.CalendarEvent
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private val MESES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
)
private val SEMANAS = List(5600) { it } // ~107 años desde el ancla
private const val MESES_TOTALES = 1272 // 106 años desde enero de 2000

fun nombreMes(m: Int) = MESES[m - 1].replaceFirstChar { it.uppercase() }

/**
 * Pantalla del calendario: cabecera de la marca, días de la semana y cuerpo con el desplazamiento elegido:
 * **vertical continuo** (semanas contiguas que cruzan el límite de mes) u **horizontal** (páginas de un mes).
 * [onTitleLongPress] abre los Ajustes (acceso oculto).
 */
@Composable
fun CalendarScreen(
    skin: CalendarSkin,
    horizontal: Boolean,
    today: LocalDate,
    events: (LocalDate) -> CalendarEvent?,
    onDayClick: (LocalDate) -> Unit,
    onTitleLongPress: () -> Unit,
    onCornerLongPress: () -> Unit = {},
) {
    var shown by remember { mutableStateOf(today) }
    Box(Modifier.fillMaxSize().background(skin.background).windowInsetsPadding(WindowInsets.systemBars)) {
        Column(Modifier.fillMaxSize()) {
            // Pulsación larga en CUALQUIER punto de la cabecera abre los Ajustes (los botones son solo atrezo).
            Box(
                Modifier.fillMaxWidth().pointerInput(Unit) {
                    // Esquina superior izquierda (invisible) = reinicio del efecto; el resto de la cabecera = Ajustes.
                    val esquina = 72.dp.toPx()
                    detectTapGestures(onLongPress = { p ->
                        if (p.x < esquina && p.y < esquina) onCornerLongPress() else onTitleLongPress()
                    })
                },
            ) {
                CalendarHeader(skin, shown, today, onTitleLongPress)
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = skin.cellGap + 4.dp)) {
                for (d in skin.weekdayLabels()) {
                    Text(
                        d, Modifier.weight(1f).padding(vertical = 6.dp), color = skin.mutedColor,
                        fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium,
                    )
                }
            }
            val onShown: (LocalDate) -> Unit = { shown = it }
            if (horizontal) PagedMonths(skin, today, events, onDayClick, onShown)
            else ContinuousWeeks(skin, today, events, onDayClick, onShown)
        }
        CalendarOverlay(skin, today)
    }
}

/** Primer día de la semana (según la skin) que contiene a [d]. */
private fun weekStart(skin: CalendarSkin, d: LocalDate): LocalDate {
    val offset = if (skin.mondayFirst) d.dayOfWeek.ordinal else (d.dayOfWeek.ordinal + 1) % 7
    return d.minus(offset, DateTimeUnit.DAY)
}

@Composable
private fun ContinuousWeeks(
    skin: CalendarSkin, today: LocalDate, events: (LocalDate) -> CalendarEvent?,
    onDayClick: (LocalDate) -> Unit, onShown: (LocalDate) -> Unit,
) {
    // Semana 0 = semana de ANCLA (2000-01-03 lunes o 2000-01-02 domingo).
    val anchor = remember(skin.mondayFirst) { if (skin.mondayFirst) LocalDate(2000, 1, 3) else LocalDate(2000, 1, 2) }
    val todayWeek = anchor.daysUntil(today) / 7
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (todayWeek - 1).coerceAtLeast(0))
    // Mes de cabecera: el del jueves/mitad de la primera semana visible (o la siguiente si está muy cortada).
    val headerDate by remember(listState, anchor) {
        derivedStateOf {
            val idx = listState.firstVisibleItemIndex + if (listState.firstVisibleItemScrollOffset > 120) 1 else 0
            anchor.plus(idx * 7 + 3, DateTimeUnit.DAY)
        }
    }
    LaunchedEffect(headerDate) { onShown(headerDate) }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 4.dp), state = listState) {
        items(SEMANAS, key = { it }) { w ->
            val start = anchor.plus(w * 7, DateTimeUnit.DAY)
            Row(Modifier.fillMaxWidth().height(skin.rowHeight)) {
                for (i in 0 until 7) {
                    val date = start.plus(i, DateTimeUnit.DAY)
                    DayCell(skin, date, date == today, false, events(date), Modifier.weight(1f)) { onDayClick(date) }
                }
            }
        }
    }
}

@Composable
private fun PagedMonths(
    skin: CalendarSkin, today: LocalDate, events: (LocalDate) -> CalendarEvent?,
    onDayClick: (LocalDate) -> Unit, onShown: (LocalDate) -> Unit,
) {
    fun monthOf(page: Int) = LocalDate(2000 + page / 12, page % 12 + 1, 1)
    val initial = (today.year - 2000) * 12 + today.monthNumber - 1
    val pager = rememberPagerState(initialPage = initial, pageCount = { MESES_TOTALES })
    LaunchedEffect(pager.currentPage) { onShown(monthOf(pager.currentPage)) }
    HorizontalPager(pager, Modifier.fillMaxSize()) { page ->
        val first = monthOf(page)
        val start = weekStart(skin, first)
        val days = first.daysUntil(first.plus(1, DateTimeUnit.MONTH))
        val weeks = (start.daysUntil(first) + days + 6) / 7
        Column(Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
            for (w in 0 until weeks) {
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    for (i in 0 until 7) {
                        val date = start.plus(w * 7 + i, DateTimeUnit.DAY)
                        val fuera = date.monthNumber != first.monthNumber
                        DayCell(skin, date, date == today, fuera, events(date), Modifier.weight(1f)) { onDayClick(date) }
                    }
                }
            }
        }
    }
}

private fun dayColor(skin: CalendarSkin, date: LocalDate): Color {
    val finde = date.dayOfWeek == DayOfWeek.SUNDAY || date.dayOfWeek == DayOfWeek.SATURDAY
    return when {
        date.dayOfWeek == DayOfWeek.SUNDAY && skin.sundayColor != null -> skin.sundayColor
        finde && skin.weekendColor != null -> skin.weekendColor
        else -> skin.textColor
    }
}

@Composable
private fun DayCell(
    skin: CalendarSkin,
    date: LocalDate,
    isToday: Boolean,
    outsideMonth: Boolean,
    event: CalendarEvent?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val base = modifier.fillMaxSize().padding(skin.cellGap)
    val shaped = if (skin.cardCells) {
        base.clip(RoundedCornerShape(skin.cellCorner)).background(skin.cellColor)
    } else {
        val linea = skin.dividerColor
        val vertical = skin.verticalLines
        base.drawBehind {
            drawLine(linea, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx())
            if (vertical) drawLine(linea, Offset(0f, 0f), Offset(0f, size.height), strokeWidth = 1.dp.toPx())
        }
    }
    Box(shaped.clickable(onClick = onClick)) {
        Column(Modifier.fillMaxSize().padding(top = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val etiqueta = if (skin.monthOnFirst && date.dayOfMonth == 1) "1 ${MESES[date.monthNumber - 1].take(3)}" else date.dayOfMonth.toString()
            if (isToday) {
                Box(
                    Modifier.clip(if (skin.todayCircle) CircleShape else RoundedCornerShape(5.dp)).background(skin.accent)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(etiqueta, color = skin.onAccent, fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false) }
            } else {
                Text(
                    etiqueta, Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    color = if (outsideMonth) skin.mutedColor.copy(alpha = 0.55f) else dayColor(skin, date),
                    fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false,
                )
            }
            if (event != null) when (skin.eventStyle) {
                EventStyle.BAND -> Box(
                    Modifier.padding(top = 3.dp, start = 1.dp, end = 1.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(skin.chipCorner)).background(skin.chipColor)
                        .padding(horizontal = 3.dp, vertical = 1.dp),
                ) {
                    Text(event.title, color = skin.onChip, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Clip)
                }
                EventStyle.TEXT -> Text(
                    event.title, Modifier.fillMaxWidth().padding(top = 3.dp, start = 2.dp, end = 1.dp),
                    color = skin.chipColor.let { if (skin.background.luminance() < 0.5f) it.copy(alpha = 1f) else it },
                    fontSize = 11.sp, lineHeight = 13.sp,
                    // Una palabra suelta no se parte en dos líneas: se recorta con puntos suspensivos.
                    maxLines = if (' ' in event.title.trim()) 2 else 1, overflow = TextOverflow.Ellipsis,
                )
                EventStyle.DOT -> Box(
                    Modifier.padding(top = 3.dp).size(5.dp).clip(CircleShape)
                        .background(if (skin.chrome == Chrome.XIAOMI) skin.textColor else skin.accent.copy(alpha = 0.45f)),
                )
            }
        }
    }
}
