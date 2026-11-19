package com.calendar.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Relación entre el **día clave** (el que pulsa el mago) y el **cumpleaños** del espectador. Ver `docs/SPEC.md` §4.
 *
 * Mago: cumpleaños B → se retrocede 2 meses → se mira la celda de encima (−7 días) = día clave.
 * App: día clave X → celda de debajo (+7 días) → se avanzan 2 meses = cumpleaños.
 *
 * Los meses se suman/restan con ajuste al último día del mes (kotlinx-datetime), de modo que si el día no existe
 * se usa el último día de ese mes (28/29-feb, 30-jun…).
 */
object KeyDay {
    /** Día clave que debe pulsar el mago para el cumpleaños [birthday]. */
    fun keyDayFor(birthday: LocalDate): LocalDate =
        birthday.minus(2, DateTimeUnit.MONTH).minus(7, DateTimeUnit.DAY)

    /** Cumpleaños deducido del día clave [keyDay] pulsado por el mago. */
    fun birthdayFrom(keyDay: LocalDate): LocalDate =
        keyDay.plus(7, DateTimeUnit.DAY).plus(2, DateTimeUnit.MONTH)

    /**
     * ¿Se recupera el cumpleaños exacto al ir y volver? Es false en los días que no existen 2 meses antes
     * (29/30 de abril, 31 de agosto, 31 de enero y 29 de febrero), que requieren el forzado manual.
     */
    fun roundTrips(birthday: LocalDate): Boolean = birthdayFrom(keyDayFor(birthday)) == birthday
}
