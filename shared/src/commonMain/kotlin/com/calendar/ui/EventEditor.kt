package com.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.calendar.domain.CalendarEvent
import kotlinx.datetime.LocalDate

/**
 * Formulario de evento (común a todas las marcas, coloreado con la [skin]). Se abre directamente al pulsar un día;
 * si el día ya tiene evento se edita. Es atrezo: solo título y notas tienen función.
 */
@Composable
fun EventEditor(
    skin: CalendarSkin,
    date: LocalDate,
    existing: CalendarEvent?,
    onSave: (title: String, notes: String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxWidth().background(skin.cellColor, RoundedCornerShape(24.dp)).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Nuevo evento", color = skin.textColor, fontSize = 20.sp)
            Text("${date.dayOfMonth} de ${nombreMes(date.monthNumber).lowercase()} de ${date.year}", color = skin.mutedColor, fontSize = 14.sp)
            OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Título") }, singleLine = true)
            OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth(), label = { Text("Descripción") }, minLines = 2)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (existing != null) TextButton(onClick = onDelete) { Text("Eliminar", color = skin.mutedColor) }
                TextButton(onClick = onDismiss) { Text("Cancelar", color = skin.mutedColor) }
                TextButton(onClick = { onSave(title.trim(), notes.trim()) }, enabled = title.isNotBlank()) {
                    Text("Guardar", color = skin.accent)
                }
            }
        }
    }
}
