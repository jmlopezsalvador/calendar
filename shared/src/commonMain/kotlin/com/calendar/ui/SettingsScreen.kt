package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.calendar.domain.AppIdentity
import com.calendar.domain.EventColor
import com.calendar.domain.RandomEvents
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Ajustes (acceso oculto: pulsación larga en el título del mes). Pantalla neutra, no imita ninguna marca. */
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onChange: (AppSettings) -> Unit,
    /** Genera eventos: (meses atrás, meses adelante, % de días) → nº de eventos creados. */
    onGenerate: (Int, Int, Int) -> Int,
    canRename: Boolean,
    restartsApp: Boolean,
    /** Aplica nombre e icono (ids). */
    onApplyIdentity: (String, String) -> Unit,
    onShowGuide: () -> Unit,
    onShowInstructions: () -> Unit,
    onClose: () -> Unit,
) {
    var atras by remember { mutableStateOf(1) }
    var adelante by remember { mutableStateOf(3) }
    var porcentaje by remember { mutableStateOf(30) }
    var resultado by remember { mutableStateOf<String?>(null) }
    // Cambio pendiente de confirmar: (nombre, icono) propuestos.
    var pendiente by remember { mutableStateOf<Pair<String, String>?>(null) }
    val skin = Skins.byId(if (settings.skinId == AppSettings.AUTO) systemSkinId() else settings.skinId)
    Column(Modifier.fillMaxSize().background(Color.White).windowInsetsPadding(WindowInsets.systemBars).dismissKeyboardOnTap()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onClose) { Text("‹ Volver", fontSize = 16.sp) }
            Text("Ajustes", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Seccion("Aspecto del calendario", inicial = true) {
                Titulo("Marca")
                Opcion("Automático (según el sistema: ${Skins.byId(systemSkinId()).nombre})", settings.skinId == AppSettings.AUTO) {
                    onChange(settings.copy(skinId = AppSettings.AUTO).clearTweaks())
                }
                for (s in Skins.all) {
                    Opcion(s.nombre, settings.skinId == s.id) { onChange(settings.copy(skinId = s.id).clearTweaks()) }
                }

                Titulo("Desplazamiento")
                Opcion("Según la marca (${if (skin.horizontalByDefault) "horizontal" else "vertical"})", settings.scroll == AppSettings.AUTO) {
                    onChange(settings.copy(scroll = AppSettings.AUTO))
                }
                Opcion("Vertical continuo (semanas)", settings.scroll == AppSettings.VERTICAL) { onChange(settings.copy(scroll = AppSettings.VERTICAL)) }
                Opcion("Horizontal (mes a mes)", settings.scroll == AppSettings.HORIZONTAL) { onChange(settings.copy(scroll = AppSettings.HORIZONTAL)) }

                Titulo("Ajuste fino")
                Deslizador("Redondeo de las celdas", settings.cornerDp ?: skin.cellCorner.value.toInt(), 0..20) { onChange(settings.copy(cornerDp = it)) }
                Deslizador("Separación entre celdas", settings.gapDp ?: skin.cellGap.value.toInt(), 0..10) { onChange(settings.copy(gapDp = it)) }
                Deslizador("Altura de fila (modo vertical)", settings.rowDp ?: skin.rowHeight.value.toInt(), 60..160) { onChange(settings.copy(rowDp = it)) }
                TextButton(onClick = { onChange(settings.clearTweaks()) }) { Text("Restablecer ajuste fino") }
            }

            Seccion("Nombre e icono de la app") {
                if (canRename) {
                    Titulo("Nombre")
                    for (o in AppIdentity.names) {
                        Opcion(o.nombre, settings.appName == o.id) {
                            if (settings.appName != o.id) pendiente = o.id to settings.appIcon
                        }
                    }
                }
                Titulo("Icono")
                for (o in AppIdentity.icons) {
                    Opcion(o.nombre, settings.appIcon == o.id) {
                        if (settings.appIcon != o.id) pendiente = settings.appName to o.id
                    }
                }
            }

            Seccion("Eventos aleatorios") {
                Text("Temas (uno por línea)", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(top = 8.dp))
                OutlinedTextField(
                    value = settings.topics ?: RandomEvents.DEFAULT_TOPICS.joinToString("\n"),
                    onValueChange = { onChange(settings.copy(topics = it)) },
                    modifier = Modifier.fillMaxWidth(), minLines = 4, maxLines = 8,
                )
                TextButton(onClick = { onChange(settings.copy(topics = null)) }) { Text("Restablecer temas") }
                Deslizador("Meses hacia atrás desde hoy", atras, 0..12) { atras = it }
                Deslizador("Meses hacia adelante desde hoy", adelante, 0..24) { adelante = it }
                Deslizador("Días con evento (%)", porcentaje, 5..100) { porcentaje = it }
                Titulo("Color de los eventos")
                Opcion("Aleatorio (todos los colores)", settings.genColor == EventColor.MODE_RANDOM) { onChange(settings.copy(genColor = EventColor.MODE_RANDOM)) }
                Opcion("El de la marca del calendario", settings.genColor == EventColor.MODE_DEFAULT) { onChange(settings.copy(genColor = EventColor.MODE_DEFAULT)) }
                for (c in EventColor.entries) {
                    Opcion("Siempre ${c.nombre.lowercase()}", settings.genColor == c.id) { onChange(settings.copy(genColor = c.id)) }
                }
                TextButton(onClick = {
                    val n = onGenerate(atras, adelante, porcentaje)
                    resultado = "Creados $n eventos (los días ocupados no se tocan)."
                }) { Text("Generar eventos") }
                resultado?.let { Text(it, fontSize = 14.sp, color = Color(0xFF5F6368)) }
            }

            Seccion("Guía e instrucciones") {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Mostrar la guía al iniciar", fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f))
                    Switch(settings.showGuide, { onChange(settings.copy(showGuide = it)) })
                }
                TextButton(onClick = onShowGuide) { Text("Ver la guía ahora") }
                TextButton(onClick = onShowInstructions) { Text("Instrucciones") }
            }

            Text(
                "Desarrollado por WikiCode. Adri & Semi 2026",
                fontSize = 13.sp, color = Color(0xFF79747E), textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
            )
        }
    }
    pendiente?.let { (n, i) ->
        AlertDialog(
            onDismissRequest = { pendiente = null },
            title = { Text("Cambiar nombre e icono") },
            text = {
                Text(
                    "La app pasará a llamarse «${AppIdentity.nameLabel(n)}» con el icono «${AppIdentity.iconLabel(i)}».\n\n" +
                        if (restartsApp) "Para hacerlo efectivo la app se reiniciará. ¿Quieres aplicarlo ahora?"
                        else "El sistema puede mostrar un aviso al cambiar el icono. ¿Quieres aplicarlo ahora?",
                )
            },
            confirmButton = { TextButton(onClick = { pendiente = null; onApplyIdentity(n, i) }) { Text("Aplicar") } },
            dismissButton = { TextButton(onClick = { pendiente = null }) { Text("Cancelar") } },
        )
    }
}

/** Sección plegable de Ajustes. */
@Composable
private fun Seccion(titulo: String, inicial: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    var abierta by remember { mutableStateOf(inicial) }
    Column(Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF7F2FA))) {
        Row(Modifier.fillMaxWidth().clickable { abierta = !abierta }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(titulo, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, modifier = Modifier.weight(1f))
            Icon(Ico.DROPDOWN, Color(0xFF49454F), 22.dp, Modifier.rotate(if (abierta) 180f else 0f))
        }
        AnimatedVisibility(abierta) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp), content = content)
        }
    }
}

@Composable
private fun Titulo(t: String) {
    Text(t, fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
}

@Composable
private fun Deslizador(texto: String, valor: Int, rango: IntRange, onChange: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text("$texto: $valor", fontSize = 15.sp, color = Color.Black)
        Slider(
            value = valor.toFloat(), onValueChange = { onChange(it.toInt()) },
            valueRange = rango.first.toFloat()..rango.last.toFloat(),
        )
    }
}

@Composable
private fun Opcion(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = seleccionada, onClick = onClick)
        Text(texto, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
    }
}
