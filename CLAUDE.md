# Proyecto móvil Android + iOS (plantilla)

App móvil para **Android y iOS** con un único código en Kotlin Multiplatform + Compose Multiplatform.
El usuario habla español: responde y comenta el código en español.

> **ESTADO: PLANTILLA SIN INICIALIZAR.** Mientras este bloque exista, el proyecto sigue siendo la plantilla genérica
> (nombre `MiApp`, paquete `com.miapp`, pantalla de ejemplo). **Lo primero que debes hacer en la primera conversación es
> seguir el apartado «Primer arranque». Cuando termines, borra este bloque y rellena «Qué hace la app».**

## Primer arranque (haz esto antes de escribir nada de funcionalidad)
1. **Saluda y explica brevemente** que el proyecto es una plantilla y que vas a inicializarlo. No empieces a programar todavía.
2. **Pregunta (con `AskUserQuestion` si ayuda) lo imprescindible:**
   - **Nombre de la app** (visible en el móvil y en el `.ipa`/APK) y, si no lo da, propón uno de una palabra.
   - **Paquete / id**: propón `com.<nombre en minúsculas>` salvo que el usuario quiera otro (p. ej. `com.wikicode.<nombre>`).
   - **Qué hace la app**: objetivo, para quién, pantallas principales, qué guarda en local, si usa sensores/vibración/
     cámara/galería, si necesita red o cuentas (por defecto NO: todo en local), idiomas, orientación (por defecto vertical).
   - **Plataformas**: Android + iOS (por defecto ambas) y si hay algo que solo exista en una.
   - Si el usuario tiene ya un documento de especificaciones: que lo deje en `docs/SPEC.md` (o en `data/` y lo lees).
3. **Escribe `docs/SPEC.md`** con lo que el usuario cuente (requisitos numerados, preguntas abiertas). Es la fuente de verdad funcional.
4. **Renombra la plantilla** (todo a la vez, con un script, y compila después):
   - `MiApp` → nombre real y `com.miapp` → paquete real en: `settings.gradle.kts` (`rootProject.name`), `app/build.gradle.kts`
     (`namespace`, `applicationId`), `shared/build.gradle.kts` (`namespace`), `AndroidManifest.xml` (`label`, tema),
     `res/values/themes.xml`, `iosApp/project.yml` (`bundleIdPrefix`, `CFBundleDisplayName`, `PRODUCT_BUNDLE_IDENTIFIER`,
     `PRODUCT_NAME`), `.github/workflows/ios.yml` (`MiApp.app`, `MiApp.ipa`, nombre del artefacto) y los nombres `MiAppRoot`.
   - **Mueve las carpetas de paquete** (`.../kotlin/com/miapp/` → `.../kotlin/<paquete>/`) en `app`, `shared/commonMain`,
     `commonTest`, `androidMain` e `iosMain`, y cambia las líneas `package` / `import`.
   - Cambia el nombre de las `SharedPreferences` (`"miapp"`) en `Platform.android.kt`.
   - Sustituye también en `docs/ARQUITECTURA.md` (§ que mencionan `com.miapp` / `MiApp`) solo si el usuario quiere mantenerlo al día; no es obligatorio.
5. **Compila y prueba** (ver «Compilar y probar»). La pantalla de ejemplo (contador) debe seguir funcionando; luego sustitúyela.
6. **Git:** si no hay repo (`git init`), pide al usuario la URL del repo de GitHub, haz el primer commit y marca `gradlew` como
   ejecutable: `git update-index --chmod=+x gradlew` (si no, el Mac de Actions da `Permission denied`).
7. **Icono:** pide al usuario un icono (déjalo en `data/icon.png`). Hasta entonces hay un icono neutro de plantilla en
   `app/src/main/res/mipmap-*` y `iosApp/iosApp/Assets.xcassets/AppIcon.appiconset` (fondo adaptativo `#00050E`).
8. **Actualiza este `CLAUDE.md`:** borra el bloque «ESTADO: PLANTILLA», cambia el título y rellena «Qué hace la app».

## Mapa de carpetas
| Carpeta | Para qué sirve |
|---|---|
| `shared/` | Módulo KMP con TODO el código: lógica pura, interfaz Compose, ajustes. `commonMain` (común), `androidMain` / `iosMain` (solo lo del sistema, `expect`/`actual`), `commonTest` (tests). |
| `app/` | Solo la envoltura Android (`MainActivity`, manifiesto, recursos, icono). |
| `iosApp/` | Envoltura iOS en SwiftUI + `project.yml` (XcodeGen genera el `.xcodeproj` en CI; no se versiona). |
| `.github/workflows/ios.yml` | Compila en un Mac de GitHub y publica un `.ipa` sin firmar. Solo `workflow_dispatch`. |
| `docs/` | `ARQUITECTURA.md` (base técnica, fuente de verdad de versiones y reglas) y `SPEC.md` (especificación funcional, se crea al inicializar). |
| `data/` | Material de referencia que aporta el usuario (capturas, icono, recursos). Se puede leer. |
| `data/android_build/` | APK descargados/generados. No se versionan. |
| `data/ios_build/` | `.ipa` descargado de Actions. No se versiona. |
| `ignore/` | **Ignorar por completo** (ver reglas). |

## Reglas para Claude
- **Ignorar por completo la carpeta `ignore/`**: no leerla, no listarla, no buscar dentro, no usarla como referencia. Excluirla
  también de búsquedas (Glob/Grep) y del control de versiones. Es el cajón del usuario para material suelto.
- La base técnica es **`docs/ARQUITECTURA.md`** (versiones, estructura, Gradle, CI de iOS, reglas de código). Si algo choca con él, consultar al usuario.
- **Antes de lanzar el workflow de iOS en GitHub Actions, preguntar siempre al usuario** (cuesta cuota de macOS; se acumulan mejoras).
  Por defecto se lanza **sin tests** (el usuario prueba en su iPhone); solo con tests si él lo pide.
- No inventar funcionalidad: la fuente de verdad es `docs/SPEC.md` y lo que pida el usuario. Ante la duda, preguntar.
- **No hacer commit ni push sin que el usuario lo pida.** No commitear si la compilación falla.
- Datos solo en local (SharedPreferences / UserDefaults y ficheros internos). Sin red ni cuentas salvo que el SPEC lo diga.
- Mantener este `CLAUDE.md` al día: al cambiar de forma relevante la app, el flujo o las decisiones, actualizarlo en la misma tarea.

## Qué hace la app
*(Pendiente: rellenar tras el primer arranque con un resumen de `docs/SPEC.md`.)*

## Reglas de código para que compile en iOS
- En `commonMain`: nada de `java.*` ni `android.*`. Usar `kotlinx-datetime` (0.6.1), `kotlin.math.*` y, si hace falta aritmética
  grande, `bignum` (`com.ionspin.kotlin:bignum:0.3.10`). Sin `String.format`, `Locale`, `SimpleDateFormat`, `Thread`, `synchronized`.
- Sin recursos de Android (`R.string`) en común; textos en el código.
- Iconos dibujados con `Canvas`/`Path` de Compose (no `material-icons-extended`).
- Lógica de dominio pura y sin UI, con tests en `commonTest` (y reloj inyectable si depende del tiempo).
- Lo del sistema (sensores, vibración, galería, portapapeles…): `expect` en `commonMain/.../ui/Platform.kt` + `actual` en `androidMain`/`iosMain`.
- `Platform.ios.kt` lleva `@file:OptIn(ExperimentalForeignApi::class)` (cinterop de Kotlin/Native).
- El fallo de iOS solo se ve en Actions (~20 min): revisar con cuidado antes de lanzarlo.
- Con scripts: en esta máquina los heredocs de Bash con contenido complejo fallan; escribe los `.py` con la herramienta Write y ejecútalos.

## Compilar y probar (Windows)
- Kotlin 2.0.21, Compose Multiplatform 1.7.1, AGP 8.7.3, Gradle 8.9, JDK 17, minSdk 26, target/compileSdk 35, iOS mínimo 15.0.
- `JAVA_HOME` apunta al JDK 17: `C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot`.
- SDK de Android en `C:\Users\jmlop\AppData\Local\Android\Sdk`: crear `local.properties` con `sdk.dir=...` (ignorado por git).
- `./gradlew.bat :shared:testDebugUnitTest assembleDebug` → APK en `app/build/outputs/apk/debug/app-debug.apk`.
  Copiar el APK a `data/android_build/` si se quiere conservar.
- Emulador AVD `calcu` (Android 15), reutilizable. Poner la zona horaria `Europe/Madrid` (arranca en GMT). Gboard puede salir en negro (es del emulador).
- `gh` en Windows: `C:\Program Files\GitHub CLI\gh.exe` (hace falta `gh auth login` una vez).
- En Windows los destinos de iOS se ignoran (`kotlin.native.ignoreDisabledTargets=true`); Android compila y testea igual.

## Flujo de trabajo
1. Desarrollar y probar en Android/JVM en local (emulador).
2. Commit y push cuando el usuario lo pida.
3. **Preguntar antes de lanzar Actions** y acumular mejoras: `gh workflow run ios.yml --ref main` (opción `-f tests=true` solo si se pide).
4. Descargar el artefacto a `data/ios_build/`: `gh run download <id> -n <Nombre>-ios-sin-firmar -D data/ios_build`.
5. Instalar el `.ipa` con Sideloadly/AltStore (Apple ID gratis; caduca a los 7 días; activar Modo desarrollador en el iPhone).

## Lecciones de iOS ya aprendidas
- `gradlew` debe estar en git con permiso de ejecución (`git update-index --chmod=+x gradlew`).
- En iOS, `UIDevice.proximityState` se lee por sondeo; apagar y reactivar la monitorización deja el estado pegado. Mantenerla activa.
- Barra de estado e indicador de inicio se ocultan desde SwiftUI (`ContentView.swift`); el bloqueo automático con `idleTimerDisabled`.

## Instrucciones para el usuario (si la app las necesita)
Si la app lleva una página de «Instrucciones» de cara al usuario final, mantenerla al día en la misma tarea en que se cambie la función.
