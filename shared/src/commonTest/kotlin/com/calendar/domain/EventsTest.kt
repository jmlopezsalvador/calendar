package com.calendar.domain

import com.calendar.ui.KeyValueStore
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MemoryStore : KeyValueStore {
    val m = HashMap<String, Any>()
    override fun getString(key: String) = m[key] as String?
    override fun getBoolean(key: String, default: Boolean) = m[key] as Boolean? ?: default
    override fun getLong(key: String, default: Long) = m[key] as Long? ?: default
    override fun putString(key: String, value: String) { m[key] = value }
    override fun putBoolean(key: String, value: Boolean) { m[key] = value }
    override fun putLong(key: String, value: Long) { m[key] = value }
}

class EventsTest {
    private val clave = LocalDate(2026, 11, 6)
    private val cumple = LocalDate(2027, 1, 13)

    @Test fun persisteYRecarga() {
        val store = MemoryStore()
        val repo = EventRepository(store)
        repo.put(CalendarEvent(clave, "Dentista\tcon tab", "línea1\nlínea2 \\ barra"))
        val otro = EventRepository(store)
        assertEquals(repo.get(clave), otro.get(clave))
        assertEquals(1, otro.all().size)
    }

    @Test fun unEventoPorDiaSeSustituye() {
        val repo = EventRepository(MemoryStore())
        repo.put(CalendarEvent(clave, "A"))
        repo.put(CalendarEvent(clave, "B"))
        assertEquals(1, repo.all().size)
        assertEquals("B", repo.get(clave)?.title)
    }

    @Test fun forzadoEnElCumple() {
        val repo = EventRepository(MemoryStore())
        MagicEngine(repo).save(clave, "Prueba", "esto es un ejemplo 8C")
        val f = assertNotNull(repo.get(cumple))
        assertEquals("Feliz cumpleaños", f.title)
        assertTrue(f.notes.contains("8 de corazones"))
        assertEquals(clave, f.forcedFrom)
    }

    @Test fun sinCartaValidaNoSeFuerza() {
        val repo = EventRepository(MemoryStore())
        MagicEngine(repo).save(clave, "Prueba", "hola mundo")
        assertNull(repo.get(cumple))
    }

    @Test fun cartaEnElTituloSiNoHayNotas() {
        val repo = EventRepository(MemoryStore())
        MagicEngine(repo).save(clave, "Gimnasio QT", "")
        assertTrue(repo.get(cumple)!!.notes.contains("Reina de tréboles"))
    }

    @Test fun sustituyeEventoExistenteEnElCumple() {
        val repo = EventRepository(MemoryStore())
        repo.put(CalendarEvent(cumple, "Dentista"))
        MagicEngine(repo).save(clave, "x", "7P")
        assertEquals("Feliz cumpleaños", repo.get(cumple)?.title)
    }

    @Test fun modificarElDiaClaveRecalcula() {
        val repo = EventRepository(MemoryStore())
        val e = MagicEngine(repo)
        e.save(clave, "x", "8C")
        e.save(clave, "x", "KD")
        assertTrue(repo.get(cumple)!!.notes.contains("Rey de diamantes"))
        e.save(clave, "x", "sin carta")
        assertNull(repo.get(cumple))
    }

    @Test fun borrarElDiaClaveRetiraElForzado() {
        val repo = EventRepository(MemoryStore())
        val e = MagicEngine(repo)
        e.save(clave, "x", "8C")
        e.delete(clave)
        assertNull(repo.get(clave))
        assertNull(repo.get(cumple))
    }

    @Test fun editarElForzadoNoDisparaNada() {
        val repo = EventRepository(MemoryStore())
        val e = MagicEngine(repo)
        e.save(clave, "x", "8C")
        e.save(cumple, "Feliz cumple editado", "texto 7P")
        assertEquals("Feliz cumple editado", repo.get(cumple)?.title)
        assertNull(repo.get(KeyDay.birthdayFrom(cumple)))
    }

    @Test
    fun resetBorraElForzadoYElEventoDePruebaPeroNoElRelleno() {
        val repo = EventRepository(MemoryStore())
        val engine = MagicEngine(repo)
        repo.put(CalendarEvent(LocalDate(2026, 11, 3), "Dentista"))
        engine.save(LocalDate(2026, 11, 19), "prueba 8C", "")
        assertEquals("Feliz cumpleaños", repo.get(LocalDate(2027, 1, 26))?.title)
        engine.reset()
        assertNull(repo.get(LocalDate(2027, 1, 26)))
        assertNull(repo.get(LocalDate(2026, 11, 19)))
        assertEquals("Dentista", repo.get(LocalDate(2026, 11, 3))?.title)
    }

    @Test
    fun resetTambienQuitaElForzadoManual() {
        val repo = EventRepository(MemoryStore())
        val engine = MagicEngine(repo)
        engine.forceAt(LocalDate(2027, 4, 30), Card.fromText("QT")!!)
        engine.reset()
        assertTrue(repo.all().isEmpty())
    }
}
