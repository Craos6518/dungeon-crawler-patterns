# Backlog del Proyecto Dungeon Crawler

Fecha de corte: 18 de marzo de 2026

## Completados

- Arquitectura base del proyecto implementada y ejecutable en consola.
- Patrones creacionales implementados: Factory Method, Builder, Abstract Factory.
- Patrones estructurales implementados: Composite, Decorator, Facade.
- Patrones de comportamiento implementados como módulos: Command, Strategy, Observer, State, Memento.
- Integración principal de combate funcionando en modo interactivo con Command, Strategy, Observer y Decorator.
- Sistema de inventario jerárquico operativo con items simples y contenedores.
- Flujo de exploración, combate, victoria/derrota y menú principal funcional.
- Guardado/carga por memento con persistencia en disco y checkpoint automático disponible.
- Flujo principal con estado de juego integrado mediante `GameStateContext` (adaptador de estados de flujo en runtime).
- Orquestación runtime del juego principal implementada con estados de ejecución:
  - `MenuRuntimeState`
  - `SetupRuntimeState`
  - `AdventureRuntimeState`
  - Loop principal delegando en `GameStateContext`.
  - Contrato de coordinación explícito vía `GameRuntimeCoordinator`.
- Restauración de sesión de juego ampliada al cargar partida:
  - Reconstrucción de héroe por clase y vida.
  - Restauración de tema de mazmorra, sala actual, oro y enemigos derrotados.
  - Restauración de inventario serializado.
- Sistema de consumibles en combate ampliado:
  - Pociones y antídoto disponibles desde menú de objetos en combate.
  - Efecto de veneno al héroe con limpieza por antídoto.
  - Defensa ahora mitiga daño del siguiente golpe enemigo.
- Eventos de combate inicial corregidos para evitar logs con valores nulos en `CombatLogger`.
- Demos ejecutables desde clases compiladas:
  - `game.Main`
  - `game.demo.IntegracionCompletaDemo`
- Implementación de interfaces clave completa a nivel de contratos y clases concretas:
  - `PersonajeFactory` y sus fábricas concretas.
  - `DungeonBuilder` y su implementación concreta.
  - `DungeonThemeFactory` y temas concretos.
  - `Command` y comandos concretos.
  - `AIStrategy` y estrategias concretas.
  - `GameObserver` y observers concretos.
  - `GameState` y estados concretos.
- Integración del patrón State en producción:
  - La orquestación runtime principal usa estados de ejecución reales (`MenuRuntimeState`, `SetupRuntimeState`, `AdventureRuntimeState`).
  - Las clases de estado legacy (`MenuState`, `ExplorationState`, `CombatState`, `InventoryState`, `GameOverState`) disponibles para demostración académica y pruebas del patrón.
  - Desacoplamiento mediante `GameRuntimeCoordinator` para que RuntimeStates no dependan directamente de `InteractiveGame`.
  - `GameStateContext` gestiona transiciones y callbacks (onEnter/onExit) correctamente.
  - Demo académica ejecutable: `game.demo.LegacyStatePatternDemo` (muestra patrón State clásico con legacy states).
  - Implementación verificada con Java 17 y Maven compilación limpia.
  - Arquitectura de dos niveles: Production RuntimeStates + Academic LegacyStates para docencia.
- Sistema de IA de enemigos mejorado:
  - Estrategias disponibles: Agresiva, Defensiva, Inteligente, Aleatoria.
  - Nuevo controlador `AdaptiveAIController` cambia dinámicamente de estrategia según vida:
    - Vida > 75%: Estrategia Agresiva
    - 50% <= Vida <= 75%: Estrategia Inteligente (análisis táctico)
    - 25% <= Vida < 50%: Estrategia Defensiva (supervivencia)
    - Vida < 25%: Inteligente (atacar enemigos débiles para terminar rápido)
  - Profundidad táctica completa con análisis de múltiples factores
  - Historial de combate para toma de decisiones inteligentes
- Configuración automatizada de JAVA_HOME:
  - Script `setup-java.sh` detecta y configura Java 17 automáticamente
  - Archivo `.envrc` para direnv (configuración permanente)
  - Guía completa en `GUIA_COMPILACION_PRUEBAS.md`
  - Pruebas automatizadas ahora ejecutables sin configuración manual
  - Compilación limpia verificada con Maven 3.6.0+
- **Orquestación total por estados concretos de dominio:**
  - `DomainGameState` - Interfaz base para estados de dominio reutilizables
  - `AbstractDomainGameState` - Clase base con inyección de dependencias
  - `GameSessionData` - Encapsulación del estado compartido de la sesión
  - `ExplorationDomainState` - Lógica de exploración (250 líneas extraídas)
    - Métodos: explorarMazmorra(), avanzarSala(), buscarTesoro(), encontrarEnemigo()
    - Callbacks para combate y victoria
  - `CombatDomainState` - Lógica de combate completa (350 líneas extraídas)
    - Loop de turnos completo
    - Gestión de acciones del jugador y del enemigo
    - Efectos de veneno y defensa
  - `SetupDomainState` - Lógica de configuración inicial (150 líneas extraídas)
    - Selección de héroe, tema y construcción de mazmorra
  - `EndGameDomainState` - Lógica de fin de juego
    - Opciones de game over (checkpoint, menú, nueva partida)
  - `RefactoredGameArchitecture` - Demostración de cómo usar los nuevos estados
  - Lógica extraída de `InteractiveGame` y completamente reutilizable
  - Independencia de Scanner y otras dependencias de UI
  - Preparado para futuro motor 2D o cualquier otra interfaz
  - **Hardening de eventos del sistema ante datos nulos o inconsistentes:**
  - Estandarización de contrato de eventos en todos los emisores (IntegratedCombatEngine, InteractiveGame, CombatDomainState)
  - Consolidación de 5 eventos críticos: ATAQUE_REALIZADO, EFECTO_APLICADO, ESTADO_CAMBIADO, ACCION_REALIZADA, COMBATE_INICIADO
  - Creación de `EventContract.java` con constantes de claves de eventos
  - Documentación completa en `EVENTO_CONTRATO_REFERENCIA.md` con contrato de cada tipo de evento
  - Validación de valor nulo en todos los emisores antes de agregarDato()
  - Eliminación de claves redundantes y estructuras incompatibles
  - Adición de información de contexto faltante (vidaRestante, ronda) en eventos de combate
  - Compilación exitosa sin errores críticos

- **Definir criterios de cierre por épica funcional:**
  - ✅ Checklist de aceptación para demo académica (10 patrones + demos + tests)
  - ✅ Checklist de aceptación para experiencia interactiva (flujo end-to-end, validación integración)
  - ✅ Checklist de aceptación para trazabilidad GDD-código (matriz completa requisitos → implementación)
  - ✅ Documento: `CRITERIOS_CIERRE_EPICAS.md` con 6 épicas y criterios detallados
  - ✅ Validación de cumplimiento: Verde ✅ - Listo para presentación académica
  - ✅ Definición de "Listo para Presentar" con estados Verde/Ámbar/Rojo

## No Completados

- Automatización CI/CD para validación continua:
  - Agregar workflow de compilación y tests en cada push/PR.
- Integración opcional de nuevos estados de dominio en InteractiveGame principal:
  - Los estados están listos y compilables, pero InteractiveGame original sigue siendo funcional
  - Refactorización completa de InteractiveGame es trabajo futuro (ver RefactoredGameArchitecture.java para referencia)

## Siguiente Foco Recomendado

1. **COMPLETADO:** Consolidar una única máquina de estados de gameplay con estados concretos de producción
2. **COMPLETADO:** Estandarizar contrato de eventos entre `InteractiveGame` e `IntegratedCombatEngine`
3. **COMPLETADO:** Definir checklist de aceptación final para entrega académica (CRITERIOS_CIERRE_EPICAS.md)
4. Añadir pipeline de CI con Java 17 + Maven test (GitHub Actions workflow)
5. **Opcionalmente:** Integrar completamente los new DomainGameStates en InteractiveGame reemplazando la lógica procedimental
6. **Opcionalmente:** Expandir a interfaz gráfica 2D (libGDX o JavaFX)