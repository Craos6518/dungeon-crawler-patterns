# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/04-testing/ESTRATEGIA_TESTING.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# ESTADO DOCUMENTAL
- Estado: historico (legacy conservado por trazabilidad)
- Referencia canonica vigente: `docs/04-testing/ESTRATEGIA_TESTING.md`
- Fecha de reclasificacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra

# Reporte de Contraste: Documentación vs. Juego Funcional
**Fecha de revisión:** 21 de marzo de 2026

---

## ✅ Confirmaciones (Documentado y Verificado en Código)

### Build e Infraestructura
| Afirmación en Docs | Estado |
|--------------------|--------|
| `mvn clean compile` exitoso | ✅ Verificado sin errores |
| 17 suites de test en `src/test/java` | ✅ Confirmado |
| 96 archivos [.java](file:///home/craos6518/Documentos/Proyecto%20Patrones%20de%20dise%C3%B1o/dungeon-crawler-patterns/src/main/java/game/Main.java) en `src/main/java` | ✅ Confirmado |

### Los 11 Patrones de Diseño
| Patrón | Paquete | Clases Clave | Estado |
|--------|---------|--------------|--------|
| **Factory Method** | `game.domain.personaje.factory` | `GuerreroFactory`, `MagoFactory`, `ArqueroFactory` +3 | ✅ |
| **Builder** | `game.dungeon.builder` | `DungeonBuilder`, `ConcreteDungeonBuilder`, `DungeonDirector` | ✅ |
| **Abstract Factory** | `game.dungeon.theme` | `FireThemeFactory`, `IceThemeFactory`, `DarkThemeFactory`, `PoisonThemeFactory` | ✅ |
| **Composite** | `game.items.model` | `Item`, `ItemComponent`, `SimpleItem`, `ContainerItem` | ✅ |
| **Decorator** | `game.effects.status` | `CharacterDecorator`, `PoisonEffect`, `BurnEffect`, `StrengthEffect`, `StunEffect` | ✅ |
| **Facade** | `game.combat.facade` | `CombatFacade` | ✅ |
| **Command** | `game.command.actions` | `Command`, `AttackCommand`, `DefendCommand`, `UseItemCommand`, `SkillCommand`, `CommandInvoker` | ✅ |
| **Strategy** | `game.ai.strategy` | `AIStrategy`, `AggressiveStrategy`, `DefensiveStrategy`, `IntelligentStrategy`, `RandomStrategy`, `AdaptiveAIController` | ✅ |
| **Observer** | `game.events.observer` | `GameObserver`, `EventManager`, `CombatLogger`, `StatisticsTracker`, `UINotifier` | ✅ |
| **State** | `game.state.game` | `GameState`, `MenuState`, `ExplorationState`, `CombatState`, `InventoryState`, `GameOverState`, `GameStateContext` | ✅ |
| **Memento** | `game.persistence.memento` | `GameMemento`, `GameOriginator`, `GameCaretaker` | ✅ |

### Sistema de Eventos
- [EventType.java](file:///home/craos6518/Documentos/Proyecto%20Patrones%20de%20dise%C3%B1o/dungeon-crawler-patterns/src/main/java/game/events/observer/EventType.java) define **19 tipos de eventos** (confirmado).
- [EventContract.java](file:///home/craos6518/Documentos/Proyecto%20Patrones%20de%20dise%C3%B1o/dungeon-crawler-patterns/src/main/java/game/events/observer/EventContract.java) **existe** en `game.events.observer` — validando el contrato central.
- 3 observers concretos: `CombatLogger`, `StatisticsTracker`, `UINotifier` ✅

---

## ⚠️ Discrepancias y Correcciones Requeridas

### GDD.md
| Afirmación | Realidad | Acción |
|-----------|---------|--------|
| "Estado: Proyecto listo" sin lista de observaciones | OK pero muy simplificado | Opcional: expandir |
| RF-08: "Sistema de experiencia y nivel" — como RF sin estado | No implementado en el flujo interactivo | Marcar como `Parcial` |

### DISEÑO.md
| Afirmación | Realidad | Acción |
|-----------|---------|--------|
| Composite: "interfaz `Item`" | Existe una variante con `ItemComponent` como clase abstracta | ✏️ Corregir a `Item` + `ItemComponent` |
| Observer: "2 observers concretos" | Hay **3**: `CombatLogger`, `StatisticsTracker`, **`UINotifier`** | ✏️ Corregir a 3 |
| Strategy: "`AdaptiveAIController`" no mencionado | La clase existe en `ai/strategy/` | ✏️ Agregar mención |

### ESTADO_PROYECTO.md
| Afirmación | Realidad | Acción |
|-----------|---------|--------|
| Backlog de tests | Baseline academico: 241 en verde, 2 omitidos | ✅ Actualizar docs historicos para reflejar metrica canonica |
| `StunEffect` no mencionado en ningún doc | Existe en `effects/status/` | ✏️ Agregar a `DISEÑO.md` |

---

## ❌ Brechas (Documentado Pero No Implementado)

| Característica | En Docs | Estado Real |
|----------------|---------|-------------|
| Sistema de Experiencia/Nivel | Mencionado en GDD RF-08 | ❌ No verificado en flujo interactivo |
| Grid 6x6 en combate | Mencionado en HU-01 | ❌ No evidenciado (modelo de consola sin grid) |
| 3 slots fijos de guardado | Mencionado en HU-05 | ❌ Guardado por nombre libre de archivo |
| Equipo de 3 héroes | Mencionado en RF-01 | ❌ 1 héroe por partida actualmente |

---

## Resumen Ejecutivo

| Área | Estado |
|------|--------|
| Build | ✅ 100% OK |
| 11 Patrones en código | ✅ Todos presentes |
| Documentación precisa | 🟡 3 correcciones menores necesarias |
| Features GDD vs código | ⚠️ 4 brechas heredadas de entregas anteriores |

> [!NOTE]
> Las 4 brechas son **conocidas** y están documentadas en [ESTADO_PROYECTO.md](file:///home/craos6518/Documentos/Proyecto%20Patrones%20de%20dise%C3%B1o/dungeon-crawler-patterns/Docs/ESTADO_PROYECTO.md) como ítems del backlog. No son errores nuevos, sino pendientes previos al alcance académico actual.

---

## Correcciones Propuestas para Documentación

1. **`DISEÑO.md`**: Agregar `ItemComponent` y `StunEffect`, actualizar observers a 3.
2. **[GDD.md](file:///home/craos6518/Documentos/Proyecto%20Patrones%20de%20dise%C3%B1o/dungeon-crawler-patterns/GDD.md)**: Marcar RF-08 como `Parcial (no implementado en flujo interactivo)`.
3. **[ESTADO_PROYECTO.md](file:///home/craos6518/Documentos/Proyecto%20Patrones%20de%20dise%C3%B1o/dungeon-crawler-patterns/Docs/ESTADO_PROYECTO.md)**: Confirmar conteo exacto de tests una vez que `mvn test` produzca salida legible.
