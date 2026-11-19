package com.calendar.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Disposición de cabecera y elementos flotantes (solo atrezo) de cada familia de calendarios. */
enum class Chrome { GOOGLE, APPLE, SAMSUNG, OPPO, XIAOMI, HUAWEI, BUSINESS, ACALENDAR, MOCA }

/** Cómo se muestra el evento dentro de la celda: banda de color, texto de color o punto. */
enum class EventStyle { BAND, TEXT, DOT }

/** Cómo se rotulan los días de la semana: «D L M…», «lun mar…» o «LU. MA.…». */
enum class WeekdayStyle { LETTER, SHORT3, UPPER2_DOT, UPPER3 }

/**
 * «Look and feel» de un calendario concreto. La pantalla mensual ([CalendarScreen]) es común y se pinta
 * con los valores de la skin; para añadir una marca o variante basta con definir otra instancia.
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
    val chrome: Chrome,
    val weekdayStyle: WeekdayStyle,
    /** Desplazamiento por defecto de la marca: true = horizontal (paginado por meses), false = vertical continuo. */
    val horizontalByDefault: Boolean,
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
    val eventStyle: EventStyle = EventStyle.BAND,
    /** Líneas verticales entre columnas además de las horizontales (celdas planas). */
    val verticalLines: Boolean = false,
    /** Fondo y texto de la barra superior (null = sin barra, se pinta sobre el fondo). */
    val headerBg: Color? = null,
    val onHeader: Color = Color.Black,
)

/** Ajuste fino sobre la skin elegida (null = valor de la skin). Ver [AppSettings]. */
fun CalendarSkin.withTweaks(s: AppSettings): CalendarSkin = copy(
    cellCorner = s.cornerDp?.dp ?: cellCorner,
    cellGap = s.gapDp?.dp ?: cellGap,
    rowHeight = s.rowDp?.dp ?: rowHeight,
)

private val DIAS_LETRA_LUN = listOf("L", "M", "X", "J", "V", "S", "D")
private val DIAS_3 = listOf("lun", "mar", "mié", "jue", "vie", "sáb", "dom")
private val DIAS_2 = listOf("LU.", "MA.", "MI.", "JU.", "VI.", "SÁ.", "DO.")
private val DIAS_U3 = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")

/** Rótulos de los días de la semana en el orden de la skin (lunes o domingo primero). */
fun CalendarSkin.weekdayLabels(): List<String> {
    val base = when (weekdayStyle) {
        WeekdayStyle.LETTER -> DIAS_LETRA_LUN.map { if (it == "D") "D" else it }
        WeekdayStyle.SHORT3 -> DIAS_3
        WeekdayStyle.UPPER2_DOT -> DIAS_2
        WeekdayStyle.UPPER3 -> DIAS_U3
    }
    return if (mondayFirst) base else listOf(base.last()) + base.dropLast(1)
}

object Skins {
    /** Google Calendar (Android): tarjetas redondeadas blancas sobre gris azulado, bandas de evento de color. */
    val Google = CalendarSkin(
        id = "google", nombre = "Google Calendar", mondayFirst = true,
        background = Color(0xFFF0F4F9), cellColor = Color.White, cardCells = true, cellCorner = 8.dp, cellGap = 3.dp,
        rowHeight = 96.dp, textColor = Color(0xFF1F1F1F), mutedColor = Color(0xFF444746),
        accent = Color(0xFF0B57D0), onAccent = Color.White,
        chipColor = Color(0xFF039BE5), onChip = Color.White, chipCorner = 4.dp,
        chrome = Chrome.GOOGLE, weekdayStyle = WeekdayStyle.SHORT3, horizontalByDefault = true,
    )

    /** Google Calendar tal como se ve en un Samsung: celdas sin redondeo, fondo lavanda muy claro, bandas pastel. */
    val GoogleEnSamsung = Google.copy(
        id = "google_samsung", nombre = "Google Calendar (en Samsung)",
        background = Color(0xFFF8F9FD), cellColor = Color(0xFFF3F4FA), cellCorner = 0.dp, cellGap = 2.dp,
        rowHeight = 100.dp, chipColor = Color(0xFF8F9BDB), chipCorner = 5.dp,
    )

    /** Apple Calendar (iOS): fondo blanco, celdas planas con línea fina, fin de semana gris, hoy en círculo rojo. */
    val Apple = CalendarSkin(
        id = "apple", nombre = "Apple Calendar", mondayFirst = false,
        background = Color.White, cellColor = Color.White, cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 92.dp, textColor = Color.Black, mutedColor = Color(0xFF8E8E93),
        accent = Color(0xFFFF3B30), onAccent = Color.White,
        chipColor = Color(0xFFCCE4FF), onChip = Color(0xFF0A4A99), chipCorner = 6.dp,
        chrome = Chrome.APPLE, weekdayStyle = WeekdayStyle.LETTER, horizontalByDefault = false,
        dividerColor = Color(0xFFE5E5EA), weekendColor = Color(0xFF8E8E93),
        headerBold = true,
    )

    /** Samsung Calendar (One UI): celdas casi blancas, domingo en rojo, hoy en cuadrado negro, mes centrado. */
    val Samsung = CalendarSkin(
        id = "samsung", nombre = "Samsung Calendar", mondayFirst = true,
        background = Color(0xFFFCFCFE), cellColor = Color(0xFFF6F5FA), cardCells = true, cellCorner = 6.dp, cellGap = 2.dp,
        rowHeight = 90.dp, textColor = Color.Black, mutedColor = Color(0xFF6E6E73),
        accent = Color.Black, onAccent = Color.White,
        chipColor = Color(0xFFFFC9C9), onChip = Color.Black, chipCorner = 2.dp,
        chrome = Chrome.SAMSUNG, weekdayStyle = WeekdayStyle.UPPER2_DOT, horizontalByDefault = true,
        sundayColor = Color(0xFFD93025), todayCircle = false,
        headerCentered = true, headerUppercase = true, monthOnFirst = false,
    )

    /** OPPO (ColorOS): usa Google Calendar con cuadrícula de líneas finas, domingo primero e iniciales. */
    val Oppo = Google.copy(
        id = "oppo", nombre = "OPPO Calendar", mondayFirst = false,
        background = Color.White, cellColor = Color.White, cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 100.dp, dividerColor = Color(0xFFE8EAF0), verticalLines = true,
        chrome = Chrome.OPPO, weekdayStyle = WeekdayStyle.LETTER, mutedColor = Color(0xFF5F6368),
        accent = Color(0xFF1A73E8), chipColor = Color(0xFF039BE5), chipCorner = 2.dp,
    )

    /** Xiaomi Mi Calendar (HyperOS oscuro): mes compacto sobre negro, hoy en círculo azul, puntos de evento. */
    val Xiaomi = CalendarSkin(
        id = "xiaomi", nombre = "Xiaomi Calendar", mondayFirst = false,
        background = Color.Black, cellColor = Color.Black, cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 70.dp, textColor = Color.White, mutedColor = Color(0xFF8A8A8E),
        accent = Color(0xFF1B7FD6), onAccent = Color.White,
        chipColor = Color.White, onChip = Color.Black, chipCorner = 2.dp,
        chrome = Chrome.XIAOMI, weekdayStyle = WeekdayStyle.UPPER3, horizontalByDefault = true,
        dividerColor = Color.Transparent, sundayColor = Color(0xFFE53935),
        eventStyle = EventStyle.DOT, monthOnFirst = false,
    )

    /** Huawei Calendar (EMUI): fondo gris muy claro, fin de semana atenuado, hoy en círculo azul, pestañas de vista. */
    val Huawei = CalendarSkin(
        id = "huawei", nombre = "Huawei Calendar", mondayFirst = true,
        background = Color(0xFFF5F5F5), cellColor = Color(0xFFF5F5F5), cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 76.dp, textColor = Color(0xFF182431), mutedColor = Color(0xFF8E9399),
        accent = Color(0xFF0A59F7), onAccent = Color.White,
        chipColor = Color(0xFF9CBBF5), onChip = Color.Black, chipCorner = 2.dp,
        chrome = Chrome.HUAWEI, weekdayStyle = WeekdayStyle.UPPER2_DOT, horizontalByDefault = true,
        dividerColor = Color.Transparent, weekendColor = Color(0xFF8E9399),
        eventStyle = EventStyle.DOT, monthOnFirst = false,
    )

    /** Business Calendar 2: barra superior negra, cuadrícula fina, eventos como texto de color. */
    val Business = CalendarSkin(
        id = "business", nombre = "Business Calendar 2", mondayFirst = false,
        background = Color.White, cellColor = Color.White, cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 110.dp, textColor = Color(0xFF333333), mutedColor = Color(0xFF9E9E9E),
        accent = Color.Black, onAccent = Color.White,
        chipColor = Color(0xFF2E7D32), onChip = Color.White, chipCorner = 2.dp,
        chrome = Chrome.BUSINESS, weekdayStyle = WeekdayStyle.UPPER3, horizontalByDefault = false,
        dividerColor = Color(0xFFD6D6D6), verticalLines = true, sundayColor = Color(0xFFD50000),
        eventStyle = EventStyle.TEXT, monthOnFirst = false, headerBg = Color.Black, onHeader = Color.White,
    )

    /** Monthly Calendar Moca (iPhone): cuadrícula fina, sábado azul y domingo rojo, hoy resaltado, texto azul. */
    val Moca = CalendarSkin(
        id = "moca", nombre = "Moca Monthly Calendar", mondayFirst = false,
        background = Color.White, cellColor = Color.White, cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 112.dp, textColor = Color.Black, mutedColor = Color(0xFF7A7467),
        accent = Color(0xFFFFFBC4), onAccent = Color.Black,
        chipColor = Color(0xFF1F45C4), onChip = Color.White, chipCorner = 0.dp,
        chrome = Chrome.MOCA, weekdayStyle = WeekdayStyle.SHORT3, horizontalByDefault = false,
        dividerColor = Color(0xFFBDBDBD), verticalLines = true, weekendColor = Color(0xFF2B35E6), sundayColor = Color(0xFFE53935),
        todayCircle = false, eventStyle = EventStyle.TEXT, monthOnFirst = false,
        headerBg = Color(0xFF8A7F6E), onHeader = Color.White,
    )

    /** aCalendar: modo oscuro denso, domingo en rojo, eventos como texto verde sobre gris oscuro. */
    val ACalendar = CalendarSkin(
        id = "acalendar", nombre = "aCalendar", mondayFirst = false,
        background = Color(0xFF212121), cellColor = Color(0xFF212121), cardCells = false, cellCorner = 0.dp, cellGap = 0.dp,
        rowHeight = 118.dp, textColor = Color.White, mutedColor = Color(0xFF9E9E9E),
        accent = Color(0xFF444444), onAccent = Color.White,
        chipColor = Color(0xFF1B8A3A), onChip = Color.White, chipCorner = 2.dp,
        chrome = Chrome.ACALENDAR, weekdayStyle = WeekdayStyle.SHORT3, horizontalByDefault = false,
        dividerColor = Color(0xFF3A3A3A), sundayColor = Color(0xFFFF1E1E),
        eventStyle = EventStyle.TEXT, monthOnFirst = false, headerBg = Color(0xFF121212), onHeader = Color.White,
    )

    val all = listOf(Google, GoogleEnSamsung, Apple, Samsung, Oppo, Xiaomi, Huawei, Business, Moca, ACalendar)
    fun byId(id: String?): CalendarSkin = all.firstOrNull { it.id == id } ?: Google
}
