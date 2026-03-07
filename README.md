# Dungeon Crawler Académico — Patrones de Diseño en Java

Proyecto académico desarrollado en **Java 17** para demostrar la aplicación correcta de **patrones de diseño de software** dentro de un videojuego simple tipo **Dungeon Crawler por turnos** ejecutado en consola.

El objetivo principal **no es crear un juego complejo**, sino **diseñar una arquitectura limpia, mantenible y defendible académicamente**.

---

# Objetivo del Proyecto

Demostrar el uso correcto de patrones de diseño en un sistema realista mediante:

- Separación clara de responsabilidades
- Dominio bien modelado
- Uso correcto de abstracciones
- Código testeable
- Arquitectura extensible

El juego se ejecuta completamente en **consola** para reducir complejidad gráfica y concentrar el esfuerzo en la **arquitectura del software**.

---

# Tecnologías Utilizadas

- **Java 17 (OpenJDK)**
- **VSCode**
- **JUnit 5**
- **PlantUML** (para diagramas)
- **Git / GitHub**

Sistema desarrollado en:

```
Fedora 43 KDE
OpenJDK 17
VSCode
```

---

# Patrones de Diseño Implementados

El proyecto implementa múltiples patrones clásicos del libro **Gang of Four**.

## Patrones Creacionales

### Factory Method

Responsable de crear personajes del juego.

Ejemplo:

```
HeroFactory
ConcreteHeroFactory
```

Permite desacoplar la lógica de creación de personajes.

---

### Builder

Generación paso a paso de la mazmorra.

```
DungeonBuilder
ConcreteDungeonBuilder
```

Permite separar la **construcción** de la **representación final**.

---

### Abstract Factory

Creación de familias de objetos coherentes según el **tema de la mazmorra**.

Ejemplo:

```
DungeonThemeFactory
FireThemeFactory
PoisonThemeFactory
```

Cada fábrica crea:

- enemigos
- tesoros
- elementos temáticos

---

## Patrones Estructurales

### Composite

Sistema jerárquico de inventario.

```
Item
 ├─ SimpleItem
 └─ ContainerItem
```

Permite tratar objetos simples y contenedores de forma uniforme.

---

### Decorator

Sistema de **efectos de estado** aplicados a personajes.

Ejemplos:

```
PoisonEffect
BurnEffect
StunEffect
```

Los efectos modifican dinámicamente el comportamiento del personaje.

---

### Facade

El sistema de combate se simplifica mediante una fachada.

```
CombatFacade
```

Oculta la complejidad del motor de combate.

---

## Patrones de Comportamiento

### Command

Encapsula cada acción del juego como un objeto ejecutable.

```
Command (interface)
 ├─ AttackCommand
 ├─ DefendCommand
 ├─ UseItemCommand
 ├─ SkillCommand
 └─ CommandInvoker (historial)
```

**Beneficios:**
- Desacopla emisor de receptor
- Permite historial de acciones (undo potencial)
- Facilita logging y replay
- Testeable individualmente

**Implementación:**
- `Command.java` - Interfaz base
- `CommandInvoker.java` - Gestor de historial
- 5 comandos concretos

---

### Strategy

Define algoritmos de comportamiento de IA intercambiables.

```
AIStrategy (interface)
 ├─ AggressiveStrategy    // Ataca al más fuerte
 ├─ DefensiveStrategy     // Defiende cuando vida < 30%
 ├─ IntelligentStrategy   // Elimina débiles primero
 ├─ RandomStrategy        // Comportamiento aleatorio
 └─ AIController          // Contexto que usa estrategias
```

**Beneficios:**
- Comportamientos intercambiables en runtime
- Enemigos pueden cambiar estrategia dinámicamente
- Extensible (fácil agregar nuevas estrategias)
- Sin condicionales complejos (if/else chains)

**Implementación:**
- `AIStrategy.java` - Interfaz base
- `AIController.java` - Contexto
- 4 estrategias concretas

---

### Observer

Sistema de eventos desacoplado para notificaciones del juego.

```
EventManager (Singleton)
 ├─ GameEvent / EventType
 └─ Observers:
     ├─ CombatLogger         // Logs de combate
     ├─ StatisticsTracker    // Métricas del juego
     └─ UINotifier           // Notificaciones UI
```

**Beneficios:**
- Comunicación desacoplada entre componentes
- Múltiples observers sin duplicar código
- Soporta suscripción por tipo de evento
- Historial de eventos mantenido

**Implementación:**
- `EventManager.java` (Singleton)
- `GameEvent.java` / `EventType.java` (enum)
- 3 observers concretos
- Sistema habilitado/deshabilitado en runtime

---

### State

Modela los diferentes estados del juego y sus transiciones.

```
GameState (interface)
 ├─ MenuState
 ├─ ExplorationState
 ├─ CombatState
 ├─ InventoryState
 ├─ GameOverState
 └─ GameStateContext    // Mantiene estado actual
```

**Beneficios:**
- Encapsula comportamiento específico de cada estado
- Transiciones explícitas y controladas
- Sin condicionales complejos basados en flags
- Cada estado es testeable individualmente

**Implementación:**
- `GameState.java` - Interfaz base
- `GameStateContext.java` - Contexto que mantiene estado
- 5 estados concretos

---

### Memento

Permite guardar y restaurar el estado completo del juego.

```
GameMemento (immutable)
 ├─ GameOriginator      // Objeto principal del juego
 └─ GameCaretaker       // Gestor de mementos
     ├─ Memoria (historial de checkpoints)
     └─ Disco (serialización)
```

**Beneficios:**
- Encapsula estado interno sin exponerlo
- Soporte save/load (memoria y disco)
- Historial de checkpoints
- Immutable (using Builder pattern)

**Implementación:**
- `GameMemento.java` - Memento inmutable con Builder
- `GameOriginator.java` - Objeto cuyo estado se guarda
- `GameCaretaker.java` - Gestor con persistencia en disco
- Serializable para guardado permanente

Evita condicionales complejos y centraliza el comportamiento de cada estado.

---

### Memento

Permite guardar y restaurar el estado del juego.

```
GameMemento
GameCaretaker
```

Se utiliza para implementar el sistema de guardado de partidas.

---

# Arquitectura del Proyecto

El proyecto se organiza en capas claras:

```
game
 ├─ Main.java
 ├─ domain/
 │   └─ personaje/
 │      ├─ Personaje.java
 │      ├─ Guerrero.java
 │      └─ EnemigoBasico.java
 ├─ combat/
 │   ├─ engine/
 │   │   └─ MotorCombate.java
 │   └─ model/
 │       └─ ResultadoAtaque.java
 ├─ ai/strategy/
 ├─ items/inventory/
 ├─ dungeon/builder/
 ├─ effects/status/
 ├─ command/actions/
 ├─ state/game/
 ├─ persistence/memento/
 └─ ui/console/
```

Esto evita dependencias cruzadas innecesarias.

---

# Ejecución del Proyecto

Compilar:

```
javac -d out $(find src -name "*.java")
```

Ejecutar:

```
java -cp out game.Main
```

---

# Testing y Calidad

El proyecto cuenta con **106 tests** que validan todos los patrones implementados.

## Tests Unitarios (51)

### Patrones Creacionales (3 suites)
- **AbstractFactoryTest** - Factorías temáticas de dungeons
- **BuilderPatternTest** - Construcción de mazmorras
- **FactoryMethodTest** - Creación de personajes

### Patrones Estructurales (3 suites)
- **CompositePatternTest** - Inventario jerárquico de items
- **DecoratorPatternTest** - Efectos de estado temporales
- **FacadePatternTest** - Interfaz simplificada del sistema de combate

### Patrones de Comportamiento (4 suites)
- **CommandPatternTest** (11 tests) - Encapsulación de acciones
- **StrategyPatternTest** (11 tests) - Comportamientos de IA
- **ObserverPatternTest** (13 tests) - Sistema de eventos
- **MementoPatternTest** (14 tests) - Guardado/restauración de estado

### Tests de Dominio y Combate (2 suites)
- **CharacterDamageTest** - Mecánicas de daño
- **CombatTurnAlternationTest** / **CombatEndTest** - Flujo de combate

## Tests de Integración (2)

- **CombatIntegrationTest** - Combate completo con múltiples patrones
- **BehavioralPatternsIntegrationTest** (6 tests) - Interacción entre Command, Strategy, Observer y Memento

## Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests de comportamiento
mvn test -Dtest="game.unit.behavioral.*Test"

# Test específico
mvn test -Dtest="CommandPatternTest"
```

Ver documentación completa en:
- [RESUMEN_TESTS_BEHAVIORAL.md](RESUMEN_TESTS_BEHAVIORAL.md)
- [README_TESTS.md](README_TESTS.md)

---

# Filosofía del Proyecto

Este proyecto sigue un principio claro:

> **Un sistema pequeño, pero arquitectónicamente correcto, es superior a un sistema grande pero mal diseñado.**

Se prioriza:

- claridad
- separación de responsabilidades
- extensibilidad
- pruebas unitarias

sobre la complejidad del juego.

---

# Autor

Proyecto académico desarrollado por:

**Andrés**

Curso: Patrones de Diseño de Software
