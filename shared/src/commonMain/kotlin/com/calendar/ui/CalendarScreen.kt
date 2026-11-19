package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlinx.datetime.plus

private val MESES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
)
private val DIAS_LUN = listOf("L", "M", "X", "J", "V", "S", "D")
private val DIAS_DOM = listOf("D", "L", "M", "X", "J", "V", "S")
private val SEMANAS = List(5600) { it } // ~107 años

fun nombreMes(m: Int) = MESES[m - 1].replaceFirstChar { it.uppercase() }

/**
 * Vista mensual **continua**: semanas contiguas en una lista vertical que cruza los límites de mes.
 * [events] devuelve el evento de un día (o null); [onDayClick] se invoca al pulsar un día;
 * [onHeaderLongClick] al mantener pulsado el título del mes (acceso oculto a Ajustes).
 */
@Composable
fun CalendarScreen(
    skin: CalendarSkin,
    today: LocalDate,
    events: (LocalDate) -> CalendarEvent?,
    onDayClick: (LocalDate) -> Unit,
    onHeaderLongClick: () -> Unit,
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

    Column(
        Modifier.fillMaxSize().background(skin.background).windowInsetsPadding(WindowInsets.systemBars),
    ) {
        var titulo = nombreMes(headerDate.monthNumber)
        if (skin.headerUppercase) titulo = titulo.uppercase()
        if (headerDate.year != today.year) titulo += " ${headerDate.year}"
        Text(
            titulo,
            Modifier.fillMaxWidth()
                .pointerInput(Unit) { detectTapGestures(onLongPress = { onHeaderLongClick() }) }
                .padding(horizontal = 20.dp, vertical = 14.dp),
            color = skin.textColor,
            fontSize = if (skin.headerBold) 30.sp else 24.sp,
            fontWeight = if (skin.headerBold) FontWeight.Bold else FontWeight.Normal,
            textAlign = if (skin.headerCentered) TextAlign.Center else TextAlign.Start,
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = skin.cellGap + 4.dp)) {
            for (d in if (skin.mondayFirst) DIAS_LUN else DIAS_DOM) {
                Text(
                    d, Modifier.weight(1f).padding(vertical = 6.dp), color = skin.mutedColor,
                    fontSize = 13.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium,
                )
            }
        }
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 4.dp), state = listState) {
            items(SEMANAS, key = { it }) { w ->
                val start = anchor.plus(w * 7, DateTimeUnit.DAY)
                Row(Modifier.fillMaxWidth().height(skin.rowHeight)) {
                    for (i in 0 until 7) {
                        val date = start.plus(i, DateTimeUnit.DAY)
                        DayCell(skin, date, date == today, events(date), Modifier.weight(1f)) { onDayClick(date) }
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
    event: CalendarEvent?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val base = modifier.fillMaxSize().padding(skin.cellGap)
    val shaped = if (skin.cardCells) {
        base.clip(RoundedCornerShape(skin.cellCorner)).background(skin.cellColor)
    } else {
        val linea = skin.dividerColor
        base.drawBehind { drawLine(linea, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1.dp.toPx()) }
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
                    color = dayColor(skin, date), fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    maxLines = 1, softWrap = false,
                )
            }
            if (event != null) {
                Box(
                    Modifier.padding(top = 3.dp, start = 1.dp, end = 1.dp).fillMaxWidth()
                        .clip(RoundedCornerShape(skin.chipCorner)).background(skin.chipColor)
                        .padding(horizontal = 3.dp, vertical = 1.dp),
                ) {
                    Text(event.title, color = skin.onChip, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Clip)
                }
            }
        }
    }
}
