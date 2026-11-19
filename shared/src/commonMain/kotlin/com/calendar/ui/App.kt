package com.calendar.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.calendar.domain.EventRepository
import com.calendar.domain.MagicEngine
import com.calendar.domain.RandomEvents
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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
    val base = Skins.byId(if (settings.skinId == AppSettings.AUTO) systemSkinId() else settings.skinId)
    val skin = base.withTweaks(settings)
    val horizontal = settings.isHorizontal(base)
    // Cada guardado incrementa la versión para recomponer las celdas.
    var version by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf<LocalDate?>(null) }
    SystemBarsEffect()
    MaterialTheme {
        @Suppress("UNUSED_EXPRESSION") version
        if (showSettings) {
            SettingsScreen(
                settings, onChange = { settings = it; settingsStore.save(it) },
                onGenerate = { atras, adelante, pct ->
                    val topics = RandomEvents.parseTopics(settings.topics ?: "").ifEmpty { RandomEvents.DEFAULT_TOPICS }
                    val n = RandomEvents.fill(
                        repo, today.minus(atras, DateTimeUnit.MONTH), today.plus(adelante, DateTimeUnit.MONTH), topics, pct,
                    )
                    version++
                    n
                },
                onClose = { showSettings = false },
            )
            return@MaterialTheme
        }
        CalendarScreen(skin, horizontal, today, events = { repo.get(it) }, onDayClick = { editing = it }, onTitleLongPress = { showSettings = true })
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
