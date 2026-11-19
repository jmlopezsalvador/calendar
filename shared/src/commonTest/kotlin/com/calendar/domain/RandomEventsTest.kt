package com.calendar.domain

import com.calendar.ui.KeyValueStore
import kotlinx.datetime.LocalDate
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RandomEventsTest {
    private class Mem : KeyValueStore {
        val m = HashMap<String, Any>()
        override fun getString(key: String) = m[key] as String?
        override fun getBoolean(key: String, default: Boolean) = m[key] as Boolean? ?: default
        override fun getLong(key: String, default: Long) = m[key] as Long? ?: default
        override fun putString(key: String, value: String) { m[key] = value }
        override fun putBoolean(key: String, value: Boolean) { m[key] = value }
        override fun putLong(key: String, value: Long) { m[key] = value }
    }

    private val desde = LocalDate(2026, 11, 1)
    private val hasta = LocalDate(2026, 11, 30)

    @Test
    fun parseTopicsIgnoraLineasVacias() {
        assertEquals(listOf("A", "B"), RandomEvents.parseTopics(" A \n\n B\n  "))
    }

    @Test
    fun alCienPorCientoRellenaTodosLosDiasLibres() {
        val repo = EventRepository(Mem())
        val n = RandomEvents.fill(repo, desde, hasta, listOf("Gimnasio"), 100, Random(1))
        assertEquals(30, n)
        assertEquals("Gimnasio", repo.get(LocalDate(2026, 11, 15))?.title)
    }

    @Test
    fun alCeroPorCientoNoCreaNada() {
        val repo = EventRepository(Mem())
        assertEquals(0, RandomEvents.fill(repo, desde, hasta, listOf("X"), 0, Random(1)))
        assertTrue(repo.all().isEmpty())
    }

    @Test
    fun noPisaEventosExistentesNiForzados() {
        val repo = EventRepository(Mem())
        repo.put(CalendarEvent(LocalDate(2026, 11, 6), "Mío"))
        MagicEngine(repo).forceAt(LocalDate(2026, 11, 7), Card.fromText("8C")!!)
        RandomEvents.fill(repo, desde, hasta, listOf("Gimnasio"), 100, Random(2))
        assertEquals("Mío", repo.get(LocalDate(2026, 11, 6))?.title)
        assertEquals("Feliz cumpleaños", repo.get(LocalDate(2026, 11, 7))?.title)
    }

    @Test
    fun sinTemasORangoInvertidoNoHaceNada() {
        val repo = EventRepository(Mem())
        assertEquals(0, RandomEvents.fill(repo, desde, hasta, emptyList(), 100))
        assertEquals(0, RandomEvents.fill(repo, hasta, desde, listOf("X"), 100))
        assertNull(repo.get(desde))
    }

    @Test
    fun coloresFijoAleatorioYDeLaMarca() {
        val fijo = EventRepository(Mem())
        RandomEvents.fill(fijo, desde, hasta, listOf("X"), 100, Random(3), EventColor.pool("verde"))
        assertTrue(fijo.all().all { it.color == EventColor.VERDE })

        val marca = EventRepository(Mem())
        RandomEvents.fill(marca, desde, hasta, listOf("X"), 100, Random(3), EventColor.pool(EventColor.MODE_DEFAULT))
        assertTrue(marca.all().all { it.color == null })

        val azar = EventRepository(Mem())
        RandomEvents.fill(azar, desde, hasta, listOf("X"), 100, Random(3), EventColor.pool(EventColor.MODE_RANDOM))
        assertTrue(azar.all().map { it.color }.toSet().size > 1)
    }

    @Test
    fun elColorDelEventoSePersiste() {
        val store = Mem()
        EventRepository(store).put(CalendarEvent(desde, "a", color = EventColor.NARANJA))
        assertEquals(EventColor.NARANJA, EventRepository(store).get(desde)?.color)
    }
}
