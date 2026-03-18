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

- Integración del patrón State en la experiencia principal:
  - Los estados existen y compilan, pero el loop principal en `InteractiveGame` no usa `GameStateContext` como orquestador central.
  - Parte del comportamiento de los estados sigue en modo demostración/simulación.
- Guardado/carga de partida en modo interactivo:
  - Se restaura estado base (vida, sala, datos del originator).
  - No se reconstruye completamente toda la sesión en curso (contexto completo de combate, inventario profundo, etc.).
- IA de enemigos:
  - Estrategias disponibles y cambio dinámico funcional.
  - La profundidad táctica en el loop interactivo es básica (reglas simples por umbrales de vida).
- Sistema de items consumibles en combate:
  - Curación con pociones funcional.
  - Otros consumibles como antídoto están declarados, pero con uso limitado en combate.
- Pruebas automatizadas en este entorno:
  - Existen reportes de pruebas exitosas en `target/surefire-reports`.
  - La ejecución local con Maven depende de configurar `JAVA_HOME` correctamente.

## No Completados

- Orquestación total del juego por State Pattern en producción:
  - Migrar el flujo principal interactivo para que dependa de `GameStateContext` y estados concretos de punta a punta.
- Restauración integral de sesión al cargar partida:
  - Recuperar estado completo de juego (estado actual, inventario completo, contexto de combate/exploración y progreso contextual).
- Completar uso de consumibles avanzados en combate:
  - Implementar efectos de antídoto y otros objetos más allá de la curación básica.
- Endurecer demo integrada ante datos nulos o inconsistentes en eventos:
  - Revisar emisión de metadatos en inicio de combate para evitar salidas ambiguas en logs.
- Automatización CI/CD para validación continua:
  - Agregar workflow de compilación y tests en cada push/PR.
- Definir criterios de cierre por épica funcional:
  - Checklist de aceptación para demo académica, experiencia interactiva y trazabilidad GDD-código.

## Siguiente Foco Recomendado

1. Integrar `GameStateContext` al flujo real de `InteractiveGame`.
2. Completar restauración integral de partida usando Memento.
3. Cerrar brechas de gameplay (antídoto/consumibles avanzados).
4. Añadir pipeline de CI con Java 17 + Maven test.