# Plan de Refactoring: Extracting Domain States from InteractiveGame

## ESTADO ACTUAL (HOY)

```
┌─────────────────────────────────────────────────────────────┐
│                      game.Main                              │
│                        (entry)                              │
└────────────────────────┬────────────────────────────────────┘
                         │
                         v
┌─────────────────────────────────────────────────────────────┐
│              InteractiveGame (1,400 líneas)                 │
│                implements GameRuntimeCoordinator            │
│                                                             │
│  ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓        │
│  ┃ RESPONSABILIDADES MONOLÍTICAS:                ┃        │
│  ┃ ✅ Orquestación (loop principal)              ┃        │
│  ┃ ✅ Exploración procedimental (250 líneas)     ┃        │
│  ┃ ✅ Combate procedimental (350 líneas)         ┃        │
│  ┃ ✅ Configuración inicial (150 líneas)         ┃        │
│  ┃ ✅ Persistencia (200 líneas)                  ┃        │
│  ┃ ✅ UI/Entrada Scanner (150 líneas)            ┃        │
│  ┃ ✅ Victoria/Derrota (100 líneas)              ┃        │
│  ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛        │
│                                                             │
│  // Métodos principales (LÓGICA PROCEDIMENTAL PURA)       │
│  private void explorarMazmorra() { ... }                  │
│  private void iniciarCombate(Personaje enemigo) { ... }   │
│  private void avanzarSala() { ... }                       │
│  private void buscarTesoro() { ... }                      │
│  private void usarConsumibleEnCombate() { ... }           │
│  private void victoria() { ... }                          │
│  private void derrota() { ... }                           │
│                                                             │
│  // RuntimeStates - solo DELEGADORES VACÍOS               │
│  private GameStateContext runtimeContext;                │
│                                                             │
│  // Dependencias de PATRÓN (24+ componentes)              │
│  private PersonajeFactory ...                            │
│  private DungeonBuilder ...                              │
│  private CommandInvoker ...                              │
│  private AIController ...                                │
│  private EventManager ...                                │
│  private GameCaretaker ...                               │
└─────────────────────────────────────────────────────────────┘
                    ↓     ↓     ↓
    ┌───────────────┴─────┴─────┴────────────────┐
    │                                             │
    v                                             v
┌─────────────────────────────┐    ┌──────────────────────────┐
│ MenuRuntimeState            │    │ SetupRuntimeState        │
│ [45 líneas - VACÍO]         │    │ [40 líneas - VACÍO]      │
│                             │    │                          │
│ actualizar() {              │    │ actualizar() {           │
│   coordinator.leerOpcion()  │    │   coordinador.config... ()│
│   switch(opcion) {          │    │   → changeState()        │
│     case 1 →                │    │ }                        │
│       new SetupRuntimeState │    │                          │
│   }                         │    │ // MÁS SOLO DELEGACIÓN   │
│ }                           │    │                          │
│                             │    │                          │
│ // DELEGADOR PURO           │    │ // DELEGADOR PURO        │
└─────────────────────────────┘    └──────────────────────────┘
                                             ↓
                                    ┌──────────────────────────┐
                                    │ AdventureRuntimeState    │
                                    │ [35 líneas - CASI VACÍO] │
                                    │                          │
                                    │ actualizar() {           │
                                    │   coordinador.ejecutar   │
                                    │   Aventura()             │
                                    │   // 1 LÍNEA ÚTIL        │
                                    │ }                        │
                                    │                          │
                                    │ // PROBLEMA:             │
                                    │ // Esto llama a:         │
                                    │ InteractiveGame.         │
                                    │ ejecutarAventuraRuntime()│
                                    │ que llama explorarMaz..()│
                                    │ que es 70 línea de while │
                                    │                          │
                                    │ ❌ TODO está acá abajo   │
                                    │ ❌ Nada en estado        │
                                    └──────────────────────────┘

PROBLEMA: Toda la lógica de dominio está en InteractiveGame
SOLUCIÓN: Extraer ExplorationDomainState, CombatDomainState, etc.
```

---

## ESTADO DESEADO (DESPUÉS DE REFACTORING)

```
┌─────────────────────────────────────────────────────────────┐
│                    game.Main                                │
│                     (entry)                                 │
└────────────────────────┬────────────────────────────────────┘
                         │
                         v
┌─────────────────────────────────────────────────────────────┐
│         InteractiveGame (600 líneas max)                    │
│    [SOLO ORQUESTACIÓN + ENTRADA/SALIDA UI]                 │
│                                                             │
│  ResponSabilidades REDUCIDAS:                              │
│  ✅ Loop principal                                          │
│  ✅ Scanner → InteractiveGame                              │
│  ✅ InteractiveGame → Estados de dominio                   │
│  ✅ Estados de dominio → UI output                         │
│  ✅ Persistencia (delegada a GameCaretaker)                │
│                                                             │
│  // Ahora es un ADAPTADOR CONSOLA, no sistema completo    │
│                                                             │
│  public class InteractiveGame {                           │
│      private GameStateContext runtimeContext;             │
│      private Scanner scanner;                             │
│      private ExplorationDomainState exploration;          │
│      private CombatDomainState combat;                    │
│                                                             │
│      void iniciar() {                                     │
│          runtimeContext = new GameStateContext(           │
│              new MenuRuntimeState(this));                 │
│          while (runtimeContext.isEjecutando()) {          │
│              runtimeContext.actualizar();                 │
│          }                                                │
│      }                                                    │
│  }                                                        │
└─────────────────────────────────────────────────────────────┘
        ↓       ↓       ↓       ↓
        │       │       │       └────────────┐
        │       │       │                    │
        v       v       v                    v
        ┌───────────────────────┐      ┌──────────────────┐
        │ RuntimeStates (thin)  │      │ Domain States    │
        │                       │      │ (NEW - gorda)    │
        └───────────────────────┘      └──────────────────┘

RUNTIME STATES (Orquestación - como hoy):
┌─────────────────────────────────────────────────────────────┐
│ MenuRuntimeState          SetupRuntimeState AdventureRuntimeState│
│ Menú principal            Preparación       Lanza aventura       │
│ → Delegador puro          → Delegador puro  → Delegador puro     │
│                                                                  │
│ Responsabilidad: cambiarEstado()                              │
│ Lógica: NULA (solo transiciones)                              │
└─────────────────────────────────────────────────────────────┘

DOMAIN STATES (Lógica - NUEVOS):
┌─────────────────────────────────────────────────────────────┐
│                                                              │
│  ┌─────────────────────────────────────────┐               │
│  │ ExplorationDomainState (250 líneas)     │               │
│  ├─────────────────────────────────────────┤               │
│  │ // Campos                               │               │
│  │ private Personaje heroe                │               │
│  │ private Dungeon mazmorra               │               │
│  │ private DungeonThemeFactory tema       │               │
│  │ private int salaActual                 │               │
│  │ private Random random                  │               │
│  │                                         │               │
│  │ // Métodos de dominio PUR               │               │
│  │ public void avanzarSala()              │               │
│  │ public void buscarTesoro()             │               │
│  │ public Personaje crearEnemigo()        │               │
│  │ public boolean verificarFinal()        │               │
│  │                                         │               │
│  │ // Callbacks para UI                   │               │
│  │ public Consumer<String> onMostrarUI    │               │
│  │ public Supplier<Integer> onLeerOpcion  │               │
│  │                                         │               │
│  │ // Independiente de Scanner            │               │
│  │ // Testeabl aisladamente               │               │
│  │ // REUTILIZABLE en motor 2D            │               │
│  └─────────────────────────────────────────┘               │
│                    ↓                                        │
│  ┌─────────────────────────────────────────┐               │
│  │ CombatDomainState (350 líneas)          │               │
│  ├─────────────────────────────────────────┤               │
│  │ // Campos                               │               │
│  │ private Personaje heroe                │               │
│  │ private Personaje enemigo              │               │
│  │ private AIController enemyAI           │               │
│  │ private int turno                      │               │
│  │ private boolean defensaActiva          │               │
│  │ private int turnosVeneno, danioVeneno  │               │
│  │                                         │               │
│  │ // Métodos de dominio PUR              │               │
│  │ public void procesarAccionHeroe(...)   │               │
│  │ public void ejecutarTurnoEnemigo()     │               │
│  │ public void aplicarEfectos()           │               │
│  │ public void actualizarEstrategiaIA()   │               │
│  │ public boolean estaFinalizado()        │               │
│  │ public Personaje getGanador()          │               │
│  │                                         │               │
│  │ // Callbacks para UI                   │               │
│  │ public Consumer<String> onMostrarUI    │               │
│  │ public Supplier<Integer> onLeerAccion  │               │
│  │                                         │               │
│  │ // Independiente de Scanner            │               │
│  │ // TODO encapsulado aquí               │               │
│  │ // REUTILIZABLE en motor 2D            │               │
│  └─────────────────────────────────────────┘               │
│                    ↓                                        │
│  ┌─────────────────────────────────────────┐               │
│  │ EndGameDomainState (100 líneas)         │               │
│  ├─────────────────────────────────────────┤               │
│  │ enum TipoFinal { VICTORIA, DERROTA }   │               │
│  │                                         │               │
│  │ public void mostrarResultado(...)      │               │
│  │ public Opcion procesarSeleccion()      │               │
│  │   → NUEVA_PARTIDA, MENU, CARGAR        │               │
│  │                                         │               │
│  │ // Encapsula toda lógica final         │               │
│  │ // REUTILIZABLE en motor 2D            │               │
│  └─────────────────────────────────────────┘               │
│                                                              │
└─────────────────────────────────────────────────────────────┘

```

---

## MAPA DE REFACTORING (PASO A PASO)

### PASO 0: Crear interfaz base (día 1)
```java
public interface DomainGameState extends GameState {
    // Métodos que todo estado de dominio DEBE tener
    default void onUIOutput(String mensaje) {}
    default int onUIInput(int min, int max) { return 0; }
}
```

### PASO 1: Extraer ExplorationDomainState (1-2 días)

**De InteractiveGame, extraer:**
```java
// LÍNEA 380-450: explorarMazmorra() + helpers
private void explorarMazmorra() { ... }      // 70 líneas
private void avanzarSala() { ... }           // 15 líneas
private void buscarTesoro() { ... }          // 25 líneas
private void encontrarEnemigo() { ... }      // 30 líneas
private SimpleItem buscarConsumiblePorNombre() { ... }
private String normalizarTexto() { ... }
```

**Crear nuevo archivo:**
```java
// src/main/java/game/state/game/domain/ExplorationDomainState.java
public class ExplorationDomainState implements DomainGameState {
    private final Personaje heroe;
    private final Dungeon mazmorra;
    private final DungeonThemeFactory tema;
    private int salaActual;
    private final Random random;
    
    // Métodos públicos (antes privados en InteractiveGame)
    public void avanzarSala() { ... }
    public void buscarTesoro() { ... }
    public Personaje crearEnemigo(boolean esJefe) { ... }
    public boolean verificarFinMazmorra() { ... }
    
    // Callbacks para que InteractiveGame maneje UI
    private Consumer<String> mostrarUI = System.out::println;
    private Supplier<Integer> leerOpcion = () -> 0;
}
```

**Impacto en InteractiveGame:**
```diff
- 250 líneas
+ 10 líneas (solo instanciación)
```

### PASO 2: Extraer CombatDomainState (2-3 días)

**De InteractiveGame, extraer:**
```java
// LÍNEA 510-700: iniciarCombate() + helpers
private void iniciarCombate(Personaje enemigo, boolean esJefe) { ... }  // 190 líneas
private void usarConsumibleEnCombate() { ... }
private void usarPocion() { ... }
private void usarAntidoto() { ... }
private void aplicarVenenoHeroeInicioTurno() { ... }
private void aplicarVenenoPorAtaqueEnemigo() { ... }
private void actualizarEstrategiaEnemiga() { ... }
private void mostrarVistaDebugIA() { ... }
```

**Crear nuevo archivo:**
```java
// src/main/java/game/state/game/domain/CombatDomainState.java
public class CombatDomainState implements DomainGameState {
    private final Personaje heroe;
    private final Personaje enemigo;
    private final AIController enemyAI;
    private int turno;
    
    // Estado procedimental (antes en InteractiveGame)
    private boolean defensaHeroeActiva;
    private int turnosVenenoHeroe;
    private int danioVenenoHeroe;
    
    // Métodos públicos
    public void procesarAccionHeroe(TipoAccion accion) { ... }
    public void ejecutarTurnoEnemigo() { ... }
    public void aplicarEfectosInicio() { ... }
    public void actualizarEstrategia() { ... }
    public boolean estaFinalizado() { ... }
    public Personaje getGanador() { ... }
}
```

**Impacto en InteractiveGame:**
```diff
- 350 líneas
+ 15 líneas (solo instanciación y callbacks UI)
```

### PASO 3: Extraer EndGameDomainState (0.5 días)

**De InteractiveGame, extraer:**
```java
// LÍNEA 709-754
private void victoria() { ... }
private void derrota() { ... }
private void mostrarOpcionesGameOver() { ... }
private boolean mostrarOpcionesGameOver() { ... }
```

**Crear:**
```java
public class EndGameDomainState implements DomainGameState {
    enum TipoFinal { VICTORIA, DERROTA }
    
    public void mostrarResultado(Personaje heroe, GameStats stats) { ... }
    public Opcion procesarSeleccion() { ... }
}
```

**Impacto:**
```diff
- 100 líneas
+ 5 líneas
```

### PASO 4: Refactor SetupRuntimeState → SetupDomainState (opcional)

**Actual:** SetupRuntimeState está vacío, delegador puro
**Opción:** Renombrarlo y darle lógica

```java
public class SetupDomainState implements DomainGameState {
    // Métodos
    public Personaje elegirHeroe() { ... }
    public DungeonThemeFactory elegirTema() { ... }
    public Dungeon construirMazmorra() { ... }
    public ContainerItem crearInventarioInicial() { ... }
}
```

---

## MÉTRICAS DE REFACTORING

### Antes (Actual)
```
InteractiveGame:         1,400 líneas
├─ Orquestación:          200 líneas (14%)
├─ Lógica exploración:    250 líneas (18%)
├─ Lógica combate:        350 líneas (25%)
├─ Config:                150 líneas (11%)
├─ Persistencia:          200 líneas (14%)
├─ UI/Entrada:            150 líneas (11%)
└─ Victoria/Derrota:      100 líneas (7%)

ExplorationDomainState: NO EXISTE
CombatDomainState:      NO EXISTE
EndGameDomainState:     NO EXISTE

TOTAL LÓGICA DE DOMINIO EN ESTADOS: 0 líneas
TOTAL LÓGICA PROCEDIMENTAL EN MONOLITO: 750 líneas
REUTILIZABILIDAD PARA MOTOR 2D: 0%
```

### Después (Deseado)
```
InteractiveGame:         600 líneas
├─ Orquestación:         200 líneas (33%)
├─ Main loop:             70 líneas (12%)
├─ UI adapter:           150 líneas (25%)
├─ Entrada/Salida:       100 líneas (17%)
└─ Hooks para modales:    80 líneas (13%)

ExplorationDomainState: 250 líneas ✅
CombatDomainState:      350 líneas ✅
EndGameDomainState:     100 líneas ✅
SetupDomainState:       150 líneas ✅

TOTAL LÓGICA DE DOMINIO EN ESTADOS: 850 líneas
TOTAL EN MONOLITO: 150 líneas (UI adapters only)
REUTILIZABILIDAD PARA MOTOR 2D: 100% (todos los DomainStates)
```

### Cambios de Responsabilidad
```
ANTES:
InteractiveGame responsable de:
  ✅ Orquestación
  ✅ Exploración (BAD - no debería)
  ✅ Combate (BAD - no debería)
  ✅ Configuración (BAD - no debería)
  ✅ Victoria/Derrota (BAD - no debería)
  ✅ UI/Entrada (OK)

DESPUÉS:
InteractiveGame responsable de:
  ✅ Orquestación (OK)
  ✅ UI/Entrada/Salida (OK)
  ❌ Exploración (AHORA EN ExplorationDomainState)
  ❌ Combate (AHORA EN CombatDomainState)
  ❌ Configuración (AHORA EN SetupDomainState)
  ❌ Victoria/Derrota (AHORA EN EndGameDomainState)
```

---

## DEPENDENCIAS Y REFACTORING

### Dependencias que se mueven (extraen a DomainStates)
```
ExplorationDomainState necesita:
├─ Personaje (tiene)
├─ Dungeon (tiene)
├─ DungeonThemeFactory (MOVER DESDE InteractiveGame)
├─ Random (MOVER DESDE InteractiveGame)
└─ ContainerItem inventario (PASAR BY REFERENCE)

CombatDomainState necesita:
├─ Personaje heroe (PASAR)
├─ Personaje enemigo (PASAR)
├─ AIController (MOVER DE InteractiveGame)
├─ CommandInvoker (PASAR)
├─ EventManager (INYECTAR)
└─ Historialía (MOVER)

EndGameDomainState necesita:
├─ Personaje heroe (PASAR)
├─ GameCaretaker (PASAR)
└─ Statistics (PASAR)
```

### Dependencias que PERMANECEN en InteractiveGame
```
├─ Scanner (entrada consola)
├─ GameStateContext (orquestación)
├─ EventManager (singleton - se inyecta a estados)
├─ GameCaretaker (persistencia)
└─ Statistics (auditoría global)
```

---

## CONTRATO NUEVO DE DomainGameState

```java
public interface DomainGameState extends GameState {
    
    /**
     * Mostrar mensaje en UI.
     * Los DomainStates NO escriben a consola directamente.
     */
    void setUIOutput(Consumer<String> output);
    
    /**
     * Leer opción del usuario.
     * Los DomainStates NO leen de Scanner directamente.
     */
    void setUIInput(Supplier<Integer> input);
    
    /**
     * ¿El estado finalizó?
     */
    boolean isFinished();
    
    /**
     * ¿Cuál es el resultado/siguiente estado?
     */
    Object getResult();
}
```

---

## BENEFICIOS ESPERADOS

### Para Acad Emicos
✅ Separación clara de responsabilidades  
✅ Estados reutilizables documentados  
✅ Cada estado testeable aisladamente  
✅ Cumplimiento del backlog 100%  
✅ Demostración de design patterns correcto

### Para Futuro Motor 2D
✅ ExplorationDomainState → Directamente reutilizable  
✅ CombatDomainState → Directamente reutilizable  
✅ EndGameDomainState → Directamente reutilizable  
✅ Sin dependencia de Scanner consola  
✅ Fácil de conectar a UI gráfica

### Para Mantenimiento
✅ InteractiveGame 70% más pequeño  
✅ Cada estado es una responsabilidad clara  
✅ Cambios en exploración NO afectan combate  
✅ Cada componente es testeable aislado  
✅ Código más legible y documentable

---

## ESFUERZO ESTIMADO

| Tarea | Esfuerzo | Notas |
|-------|----------|-------|
| ExplorationDomainState | 1-2 días | Extracción + tests |
| CombatDomainState | 2-3 días | Más complejo, más lógica |
| EndGameDomainState | 0.5 días | Relativamente simple |
| SetupDomainState | 1 día | Refactor opcional |
| Refactor InteractiveGame | 1 día | Inyectar estados |
| Tests nuevos | 2 días | Cada estado unitario + integración |
| **TOTAL** | **7-9 días** | Uno a dos sprints |

---

## RIESGO Y MITIGACIÓN

| Riesgo | Probabilidad | Mitigation |
|--------|---|---|
| Romper funcionalidad existente | Media | Tests: cada estado debe pasar tests del legacy state |
| Cambiar interfaz de usuario | Baja | La UI de consola sigue igual |
| Aumentar coupling | Baja | Inyección de dependencias clara |
| Refactoring toma más tiempo | Media | Empezar por ExplorationDomainState (más simple) |

---

## CONCLUSIÓN

El refactoring a **DomainStates** es técnicamente factible y completaría el backlog item:
> "Orquestación total por estados concretos de dominio reutilizables por motor 2D"

Sin este refactoring, el proyecto es **funcional pero monolítico**, y no cumple el objetivo de "estados reutilizables".

El costo es medio (7-9 días) pero el beneficio es alto (cumplimiento de requisitos + reutilización futura).
