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

Cada acción de combate es representada como un comando.

```
Command
 ├─ MoveCommand
 ├─ AttackCommand
 ├─ SkillCommand
 └─ UseItemCommand
```

Esto permite:

- ejecutar acciones
- deshacer acciones
- registrar acciones

---

### Strategy

Define comportamientos de IA intercambiables.

```
AIStrategy
 ├─ AggressiveStrategy
 ├─ DefensiveStrategy
 └─ IntelligentStrategy
```

Los enemigos pueden cambiar su comportamiento sin modificar su clase.

---

### Observer

Sistema de eventos del juego.

Se utiliza para:

- notificaciones
- actualización de UI
- log de combate

---

### State

Modela los diferentes estados del juego.

```
GameState
 ├─ MenuState
 ├─ ExplorationState
 ├─ CombatState
 ├─ InventoryState
 └─ GameOverState
```

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
