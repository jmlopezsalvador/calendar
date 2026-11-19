package com.calendar.ui

/** Acciones del sistema que implementa cada plataforma (Android en `app/`, iOS en `iosMain`). */
interface PlatformActions {
    /** Vibración doble (confirma el reinicio del efecto y la entrada a Ajustes). */
    fun doubleVibrate()

    /** ¿Se puede cambiar el nombre de la app? (Android sí; iOS no lo permite.) */
    val canRename: Boolean

    /** ¿Aplicar el cambio de nombre/icono reinicia la app? */
    val restartsApp: Boolean

    /** Aplica el nombre y el icono elegidos (ids de [com.calendar.domain.AppIdentity]). */
    fun applyIdentity(nameId: String, iconId: String)
}

/** Sin acciones del sistema (vista previa y tests). */
object NoPlatformActions : PlatformActions {
    override fun doubleVibrate() {}
    override val canRename = false
    override val restartsApp = false
    override fun applyIdentity(nameId: String, iconId: String) {}
}
