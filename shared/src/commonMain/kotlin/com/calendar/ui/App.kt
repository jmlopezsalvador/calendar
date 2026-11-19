package com.calendar.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.calendar.domain.EventRepository
import com.calendar.domain.MagicEngine
import com.calendar.domain.ManualForce
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
fun CalendarRoot(prefs: KeyValueStore, platform: PlatformActions = NoPlatformActions) {
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
                canRename = platform.canRename, restartsApp = platform.restartsApp,
                onApplyIdentity = { n, i ->
                    val nuevo = settings.copy(appName = n, appIcon = i)
                    settings = nuevo; settingsStore.save(nuevo)
                    platform.applyIdentity(n, i)
                },
                onForce = { f, c -> ManualForce.apply(engine, f, c).also { if (it) version++ } },
                onClose = { showSettings = false },
            )
            return@MaterialTheme
        }
        // La lambda cambia con cada versión para que las celdas ya compuestas se repinten tras guardar/reiniciar.
        val eventos = remember(version) { { d: LocalDate -> repo.get(d) } }
        CalendarScreen(skin, horizontal, today, events = eventos, onDayClick = { editing = it }, onTitleLongPress = { platform.doubleVibrate(); showSettings = true },
            onCornerLongPress = { engine.reset(); version++; platform.doubleVibrate() },
        )
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
