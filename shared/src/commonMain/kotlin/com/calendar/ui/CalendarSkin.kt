package com.calendar.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * «Look and feel» de un calendario concreto. La pantalla mensual ([CalendarScreen]) es común y se pinta
 * con los valores de la skin; para añadir una marca basta con definir otra instancia.
 */
data class CalendarSkin(
    val id: String,
    val nombre: String,
    val mondayFirst: Boolean,
    val background: Color,
    val cellColor: Color,
    val cellCorner: Dp,
    val cellGap: Dp,
    val rowHeight: Dp,
    val textColor: Color,
    val mutedColor: Color,
    val accent: Color,
    val onAccent: Color,
    val chipColor: Color,
    val onChip: Color,
    val chipCorner: Dp,
    /** Muestra el mes tras el número del día 1 («1 dic»). */
    val monthOnFirst: Boolean = true,
)

object Skins {
    /** Google Calendar (Android): tarjetas redondeadas blancas sobre gris azulado, bandas de evento de color. */
    val Google = CalendarSkin(
        id = "google", nombre = "Google Calendar", mondayFirst = true,
        background = Color(0xFFF0F4F9), cellColor = Color.White, cellCorner = 8.dp, cellGap = 3.dp,
        rowHeight = 96.dp, textColor = Color(0xFF1F1F1F), mutedColor = Color(0xFF747775),
        accent = Color(0xFF0B57D0), onAccent = Color.White,
        chipColor = Color(0xFF039BE5), onChip = Color.White, chipCorner = 4.dp,
    )

    val all = listOf(Google)
    fun byId(id: String?): CalendarSkin = all.firstOrNull { it.id == id } ?: Google
}
