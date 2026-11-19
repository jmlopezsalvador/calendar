package com.calendar.ui

/** Almacenamiento clave-valor local. Android: SharedPreferences. iOS: NSUserDefaults (ver las `actual`). */
interface KeyValueStore {
    fun getString(key: String): String?
    fun getBoolean(key: String, default: Boolean): Boolean
    fun getLong(key: String, default: Long): Long
    fun putString(key: String, value: String)
    fun putBoolean(key: String, value: Boolean)
    fun putLong(key: String, value: Long)
}

/** Ajustes de la app. Ampliar aquí (y en load/save) cuando haya opciones reales. */
data class AppSettings(
    /** `auto` = según el sistema; o el id de una skin de [Skins]. */
    val skinId: String = AUTO,
) {
    companion object { const val AUTO = "auto" }
}

class SettingsStore(private val prefs: KeyValueStore) {
    fun load(): AppSettings = AppSettings(skinId = prefs.getString("skin") ?: AppSettings.AUTO)
    fun save(s: AppSettings) { prefs.putString("skin", s.skinId) }
}
