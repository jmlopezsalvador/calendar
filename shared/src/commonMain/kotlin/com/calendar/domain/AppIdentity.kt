package com.calendar.domain

/** Opción elegible (id estable + texto para Ajustes). */
data class IdentityOption(val id: String, val nombre: String)

/**
 * Nombre e icono de la app seleccionables (SPEC R1). En Android cada combinación es un `activity-alias` del manifiesto
 * (se generan con la misma convención de nombre: [aliasName]); en iOS solo se puede cambiar el icono.
 */
object AppIdentity {
    val names = listOf(
        IdentityOption("calendar", "Calendar"),
        IdentityOption("calendario", "Calendario"),
        IdentityOption("agenda", "Agenda"),
    )

    /** Iconos tomados del kit de calendarios. */
    val icons = listOf(
        IdentityOption("google", "Google Calendar"),
        IdentityOption("samsung", "Samsung Calendar"),
        IdentityOption("apple", "Apple Calendar"),
        IdentityOption("xiaomi", "Xiaomi Calendar"),
        IdentityOption("xiaomi_google", "Google en Xiaomi"),
        IdentityOption("oppo_google", "Google en OPPO"),
        IdentityOption("huawei", "Huawei Calendar"),
        IdentityOption("business", "Business Calendar 2"),
        IdentityOption("moca", "Moca Monthly Calendar"),
        IdentityOption("acalendar", "aCalendar"),
    )

    const val DEFAULT_NAME = "calendar"
    const val DEFAULT_ICON = "google"

    fun nameLabel(id: String) = names.firstOrNull { it.id == id }?.nombre ?: names.first().nombre
    fun iconLabel(id: String) = icons.firstOrNull { it.id == id }?.nombre ?: icons.first().nombre

    /** Nombre del `activity-alias` de Android para la combinación elegida. */
    fun aliasName(nameId: String, iconId: String) = "${nameId}_$iconId"

    /** Todas las combinaciones posibles (para activar una y desactivar el resto). */
    fun allAliases(): List<String> = names.flatMap { n -> icons.map { i -> aliasName(n.id, i.id) } }
}
