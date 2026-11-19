package com.calendar.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CardTest {
    @Test fun ejemplosDelSpec() {
        assertEquals(Card(8, Suit.CORAZONES), Card.fromText("hola 8C"))
        assertEquals(Card(7, Suit.PICAS), Card.fromText("Dentista 7p"))
        assertEquals(Card(12, Suit.TREBOLES), Card.fromText("cena Q T"))
        assertEquals(Card(10, Suit.TREBOLES), Card.fromText("algo 0t"))
        assertEquals(Card(1, Suit.CORAZONES), Card.fromText("x 1c"))
        assertEquals(Card(1, Suit.DIAMANTES), Card.fromText("AD"))
        assertEquals(Card(10, Suit.PICAS), Card.fromText("gym TP  \n"))
    }

    @Test fun invalidas() {
        assertNull(Card.fromText(""))
        assertNull(Card.fromText("C"))
        assertNull(Card.fromText("gimnasio"))
        assertNull(Card.fromText("hola 9X"))
        assertNull(Card.fromText("hola 1P2"))
    }

    @Test fun nombres() {
        assertEquals("8 de corazones", Card(8, Suit.CORAZONES).nombre)
        assertEquals("Reina de tréboles", Card(12, Suit.TREBOLES).nombre)
        assertEquals("As de picas", Card(1, Suit.PICAS).nombre)
    }
}

class KeyDayTest {
    private fun d(y: Int, m: Int, day: Int) = LocalDate(y, m, day)

    @Test fun ejemploDelSpec() {
        assertEquals(d(2026, 11, 6), KeyDay.keyDayFor(d(2027, 1, 13)))
        assertEquals(d(2027, 1, 13), KeyDay.birthdayFrom(d(2026, 11, 6)))
    }

    @Test fun diaClaveSiempreEnLaMismaColumna() {
        // −2 meses y −7 días: la celda de encima conserva el día de la semana de B₋₂ₘ.
        assertEquals(d(2026, 11, 13).dayOfWeek, d(2026, 11, 6).dayOfWeek)
    }

    @Test fun finDeAnoYBisiestos() {
        assertEquals(d(2026, 12, 25), KeyDay.keyDayFor(d(2027, 2, 25)).plus(7, DateTimeUnit.DAY))
        assertEquals(d(2028, 2, 29), KeyDay.birthdayFrom(KeyDay.keyDayFor(d(2028, 2, 29))))
        assertEquals(d(2028, 4, 29), KeyDay.birthdayFrom(KeyDay.keyDayFor(d(2028, 4, 29)))) // bisiesto: feb tiene 29
    }

    @Test fun casosQueNoSeRecuperan() {
        // En 2027 (no bisiesto): 29/30-abr y 31-ago no existen dos meses antes y se recuperan mal.
        assertEquals(false, KeyDay.roundTrips(d(2027, 4, 29)))
        assertEquals(false, KeyDay.roundTrips(d(2027, 4, 30)))
        assertEquals(false, KeyDay.roundTrips(d(2027, 8, 31)))
        assertEquals(false, KeyDay.roundTrips(d(2027, 1, 31))) // noviembre tiene 30 días
        assertEquals(d(2027, 4, 28), KeyDay.birthdayFrom(KeyDay.keyDayFor(d(2027, 4, 30))))
    }

    @Test fun conTresMesesSeRecuperanLosCasosDeDosMeses() {
        // 29/30-abr, 31-ago y 31-ene no tienen día clave con 2 meses, pero sí con 3 (todo el día activado).
        for (b in listOf(d(2027, 4, 30), d(2027, 8, 31), d(2027, 1, 31), d(2027, 4, 29))) {
            assertEquals(false, KeyDay.roundTrips(b, 2), "2 meses $b")
            assertEquals(true, KeyDay.roundTrips(b, 3), "3 meses $b")
        }
        // Ejemplo: 30-abr → 30-ene → día clave 23-ene (3 meses).
        assertEquals(d(2027, 1, 23), KeyDay.keyDayFor(d(2027, 4, 30), 3))
        assertEquals(d(2027, 4, 30), KeyDay.birthdayFrom(d(2027, 1, 23), 3))
    }

    @Test fun losFallosDeCadaVarianteSonLosPrevistosYNoSeSolapan() {
        var d0 = d(2026, 1, 1)
        val fin = d(2032, 12, 31)
        while (d0 <= fin) {
            val m = d0.monthNumber
            val dia = d0.dayOfMonth
            if (!KeyDay.roundTrips(d0, 2)) {
                val previsto = (m == 4 && dia >= 29) || (m == 8 && dia == 31) || (m == 1 && dia == 31)
                assertEquals(true, previsto, "Fallo inesperado con 2 meses en $d0")
            }
            if (!KeyDay.roundTrips(d0, 3)) {
                val previsto = (m == 5 && dia >= 29) || (m == 7 && dia == 31) || (m == 12 && dia == 31)
                assertEquals(true, previsto, "Fallo inesperado con 3 meses en $d0")
            }
            // Entre las dos variantes se cubre cualquier cumpleaños.
            assertEquals(true, KeyDay.isReachable(d0), "Cumpleaños inalcanzable: $d0")
            d0 = d0.plus(1, DateTimeUnit.DAY)
        }
    }
}
