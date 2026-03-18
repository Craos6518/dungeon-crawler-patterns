# Contraste: Documentacion vs Codigo

Fecha de corte: 14 de marzo de 2026

Este documento contrasta los requerimientos funcionales (RF) e historias de usuario (HU) definidos en `Docs/proyecto_patrones.md` contra la implementacion real en codigo fuente del proyecto `dungeon-crawler-patterns`.

## Resumen rapido

- Patrones de diseno en codigo: **11 GoF** (si se incluye State).
- Heroes jugables definidos: **3** (`Guerrero`, `Mago`, `Arquero`).
- Mazmorras predefinidas por Builder/Director: **3** (`Basica`, `Fuego`, `Oscura`).
- Temas de Abstract Factory: **4** (`Fuego`, `Hielo`, `Oscuridad`, `Veneno`).

## Tabla RF (Requerimientos Funcionales)

| ID | Requerimiento (documentacion) | Estado vs codigo | Evidencia (archivo/clase) |
|---|---|---|---|
| RF-01 | Crear nueva partida con equipo de hasta 3 heroes | **Parcial** (nueva partida existe, pero se crea 1 heroe) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| RF-02 | Cargar partida desde 3 slots | **Parcial** (carga por nombre de archivo, no 3 slots fijos) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`, `dungeon-crawler-patterns/src/main/java/game/persistence/memento/GameCaretaker.java` |
| RF-03 | Exploracion en mazmorra generada proceduralmente | **Parcial/No** (exploracion existe, mazmorra es predefinida por director) | `dungeon-crawler-patterns/src/main/java/game/dungeon/builder/DungeonDirector.java`, `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| RF-04 | Encuentro automatico con enemigos al entrar a sala | **Si** | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| RF-05 | Combate tactico por turnos en grid 6x6 | **Parcial** (combate por turnos si, grid 6x6 no evidenciado) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`, `dungeon-crawler-patterns/src/main/java/game/combat/engine/MotorCombate.java` |
| RF-06 | Acciones: moverse, atacar, habilidad, usar item, pasar turno | **Parcial** (atacar/defender/inventario; habilidad/item simplificados; mover/pasar turno no completos) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`, `dungeon-crawler-patterns/src/main/java/game/command/actions/AttackCommand.java`, `dungeon-crawler-patterns/src/main/java/game/command/actions/DefendCommand.java`, `dungeon-crawler-patterns/src/main/java/game/command/actions/SkillCommand.java`, `dungeon-crawler-patterns/src/main/java/game/command/actions/UseItemCommand.java` |
| RF-07 | IA de enemigos (Agresiva, Defensiva, Inteligente) | **Parcial** (estrategias implementadas; uso activo en juego interactivo es limitado) | `dungeon-crawler-patterns/src/main/java/game/ai/strategy/AggressiveStrategy.java`, `dungeon-crawler-patterns/src/main/java/game/ai/strategy/DefensiveStrategy.java`, `dungeon-crawler-patterns/src/main/java/game/ai/strategy/IntelligentStrategy.java`, `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| RF-08 | Sistema de experiencia y nivel | **No evidenciado en flujo principal** | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| RF-09 | Inventario jerarquico | **Si** | `dungeon-crawler-patterns/src/main/java/game/items/model/ContainerItem.java`, `dungeon-crawler-patterns/src/main/java/game/items/model/SimpleItem.java`, `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| RF-10 | Efectos de estado temporales | **Si** | `dungeon-crawler-patterns/src/main/java/game/effects/status/PoisonEffect.java`, `dungeon-crawler-patterns/src/main/java/game/effects/status/BurnEffect.java`, `dungeon-crawler-patterns/src/main/java/game/effects/status/StrengthEffect.java`, `dungeon-crawler-patterns/src/main/java/game/effects/status/CharacterDecorator.java` |
| RF-11 | Guardar partida fuera de combate | **Si/Parcial** (guardado funciona; restauracion completa de gameplay es parcial) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`, `dungeon-crawler-patterns/src/main/java/game/persistence/memento/GameMemento.java`, `dungeon-crawler-patterns/src/main/java/game/persistence/memento/GameOriginator.java`, `dungeon-crawler-patterns/src/main/java/game/persistence/memento/GameCaretaker.java` |
| RF-12 | Estados del juego (Menu, Exploracion, Combate, Inventario, GameOver) | **Si (capa State), integracion jugable parcial** | `dungeon-crawler-patterns/src/main/java/game/state/game/GameState.java`, `dungeon-crawler-patterns/src/main/java/game/state/game/GameStateContext.java`, `dungeon-crawler-patterns/src/main/java/game/state/game/MenuState.java`, `dungeon-crawler-patterns/src/main/java/game/state/game/ExplorationState.java`, `dungeon-crawler-patterns/src/main/java/game/state/game/CombatState.java`, `dungeon-crawler-patterns/src/main/java/game/state/game/InventoryState.java`, `dungeon-crawler-patterns/src/main/java/game/state/game/GameOverState.java` |

## Tabla HU (Historias de Usuario)

| ID | Historia de usuario (documentacion) | Estado vs codigo | Evidencia (archivo/clase) |
|---|---|---|---|
| HU-01 | Combate basico por turnos con grid 6x6 | **Parcial** (turnos si, grid 6x6 no evidenciado) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`, `dungeon-crawler-patterns/src/main/java/game/combat/engine/MotorCombate.java` |
| HU-02 | Creacion de heroes (clase + nombre) | **Si** (3 clases disponibles con stats diferenciados) | `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`, `dungeon-crawler-patterns/src/main/java/game/domain/personaje/factory/GuerreroFactory.java`, `dungeon-crawler-patterns/src/main/java/game/domain/personaje/factory/MagoFactory.java`, `dungeon-crawler-patterns/src/main/java/game/domain/personaje/factory/ArqueroFactory.java` |
| HU-03 | Gestion de inventario | **Parcial/Si** (estructura jerarquica funcional; equipamiento y consumibles simplificados) | `dungeon-crawler-patterns/src/main/java/game/items/model/ContainerItem.java`, `dungeon-crawler-patterns/src/main/java/game/items/model/SimpleItem.java`, `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |
| HU-04 | Generacion procedural y rejugabilidad (2 temas) | **Parcial** (hay 4 temas y variacion tematica, pero construccion procedural no completa) | `dungeon-crawler-patterns/src/main/java/game/dungeon/theme/FireThemeFactory.java`, `dungeon-crawler-patterns/src/main/java/game/dungeon/theme/IceThemeFactory.java`, `dungeon-crawler-patterns/src/main/java/game/dungeon/theme/DarkThemeFactory.java`, `dungeon-crawler-patterns/src/main/java/game/dungeon/theme/PoisonThemeFactory.java`, `dungeon-crawler-patterns/src/main/java/game/dungeon/builder/DungeonDirector.java` |
| HU-05 | Guardado de partida (3 slots, persistencia completa) | **Parcial** (persistencia existe; slots fijos y restauracion completa no cerrados) | `dungeon-crawler-patterns/src/main/java/game/persistence/memento/GameCaretaker.java`, `dungeon-crawler-patterns/src/main/java/game/persistence/memento/GameMemento.java`, `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java` |

## Llamados a diagramas PNG

- [DIAGRAMA DE CLASES - Arquitectura Base Completa](Diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20Arquitectura%20Base%20Completa.png)
- [DIAGRAMA DE CLASES - SISTEMA DE COMBATE DETALLADO](Diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20SISTEMA%20DE%20COMBATE%20DETALLADO.png)
- [DIAGRAMA - SUBSISTEMA DECORATOR](Diagramas/DIAGRAMA%20%E2%80%93%20SUBSISTEMA%20DECORATOR.png)
- [DIAGRAMA - Version Arquitectonicamente Impecable](Diagramas/DIAGRAMA%20%E2%80%93%20Versi%C3%B3n%20Arquitect%C3%B3nicamente%20Impecable.png)
- [DIAGRAMA INTEGRADO COMPLETO](Diagramas/DIAGRAMA%20INTEGRADO%20COMPLETO.png)
- [DIAGRAMA DE SECUENCIA - Turno de Ataque](Diagramas/DIAGRAMA%20DE%20SECUENCIA%20%E2%80%93%20Turno%20de%20Ataque.png)
- [DIAGRAMA DE SECUENCIA - Procesamiento de Efectos por Turno](Diagramas/DIAGRAMA%20DE%20SECUENCIA%20%E2%80%93%20Procesamiento%20de%20Efectos%20por%20Turno.png)
- [DIAGRAMA DE ESTADOS - Combate](Diagramas/DIAGRAMA%20DE%20ESTADOS%20%E2%80%93%20Combate.png)
- [DIAGRAMA DE ACTIVIDADES - Flujo de Turno](Diagramas/DIAGRAMA%20DE%20ACTIVIDADES%20%E2%80%93%20Flujo%20de%20Turno.png)
- [DIAGRAMA DE COMPONENTES - Arquitectura General](Diagramas/DIAGRAMA%20DE%20COMPONENTES%20%E2%80%93%20Arquitectura%20General.png)
- [DIAGRAMA ACTUALIZADO - Motor con Estado Interno](Diagramas/DIAGRAMA%20ACTUALIZADO%20%E2%80%93%20Motor%20con%20Estado%20Interno.png)
- [Secuencia Actualizada del Turno Alternado](Diagramas/Secuencia%20Actualizada%20del%20Turno%20Alternado.png)

## Notas de consistencia para defender el proyecto

- La documentacion publica menciona "10 patrones" y en la practica hay implementacion de `State` en codigo, por lo que academicamente conviene reportar 11 GoF en esta version.
- El alcance jugable actual prioriza demostracion arquitectonica sobre videojuego completo (varias funciones estan simplificadas para demo).
- Para cerrar brechas RF/HU: equipo de 3 heroes, slots fijos de guardado, grid 6x6 real, y proceduralidad de mazmorra completa.
