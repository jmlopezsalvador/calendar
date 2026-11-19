package com.calendar.domain

/** Colores que se pueden dar a un evento. `argb` es el color y [textoOscuro] indica si el texto sobre él debe ser negro. */
enum class EventColor(val id: String, val nombre: String, val argb: Long, val textoOscuro: Boolean = false) {
    AZUL("azul", "Azul", 0xFF1E88E5),
    VERDE("verde", "Verde", 0xFF2E9E57),
    ROJO("rojo", "Rojo", 0xFFE53935),
    AMARILLO("amarillo", "Amarillo", 0xFFF6BF26, textoOscuro = true),
    NARANJA("naranja", "Naranja", 0xFFF57C00);

    companion object {
        /** Modo de color de los eventos generados: aleatorio, el de la marca o un color fijo. */
        const val MODE_RANDOM = "random"
        const val MODE_DEFAULT = "default"

        fun fromId(id: String?): EventColor? = entries.firstOrNull { it.id == id }

        /** Colores entre los que elige el generador según el [mode] (`null` = el color propio de la marca). */
        fun pool(mode: String): List<EventColor?> = when (mode) {
            MODE_RANDOM -> entries
            MODE_DEFAULT -> listOf(null)
            else -> listOf(fromId(mode))
        }
    }
}
