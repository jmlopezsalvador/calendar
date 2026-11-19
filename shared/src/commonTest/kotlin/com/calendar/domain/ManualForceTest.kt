package com.calendar.domain

import com.calendar.ui.KeyValueStore
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ManualForceTest {
    private class Mem : KeyValueStore {
        val m = HashMap<String, Any>()
        override fun getString(key: String) = m[key] as String?
        override fun getBoolean(key: String, default: Boolean) = m[key] as Boolean? ?: default
        override fun getLong(key: String, default: Long) = m[key] as Long? ?: default
        override fun putString(key: String, value: String) { m[key] = value }
        override fun putBoolean(key: String, value: Boolean) { m[key] = value }
        override fun putLong(key: String, value: Long) { m[key] = value }
    }

    @Test
    fun interpretaFechasEnVariosFormatos() {
        assertEquals(LocalDate(2027, 4, 30), ManualForce.parseDate("30/04/2027"))
        assertEquals(LocalDate(2027, 8, 31), ManualForce.parseDate("31-8-27"))
        assertEquals(LocalDate(2028, 2, 29), ManualForce.parseDate(" 29.2.2028 "))
    }

    @Test
    fun rechazaFechasInvalidas() {
        assertNull(ManualForce.parseDate("31/04/2027"))
        assertNull(ManualForce.parseDate("29/02/2027"))
        assertNull(ManualForce.parseDate("hola"))
        assertNull(ManualForce.parseDate("1/2"))
    }

    @Test
    fun forzaUnCumpleanosSinDiaClave() {
        val repo = EventRepository(Mem())
        val engine = MagicEngine(repo)
        assertTrue(ManualForce.apply(engine, "30/04/2027", "8C"))
        val e = repo.get(LocalDate(2027, 4, 30))!!
        assertEquals("Feliz cumpleaños", e.title)
        assertTrue(e.notes.contains("8 de corazones"))
    }

    @Test
    fun conDatosInvalidosNoCambiaNada() {
        val repo = EventRepository(Mem())
        val engine = MagicEngine(repo)
        assertFalse(ManualForce.apply(engine, "30/04/2027", "ZZ"))
        assertFalse(ManualForce.apply(engine, "31/04/2027", "8C"))
        assertTrue(repo.all().isEmpty())
    }
}
