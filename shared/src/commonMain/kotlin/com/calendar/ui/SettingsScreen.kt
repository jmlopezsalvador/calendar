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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
    onClose: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Color.White).windowInsetsPadding(WindowInsets.systemBars)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onClose) { Text("‹ Volver", fontSize = 16.sp) }
            Text("Ajustes", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Text("Aspecto del calendario", fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.padding(vertical = 8.dp))
            Opcion("Automático (según el sistema: ${Skins.byId(systemSkinId()).nombre})", settings.skinId == AppSettings.AUTO) {
                onChange(settings.copy(skinId = AppSettings.AUTO))
            }
            for (s in Skins.all) {
                Opcion(s.nombre, settings.skinId == s.id) { onChange(settings.copy(skinId = s.id)) }
            }
        }
    }
}

@Composable
private fun Opcion(texto: String, seleccionada: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = seleccionada, onClick = onClick)
        Text(texto, fontSize = 16.sp, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
    }
}
