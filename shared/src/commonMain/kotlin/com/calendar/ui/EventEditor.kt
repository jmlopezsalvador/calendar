package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.calendar.domain.CalendarEvent
import kotlinx.datetime.LocalDate

/** Familia de formulario: cada marca usa la disposición de su propia app. */
private enum class EditorStyle { GOOGLE, APPLE, SAMSUNG }

private fun CalendarSkin.editorStyle() = when (chrome) {
    Chrome.GOOGLE, Chrome.OPPO, Chrome.BUSINESS, Chrome.ACALENDAR -> EditorStyle.GOOGLE
    Chrome.APPLE, Chrome.MOCA -> EditorStyle.APPLE
    Chrome.SAMSUNG, Chrome.XIAOMI, Chrome.HUAWEI -> EditorStyle.SAMSUNG
}

/**
 * Formulario de evento a pantalla completa con la disposición y los colores de la marca [skin].
 * Se abre directamente al pulsar un día; si el día ya tiene evento se edita. Es atrezo: solo título y
 * descripción tienen función; el resto de filas (todo el día, fechas, repetición…) son decorativas.
 */
@Composable
fun EventEditor(
    skin: CalendarSkin,
    date: LocalDate,
    existing: CalendarEvent?,
    onSave: (title: String, notes: String, allDay: Boolean) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    // «Todo el día» tiene función oculta: en el día clave significa «3 meses» en vez de 2 (ver KeyDay).
    var allDay by remember { mutableStateOf(existing?.allDay ?: false) }
    val fecha = "${date.dayOfMonth} de ${nombreMes(date.monthNumber).lowercase()} de ${date.year}"
    val oscuro = skin.background.luminance() < 0.5f
    val canSave = title.isNotBlank()
    val guardar = { onSave(title.trim(), notes.trim(), allDay) }
    val estilo = skin.editorStyle()
    val fondo = if (estilo == EditorStyle.APPLE) (if (oscuro) Color.Black else Color(0xFFF2F2F7)) else skin.background
    val tarjeta = if (oscuro) Color(0xFF1C1C1E) else Color.White
    val texto = skin.textColor
    val tenue = skin.mutedColor

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.fillMaxSize().background(fondo).windowInsetsPadding(WindowInsets.systemBars)
                .windowInsetsPadding(WindowInsets.ime),
        ) {
            when (estilo) {
                EditorStyle.GOOGLE -> {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.clip(RoundedCornerShape(50)).clickable(onClick = onDismiss).padding(8.dp)) { Icon(Ico.CLOSE, texto, 26.dp) }
                        Spacer(Modifier.weight(1f))
                        Box(
                            Modifier.clip(RoundedCornerShape(50)).background(if (canSave) skin.accent else tenue.copy(alpha = 0.3f))
                                .clickable(enabled = canSave, onClick = guardar).padding(horizontal = 22.dp, vertical = 10.dp),
                        ) { Text("Guardar", color = skin.onAccent, fontSize = 15.sp, fontWeight = FontWeight.Medium) }
                    }
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                        Campo(title, { title = it }, "Añade un título", texto, tenue, 26.sp, singleLine = true, cursor = skin.accent)
                        Box(Modifier.fillMaxWidth().height(2.dp).background(skin.accent))
                        Spacer(Modifier.height(8.dp))
                        FilaSwitch("Todo el día", allDay, { allDay = it }, texto, skin)
                        Fila(fecha, texto)
                        Fila("Zona horaria: hora estándar de Europa central", tenue)
                        Fila("No se repite", tenue)
                        Fila("Añadir notificación", tenue)
                        Spacer(Modifier.height(4.dp))
                        Campo(notes, { notes = it }, "Añadir descripción", texto, tenue, 16.sp, singleLine = false, cursor = skin.accent)
                        if (existing != null) Eliminar("Eliminar", onDelete, tenue)
                    }
                }

                EditorStyle.APPLE -> {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Cancelar", color = Color(0xFF007AFF), fontSize = 17.sp, modifier = Modifier.clickable(onClick = onDismiss))
                        Spacer(Modifier.weight(1f))
                        Text(if (existing != null) "Detalles" else "Nuevo", color = texto, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.weight(1f))
                        Text(
                            if (existing != null) "OK" else "Añadir", fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                            color = if (canSave) Color(0xFF007AFF) else tenue,
                            modifier = Modifier.clickable(enabled = canSave, onClick = guardar),
                        )
                    }
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tarjeta).padding(horizontal = 16.dp, vertical = 4.dp)) {
                            Campo(title, { title = it }, "Título", texto, tenue, 17.sp, singleLine = true, cursor = Color(0xFF007AFF))
                            Linea(tenue)
                            Fila("Ubicación", tenue)
                        }
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tarjeta).padding(horizontal = 16.dp, vertical = 2.dp)) {
                            FilaSwitch("Todo el día", allDay, { allDay = it }, texto, skin, verde = true)
                            Linea(tenue)
                            FilaValor("Empieza", fecha, texto, tenue)
                            Linea(tenue)
                            FilaValor("Termina", fecha, texto, tenue)
                            Linea(tenue)
                            FilaValor("Repetir", "Nunca", texto, tenue)
                        }
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tarjeta).padding(horizontal = 16.dp, vertical = 4.dp)) {
                            Campo(notes, { notes = it }, "Notas", texto, tenue, 17.sp, singleLine = false, cursor = Color(0xFF007AFF))
                            Spacer(Modifier.height(40.dp))
                        }
                        if (existing != null) {
                            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tarjeta).clickable(onClick = onDelete).padding(14.dp), contentAlignment = Alignment.Center) {
                                Text("Eliminar evento", color = Color(0xFFFF3B30), fontSize = 17.sp)
                            }
                        }
                    }
                }

                EditorStyle.SAMSUNG -> {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Cancelar", color = texto, fontSize = 16.sp, modifier = Modifier.clickable(onClick = onDismiss))
                        Spacer(Modifier.weight(1f))
                        Text(
                            "Guardar", fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            color = if (canSave) (if (skin.accent.luminance() < 0.15f && !oscuro) Color(0xFF3E91FF) else skin.accent) else tenue,
                            modifier = Modifier.clickable(enabled = canSave, onClick = guardar),
                        )
                    }
                    Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
                        Campo(title, { title = it }, "Título", texto, tenue, 26.sp, singleLine = true, cursor = texto)
                        Linea(tenue)
                        Spacer(Modifier.height(8.dp))
                        FilaSwitch("Todo el día", allDay, { allDay = it }, texto, skin)
                        FilaValor("Empieza", fecha, texto, tenue)
                        FilaValor("Termina", fecha, texto, tenue)
                        Linea(tenue)
                        Fila("Repetir: nunca", tenue)
                        Fila("Añadir ubicación", tenue)
                        Fila("Añadir notificación", tenue)
                        Linea(tenue)
                        Campo(notes, { notes = it }, "Descripción", texto, tenue, 16.sp, singleLine = false, cursor = texto)
                        if (existing != null) Eliminar("Eliminar", onDelete, Color(0xFFD93025))
                    }
                }
            }
        }
    }
}

/** Campo de texto sin marco con marcador de posición. */
@Composable
private fun Campo(
    valor: String, onChange: (String) -> Unit, marcador: String, color: Color, tenue: Color,
    tamano: TextUnit, singleLine: Boolean, cursor: Color,
) {
    Box(Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        if (valor.isEmpty()) Text(marcador, color = tenue, fontSize = tamano)
        BasicTextField(
            valor, onChange, Modifier.fillMaxWidth(), singleLine = singleLine,
            textStyle = TextStyle(color = color, fontSize = tamano), cursorBrush = SolidColor(cursor),
        )
    }
}

@Composable
private fun Fila(texto: String, color: Color) {
    Text(texto, color = color, fontSize = 16.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp))
}

@Composable
private fun FilaValor(etiqueta: String, valor: String, color: Color, tenue: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, color = color, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(valor, color = tenue, fontSize = 16.sp)
    }
}

@Composable
private fun FilaSwitch(texto: String, valor: Boolean, onChange: (Boolean) -> Unit, color: Color, skin: CalendarSkin, verde: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(texto, color = color, fontSize = 16.sp, modifier = Modifier.weight(1f))
        val activo = if (verde) Color(0xFF34C759) else skin.accent.let { if (it.luminance() < 0.1f) Color(0xFF3E91FF) else it }
        Switch(
            valor, onChange,
            colors = SwitchDefaults.colors(checkedTrackColor = activo, checkedThumbColor = Color.White),
        )
    }
}

@Composable
private fun Linea(color: Color) {
    Box(Modifier.fillMaxWidth().height(1.dp).background(color.copy(alpha = 0.25f)))
}

@Composable
private fun Eliminar(texto: String, onClick: () -> Unit, color: Color) {
    Text(texto, color = color, fontSize = 16.sp, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 18.dp))
}
