package com.calendar.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.calendar.domain.EventColor
import com.calendar.domain.EventRepository
import com.calendar.ui.guide.GuideDialog
import com.calendar.ui.guide.InstructionsScreen
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
fun CalendarRoot(prefs: KeyValueStore, platform: PlatformActions = NoPlatformActions) {
    val repo = remember { EventRepository(prefs) }
    val engine = remember { MagicEngine(repo) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val settingsStore = remember { SettingsStore(prefs) }
    var settings by remember { mutableStateOf(settingsStore.load()) }
    var showSettings by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }
    // La guía se abre sola al iniciar si está activada; también se puede abrir desde Ajustes.
    var showGuide by remember { mutableStateOf(settings.showGuide) }
    val base = Skins.byId(if (settings.skinId == AppSettings.AUTO) systemSkinId() else settings.skinId)
    val skin = base.withTweaks(settings)
    val horizontal = settings.isHorizontal(base)
    // Cada guardado incrementa la versión para recomponer las celdas.
    var version by remember { mutableStateOf(0) }
    var editing by remember { mutableStateOf<LocalDate?>(null) }
    SystemBarsEffect()
    MaterialTheme {
        @Suppress("UNUSED_EXPRESSION") version
        run pantallas@{
        if (showInstructions) {
            InstructionsScreen(onClose = { showInstructions = false })
            return@pantallas
        }
        if (showSettings) {
            SettingsScreen(
                settings, onChange = { settings = it; settingsStore.save(it) },
                onGenerate = { atras, adelante, pct ->
                    val topics = RandomEvents.parseTopics(settings.topics ?: "").ifEmpty { RandomEvents.DEFAULT_TOPICS }
                    val n = RandomEvents.fill(
                        repo, today.minus(atras, DateTimeUnit.MONTH), today.plus(adelante, DateTimeUnit.MONTH), topics, pct,
                        colors = EventColor.pool(settings.genColor, EventColor.parseList(settings.genColors)),
                    )
                    version++
                    n
                },
                onDeleteAll = { val n = repo.all().size; repo.clear(); version++; n },
                canRename = platform.canRename, restartsApp = platform.restartsApp,
                onApplyIdentity = { n, i ->
                    val nuevo = settings.copy(appName = n, appIcon = i)
                    settings = nuevo; settingsStore.save(nuevo)
                    platform.applyIdentity(n, i)
                },
                onShowGuide = { showGuide = true }, onShowInstructions = { showInstructions = true },
                onClose = { showSettings = false },
            )
            return@pantallas
        }
        // La lambda cambia con cada versión para que las celdas ya compuestas se repinten tras guardar/reiniciar.
        val eventos = remember(version) { { d: LocalDate -> repo.get(d) } }
        CalendarScreen(skin, horizontal, today, events = eventos, onDayClick = { editing = it }, onTitleLongPress = { platform.doubleVibrate(); showSettings = true },
            onCornerLongPress = { engine.reset(); version++; platform.doubleVibrate() },
        )
        editing?.let { d ->
            EventEditor(
                skin, d, repo.get(d),
                onSave = { t, n, todoElDia, color -> engine.save(d, t, n, todoElDia, color); version++; editing = null },
                onDelete = { engine.delete(d); version++; editing = null },
                onDismiss = { editing = null },
            )
        }
        }
        // La guía va la última para quedar por encima de cualquier pantalla.
        if (showGuide) {
            GuideDialog(noMostrarInicial = !settings.showGuide, onFinish = { noMostrar ->
                showGuide = false
                val nuevo = settings.copy(showGuide = !noMostrar)
                settings = nuevo; settingsStore.save(nuevo)
            })
        }
    }
}
