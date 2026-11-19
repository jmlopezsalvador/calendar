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

/** Ajustes de la app. */
data class AppSettings(
    /** `auto` = según el sistema; o el id de una skin de [Skins]. */
    val skinId: String = AUTO,
    /** `auto` = según la marca, `vertical` (semanas continuas) u `horizontal` (mes a mes). */
    val scroll: String = AUTO,
    /** Ajuste fino sobre la skin (null = valor de la skin). */
    val cornerDp: Int? = null,
    val gapDp: Int? = null,
    val rowDp: Int? = null,
    /** Temas de los eventos aleatorios, uno por línea (null = los de por defecto). */
    val topics: String? = null,
    /** Nombre e icono de la app (ids de AppIdentity). */
    val appName: String = "calendar",
    val appIcon: String = "google",
    /** Mostrar la guía de inicio al arrancar la app. */
    val showGuide: Boolean = true,
    /** Color de los eventos generados: `random`, `default` (el de la marca) o el id de un color. */
    val genColor: String = "random",
    /** Título del evento con salto de línea en la celda del día (false = una sola línea recortada). */
    val wrapTitle: Boolean = false,
) {
    companion object {
        const val AUTO = "auto"
        const val VERTICAL = "vertical"
        const val HORIZONTAL = "horizontal"
    }

    /** ¿Desplazamiento horizontal para [skin]? */
    fun isHorizontal(skin: CalendarSkin): Boolean = when (scroll) {
        HORIZONTAL -> true
        VERTICAL -> false
        else -> skin.horizontalByDefault
    }

    fun clearTweaks() = copy(cornerDp = null, gapDp = null, rowDp = null)
}

class SettingsStore(private val prefs: KeyValueStore) {
    private fun opt(key: String): Int? = prefs.getLong(key, -1L).takeIf { it >= 0 }?.toInt()
    private fun put(key: String, v: Int?) = prefs.putLong(key, v?.toLong() ?: -1L)

    fun load(): AppSettings = AppSettings(
        skinId = prefs.getString("skin") ?: AppSettings.AUTO,
        scroll = prefs.getString("scroll") ?: AppSettings.AUTO,
        cornerDp = opt("tw_corner"), gapDp = opt("tw_gap"), rowDp = opt("tw_row"),
        topics = prefs.getString("topics")?.takeIf { it.isNotBlank() },
        appName = prefs.getString("app_name") ?: "calendar",
        appIcon = prefs.getString("app_icon") ?: "google",
        showGuide = prefs.getBoolean("guide_show", true),
        genColor = prefs.getString("gen_color") ?: "random",
        wrapTitle = prefs.getBoolean("wrap_title", false),
    )

    fun save(s: AppSettings) {
        prefs.putString("skin", s.skinId)
        prefs.putString("scroll", s.scroll)
        put("tw_corner", s.cornerDp); put("tw_gap", s.gapDp); put("tw_row", s.rowDp)
        prefs.putString("topics", s.topics ?: "")
        prefs.putString("app_name", s.appName)
        prefs.putString("app_icon", s.appIcon)
        prefs.putBoolean("guide_show", s.showGuide)
        prefs.putString("gen_color", s.genColor)
        prefs.putBoolean("wrap_title", s.wrapTitle)
    }
}
