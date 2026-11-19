package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

/** Primer día del mes número [idx] contando desde enero de 2000. */
private fun monthStart(idx: Int) = LocalDate(2000 + idx / 12, idx % 12 + 1, 1)

/** Filas (semanas) que ocupa el mes [first] con la skin dada: cada mes arranca en su propia fila. */
private fun weeksOf(skin: CalendarSkin, first: LocalDate): Int {
    val days = first.daysUntil(first.plus(1, DateTimeUnit.MONTH))
    return (weekStart(skin, first).daysUntil(first) + days + 6) / 7
}

/**
 * Desplazamiento vertical continuo. Como en los calendarios originales, **cada mes empieza en su propia fila**:
 * el día 1 cae en su columna de la semana y las celdas de los otros meses se dejan vacías, de modo que el salto de
 * mes se ve claramente (la última fila de un mes y la primera del siguiente no comparten línea).
 */
@Composable
private fun ContinuousWeeks(
    skin: CalendarSkin, today: LocalDate, events: (LocalDate) -> CalendarEvent?,
    onDayClick: (LocalDate) -> Unit, onShown: (LocalDate) -> Unit,
) {
    // rowStarts[m] = índice de la primera fila del mes m; el último elemento es el total de filas.
    val rowStarts = remember(skin.mondayFirst) {
        IntArray(MESES_TOTALES + 1).also { a ->
            for (m in 0 until MESES_TOTALES) a[m + 1] = a[m] + weeksOf(skin, monthStart(m))
        }
    }
    fun monthOfRow(row: Int): Int {
        var lo = 0
        var hi = MESES_TOTALES - 1
        while (lo < hi) {
            val mid = (lo + hi + 1) / 2
            if (rowStarts[mid] <= row) lo = mid else hi = mid - 1
        }
        return lo
    }
    val todayMonth = (today.year - 2000) * 12 + today.monthNumber - 1
    val todayRow = rowStarts[todayMonth] + weekStart(skin, monthStart(todayMonth)).daysUntil(today) / 7
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (todayRow - 1).coerceAtLeast(0))
    // Mes de cabecera: el de la primera fila visible (o el de la siguiente si está muy cortada).
    val headerDate by remember(listState, rowStarts) {
        derivedStateOf {
            val row = listState.firstVisibleItemIndex + if (listState.firstVisibleItemScrollOffset > 120) 1 else 0
            monthStart(monthOfRow(row.coerceAtMost(rowStarts[MESES_TOTALES] - 1)))
        }
    }
    LaunchedEffect(headerDate) { onShown(headerDate) }
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 4.dp), state = listState) {
        items(rowStarts[MESES_TOTALES], key = { it }) { r ->
            val m = monthOfRow(r)
            val first = monthStart(m)
            val start = weekStart(skin, first).plus((r - rowStarts[m]) * 7, DateTimeUnit.DAY)
            Row(Modifier.fillMaxWidth().height(skin.rowHeight)) {
                for (i in 0 until 7) {
                    val date = start.plus(i, DateTimeUnit.DAY)
                    if (date.monthNumber == first.monthNumber) {
                        DayCell(skin, date, date == today, false, events(date), Modifier.weight(1f)) { onDayClick(date) }
                    } else {
                        // Celda de otro mes: vacía, sin tarjeta ni línea, para que se aprecie el salto de mes.
                        Spacer(Modifier.weight(1f))
                    }
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
    // Evento de «Todo el día»: rellena la celda entera con el color del evento (salvo en las skins de puntos).
    val relleno: Color? = event?.takeIf { it.allDay && skin.eventStyle != EventStyle.DOT }
        ?.let { e -> e.color?.let { Color(it.argb) } ?: skin.chipColor }
    val sobreRelleno = event?.color?.let { if (it.textoOscuro) Color.Black else Color.White } ?: skin.onChip
    val base = modifier.fillMaxSize().padding(skin.cellGap)
    val shaped = if (skin.cardCells) {
        base.clip(RoundedCornerShape(skin.cellCorner)).background(relleno ?: skin.cellColor)
    } else {
        val linea = skin.dividerColor
        val vertical = skin.verticalLines
        base.then(if (relleno != null) Modifier.background(relleno) else Modifier).drawBehind {
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
                    color = if (relleno != null) sobreRelleno else if (outsideMonth) skin.mutedColor.copy(alpha = 0.55f) else dayColor(skin, date),
                    fontSize = 15.sp, fontWeight = FontWeight.Medium, maxLines = 1, softWrap = false,
                )
            }
            if (event != null && relleno != null) {
                Text(
                    event.title, Modifier.fillMaxWidth().padding(top = 3.dp, start = 3.dp, end = 2.dp),
                    color = sobreRelleno, fontSize = if (skin.wrapTitle) 10.sp else 11.sp, lineHeight = 12.sp,
                    maxLines = if (skin.wrapTitle) 5 else 1, overflow = TextOverflow.Ellipsis,
                )
            } else if (event != null) when (skin.eventStyle) {
                EventStyle.BAND -> Box(
                    Modifier.padding(top = 3.dp, start = 1.dp, end = 1.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(skin.chipCorner)).background(event.color?.let { Color(it.argb) } ?: skin.chipColor)
                        .padding(horizontal = 3.dp, vertical = 1.dp),
                ) {
                    Text(event.title, color = event.color?.let { if (it.textoOscuro) Color.Black else Color.White } ?: skin.onChip, fontSize = if (skin.wrapTitle) 10.sp else 11.sp, lineHeight = 12.sp, maxLines = if (skin.wrapTitle) 5 else 1, overflow = if (skin.wrapTitle) TextOverflow.Ellipsis else TextOverflow.Clip)
                }
                EventStyle.TEXT -> Text(
                    event.title, Modifier.fillMaxWidth().padding(top = 3.dp, start = 2.dp, end = 1.dp),
                    color = event.color?.let { Color(it.argb) } ?: skin.chipColor,
                    fontSize = 11.sp, lineHeight = 13.sp,
                    // Una palabra suelta no se parte en dos líneas: se recorta con puntos suspensivos.
                    maxLines = if (skin.wrapTitle) 5 else if (' ' in event.title.trim()) 2 else 1, overflow = TextOverflow.Ellipsis,
                )
                EventStyle.DOT -> Box(
                    Modifier.padding(top = 3.dp).size(5.dp).clip(CircleShape)
                        .background(event.color?.let { Color(it.argb) } ?: if (skin.chrome == Chrome.XIAOMI) skin.textColor else skin.accent.copy(alpha = 0.45f)),
                )
            }
        }
    }
}
