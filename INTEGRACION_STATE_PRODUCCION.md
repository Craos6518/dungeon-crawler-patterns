# Integración del Patrón State en Producción

**Fecha de actualización:** 18 de marzo de 2026

## Resumen

El patrón State está **completamente integrado en producción** con dos niveles claramente separados:

1. **RuntimeStates** (Producción): Orquestación real del juego en `InteractiveGame`
2. **Legacy States** (Académica): Demostración del patrón para fines educativos

## 1. RuntimeStates - Implementación de Producción

### Ubicación
- Paquete: `game.state.game.runtime`
- Clases:
  - `MenuRuntimeState.java`
  - `SetupRuntimeState.java`
  - `AdventureRuntimeState.java`

### Responsabilidades

| Estado | Responsabilidad |
|--------|-----------------|
| **MenuRuntimeState** | Mostrar menú principal, procesar selección de opciones, navegar a Setup/Adventure/Estadísticas |
| **SetupRuntimeState** | Configurar nueva partida (elegir héroe, tema, construir mazmorra) |
| **AdventureRuntimeState** | Ejecutar la aventura real (exploración + combate), manejar victoria/derrota |

### Flujo de Ejecución

```
InteractiveGame.main()
    ↓
InteractiveGame.iniciar()
    ├─ Crear MenuRuntimeState
    └─ Loop: runtimeContext.actualizar()
        ↓
    MenuRuntimeState.actualizar()
        ├─ Opción 1 (Nueva Partida)
        │   └─ Transición: SetupRuntimeState
        ├─ Opción 2 (Cargar Partida)
        │   └─ Transición: AdventureRuntimeState
        └─ Opción 4 (Salir)
            └─ Detener juego
    
    SetupRuntimeState.actualizar()
        └─ coordinator.configurarNuevaPartidaRuntime()
        ├─ OK → Transición: AdventureRuntimeState
        └─ Error → Transición: MenuRuntimeState
    
    AdventureRuntimeState.actualizar()
        └─ coordinator.ejecutarAventuraRuntime()
        ├─ Nueva partida → Transición: SetupRuntimeState
        ├─ Reanudar → Transición: AdventureRuntimeState
        └─ Fin juego → Transición: MenuRuntimeState
```

### Contrato: GameRuntimeCoordinator

El desacoplamiento se logra mediante `GameRuntimeCoordinator`:

```java
public interface GameRuntimeCoordinator {
    void cambiarEstadoRuntime(GameState nuevoEstado);
    void cambiarEstadoFlujoRuntime(String nombreFlojo);
    int leerOpcionMenuPrincipal();
    boolean configurarNuevaPartidaRuntime();
    boolean cargarPartidaDesdeMenuRuntime();
    void ejecutarAventuraRuntime();
    boolean estaJuegoActivoRuntime();
    boolean consumirSolicitudNuevaPartida();
    boolean consumirSolicitudReanudarExploracion();
    void mostrarEstadisticasRuntime();
    void detenerJuegoRuntime();
}
```

**Beneficio:** Los RuntimeStates NO dependen de `InteractiveGame` directamente, solo del contrato `GameRuntimeCoordinator`.

## 2. Legacy States - Implementación Académica

### Ubicación
- Paquete: `game.state.game`
- Clases:
  - `MenuState.java`
  - `ExplorationState.java`
  - `CombatState.java`
  - `InventoryState.java`
  - `GameOverState.java`

### Propósito

Estos estados están implementados para:
- **Demostración educativa** del patrón State puro
- **Validación académica** de la correcta implementación del patrón
- **Pruebas unitarias** de comportamiento por estado
- **Referencia clara** de cómo el patrón elimina condicionales

### Flujo (Diseño Académico)

```
MenuState
    ↓
ExplorationState ←→ InventoryState
    ↓
CombatState
    ↓
GameOverState
    ↓
MenuState (loop)
```

### Demo de Legacy States

Ejecutar: `java game.demo.LegacyStatePatternDemo`

Esta demostración muestra:
- Cómo se usan los legacy states
- Las transiciones explícitas entre estados
- El desacoplamiento mediante GameStateContext
- La separación clara de responsabilidades

## 3. Separación de Responsabilidades

### GameStateContext
- **Ubicación:** `game.state.game.GameStateContext.java`
- **Responsabilidad:** Gobernar las transiciones (onExit → cambio → onEnter)
- **Usado por:** Ambos tipos de estados (RuntimeStates y Legacy States)

### Interfaz GameState
- **Ubicación:** `game.state.game.GameState.java`
- **Contrato:** `manejarEntrada()`, `actualizar()`, `render()`, `onEnter()`, `onExit()`, `getNombre()`
- **Implementado por:** MenuRuntimeState, SetupRuntimeState, AdventureRuntimeState, MenuState, ExplorationState, CombatState, InventoryState, GameOverState

## 4. Ejecución en Producción

### Punto de Entrada Principal
```
java game.InteractiveGame
```

**Qué sucede:**
1. `InteractiveGame.main()` se ejecuta
2. Crea una instancia de `InteractiveGame` que implementa `GameRuntimeCoordinator`
3. Llama a `iniciar()`
4. Crea `GameStateContext` con `MenuRuntimeState` inicial
5. Loop principal: `while(juegoActivo && runtimeContext.isEjecutando()) { runtimeContext.actualizar(); }`
6. Cada `actualizar()` delega al RuntimeState actual
7. Los RuntimeStates orquestan el juego real

### Ciclo de Estado Actual
- Inicio → MenuRuntimeState → SetupRuntimeState → AdventureRuntimeState → MenuRuntimeState → ...
- Persistencia: Memento (GameOriginator, GameCaretaker)
- Observadores: EventManager, CombatLogger, StatisticsTracker

## 5. Integración de Patrones

El patrón State se integra con otros patrones implementados:

| Patrón | Ubicación | Integración |
|--------|-----------|------------|
| **Command** | `game.command.actions` | AccionesAtaque, Defensa, DemoralizeAction usadas en combate |
| **Strategy** | `game.ai.strategy` | AIStrategy cambia dinámicamente durante combate |
| **Observer** | `game.events.observer` | EventManager notifica observadores en transiciones |
| **Memento** | `game.persistence.memento` | GameMemento captura estado para persistencia |
| **Factory** | `game.domain.personaje.factory` | Creación de personajes al configurar partida |
| **Builder** | `game.dungeon.builder` | Construcción de mazmorras en SetupRuntimeState |
| **Composite** | `game.items.model` | Inventario jerárquico (contenedores + items) |
| **Decorator** | `game.effects.status` | Efectos como veneno, defensa en combate |

## 6. Checklist de Completitud

- ✅ GameStateContext implementado y funcional
- ✅ Interfaz GameState define el contrato
- ✅ RuntimeStates (MenuRuntimeState, SetupRuntimeState, AdventureRuntimeState) implementados
- ✅ Legacy States (MenuState, ExplorationState, CombatState, InventoryState, GameOverState) implementados
- ✅ GameRuntimeCoordinator define el contrato de desacoplamiento
- ✅ InteractiveGame implementa GameRuntimeCoordinator
- ✅ Loop principal usa GameStateContext con RuntimeStates
- ✅ Transiciones de estado explícitas y controladas
- ✅ Callbacks onEnter/onExit funcionan correctamente
- ✅ Persistencia integrada (Memento)
- ✅ Observadores se notifican en cambios de estado
- ✅ Demo académica disponible (LegacyStatePatternDemo)
- ✅ Documentación clara de separación (este documento)

## 7. Próximas Mejoras Potenciales (No Requerido)

- State machine graph visualization
- Logging de transiciones detallado
- Validación de transiciones permitidas
- Refactorización de `explorarMazmorra()` a método independiente fuera del loop
- Tests unitarios para cada RuntimeState

## Conclusión

La **integración del patrón State en producción está COMPLETADA**:

1. **RuntimeStates** orquestan el flujo real del juego sin acoplar directamente código de negocio
2. **Legacy States** demuestran el patrón para fines académicos y validación
3. **Separación clara** mediante GameStateContext y GameRuntimeCoordinator
4. **Contrato explícito** que permite cambios futuros sin afectar otros componentes
5. **Documentación y demostración** son claras y accesibles

El proyecto está listo para entregas académicas y demostración de patrones de diseño.
