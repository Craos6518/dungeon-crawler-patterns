# Implementacion del GDD

Este documento traduce el GDD del proyecto a elementos verificables en codigo.

Referencia principal del diseno:

- `../GDD.md`

## Matriz de trazabilidad

| Requisito del GDD | Estado | Implementacion |
|---|---|---|
| Seleccion de heroe (Guerrero, Mago, Arquero) | Implementado | `src/main/java/game/InteractiveGame.java` + `src/main/java/game/domain/personaje/factory/` |
| Mazmorra tematica con salas | Implementado | `src/main/java/game/dungeon/builder/` + `src/main/java/game/dungeon/theme/` |
| Combate por turnos | Implementado | `src/main/java/game/InteractiveGame.java` + `src/main/java/game/combat/engine/` |
| Acciones de combate: atacar, defender, usar objeto, usar habilidad | Implementado | `src/main/java/game/InteractiveGame.java` + `src/main/java/game/command/actions/` |
| Inventario jerarquico | Implementado | `src/main/java/game/items/model/ContainerItem.java` + `src/main/java/game/items/model/SimpleItem.java` |
| Efectos de estado | Implementado | `src/main/java/game/effects/status/` |
| IA adaptable por estrategia | Implementado en arquitectura, parcial en loop interactivo | `src/main/java/game/ai/strategy/` |
| Sistema de eventos | Implementado | `src/main/java/game/events/observer/` |
| Guardado y restauracion de estado | Implementado (con alcance parcial en experiencia interactiva) | `src/main/java/game/persistence/memento/` |
| Estados del juego (menu, exploracion, combate, inventario, game over) | Implementado | `src/main/java/game/state/game/` |

## Criterios de validacion academica

- El GDD no describe mecanicas que no tengan correlato en codigo.
- Cada patron mencionado en diseno tiene clases concretas asociadas.
- El flujo interactivo principal permite demostrar los patrones sin depender de interfaz grafica.

## Evidencia de ejecucion sugerida

```bash
cd dungeon-crawler-patterns
mvn clean test
mvn exec:java -Dexec.mainClass="game.InteractiveGame"
```

## Notas de alcance

- El guardado/carga restaura estado del originator y checkpoints, pero no reconstruye toda la sesion de juego en curso.
- La IA por Strategy existe a nivel de arquitectura y pruebas; puede expandirse en la toma de decisiones enemiga del modo interactivo.
