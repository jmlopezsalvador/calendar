@file:OptIn(ExperimentalForeignApi::class)

package com.calendar.ui

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSTimer
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/**
 * iOS: vibración doble con el motor háptico y cambio de icono con los iconos alternativos declarados en
 * `iosApp/project.yml` (`alt_<id>`). iOS no permite cambiar el nombre de la app, por eso [canRename] es false.
 */
class IosPlatform(private val setIcon: (String) -> Unit) : PlatformActions {
    override val canRename = false
    override val restartsApp = false

    override fun doubleVibrate() {
        val g = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
        g.prepare()
        g.impactOccurred()
        NSTimer.scheduledTimerWithTimeInterval(0.18, repeats = false) { _ -> g.impactOccurred() }
    }

    override fun applyIdentity(nameId: String, iconId: String) {
        // El cambio de icono lo hace Swift (ContentView.swift): la API no se expone bien a Kotlin/Native.
        setIcon("alt_$iconId")
    }
}
