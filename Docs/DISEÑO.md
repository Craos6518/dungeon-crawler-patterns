# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/02-architecture/ARQUITECTURA_RUNTIME.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# Diseño Técnico y Patrones de Diseño

## 1. Visión General de la Arquitectura
El proyecto integra **11 patrones de diseño** base Gang of Four (GoF) para crear un sistema modular, extensible y testeable.

### Capas del Sistema
- **game.domain**: Modelado de personajes y lógica base.
- **game.combat**: Motor de combate y fachada.
- **game.dungeon**: Construcción y tematización de mazmorras.
- **game.command**: Encapsulación de acciones.
- **game.ai**: Estrategias de inteligencia artificial.
- **game.items**: Gestión de inventario jerárquico.
- **game.effects**: Decoradores para estados alterados.
- **game.events**: Sistema de notificación desacoplado.
- **game.state**: Gestión de estados globales del juego.
- **game.persistence**: Sistema de guardado y mementos.

## 2. Implementación de Patrones

### Patrones Creacionales
- **Factory Method**: Creación de `Personaje` mediante `PersonajeFactory` (`GuerreroFactory`, `MagoFactory`, etc.).
- **Builder**: Construcción de `Dungeon` paso a paso mediante `DungeonBuilder` y `DungeonDirector`.
- **Abstract Factory**: Creación de familias de enemigos y tesoros temáticos mediante `DungeonThemeFactory` (Fuego, Hielo, Oscuridad, Veneno).

### Patrones Estructurales
- **Composite**: El inventario usa la interfaz `Item` y la clase base `ItemComponent`, permitiendo que `ContainerItem` contenga tanto `SimpleItem` como otros contenedores.
- **Decorator**: `CharacterDecorator` y sus subclases (`PoisonEffect`, `BurnEffect`, `StrengthEffect`, `StunEffect`) envuelven a `Personaje` para modificar su comportamiento dinámicamente.
- **Facade**: `CombatFacade` y `PersistenceFacade` simplifican la interacción con subsistemas complejos (combate y guardado).

### Patrones de Comportamiento
- **Command**: Acciones como `AttackCommand`, `DefendCommand`, `UseItemCommand` y `SkillCommand` se ejecutan mediante un `CommandInvoker` que mantiene el historial.
- **Strategy**: Los enemigos usan `AIStrategy` (`AggressiveStrategy`, `DefensiveStrategy`, `IntelligentStrategy`, `RandomStrategy`, `AdaptiveAIController`) para decidir sus acciones.
- **Observer**: `EventManager` (Singleton) permite que `CombatLogger`, `StatisticsTracker` y `UINotifier` reaccionen a eventos sin acoplamiento.
- **State**: `GameStateContext` gestiona la transición entre `MenuState`, `ExplorationState`, `CombatState`, `InventoryState` y `GameOverState`.
- **Memento**: `GameMemento` captura el estado de `GameOriginator`, gestionado por `GameCaretaker` para persistencia.

## 3. Integración de Patrones (IntegratedCombatEngine)
El sistema de combate es el "Hub" donde convergen la mayoría de los patrones:
1. **Command** encapsula la acción del turno.
2. **Strategy** decide la acción del enemigo.
3. **Observer** notifica los resultados a los loggers.
4. **Decorator** aplica efectos de estado al inicio/fin del turno.
5. **Facade** coordina todo el flujo del motor.

## 4. Plan de Refactorización y Mejora
- **Domain States**: Migración hacia una arquitectura basada en estados de dominio para mayor desacoplamiento en `InteractiveGame`.
- **Hardening de Eventos**: Estandarización de las claves y valores en el sistema de `GameEvent` para evitar nulidades e inconsistencias.
