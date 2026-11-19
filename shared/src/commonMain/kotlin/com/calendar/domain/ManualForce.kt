package com.calendar.domain

import kotlinx.datetime.LocalDate

/**
 * Forzado manual (SPEC §4): para los cumpleaños sin día clave posible (29/30-abr, 31-ago, 31-ene, 29-feb)
 * el mago fija a mano la fecha del cumpleaños y la carta desde los Ajustes ocultos.
 */
object ManualForce {

    /** Interpreta «dd/mm/aaaa» (también con guiones o puntos, y año opcional de 2 cifras). Null si no es una fecha válida. */
    fun parseDate(text: String): LocalDate? {
        val p = text.trim().split('/', '-', '.', ' ').filter { it.isNotEmpty() }
        if (p.size != 3) return null
        val d = p[0].toIntOrNull() ?: return null
        val m = p[1].toIntOrNull() ?: return null
        var y = p[2].toIntOrNull() ?: return null
        if (y < 100) y += 2000
        return try { LocalDate(y, m, d) } catch (_: IllegalArgumentException) { null }
    }

    /** Aplica el forzado manual. Devuelve false si la fecha o la carta no son válidas (no cambia nada). */
    fun apply(engine: MagicEngine, dateText: String, cardText: String): Boolean {
        val date = parseDate(dateText) ?: return false
        val card = Card.fromText(cardText) ?: return false
        engine.forceAt(date, card)
        return true
    }
}
