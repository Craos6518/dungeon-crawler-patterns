# Game Design Document (GDD) - Dungeon Crawler Académico

## 1. Resumen del Juego
Dungeon Crawler Académico es un videojuego por consola desarrollado en Java 17 cuyo objetivo es demostrar la implementación e integración de patrones de diseño de software en un sistema interactivo.

## 2. Requerimientos Funcionales e Historias de Usuario
### Requerimientos Funcionales (RF)
- **RF-01**: Crear Nueva Partida con un héroe seleccionado.
- **RF-02**: Cargar Partida desde slots disponibles.
- **RF-03**: Exploración de Mazmorra procedural/temática.
- **RF-04**: Encuentro con Enemigos automático.
- **RF-05**: Sistema de Combate por Turnos.
- **RF-06**: Acciones en Combate (Atacar, Defender, Usar Item, Habilidad).
- **RF-07**: IA de Enemigos (Agresiva, Defensiva, Inteligente).
- **RF-08**: Sistema de Experiencia y Nivel (Implementado con `LevelUpCommand` en flujo interactivo).
- **RF-09**: Inventario Jerárquico (Composite).
- **RF-10**: Efectos de Estado (Decorator).
- **RF-11**: Guardar Partida (Memento).
- **RF-12**: Estados del Juego (State).

### Historias de Usuario (HU)
- **HU-01: Combate Básico**: Como jugador quiero combatir enemigos por turnos (combate directo 1x1 sin grid).
- **HU-02: Creación de Héroes**: Como jugador quiero elegir clase y nombre.
- **HU-03: Sistema de Inventario**: Como jugador quiero gestionar ítems.
- **HU-04: Generación Procedural**: Como jugador quiero mazmorras diferentes.
- **HU-05: Guardado de Partida**: Como jugador quiero guardar mi progreso.

## 3. Mecánicas del Juego
### Exploración
El jugador recorre salas de la mazmorra. Cada sala puede incluir enemigos, objetos y eventos.

### Combate
Acciones: Atacar, Defender, Usar Objeto, Habilidad. Los enemigos actúan según su `AIStrategy`.

### Inventario
Estructura compuesta (Mochila -> Bolsa -> Items).

## 4. Trazabilidad GDD -> Código
| Requisito | Patrón | Implementación Principal |
|-----------|--------|--------------------------|
| Selección de héroe | Factory Method | `game.domain.personaje.factory` |
| Mazmorra temática/procedural | Abstract Factory + Builder | `game.dungeon.builder`, `game.dungeon.theme`, `game.state.domain.setup` |
| Combate por turnos | Command + Strategy | `game.combat.engine`, `game.command.actions`, `game.ai.strategy` |
| Inventario | Composite | `game.items.model` |
| Efectos de estado | Decorator | `game.effects.status` |
| Eventos | Observer | `game.events.observer` |
| Guardado/Carga | Memento + Facade | `game.persistence.memento` |
| Estados del juego | State | `game.state.domain`, `game.refactoring` |

## 5. Criterios de Aceptación y Cierre (Épicas)
### EP-001: Creación Flexible de Personajes (Factory Method)
- [x] Clases Factory por tipo de héroe.
- [x] Selección en menú inicial.

### EP-002: Generación de Mazmorras Temáticas (Abstract Factory + Builder)
- [x] 4 temas (Fuego, Hielo, Oscuridad, Veneno).
- [x] Construcción procedural dinámica mediante Builder.

### EP-003: Sistema de Combate Integrado (Strategy + Command + Decorator + Observer)
- [x] IA adaptable.
- [x] Acciones encapsuladas en comandos.
- [x] Efectos de estado apilables.
- [x] Notificaciones vía Observer.

### EP-004: Inventario Jerárquico (Composite)
- [x] Contenedores anidables.

### EP-005: Persistencia y Guardado (Memento + Facade)
- [x] Captura de estado inmutable.
- [x] Serialización a disco.

### EP-006: Orquestación Global de Flujo (State)
- [x] Transiciones entre Menú, Exploración, Combate y GameOver en runtime basado en DomainStates.

## 6. Contraste: Documentación vs Código
- **Patrones**: 11 patrones implementados (incluyendo State).
- **Héroes**: 3 clases (Guerrero, Mago, Arquero).
- **Temas**: 4 temas disponibles.
- **Estado**: Proyecto listo para presentación académica (Verde).
