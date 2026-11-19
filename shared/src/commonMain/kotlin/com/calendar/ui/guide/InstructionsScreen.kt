package com.calendar.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Morado = Color(0xFF6750A4)
private val Tenue = Color(0xFF5F6368)

/** Instrucciones detalladas: efecto, ajustes y modo de uso, con ilustraciones. */
@Composable
fun InstructionsScreen(onClose: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color.White).windowInsetsPadding(WindowInsets.systemBars)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onClose) { Text("‹ Volver", fontSize = 16.sp) }
            Text("Instrucciones", fontSize = 20.sp, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp).padding(bottom = 32.dp)) {
            Seccion("¿Qué es Calendar?")
            P("Un calendario falso para un efecto de magia. Imita el aspecto de calendarios reales (Google, Apple, Samsung, OPPO, Xiaomi, Huawei, Business Calendar 2, Moca y aCalendar) y guarda eventos como cualquier calendario. Todo se guarda solo en tu móvil: no usa internet ni cuentas.")
            Arte { ArtBienvenida() }

            Seccion("El efecto, visto por el público")
            P("Le pides a una persona su fecha de cumpleaños. Abres el calendario, tocas un día y apuntas un evento, como quien busca algo. Después vas al día de su cumpleaños y ya hay un evento «Feliz cumpleaños» cuya descripción dice el nombre de su carta.")
            Arte { ArtEfecto() }

            Seccion("Cómo se hace, paso a paso")
            Paso("1", "Pide el cumpleaños", "Que te diga solo el día y el mes, por ejemplo «13 de enero». Anota en qué año quieres que aparezca (el próximo, por ejemplo).")
            Paso("2", "Calcula el día clave", "Retrocede 2 meses desde el cumpleaños (13 de enero → 13 de noviembre) y sube una fila: la celda de encima, 7 días antes (6 de noviembre). Es siempre la misma cuenta: cumpleaños − 2 meses − 7 días. Si ese día no existe, mira «Días especiales».")
            Arte { ArtDiaClave() }
            Paso("3", "Pulsa el día clave", "Al tocar un día se abre directamente el formulario del evento. Si el día ya tenía un evento, se abre para editarlo.")
            Paso("4", "Escribe el texto con la carta", "Escribe lo que quieras en el título o en la descripción, pero que termine en dos letras: valor y palo de la carta (por ejemplo «Cena con Marta 8C»). La app busca la carta primero en la descripción y, si no hay, en el título.")
            Arte { ArtCarta() }
            Paso("5", "Guarda", "Al guardar, la app calcula el cumpleaños (día clave + 7 días + 2 meses) y crea ahí el evento «Feliz cumpleaños» con el texto «¡Feliz cumpleaños! Tu carta es el 8 de corazones.». Si ese día ya tenía un evento, lo sustituye.")
            Paso("6", "Enséñalo", "Navega hasta el cumpleaños y abre el evento: el mensaje y la carta ya están ahí.")
            P("Si editas el evento del día clave, el mensaje se recalcula; si lo borras, el mensaje desaparece. Si las dos últimas letras no forman una carta válida, no ocurre nada: el evento es normal.")

            Seccion("La carta")
            P("Las dos últimas letras del texto (sin contar espacios, sin distinguir mayúsculas) son valor y palo:")
            Tabla(
                listOf(
                    "Valor" to "A o 1 = As · 2 a 9 · 0 o T = 10 · J = Jota · Q = Reina · K = Rey",
                    "Palo" to "C = corazones · P = picas · T = tréboles · D = diamantes",
                ),
            )
            P("Ejemplos: 8C = 8 de corazones · 7P = 7 de picas · QT = Reina de tréboles · 0T = 10 de tréboles · 1C = As de corazones.")

            Seccion("Días especiales")
            P("Hay cumpleaños cuya fecha no existe dos meses antes: el 30 de abril (30 de febrero no existe), el 29 de abril en años no bisiestos, el 31 de agosto (31 de junio) y el 31 de enero (31 de noviembre). Para ellos usa el interruptor «Todo el día» del formulario, que significa «tres meses» en lugar de dos.")
            Paso("A", "Retrocede un mes más", "Si dos meses antes el día no existe, ve un mes más atrás (por ejemplo, cumpleaños 30 de abril → 30 de enero) y toma la celda de encima (23 de enero).")
            Paso("B", "Activa «Todo el día»", "Pulsa ese día, activa «Todo el día», escribe el texto con la carta y guarda. La app coloca el «Feliz cumpleaños» tres meses después (30 de abril).")
            P("Con tres meses, los cumpleaños que fallan son otros (29, 30 y 31 de mayo, 31 de julio y 31 de diciembre), y esos sí existen dos meses antes. Entre las dos opciones se cubre cualquier fecha. Los demás cumpleaños (incluido el 29 de febrero) usan el método normal, con «Todo el día» desactivado, que es como se abre el formulario por defecto.")

            Seccion("Gestos ocultos")
            P("La app no muestra ningún botón de ajustes, para que nadie sospeche. Todo se hace con pulsación larga, y cada gesto vibra dos veces:")
            Arte { ArtGestos() }
            P("• Pulsación larga en cualquier punto de la cabecera (el título, los botones…): abre los Ajustes.\n• Pulsación larga en la esquina superior izquierda: reinicia el efecto. Borra el «Feliz cumpleaños» y el evento de prueba que lo creó, y deja el resto de eventos para poder repetirlo.")

            Seccion("Ajustes")
            Item("Guía e instrucciones", "Vuelve a ver la guía de inicio, actívala o desactívala al arrancar y abre estas instrucciones.")
            Item("Aspecto del calendario", "Elige la marca que imitar: Google, Google en Samsung, Apple, Samsung, OPPO, Xiaomi, Huawei, Business Calendar 2, Moca o aCalendar. «Automático» usa la del sistema: Samsung en un Samsung, Google en el resto de Android y Apple en iPhone.")
            Arte { ArtAjustes() }
            Item("Desplazamiento", "Vertical continuo (semanas seguidas, como Apple o Moca) u horizontal mes a mes (como Google o Samsung). «Según la marca» respeta el de cada una.")
            Item("Ajuste fino", "«Título del evento en el día» (una línea recortada o varias líneas con salto de línea) y, en el ajuste fino, redondeo y separación de las celdas y altura de las filas, para clavar detalles de la versión de calendario que imites. Al cambiar de marca se restablece.")
            Item("Nombre e icono de la app", "En Android puedes elegir entre Calendar, Calendario y Agenda, y entre diez iconos de calendarios reales. Al elegir uno sale un aviso: la app se reiniciará para aplicarlo (Cancelar o Aplicar). En iPhone solo se puede cambiar el icono; el nombre no.")
            Item("Eventos aleatorios", "Rellena el calendario con eventos creíbles (gimnasio, dentista, peluquería…) para que parezca usado. Edita la lista de temas (uno por línea), elige cuántos meses hacia atrás y adelante, el porcentaje de días con evento y su color (aleatorio, el de la marca o uno fijo). No toca los días que ya tienen evento.")

            Seccion("Consejos")
            P("• Prepara el calendario antes: elige la marca, rellena con eventos aleatorios y, si quieres, cambia el nombre y el icono.\n• Solo hay un evento por día: guardar sobre un día ocupado lo sustituye. Cada evento puede llevar un color (fila «Color» del formulario) y, si es de «Todo el día», rellena la celda entera del día.\n• Para repetir el efecto, reinícialo con la pulsación larga en la esquina antes de empezar de nuevo.\n• La carta del evento de prueba es la que revelará la app: asegúrate de que coincide con la que quieres mostrar.")
        }
    }
}

@Composable
private fun Seccion(t: String) {
    Text(t, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Morado, modifier = Modifier.padding(top = 26.dp, bottom = 8.dp))
}

@Composable
private fun P(t: String) {
    Text(t, fontSize = 15.sp, color = Color(0xFF333333), lineHeight = 22.sp, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun Arte(content: @Composable () -> Unit) {
    Box(
        Modifier.fillMaxWidth().padding(vertical = 10.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF7F2FA)).padding(16.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun Paso(n: String, titulo: String, texto: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Box(Modifier.padding(end = 12.dp).clip(RoundedCornerShape(50)).background(Morado).padding(horizontal = 10.dp, vertical = 4.dp)) {
            Text(n, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Column(Modifier.weight(1f)) {
            Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(texto, fontSize = 15.sp, color = Color(0xFF333333), lineHeight = 22.sp)
        }
    }
}

@Composable
private fun Item(titulo: String, texto: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(titulo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(texto, fontSize = 15.sp, color = Color(0xFF333333), lineHeight = 22.sp)
    }
}

@Composable
private fun Tabla(filas: List<Pair<String, String>>) {
    Column(
        Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF7F2FA)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for ((a, b) in filas) {
            Row {
                Text(a, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Morado, modifier = Modifier.padding(end = 12.dp))
                Text(b, fontSize = 14.sp, color = Tenue, modifier = Modifier.weight(1f))
            }
        }
    }
}
