# Patron State en Runtime

- Fecha de revision: 2026-04-13
- Rama auditada: Refactorizacion
- Estado: vigente

## Problema real que resuelve
El runtime necesita transiciones de pantalla coherentes (`menu`, `hero`, `exploration`, `combat`, `treasure`, etc.) sin depender de strings mutables dispersos.

## Clases principales (rutas reales)
- `src/main/java/game/state/game/GameStateContext.java`
- `src/main/java/game/state/game/GameState.java`
- `src/main/java/game/state/game/MenuState.java`
- `src/main/java/game/state/game/CombatState.java`
- `src/main/java/game/state/game/ExplorationState.java`
- `src/main/java/game/state/game/InventoryState.java`
- `src/main/java/game/state/game/GameOverState.java`
- `src/main/java/game/state/game/runtime/MenuRuntimeState.java`
- `src/main/java/game/state/game/runtime/AdventureRuntimeState.java`
- `src/main/java/game/state/game/runtime/SetupRuntimeState.java`
- `src/main/java/game/state/game/runtime/GameRuntimeCoordinator.java`
- `src/main/java/game/application/state/GameFlowState.java`
- `src/main/java/game/application/state/GameSession.java` (usa `GameStateContext` via `transitionTo`)
- `src/main/java/game/application/runtime/GameRuntime.java` (dispara transiciones por comando)

## Conexion con runtime productivo
- `GameRuntime` procesa comandos y llama `session.transitionTo(...)`.
- `GameSession` delega transicion en `GameStateContext`.
- `activeScreen` se sincroniza desde el estado del contexto; no conduce el flujo.

## Test de validacion en runtime real
- `src/test/java/game/unit/behavioral/StatePatternTest.java`
- `src/test/java/game/integration/behavioral/GameRuntimeStateFlowIntegrationTest.java`

## Diagrama expandido
```mermaid
classDiagram
    class GameState {
        <<interface>>
    }
    GameState <|.. MenuState
    GameState <|.. CombatState
    GameState <|.. ExplorationState
    GameState <|.. InventoryState
    GameState <|.. GameOverState
    GameState <|.. MenuRuntimeState
    GameState <|.. AdventureRuntimeState
    GameState <|.. SetupRuntimeState
    GameStateContext --> GameState : estadoActual
    MenuRuntimeState --> GameRuntimeCoordinator
    AdventureRuntimeState --> GameRuntimeCoordinator
    SetupRuntimeState --> GameRuntimeCoordinator
    GameRuntime --> GameSession : transitionTo()
    GameSession --> GameFlowState : activeState()
```
