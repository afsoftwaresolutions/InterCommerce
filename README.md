# InterCommerce

Aplicacion Android de catalogo y carrito con enfoque offline-first para consulta de productos, detalle y gestion de carrito.

## Arquitectura elegida

Se adopto una arquitectura en capas inspirada en **Clean Architecture + MVVM**:

- **presentation**
  - UI en Jetpack Compose.
  - `ViewModel` para manejo de estado (`StateFlow`) y eventos de UI.
  - Navegacion con `navigation-compose`.
- **domain**
  - Modelos de negocio (`Money`, `Percentage`, `CartItem`, etc.).
  - Casos de uso (`usecase`) con reglas de negocio puras y testeables.
  - Contratos de repositorio (`ProductRepository`, `CartRepository`).
- **data**
  - Implementaciones de repositorio.
  - Fuentes remotas (Retrofit/OkHttp) y locales (Room).
  - Mappers DTO/Entity/Domain.
  - Manejo de errores uniforme (`DataResult`, `DataError`).
- **di**
  - Inyeccion de dependencias con Hilt para desacoplar construccion y consumo.

### Por que esta arquitectura

- Separa responsabilidades y reduce acoplamiento.
- Facilita pruebas unitarias en dominio y casos de uso sin dependencias de Android.
- Permite evolucionar la UI sin romper logica de negocio.
- Mejora mantenibilidad y escalabilidad para nuevas features.

## Persistencia elegida: Room (sobre SQLite)

Se eligio **Room** como capa de persistencia principal.

### Justificacion tecnica

- **Integracion nativa Android**: trabaja bien con coroutines, Flow y Paging 3.
- **Seguridad de tipos** en queries y entidades, reduciendo errores comunes de SQLite crudo.
- **Relaciones y transacciones** robustas para entidades compuestas (`Product`, tags, imagenes, reviews, catalog entries, remote keys, cart items).
- **Escenario offline-first**: permite mostrar datos cacheados cuando falla la red.

### Estrategia para mitigar perdida de datos

El proyecto aplica varias medidas:

1. **Escrituras transaccionales** con `database.withTransaction` en operaciones criticas para evitar estados parciales.
2. **Modelo cacheado local** con Room para catalogo y detalle, desacoplando lectura de la disponibilidad inmediata de red.
3. **Paginacion con `RemoteMediator` + remote keys** para sincronizacion incremental y reintentos controlados.
4. **Fallback remoto -> local** en busquedas/listados cuando hay error de red.
5. **Manejo explicito de errores** (`safeApiCall`, `safeDatabaseCall`) para mapear fallos de red/BD y no perder flujo funcional.
6. **Validacion de conectividad** antes de llamadas remotas en `DefaultProductRemoteDataSource` para evitar operaciones inutiles y respuestas inconsistentes.

## Requisitos tecnicos

- Android Studio reciente (recomendado: version con soporte AGP/Gradle actuales).
- JDK 11.
- Android SDK con `compileSdk 36`.
- Dispositivo fisico o emulador Android para pruebas instrumentadas.

## Como ejecutar la app

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Deja que Gradle sincronice dependencias.
4. Ejecuta el modulo `app` en un emulador/dispositivo.

Tambien puedes validar compilacion por terminal:

```bash
./gradlew :app:assembleDebug
```

En Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Como correr la suite de pruebas

### Pruebas unitarias (JVM)

Comando solicitado:

```bash
./gradlew test
```

Opcional (solo modulo app, variante debug):

```bash
./gradlew :app:testDebugUnitTest
```

En Windows PowerShell:

```powershell
.\gradlew.bat test
.\gradlew.bat :app:testDebugUnitTest
```

### Pruebas instrumentadas (Android)

Requieren emulador/dispositivo encendido:

```bash
./gradlew :app:connectedDebugAndroidTest
```

En Windows PowerShell:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

## Supuestos tecnicos y limitaciones

- La API remota usada es `dummyjson.com`; es un backend de demostracion, no productivo.
- No hay autenticacion/usuarios; el carrito se maneja localmente.
- El cache local prioriza disponibilidad y resiliencia; no implementa sync bidireccional compleja.
- No se incluye (por ahora) encriptacion de base de datos local.
- `CACHE_TIMEOUT_MILLIS` en paginacion es fijo y puede requerir ajuste segun reglas de negocio.
- El estado de conectividad se valida con capacidades de red del sistema; en redes inestables puede haber cambios rapidos de estado entre chequeos.

## Estructura resumida

```text
app/src/main/java/com/afsoftwaresolutions/intercommerce/
  presentation/
  domain/
  data/
  di/
  core/
```

## Notas finales

El enfoque actual prioriza:

- robustez de dominio (calculos monetarios y reglas de stock),
- persistencia confiable con Room,
- y experiencia usable aun sin conexion estable.

