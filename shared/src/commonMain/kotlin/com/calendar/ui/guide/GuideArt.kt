package com.calendar.ui.guide

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calendar.ui.CalendarSkin
import com.calendar.ui.Skins
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

/** Colores de las ilustraciones de la guía y de las instrucciones. */
private val Morado = Color(0xFF6750A4)
private val Naranja = Color(0xFFF57C00)
private val Rosa = Color(0xFFD81B60)
private val Gris = Color(0xFF5F6368)

/** Una celda destacada de [MiniMonth]. */
data class Mark(val color: Color, val text: String? = null)

/** Valor 0..1 que sube y baja sin parar (para pulsos). */
@Composable
fun rememberPulse(ms: Int = 900): Float {
    val t = rememberInfiniteTransition(label = "pulso")
    val v by t.animateFloat(0f, 1f, infiniteRepeatable(tween(ms, easing = LinearEasing), RepeatMode.Reverse), label = "pulso")
    return v
}

/** Índice 0 until [count] que avanza cada [ms] milisegundos. */
@Composable
fun rememberPhase(count: Int, ms: Long): Int {
    var p by remember { mutableStateOf(0) }
    LaunchedEffect(count, ms) {
        while (true) { delay(ms); p = (p + 1) % count }
    }
    return p
}

/** Mes dibujado en pequeño con celdas marcadas; [pulseDay] lleva un aro que late. */
@Composable
fun MiniMonth(
    year: Int, month: Int, marks: Map<Int, Mark> = emptyMap(), skin: CalendarSkin = Skins.Google,
    cell: Dp = 40.dp, pulseDay: Int? = null, pulse: Float = 0f, modifier: Modifier = Modifier,
) {
    val first = LocalDate(year, month, 1)
    val offset = first.dayOfWeek.ordinal // lunes = 0
    val days = first.daysUntil(first.plus(1, DateTimeUnit.MONTH))
    val weeks = (offset + days + 6) / 7
    Column(modifier.background(skin.background, RoundedCornerShape(12.dp)).padding(6.dp)) {
        Row {
            for (l in listOf("L", "M", "X", "J", "V", "S", "D")) {
                Box(Modifier.size(cell, 20.dp), contentAlignment = Alignment.Center) {
                    Text(l, fontSize = 10.sp, color = skin.mutedColor)
                }
            }
        }
        for (w in 0 until weeks) {
            Row {
                for (i in 0 until 7) {
                    val d = w * 7 + i - offset + 1
                    Box(Modifier.size(cell).padding(1.5.dp), contentAlignment = Alignment.Center) {
                        if (d in 1..days) {
                            val m = marks[d]
                            Box(
                                Modifier.fillMaxSize().clip(RoundedCornerShape(skin.cellCorner.coerceAtMost(7.dp)))
                                    .background(m?.color ?: skin.cellColor),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        d.toString(), fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                        color = if (m != null) Color.White else skin.textColor,
                                    )
                                    if (m?.text != null) Text(m.text, fontSize = 8.sp, color = Color.White, maxLines = 1)
                                }
                            }
                            if (d == pulseDay) {
                                Box(
                                    Modifier.fillMaxSize().scale(1f + 0.12f * pulse)
                                        .border(2.dp, Morado.copy(alpha = 0.35f + 0.65f * pulse), RoundedCornerShape(8.dp)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Pie(texto: String) {
    Text(texto, fontSize = 13.sp, color = Gris, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
}

/** Portada: el mismo mes con el aspecto de varias marcas, una tras otra. */
@Composable
fun ArtBienvenida(modifier: Modifier = Modifier) {
    val skins = listOf(Skins.Google, Skins.Apple, Skins.Samsung, Skins.Xiaomi, Skins.Business, Skins.Huawei)
    val fase = rememberPhase(skins.size, 1500)
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Crossfade(skins[fase], label = "marca") { s ->
            MiniMonth(2026, 11, mapOf(19 to Mark(s.accent.takeIf { it.luminance() > 0.05f } ?: Morado)), skin = s, cell = 36.dp)
        }
        Pie(skins[fase].nombre)
    }
}

private fun Color.luminance(): Float = 0.299f * red + 0.587f * green + 0.114f * blue

/** El efecto en dos tiempos: se pulsa el día clave y aparece el «Feliz cumpleaños» en el cumpleaños. */
@Composable
fun ArtEfecto(modifier: Modifier = Modifier) {
    val fase = rememberPhase(2, 2800)
    val pulso = rememberPulse()
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Crossfade(fase, label = "efecto") { f ->
            if (f == 0) MiniMonth(2026, 11, mapOf(6 to Mark(Morado, "8C")), pulseDay = 6, pulse = pulso, cell = 36.dp)
            else MiniMonth(2027, 1, mapOf(13 to Mark(Rosa, "Feliz")), cell = 36.dp)
        }
        Pie(if (fase == 0) "1. Pulsas el día clave y escribes un texto que acaba en la carta («…8C»)" else "2. En el cumpleaños aparece «Feliz cumpleaños» con la carta")
    }
}

/** Cómo se elige el día clave: cumpleaños menos dos meses, y la celda de encima. */
@Composable
fun ArtDiaClave(modifier: Modifier = Modifier) {
    val pulso = rememberPulse()
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        MiniMonth(2026, 11, mapOf(13 to Mark(Naranja, "−2 meses"), 6 to Mark(Morado, "CLAVE")), pulseDay = 6, pulse = pulso, cell = 36.dp)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(Naranja)); Spacer(Modifier.width(6.dp))
            Text("13 nov = cumpleaños (13 ene) menos 2 meses", fontSize = 12.sp, color = Gris)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(Morado)); Spacer(Modifier.width(6.dp))
            Text("6 nov = la celda justo encima: el día clave", fontSize = 12.sp, color = Gris)
        }
    }
}

/** Cómo se escribe la carta: se teclea un texto y las dos últimas letras se convierten en la carta. */
@Composable
fun ArtCarta(modifier: Modifier = Modifier) {
    val texto = "Cena con Marta 8C"
    var n by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            n = 0
            while (n < texto.length) { delay(140); n++ }
            delay(2600)
        }
    }
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.5.dp, Morado, RoundedCornerShape(12.dp)).padding(14.dp),
        ) {
            val fin = texto.length - 2
            Text(
                buildAnnotatedString {
                    val visible = texto.take(n)
                    withStyle(SpanStyle(color = Color.Black)) { append(visible.take(fin)) }
                    if (n > fin) withStyle(SpanStyle(color = Rosa, fontWeight = FontWeight.Bold)) { append(visible.drop(fin)) }
                },
                fontSize = 20.sp,
            )
        }
        Spacer(Modifier.height(12.dp))
        Text("↓", fontSize = 22.sp, color = Gris, modifier = Modifier.alpha(if (n >= texto.length) 1f else 0.2f))
        Box(
            Modifier.alpha(if (n >= texto.length) 1f else 0.2f).clip(RoundedCornerShape(12.dp)).background(Rosa.copy(alpha = 0.12f)).padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text("8C = 8 de corazones", fontSize = 18.sp, color = Rosa, fontWeight = FontWeight.Medium)
        }
    }
}

/** Los dos gestos ocultos de la cabecera: Ajustes (toda la cabecera) y reinicio (esquina izquierda). */
@Composable
fun ArtGestos(modifier: Modifier = Modifier) {
    val fase = rememberPhase(2, 2400)
    val pulso = rememberPulse(1000)
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF0F4F9)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(Modifier.padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { Box(Modifier.size(22.dp, 3.dp).background(Color.DarkGray)) }
                }
                Spacer(Modifier.width(20.dp))
                Text("Noviembre", fontSize = 20.sp, color = Color.DarkGray)
                Spacer(Modifier.weight(1f))
                repeat(3) { Box(Modifier.padding(start = 10.dp).size(18.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.5f))) }
            }
            // Aro de pulsación larga: en la esquina (reinicio) o en el centro (Ajustes).
            val alineacion = if (fase == 0) Alignment.CenterStart else Alignment.Center
            Box(
                Modifier.align(alineacion).padding(start = if (fase == 0) 10.dp else 0.dp).size(46.dp).scale(0.8f + 0.4f * pulso)
                    .border(3.dp, (if (fase == 0) Naranja else Morado).copy(alpha = 0.9f - 0.6f * pulso), CircleShape),
            )
        }
        Pie(if (fase == 0) "Pulsación larga en la esquina superior izquierda: reinicia el efecto" else "Pulsación larga en el resto de la cabecera: abre los Ajustes")
    }
}

/** Resumen de Ajustes: las marcas disponibles. */
@Composable
fun ArtAjustes(modifier: Modifier = Modifier) {
    val fase = rememberPhase(Skins.all.size, 900)
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for ((i, s) in Skins.all.withIndex()) {
                Box(
                    Modifier.size(if (i == fase) 30.dp else 22.dp).clip(CircleShape)
                        .background(if (s.background.luminance() < 0.2f) Color.DarkGray else s.accent.takeIf { it.luminance() > 0.05f } ?: Morado),
                )
            }
        }
        Pie(Skins.all[fase].nombre)
    }
}
