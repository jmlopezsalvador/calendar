package com.calendar.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Al tocar una zona vacía (que no consume el toque ningún botón ni campo de texto) se quita el foco del campo
 * activo, lo que cierra el teclado.
 */
@Composable
fun Modifier.dismissKeyboardOnTap(): Modifier {
    val foco = LocalFocusManager.current
    return pointerInput(foco) { detectTapGestures { foco.clearFocus() } }
}
