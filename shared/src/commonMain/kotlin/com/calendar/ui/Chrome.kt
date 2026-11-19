package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate

/** Título del mes que se muestra en la cabecera (la marca decide mayúsculas y si lleva año). */
private fun tituloMes(skin: CalendarSkin, d: LocalDate, today: LocalDate): String {
    var t = nombreMes(d.monthNumber)
    if (skin.headerUppercase) t = t.uppercase()
    if (skin.chrome != Chrome.APPLE && d.year != today.year) t += " ${d.year}"
    return t
}

/**
 * Cabecera de la app según la marca: botones de atrezo (no hacen nada). No se dibujan avatares ni iconos de login.
 * [onTitleLongPress] abre los Ajustes ocultos.
 */
@Composable
fun CalendarHeader(skin: CalendarSkin, shown: LocalDate, today: LocalDate, onTitleLongPress: () -> Unit) {
    val titleMod = Modifier.pointerInput(Unit) { detectTapGestures(onLongPress = { onTitleLongPress() }) }
    val titulo = tituloMes(skin, shown, today)
    when (skin.chrome) {
        Chrome.GOOGLE -> Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Ico.MENU, skin.textColor, 26.dp)
            Spacer(Modifier.width(22.dp))
            Row(titleMod, verticalAlignment = Alignment.CenterVertically) {
                Text(titulo, color = skin.textColor, fontSize = 24.sp, maxLines = 1)
                Spacer(Modifier.width(6.dp))
                Icon(Ico.DROPDOWN, skin.textColor, 22.dp)
            }
            Spacer(Modifier.weight(1f))
            Icon(Ico.SEARCH, skin.mutedColor, 26.dp)
            Spacer(Modifier.width(18.dp))
            DayIcon(today.dayOfMonth, skin.mutedColor)
            Spacer(Modifier.width(18.dp))
            Icon(Ico.TASKS, skin.mutedColor, 26.dp)
        }
        Chrome.SAMSUNG -> Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Ico.MENU, skin.textColor, 24.dp)
            Text(
                titulo, titleMod.weight(1f), color = skin.textColor, fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 1,
            )
            Icon(Ico.PENCIL, skin.textColor, 24.dp)
            Spacer(Modifier.width(14.dp))
            Icon(Ico.SEARCH, skin.textColor, 24.dp)
            Spacer(Modifier.width(14.dp))
            DayIcon(today.dayOfMonth, skin.textColor, 24.dp)
        }
        Chrome.APPLE -> Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Pill(skin) {
                    Icon(Ico.CHEVRON_LEFT, skin.textColor, 22.dp)
                    Text("${shown.year}", color = skin.textColor, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.weight(1f))
                Pill(skin) { Icon(Ico.LIST, skin.textColor, 24.dp) }
                Spacer(Modifier.width(10.dp))
                Pill(skin) {
                    Icon(Ico.SEARCH, skin.textColor, 24.dp)
                    Spacer(Modifier.width(14.dp))
                    Icon(Ico.PLUS, skin.textColor, 24.dp)
                }
            }
            Text(
                titulo, titleMod.padding(start = 8.dp, top = 6.dp), color = skin.textColor,
                fontSize = 32.sp, fontWeight = FontWeight.Bold, maxLines = 1,
            )
        }
    }
}

/** Píldora clara con sombra suave de los botones de Apple. */
@Composable
private fun Pill(skin: CalendarSkin, content: @Composable () -> Unit) {
    Row(
        Modifier.shadow(3.dp, RoundedCornerShape(50)).clip(RoundedCornerShape(50)).background(Color(0xFFFAFAFA))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) { content() }
}

/** Elementos flotantes inferiores (botón «+», barra «Hoy», barra «Añadir…»). Solo atrezo. */
@Composable
fun BoxScope.CalendarOverlay(skin: CalendarSkin, today: LocalDate) {
    when (skin.chrome) {
        Chrome.GOOGLE -> Box(
            Modifier.align(Alignment.BottomEnd).padding(20.dp).size(60.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp)).clip(RoundedCornerShape(18.dp)).background(Color(0xFFD3E3FD)),
            contentAlignment = Alignment.Center,
        ) { Icon(Ico.PLUS, Color(0xFF0B57D0), 28.dp) }

        Chrome.SAMSUNG -> Row(
            Modifier.align(Alignment.BottomCenter).padding(horizontal = 28.dp, vertical = 16.dp).fillMaxWidth()
                .shadow(5.dp, RoundedCornerShape(50)).clip(RoundedCornerShape(50)).background(Color.White)
                .padding(horizontal = 22.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Añadir el ${today.dayOfMonth} de ${nombreMes(today.monthNumber).lowercase()}", color = Color(0xFF6E6E73), fontSize = 15.sp, maxLines = 1, modifier = Modifier.weight(1f))
            Icon(Ico.PLUS, Color.Black, 24.dp)
        }

        Chrome.APPLE -> Row(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
        ) {
            Pill(skin) { Text("Hoy", color = skin.textColor, fontSize = 17.sp, fontWeight = FontWeight.Medium) }
            Pill(skin) {
                Icon(Ico.GRID, skin.textColor, 26.dp)
                Spacer(Modifier.width(18.dp))
                Icon(Ico.TRAY, skin.textColor, 26.dp)
            }
        }
    }
}
