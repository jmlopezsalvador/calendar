package com.calendar.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

private val Morado = Color(0xFF6750A4)

/** Una fase de la guía: título, texto y su ilustración animada. */
private class Fase(val titulo: String, val texto: String, val arte: @Composable () -> Unit)

private val FASES = listOf(
    Fase(
        "Bienvenido a Calendar",
        "Es un calendario falso para magia. Se ve como el calendario de varias marcas (Google, Apple, Samsung, Xiaomi…) " +
            "y nadie sospecha de él. Todo se guarda solo en tu móvil.",
    ) { ArtBienvenida() },
    Fase(
        "El efecto",
        "Le pides al espectador su fecha de cumpleaños. Tocas un día del calendario, escribes un evento y, al buscar " +
            "después su cumpleaños, ya hay un «Feliz cumpleaños» con el nombre de su carta.",
    ) { ArtEfecto() },
    Fase(
        "El día clave",
        "Se calcula con el cumpleaños: retrocede 2 meses y sube a la celda de encima. La app hace la cuenta " +
            "al revés y coloca el mensaje en el cumpleaños exacto. Ejemplo: cumpleaños 13 de enero → día clave 6 de noviembre. Si ese día no existe, retrocede 3 meses y activa «Todo el día».",
    ) { ArtDiaClave() },
    Fase(
        "La carta",
        "En el evento escribe cualquier texto cuyas dos últimas letras sean la carta: valor + palo. " +
            "Valores: A, 2–9, 0 (10), J, Q, K. Palos: C corazones, P picas, T tréboles, D diamantes.",
    ) { ArtCarta() },
    Fase(
        "Gestos ocultos",
        "No hay botones visibles de ajustes. Con una pulsación larga en la cabecera abres los Ajustes; " +
            "en su esquina superior izquierda reinicias el efecto para repetirlo. Ambos vibran dos veces.",
    ) { ArtGestos() },
    Fase(
        "A tu gusto",
        "En Ajustes eliges la marca, el desplazamiento, el nombre y el icono de la app, rellenas el calendario con " +
            "eventos creíbles y vuelves a ver esta guía o las instrucciones completas.",
    ) { ArtAjustes() },
)

/**
 * Guía de inicio por fases (pantalla completa). En la última fase ofrece no volver a mostrarla.
 * [onFinish] recibe `true` si el usuario marcó «No volver a mostrar».
 */
@Composable
fun GuideDialog(noMostrarInicial: Boolean, onFinish: (noMostrar: Boolean) -> Unit) {
    val pager = rememberPagerState(pageCount = { FASES.size })
    val scope = rememberCoroutineScope()
    var noMostrar by remember { mutableStateOf(noMostrarInicial) }
    val ultima = pager.currentPage == FASES.size - 1
    // Capa a pantalla completa por encima de la app (no es un Dialog del sistema, así respeta los márgenes de las barras).
    run {
        Box(
            Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.55f)).pointerInput(Unit) { detectTapGestures { } }
                .windowInsetsPadding(WindowInsets.systemBars),
            contentAlignment = Alignment.Center,
        ) {
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 24.dp).fillMaxWidth().fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(28.dp)).background(Color.White),
        ) {
            HorizontalPager(pager, Modifier.weight(1f)) { i ->
                val f = FASES[i]
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("${i + 1} / ${FASES.size}", fontSize = 13.sp, color = Color(0xFF79747E))
                    Text(
                        f.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black,
                        textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
                    )
                    Box(Modifier.fillMaxWidth().padding(bottom = 20.dp), contentAlignment = Alignment.Center) { f.arte() }
                    Text(f.texto, fontSize = 16.sp, color = Color(0xFF333333), textAlign = TextAlign.Center, lineHeight = 23.sp)
                }
            }
            if (ultima) {
                Row(
                    Modifier.fillMaxWidth().clickable { noMostrar = !noMostrar }.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(noMostrar, { noMostrar = it })
                    Text("No volver a mostrar esta guía", fontSize = 15.sp, color = Color.Black)
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (ultima) TextButton(onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } }) { Text("Atrás") }
                else TextButton(onClick = { onFinish(noMostrar) }) { Text("Saltar") }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    for (i in FASES.indices) {
                        Box(
                            Modifier.size(if (i == pager.currentPage) 10.dp else 7.dp).clip(CircleShape)
                                .background(if (i == pager.currentPage) Morado else Color(0xFFCAC4D0)),
                        )
                    }
                }
                TextButton(onClick = {
                    if (ultima) onFinish(noMostrar) else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                }) { Text(if (ultima) "Empezar" else "Siguiente", fontWeight = FontWeight.Bold) }
            }
        }
        }
    }
}
