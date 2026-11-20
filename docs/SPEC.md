# SPEC — Calendar (herramienta de magia)

Fuente de verdad funcional. Requisitos numerados; las preguntas abiertas están al final.

## 1. Resumen
App Android + iOS que **emula un calendario real** (look and feel de distintas marcas/sistemas) y esconde un efecto de magia:
el espectador cree que el calendario guarda eventos, pero el mago fuerza un evento «Feliz cumpleaños» + la carta elegida
en el día de cumpleaños del espectador. Todo en local, sin red ni cuentas.

## 2. El efecto (guion)
1. Se pregunta al espectador su fecha de nacimiento y se le hace elegir una carta (baraja francesa).
2. Se abre la app: calendario mensual. El mago dice que suele guardar eventos en el calendario.
3. El mago pulsa un día concreto (el **día clave**, ver §4), se abre el formulario, escribe un evento de prueba y lo guarda.
4. Se vuelve a preguntar el cumpleaños y se pide al espectador que navegue hasta ese día.
5. En su día aparece el evento «Feliz cumpleaños» y, al abrirlo, un mensaje de feliz cumpleaños con **su carta**.

## 3. Requisitos funcionales
- **R1. Nombre y icono seleccionables.** En Ajustes se elige el nombre visible y el icono de la app entre un conjunto
  incluido en el paquete. Nombres iniciales: `Calendar`, `Calendario`, `Agenda` (se ampliarán). Iconos: los de `data/visuals`
  (`icono.png` de cada marca). Android: *activity-alias*; iOS: *alternate icons* (el nombre en iOS solo cambia dentro de la app, no el del icono de inicio).
- **R2. Look and feel por marca.** Ajustes lista las marcas del kit y permite elegir una. Solo se replica la **pantalla del calendario**
  (vista mensual); el formulario de evento es común en estructura pero **tomando colores, tipografía y tipo de componentes de la marca elegida**.
  Marcas del kit: Samsung, Apple, Google, Xiaomi, OPPO (= Google), Huawei, Business Calendar 2, Monthly Calendar Moca, aCalendar.
- **R2b. Acceso a Ajustes oculto:** pulsación larga en cualquier punto de la cabecera (el espectador no ve ningún botón de ajustes). Vibración doble al entrar.
- **R2c. Reinicio del efecto:** pulsación larga en la esquina superior izquierda de la cabecera (zona invisible) borra el «Feliz cumpleaños» forzado y el evento de prueba que lo originó, para repetir el efecto; el resto de eventos se conserva. Vibración doble al reiniciar.
- **R1c. Guía de inicio e instrucciones.** Al iniciar se abre una guía por fases (objetivo, efecto, día clave, carta, gestos ocultos, ajustes) con ilustraciones animadas; en la última fase se ofrece «No volver a mostrar». En Ajustes se puede activar/desactivar su aparición al iniciar, volver a verla y abrir las Instrucciones (página con la explicación detallada del efecto, los ajustes y el modo de uso, con ilustraciones).
- **R1b. Cambio de nombre/icono:** al elegir uno en Ajustes sale un modal que explica el cambio y avisa de que la app se reiniciará (Cancelar / Aplicar). iOS solo permite cambiar el icono, no el nombre.
- **R3. Detección automática del sistema.** Opción «Automático» (por defecto): Android → plantilla Google (o Samsung/Xiaomi/Huawei/OPPO
  según `Build.MANUFACTURER`); iOS → Apple. El usuario puede cambiarla.
- **R4. Vista mensual continua.** Al abrir, calendario mensual con scroll continuo entre meses (semanas contiguas que cruzan el límite de mes).
  La dirección (vertical/horizontal) y el comportamiento (ajuste al mes, etc.) dependen de la marca; se define a partir de
  `data/visuals/Kit_calendarios` (`Ficha.md`, `Matriz_navegacion.csv`). Se ignoran iconos/elementos de login de usuario en las capturas.
- **R14. Cabeceras y menús fieles (no funcionales).** Cada marca replica sus botones de cabecera, selector de mes, barra de acciones, botón flotante «+», etc.
  (menú, búsqueda, «hoy», tareas, filtros…) dibujados con `Canvas`/`Path`, solo de atrezo. **Se omiten avatares/iconos de login de usuario.**
- **R15. Desplazamiento configurable.** Ajuste: `Según la marca` (por defecto) / `Vertical continuo` (semanas contiguas, pero **cada mes empieza en su propia fila**: el día 1 cae en su columna y las celdas de los meses vecinos se dejan vacías, como en los calendarios originales, para que el salto de mes se vea claro) / `Horizontal` (paginado mes a mes).
- **R16. Versiones y ajuste fino.** Una marca puede tener **variantes** (p. ej. «Google Calendar en Samsung»: celdas sin redondeo) y Ajustes permite afinar
  detalles (redondeo, separación, altura de fila, elementos de cabecera visibles…) guardados como sobrescrituras de la skin.
- **R5. Eventos.** Pulsar un día abre directamente el formulario (sin pantalla de resumen del día). Máximo **un evento por día**.
  Si el día ya tiene evento, el formulario se abre para modificarlo. El título del evento se ve como resumen dentro de la celda del día.
- **R6. Formulario de evento (atrezo).** Título, texto/notas, y campos de relleno (todo el día, repetición, hora…) sin función real.
  Cada evento puede llevar un **color** (azul, verde, rojo, amarillo o naranja; sin elegir = el color propio de la marca) que se ve en su celda. Un evento de **«Todo el día»** rellena la celda entera del día con su color (salvo en las skins de puntos, Xiaomi y Huawei). Se ampliará más adelante.
- **R7. Eventos sintéticos.** Función para rellenar aleatoriamente un rango de fechas con eventos creíbles (gimnasio, dentista,
  peluquería, cumpleaños, cita médica, quedada con amigos…). La lista de temas es editable en Ajustes, y también el color de los eventos generados: aleatorio entre todos, una **selección de colores** (con uno solo, todos los eventos salen de ese color; con varios, al azar entre ellos) o el de la marca. Si el día ya tiene evento, se abre el formulario con él.
- **R8. Forzado.** Al guardar el evento en el **día clave** (R9), la app inserta/sustituye en el **día de cumpleaños** deducido
  un evento «Feliz cumpleaños» cuyo texto incluye la carta (R10). Si el día ya tenía evento, se sustituye.
  Si luego el mago modifica el evento del día clave, el forzado se recalcula.
- **R9. Día clave → cumpleaños (ver §4).**
- **R10. Carta.** El evento de prueba lleva un texto cualquiera; **sus dos últimas letras** codifican la carta: valor + palo.
  Valor: `A`/`1`=as, `2`–`9`, `0`/`T`=10 (`T` también por «ten»), `J`, `Q`, `K`. Palo: `C`=corazones, `P`=picas, `T`=tréboles, `D`=diamantes.
  Ejemplos: `8C`=8♥, `7P`=7♠, `QT`=Q♣, `0T`=10♣, `1C`=A♥. Insensible a mayúsculas y a espacios (`Q T` = `QT`). La carta se busca primero en la descripción y, si no hay, en el título. Si las dos últimas letras no forman una carta válida,
  no se fuerza nada (el evento es normal).
- **R11. Mensaje final.** Al abrir el evento forzado se muestra «¡Feliz cumpleaños!» y el nombre de la carta en español
  (p. ej. «8 de corazones»). El aspecto debe parecer un evento normal con ese texto.
- **R12. Persistencia local** de eventos y ajustes (SharedPreferences / UserDefaults o fichero interno).
- **R13. Plataformas:** Android + iOS, orientación vertical, idioma español.

## 4. Algoritmo del día clave → cumpleaños
Definiciones: rejilla de semanas, cada celda es un día; «misma vertical» = misma columna = ±7 días.

**Dirección mago (cómo elige el día clave):** dado el cumpleaños `B`, se retrocede 2 meses → `B₋₂ₘ`, y el día clave es la celda
**inmediatamente encima** (`B₋₂ₘ − 7 días`). Ejemplo: `B = 13-ene-2027` → `13-nov-2026` → día clave `6-nov-2026`.

**Dirección app (lo que implementamos):** al guardarse un evento de prueba en el día `X`, la app deduce
`B = (X + 7 días) + 2 meses`. Ejemplo: `X = 6-nov-2026` → `13-nov-2026` → `B = 13-ene-2027`. El forzado va en esa fecha (año incluido).

**Días que no existen al retroceder 2 meses** (la dirección mago es many-to-one en estos casos):
| Cumpleaños `B` | Problema |
|---|---|
| 29 y 30 abril | febrero no tiene 29 (año no bisiesto) / 30 |
| 31 agosto | junio no tiene 31 |
| 31 enero | noviembre no tiene 31 |

*Nota:* el 29 de febrero **sí** tiene día clave (29-feb − 2 meses = 29-dic, y la celda de encima es el 22-dic); solo falta en los años no bisiestos porque esa fecha no existe.

**Solución: la marca «Todo el día» (variante de 3 meses).** El formulario de evento lleva el interruptor «Todo el día» (por defecto
desactivado). Activado en el evento del día clave significa «3 meses» en vez de 2: `B = (X + 7 días) + 3 meses`. El mago, cuando el
día `B − 2 meses` no existe en el calendario, retrocede un mes más (`B − 3 meses`), toma la celda de encima y activa «Todo el día».
Los cumpleaños sin día clave con 2 meses (29/30-abr, 31-ago, 31-ene) existen 3 meses antes, y los que no existen 3 meses antes
(29/30/31-may —el 29 solo en año no bisiesto—, 31-jul, 31-dic) existen 2 meses antes: **no se solapan, así que entre las dos variantes
se cubre cualquier cumpleaños** (lo comprueba un test para 2026–2032). Ejemplo: `B = 30-abr-2027` → `30-ene-2027` → día clave `23-ene-2027` con «Todo el día».
Al guardar de nuevo el día clave con otro valor del interruptor, el forzado anterior se retira y se coloca el nuevo.

Los tests unitarios de este algoritmo van en `commonTest` (bisiestos, fin de año, meses de 28/29/30/31 días, ida y vuelta).

## 5. Preguntas abiertas
- Elegir el día clave usando el domingo o el lunes como inicio de semana no afecta (siempre ±7 días), pero ¿se muestran semanas de lunes o domingo según la marca? (lo marca el kit).
- Alcance del icono alternativo en iOS (requiere los PNG en el proyecto Xcode generado por XcodeGen).
- Formato exacto del mensaje de feliz cumpleaños y del título del evento forzado.
- Campos del formulario de evento más allá del título y las notas.
- Qué hacer si el día clave cae en un día que ya tiene evento (propuesta: se sobrescribe igual que cualquier otro).
