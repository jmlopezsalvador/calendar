package com.calendar.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.calendar.domain.EventRepository
import com.calendar.domain.MagicEngine
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Raíz Compose común (Android e iOS): calendario mensual continuo + formulario de evento. */
@Composable
fun CalendarRoot(prefs: KeyValueStore) {
    val repo = remember { EventRepository(prefs) }
    val engine = remember { MagicEngine(repo) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val settingsStore = remember { SettingsStore(prefs) }
    var settings by remember { mutableStateOf(settingsStore.load()) }
    var showSettings by remember { mutableStateOf(false) }
    val skin = Skins.byId(if (settings.skinId == AppSettings.AUTO) systemSkinId() else settings.skinId)
    // Cada guardado incrementa la versión para recomponer las celdas.
    var version by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf<LocalDate?>(null) }
    SystemBarsEffect()
    MaterialTheme {
        @Suppress("UNUSED_EXPRESSION") version
        if (showSettings) {
            SettingsScreen(settings, onChange = { settings = it; settingsStore.save(it) }, onClose = { showSettings = false })
            return@MaterialTheme
        }
        CalendarScreen(skin, today, events = { repo.get(it) }, onDayClick = { editing = it }, onHeaderLongClick = { showSettings = true })
        editing?.let { d ->
            EventEditor(
                skin, d, repo.get(d),
                onSave = { t, n -> engine.save(d, t, n); version++; editing = null },
                onDelete = { engine.delete(d); version++; editing = null },
                onDismiss = { editing = null },
            )
        }
    }
}
