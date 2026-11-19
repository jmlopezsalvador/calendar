package com.calendar.domain

import com.calendar.ui.KeyValueStore
import kotlinx.datetime.LocalDate

/**
 * Evento del calendario (máximo uno por día).
 * [forcedFrom]: si no es null, es el evento forzado por el truco y apunta al día clave que lo originó.
 */
data class CalendarEvent(
    val date: LocalDate,
    val title: String,
    val notes: String = "",
    val forcedFrom: LocalDate? = null,
    /** Marca «Todo el día» del formulario: en el día clave significa «3 meses» en vez de 2 (ver [KeyDay]). */
    val allDay: Boolean = false,
    /** Color elegido para el evento (null = el color propio de la marca del calendario). */
    val color: EventColor? = null,
) {
    val isForced: Boolean get() = forcedFrom != null
}

/** Eventos persistidos en un [KeyValueStore]. Un evento por día; guardar en un día ocupado lo sustituye. */
class EventRepository(private val prefs: KeyValueStore) {
    private val map = HashMap<LocalDate, CalendarEvent>()

    init {
        prefs.getString(KEY)?.lines()?.forEach { line ->
            decode(line)?.let { map[it.date] = it }
        }
    }

    fun get(date: LocalDate): CalendarEvent? = map[date]
    fun all(): List<CalendarEvent> = map.values.sortedBy { it.date }

    fun put(e: CalendarEvent) { map[e.date] = e; persist() }
    fun remove(date: LocalDate) { if (map.remove(date) != null) persist() }
    fun clear() { map.clear(); persist() }

    private fun persist() { prefs.putString(KEY, all().joinToString("\n") { encode(it) }) }

    private companion object {
        const val KEY = "events"

        fun esc(s: String) = buildString {
            for (c in s) when (c) {
                '\\' -> append("\\\\")
                '\n' -> append("\\n")
                '\t' -> append("\\t")
                else -> append(c)
            }
        }

        fun unesc(s: String) = buildString {
            var i = 0
            while (i < s.length) {
                val c = s[i]
                if (c == '\\' && i + 1 < s.length) {
                    when (s[i + 1]) { 'n' -> append('\n'); 't' -> append('\t'); else -> append(s[i + 1]) }
                    i += 2
                } else { append(c); i++ }
            }
        }

        fun encode(e: CalendarEvent) =
            listOf(e.date.toString(), e.forcedFrom?.toString() ?: "", esc(e.title), esc(e.notes), if (e.allDay) "1" else "0", e.color?.id ?: "").joinToString("\t")

        fun decode(line: String): CalendarEvent? {
            val p = line.split('\t')
            if (p.size !in 4..6) return null // 4 y 5 = formatos anteriores (sin «todo el día» / sin color)
            return try {
                CalendarEvent(
                    LocalDate.parse(p[0]), unesc(p[2]), unesc(p[3]),
                    p[1].takeIf { it.isNotEmpty() }?.let { LocalDate.parse(it) },
                    allDay = p.getOrNull(4) == "1",
                    color = EventColor.fromId(p.getOrNull(5)),
                )
            } catch (_: IllegalArgumentException) { null }
        }
    }
}

/**
 * Reglas del truco (SPEC R8–R11). Guarda eventos y, si el evento de prueba termina en una carta válida,
 * fuerza el «Feliz cumpleaños» en el cumpleaños deducido del día clave.
 */
class MagicEngine(private val repo: EventRepository) {

    /** Guarda el evento que escribe el usuario/mago en [date] y aplica el forzado. */
    fun save(date: LocalDate, title: String, notes: String, allDay: Boolean = false, color: EventColor? = null) {
        val previous = repo.get(date)
        // Editar el evento forzado lo deja como está de forzado y no dispara nada nuevo.
        if (previous?.isForced == true) {
            repo.put(previous.copy(title = title, notes = notes, color = color))
            return
        }
        repo.put(CalendarEvent(date, title, notes, allDay = allDay, color = color))
        // Se limpia el forzado anterior de este día clave y se vuelve a calcular.
        removeForcedBy(date)
        val card = cardOf(title, notes) ?: return
        // «Todo el día» activado = 3 meses; desactivado = 2 meses.
        forceAt(KeyDay.birthdayFrom(date, KeyDay.monthsFor(allDay)), card, source = date)
    }

    /** Borra el evento de [date]; si era un día clave, también retira su forzado. */
    fun delete(date: LocalDate) {
        repo.remove(date)
        removeForcedBy(date)
    }

    /**
     * Reinicia el efecto para poder repetirlo: borra los eventos forzados y los eventos de prueba (día clave) que
     * los originaron. El resto de eventos (los de relleno) se conservan.
     */
    fun reset() {
        for (e in repo.all()) {
            val source = e.forcedFrom ?: continue
            repo.remove(e.date)
            val test = repo.get(source)
            if (source != e.date && test != null && !test.isForced) repo.remove(source)
        }
    }

    /** Coloca el «Feliz cumpleaños» con su carta en [birthday]; [source] es el día clave que lo originó. */
    fun forceAt(birthday: LocalDate, card: Card, source: LocalDate = birthday) {
        repo.put(forcedEvent(birthday, card, source))
    }

    /** La carta se busca primero en las notas (el «texto» del evento) y, si no hay, en el título. */
    fun cardOf(title: String, notes: String): Card? = Card.fromText(notes) ?: Card.fromText(title)

    /** Quita el forzado originado por [source], sea con 2 o con 3 meses. */
    private fun removeForcedBy(source: LocalDate) {
        for (e in repo.all()) if (e.forcedFrom == source) repo.remove(e.date)
    }

    private fun forcedEvent(birthday: LocalDate, card: Card, source: LocalDate) = CalendarEvent(
        date = birthday,
        title = "Feliz cumpleaños",
        notes = "¡Feliz cumpleaños! Tu carta es el ${card.nombre}.",
        forcedFrom = source,
    )
}
