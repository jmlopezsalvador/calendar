package com.calendar.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlin.random.Random

/** Generador de eventos sintéticos creíbles (SPEC R7). Lógica pura, con el azar inyectable para los tests. */
object RandomEvents {

    /** Temas por defecto; el usuario puede editarlos en Ajustes (uno por línea). */
    val DEFAULT_TOPICS = listOf(
        "Gimnasio", "Dentista", "Peluquería", "Cumpleaños de Marta", "Cita médica",
        "Quedada con amigos", "Cena con la familia", "Reunión de trabajo", "Compra semanal",
        "Revisión del coche", "Clase de inglés", "Cine", "Veterinario", "Pagar el recibo",
    )

    /** Parte el texto de Ajustes (un tema por línea) y descarta líneas vacías. */
    fun parseTopics(text: String): List<String> = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

    /**
     * Rellena `[from, to]` con eventos: cada día **libre** recibe uno con probabilidad [percent] (0–100).
     * No toca los días que ya tienen evento (ni los forzados por el truco). Devuelve cuántos ha creado.
     */
    fun fill(
        repo: EventRepository,
        from: LocalDate,
        to: LocalDate,
        topics: List<String>,
        percent: Int,
        random: Random = Random.Default,
    ): Int {
        if (topics.isEmpty() || to < from) return 0
        var created = 0
        for (i in 0..from.daysUntil(to)) {
            val d = from.plus(i, DateTimeUnit.DAY)
            if (repo.get(d) != null) continue
            if (random.nextInt(100) >= percent) continue
            repo.put(CalendarEvent(d, topics[random.nextInt(topics.size)]))
            created++
        }
        return created
    }
}
