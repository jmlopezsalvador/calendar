package com.calendar.domain

/** Palos de la baraja francesa y la letra que los codifica al final del texto del evento. */
enum class Suit(val code: Char, val nombre: String) {
    CORAZONES('C', "corazones"),
    PICAS('P', "picas"),
    TREBOLES('T', "tréboles"),
    DIAMANTES('D', "diamantes"),
}

/** Carta de la baraja francesa. [value]: 1 = as, 11 = J, 12 = Q, 13 = K. */
data class Card(val value: Int, val suit: Suit) {
    init {
        require(value in 1..13) { "Valor de carta fuera de rango: $value" }
    }

    /** Nombre en español, p. ej. «8 de corazones» o «Reina de tréboles». */
    val nombre: String
        get() {
            val v = when (value) {
                1 -> "As"
                11 -> "Jota"
                12 -> "Reina"
                13 -> "Rey"
                else -> value.toString()
            }
            return "$v de ${suit.nombre}"
        }

    companion object {
        /**
         * Extrae la carta de las **dos últimas letras** del texto (ignorando espacios y saltos de línea finales
         * o intermedios, así «Q T» equivale a «QT»). Valor: A/1 = as, 2–9, 0/T = 10, J, Q, K.
         * Palo: C corazones, P picas, T tréboles, D diamantes. Devuelve null si no forman una carta válida.
         */
        fun fromText(text: String): Card? {
            val letras = text.filter { !it.isWhitespace() }
            if (letras.length < 2) return null
            val v = when (letras[letras.length - 2].uppercaseChar()) {
                'A', '1' -> 1
                in '2'..'9' -> letras[letras.length - 2] - '0'
                '0', 'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                else -> return null
            }
            val palo = Suit.entries.firstOrNull { it.code == letras.last().uppercaseChar() } ?: return null
            return Card(v, palo)
        }
    }
}
