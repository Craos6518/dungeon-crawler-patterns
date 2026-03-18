# Backlog del Proyecto Dungeon Crawler

Fecha de corte: 17 de marzo de 2026

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

## Parcialmente Completados

- Integración del patrón State en producción:
  - La orquestación runtime principal ya usa estados de ejecución reales.
  - Las clases de estado legacy (`MenuState`, `ExplorationState`, etc.) permanecen para demostración académica y pruebas del patrón.
- IA de enemigos:
  - Estrategias disponibles y cambio dinámico funcional.
  - La profundidad táctica en el loop interactivo es básica (reglas simples por umbrales de vida).
- Pruebas automatizadas en este entorno:
  - Existen reportes de pruebas exitosas en `target/surefire-reports`.
  - La ejecución local con Maven depende de configurar `JAVA_HOME` correctamente.

## No Completados

- Orquestación total por estados concretos de dominio:
  - Reducir más lógica procedimental interna de `InteractiveGame` trasladando segmentos de exploración/combate a estados específicos de dominio reutilizables por el futuro motor 2D.
- Endurecer demo integrada ante datos nulos o inconsistentes en eventos:
  - Revisar también los eventos emitidos por `IntegratedCombatEngine` para mantener contrato consistente de claves entre todos los emisores.
- Automatización CI/CD para validación continua:
  - Agregar workflow de compilación y tests en cada push/PR.
- Definir criterios de cierre por épica funcional:
  - Checklist de aceptación para demo académica, experiencia interactiva y trazabilidad GDD-código.

## Siguiente Foco Recomendado

1. Consolidar una única máquina de estados de gameplay con estados concretos de producción.
2. Estandarizar contrato de eventos entre `InteractiveGame` e `IntegratedCombatEngine`.
3. Añadir pipeline de CI con Java 17 + Maven test.
4. Definir checklist de aceptación final para entrega académica.