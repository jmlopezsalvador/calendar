package com.calendar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.calendar.domain.AppIdentity
import com.calendar.ui.CalendarRoot
import com.calendar.ui.PlatformActions
import com.calendar.ui.SharedPrefsStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val prefs = SharedPrefsStore(applicationContext)
        val platform = AndroidPlatform(this)
        setContent { CalendarRoot(prefs, platform) }
    }
}

/** Android: vibración y cambio de nombre/icono mediante los `activity-alias` del manifiesto. */
private class AndroidPlatform(private val activity: ComponentActivity) : PlatformActions {
    override val canRename = true
    override val restartsApp = true

    @Suppress("DEPRECATION")
    override fun doubleVibrate() {
        val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 70, 90, 70), -1))
    }

    override fun applyIdentity(nameId: String, iconId: String) {
        val pm = activity.packageManager
        val destino = AppIdentity.aliasName(nameId, iconId)
        fun componente(alias: String) = ComponentName(activity.packageName, "com.calendar.alias.$alias")
        // Primero se activa el nuevo y luego se desactivan los demás, para no quedarse nunca sin icono.
        pm.setComponentEnabledSetting(componente(destino), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        for (a in AppIdentity.allAliases()) {
            if (a != destino) {
                pm.setComponentEnabledSetting(componente(a), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            }
        }
        // Reinicio: se cierra esta tarea y se abre la app desde el alias nuevo.
        val reinicio = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(componente(destino))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.finishAffinity()
        activity.startActivity(reinicio)
    }
}
