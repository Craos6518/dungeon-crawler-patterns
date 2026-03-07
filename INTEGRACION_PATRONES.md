# Integración de Patrones de Diseño

Este documento explica cómo **todos los patrones de diseño implementados trabajan juntos** en el sistema de combate del proyecto Dungeon Crawler.

---

## Visión General

El proyecto integra **10 patrones de diseño** que colaboran para crear un sistema de combate completo y extensible:

### Patrones Creacionales (3)
- **Factory Method**: Creación flexible de personajes
- **Builder**: Construcción paso a paso de mazmorras
- **Abstract Factory**: Familias temáticas coherentes

### Patrones Estructurales (3)
- **Composite**: Sistema jerárquico de inventario
- **Decorator**: Efectos de estado dinámicos
- **Facade**: Interfaz simplificada del combate

### Patrones de Comportamiento (4)
- **Command**: Encapsulación de acciones
- **Strategy**: Comportamientos de IA intercambiables
- **Observer**: Sistema de eventos desacoplado
- **Memento**: Guardado y restauración de estado

---

## Flujo de Integración

### 1. Preparación del Combate

#### Factory Method + Abstract Factory → Personajes
```
PersonajeFactory guerreroFactory = new GuerreroFactory(150, 25);
Personaje heroe = guerreroFactory.crearPersonaje("Arthas");

DungeonThemeFactory tema = new FireThemeFactory();
Personaje enemigo = tema.crearJefe();
```

**Colaboración**: Las factories crean personajes que serán utilizados en el combate.

---

#### Builder → Mazmorra
```
DungeonBuilder builder = new ConcreteDungeonBuilder();
DungeonDirector director = new DungeonDirector(builder);
Dungeon mazmorra = director.construirMazmorraFuego();
```

**Colaboración**: La mazmorra define el contexto temático del combate.

---

#### Composite → Inventario
```
ContainerItem mochila = new ContainerItem("Mochila", "...", 10, 2);
mochila.agregar(new SimpleItem("Espada", "...", "Arma", 500, 8));
mochila.agregar(new SimpleItem("Poción", "...", "Consumible", 150, 1));
```

**Colaboración**: Los items del inventario pueden ser usados durante el combate mediante Commands.

---

#### Decorator → Efectos de Estado
```
Personaje heroeConEfectos = new StrengthEffect(heroe, 10, 3);
Personaje enemigoConEfectos = new BurnEffect(enemigo, 5, 5);
```

**Colaboración**: Los efectos se aplican automáticamente cada turno durante el combate.

---

### 2. Ejecución del Combate (Integración Central)

#### IntegratedCombatEngine: Hub de Integración

```java
IntegratedCombatEngine motor = new IntegratedCombatEngine(
    heroe,              // Del Factory Method
    enemigo,            // Del Abstract Factory  
    new AggressiveStrategy()  // Patrón Strategy
);
```

Este motor integra **5 patrones simultáneamente**:

##### Command: Encapsulación de Acciones
```java
Command comando = new AttackCommand(atacante, defensor);
invoker.ejecutarComando(comando);  // Historial de comandos
```

**Beneficio**: Cada acción es un objeto con historial. Permite undo/redo potencial.

---

##### Strategy: IA Dinámica
```java
AIController aiController = new AIController(enemigo, estrategia);
Command accion = aiController.decidirAccion(objetivos);

// Cambiar estrategia en runtime
motor.cambiarEstrategiaIA(new DefensiveStrategy());
```

**Beneficio**: El enemigo puede cambiar su comportamiento durante el combate.

---

##### Observer: Sistema de Eventos
```java
EventManager manager = EventManager.getInstance();
manager.suscribir(new CombatLogger());
manager.suscribir(new StatisticsTracker());

// El motor notifica automáticamente
manager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
    .agregarDato("danio", 25));
```

**Beneficio**: Componentes desacoplados reciben notificaciones sin conocerse entre sí.

---

##### Decorator: Efectos Automáticos
```java
// En cada turno, el motor aplica efectos
if (personaje instanceof CharacterDecorator) {
    decorator.aplicarEfecto();
}
```

**Beneficio**: Los efectos se procesan automáticamente sin lógica condicional compleja.

---

##### Facade: API Simplificada
```java
CombatFacade facade = new CombatFacade();
facade.iniciarCombate(heroe, enemigo);
facade.ejecutarRonda();
Personaje ganador = facade.obtenerGanador();
```

**Beneficio**: Oculta la complejidad del motor integrado detrás de una interfaz simple.

---

### 3. Post-Combate

#### Memento: Persistencia
```java
GameOriginator juego = new GameOriginator(heroe.getNombre());
GameMemento checkpoint = juego.guardar();

// Más tarde...
juego.restaurar(checkpoint);
```

**Beneficio**: Permite guardar/cargar partidas sin exponer el estado interno.

---

## Diagrama de Integración

```
┌─────────────────────────────────────────────────────────────┐
│                    PREPARACIÓN                               │
│                                                              │
│  Factory Method ──┐                                          │
│  Abstract Factory ├──> Personajes ──┐                       │
│                   │                  │                       │
│  Builder ─────────┼──> Mazmorra ────┤                       │
│                   │                  │                       │
│  Composite ───────┴──> Inventario ──┤                       │
│                                      ▼                       │
│                              ┌───────────────┐              │
│                              │   Decorator   │              │
│                              │ (Efectos)     │              │
│                              └───────┬───────┘              │
└──────────────────────────────────────┼──────────────────────┘
                                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    COMBATE (HUB)                             │
│                                                              │
│         ┌─────────────────────────────────┐                 │
│         │  IntegratedCombatEngine         │                 │
│         │  (Facade Interno)               │                 │
│         └────────────┬────────────────────┘                 │
│                      │                                       │
│      ┌───────────────┼───────────────┐                      │
│      ▼               ▼               ▼                       │
│  ┌─────────┐   ┌──────────┐   ┌─────────┐                  │
│  │ Command │   │ Strategy │   │Observer │                  │
│  │(Acciones)│  │   (IA)   │   │(Eventos)│                  │
│  └─────────┘   └──────────┘   └─────────┘                  │
│                                                              │
└──────────────────────────────┬───────────────────────────────┘
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    POST-COMBATE                              │
│                                                              │
│                      Memento                                 │
│                  (Persistencia)                              │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## Ejecución de la Demo Integrada

### Compilar el proyecto
```bash
cd dungeon-crawler-patterns
mvn compile
```

### Ejecutar demo básica (solo patrones creacionales)
```bash
mvn exec:java -Dexec.mainClass="game.Main"
```

### Ejecutar demo INTEGRADA (todos los patrones)
```bash
mvn exec:java -Dexec.mainClass="game.demo.IntegracionCompletaDemo"
```

O con el Main:
```bash
mvn exec:java -Dexec.mainClass="game.Main" -Dexec.args="--integracion"
```

---

## Salida Esperada

La demo integrada muestra:

1. **FASE 1: PREPARACIÓN**
   - ⚔️ Factory Method creando héroe
   - 🎒 Composite construyendo inventario
   - 🏰 Builder generando mazmorra
   - 🎯 Abstract Factory creando enemigo temático
   - ✨ Decorator aplicando efectos

2. **FASE 2: COMBATE**
   - 📡 Observer configurando listeners
   - ⚔️ Command ejecutando acciones
   - 🧠 Strategy decidiendo comportamiento de IA
   - 🔄 Alternancia de turnos con efectos automáticos
   - 🏆 Determinación del ganador

3. **FASE 3: POST-COMBATE**
   - 📊 Estadísticas acumuladas
   - 💾 Memento guardando/restaurando estado

---

## Beneficios de la Integración

### 1. Separación de Responsabilidades
Cada patrón tiene un rol claro y no invade el espacio de otros.

### 2. Extensibilidad
- Nuevos personajes: Agregar factory
- Nuevos efectos: Crear decorador
- Nuevas estrategias: Implementar Strategy
- Nuevos eventos: Suscribir observer

### 3. Mantenibilidad
El código es modular y cada componente es testeable independientemente.

### 4. Reutilización
Los patrones pueden usarse en otros contextos (no solo combate).

### 5. Testeo
Cada patrón tiene sus propios tests unitarios (106 tests en total).

---

## Tests de Integración

El proyecto incluye tests que verifican la colaboración entre patrones:

```bash
# Test de integración de combate
mvn test -Dtest="CombatIntegrationTest"

# Test de integración de comportamiento
mvn test -Dtest="BehavioralPatternsIntegrationTest"
```

Ver [README_TESTS.md](README_TESTS.md) para más detalles.

---

## Conclusión

Este sistema demuestra cómo **múltiples patrones trabajan juntos** para crear una arquitectura:

- ✅ Limpia y organizada
- ✅ Extensible y flexible
- ✅ Testeable y mantenible
- ✅ Académicamente defendible

El **IntegratedCombatEngine** es el corazón que conecta todos los patrones en un sistema cohesivo y funcional.
