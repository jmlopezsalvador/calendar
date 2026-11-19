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
    /** true: cada día es una tarjeta; false: celdas planas separadas por una línea fina. */
    val cardCells: Boolean,
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
    val dividerColor: Color = Color(0xFFE0E0E0),
    /** Color de sábado y domingo (null = el normal). */
    val weekendColor: Color? = null,
    /** Color del domingo, con prioridad sobre [weekendColor]. */
    val sundayColor: Color? = null,
    /** El día de hoy se marca con un círculo (true) o un cuadrado redondeado (false). */
    val todayCircle: Boolean = true,
    val headerCentered: Boolean = false,
    val headerUppercase: Boolean = false,
    val headerBold: Boolean = false,
    /** Muestra el mes tras el número del día 1 («1 dic»). */
    val monthOnFirst: Boolean = true,
)

object Skins {
    /** Google Calendar (Android): tarjetas redondeadas blancas sobre gris azulado, bandas de evento de color. */
    val Google = CalendarSkin(
        id = "google", nombre = "Google Calendar", mondayFirst = true,
        background = Color(0xFFF0F4F9), cellColor = Color.White, cardCells = true, cellCorner = 8.dp, cellGap = 3.dp,
        rowHeight = 96.dp, textColor = Color(0xFF1F1F1F), mutedColor = Color(0xFF747775),
        accent = Color(0xFF0B57D0), onAccent = Color.White,
        chipColor = Color(0xFF039BE5), onChip = Color.White, chipCorner = 4.dp,
    )

    /** Apple Calendar (iOS): fondo blanco, celdas planas con línea fina, fin de semana gris, hoy en círculo rojo. */
    val Apple = CalendarSkin(
        id = "apple", nombre = "Apple Calendar", mondayFirst = false,
        background = Color.White, cellColor = Color.White, cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 92.dp, textColor = Color.Black, mutedColor = Color(0xFF8E8E93),
        accent = Color(0xFFFF3B30), onAccent = Color.White,
        chipColor = Color(0xFFCCE4FF), onChip = Color(0xFF0A4A99), chipCorner = 6.dp,
        dividerColor = Color(0xFFE5E5EA), weekendColor = Color(0xFF8E8E93),
        headerBold = true,
    )

    /** Samsung Calendar (One UI): celdas casi blancas, domingo en rojo, hoy en cuadrado negro, mes centrado. */
    val Samsung = CalendarSkin(
        id = "samsung", nombre = "Samsung Calendar", mondayFirst = true,
        background = Color(0xFFFCFCFE), cellColor = Color(0xFFF6F5FA), cardCells = true, cellCorner = 6.dp, cellGap = 2.dp,
        rowHeight = 90.dp, textColor = Color.Black, mutedColor = Color(0xFF9A9AA0),
        accent = Color.Black, onAccent = Color.White,
        chipColor = Color(0xFFFFC9C9), onChip = Color.Black, chipCorner = 2.dp,
        sundayColor = Color(0xFFD93025), todayCircle = false,
        headerCentered = true, headerUppercase = true, monthOnFirst = false,
    )

    val all = listOf(Google, Apple, Samsung)
    fun byId(id: String?): CalendarSkin = all.firstOrNull { it.id == id } ?: Google
}
