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
 * Variante «Todo el día»: si el evento del día clave se marca como de día completo, se usan **3 meses** en vez de 2.
 * Los cumpleaños que no existen 2 meses antes (29/30-abr, 31-ago, 31-ene) sí existen 3 meses antes, y los que no
 * existen 3 meses antes (29/30/31-may, 31-jul, 31-dic) sí 2 meses antes: entre las dos variantes se cubren todos.
 *
 * Los meses se suman/restan con ajuste al último día del mes (kotlinx-datetime), de modo que si el día no existe
 * se usa el último día de ese mes (28/29-feb, 30-jun…).
 */
object KeyDay {
    const val MONTHS_DEFAULT = 2
    const val MONTHS_ALL_DAY = 3

    /** Meses que se retroceden según la marca «Todo el día» del evento. */
    fun monthsFor(allDay: Boolean) = if (allDay) MONTHS_ALL_DAY else MONTHS_DEFAULT

    /** Día clave que debe pulsar el mago para el cumpleaños [birthday] (con [months] meses de retroceso). */
    fun keyDayFor(birthday: LocalDate, months: Int = MONTHS_DEFAULT): LocalDate =
        birthday.minus(months, DateTimeUnit.MONTH).minus(7, DateTimeUnit.DAY)

    /** Cumpleaños deducido del día clave [keyDay] pulsado por el mago (con [months] meses de avance). */
    fun birthdayFrom(keyDay: LocalDate, months: Int = MONTHS_DEFAULT): LocalDate =
        keyDay.plus(7, DateTimeUnit.DAY).plus(months, DateTimeUnit.MONTH)

    /**
     * ¿Se recupera el cumpleaños exacto al ir y volver con [months] meses? Con 2 meses es false en los días que no
     * existen 2 meses antes (29/30-abr, 31-ago, 31-ene); con 3 meses, en 29/30/31-may, 31-jul y 31-dic.
     */
    fun roundTrips(birthday: LocalDate, months: Int = MONTHS_DEFAULT): Boolean =
        birthdayFrom(keyDayFor(birthday, months), months) == birthday

    /** ¿Algún día clave (con 2 o con 3 meses) produce exactamente este cumpleaños? */
    fun isReachable(birthday: LocalDate): Boolean = roundTrips(birthday, MONTHS_DEFAULT) || roundTrips(birthday, MONTHS_ALL_DAY)
}
