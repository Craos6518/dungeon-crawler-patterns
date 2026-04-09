# Dungeon Crawler Patterns

Estado documental:
- Tipo: indice operativo del repositorio
- Fuente de verdad de documentacion: docs/README.md

Proyecto academico en Java 17 orientado a demostrar patrones de diseno en runtime real (web y consola), con trazabilidad a codigo y pruebas.

## Proposito academico
- Evidenciar patrones ejecutandose en flujo productivo, no solo en demos.
- Mantener coherencia entre arquitectura, codigo y evidencia de tests.
- Permitir defensa tecnica y onboarding sin depender de lectura completa del codigo.

## Resumen arquitectonico
- Runtime unico: `GameRuntime`.
- Estado de sesion: `GameSession`.
- Flujo de pantallas: `GameStateContext` + `GameFlowState`.
- Persistencia: `GameSessionMementoMapper` + `GameCaretaker` + `RuntimeSaveSlotManager`.
- Eventos: `EventManager` + `EventContractValidator` + observers de sesion.

## Patrones implementados (runtime productivo)

| Patrón | Implementación | Runtime | Test |
|--------|---------------|--------|------|
| State | [`GameSession` + `GameStateContext`](src/main/java/game/application/state/GameSession.java) | Sí | [`GameRuntimeStateFlowIntegrationTest`](src/test/java/game/integration/behavioral/GameRuntimeStateFlowIntegrationTest.java) |
| Observer | [`EventManager` + `SessionEventFeedObserver`](src/main/java/game/application/state/GameSessionFactory.java) | Sí | [`EventObserversRuntimeIntegrationTest`](src/test/java/game/integration/behavioral/EventObserversRuntimeIntegrationTest.java) |
| Decorator | [`CombatStatusDecoratorPipeline`](src/main/java/game/domain/combat/CombatStatusDecoratorPipeline.java) | Sí | [`CombatDecoratorIntegrationTest`](src/test/java/game/unit/domain/combat/CombatDecoratorIntegrationTest.java) |
| Composite | [`Inventory` + `ItemComponent`](src/main/java/game/domain/inventory/Inventory.java) | Sí | [`UseItemUseCaseCompositeHierarchyTest`](src/test/java/game/unit/application/UseItemUseCaseCompositeHierarchyTest.java) |
| Builder (procedural) | [`ProceduralDungeonGenerator`](src/main/java/game/dungeon/builder/ProceduralDungeonGenerator.java) | Sí | [`ProceduralDungeonSeedDeterminismTest`](src/test/java/game/unit/creational/ProceduralDungeonSeedDeterminismTest.java) |
| Memento | [`GameSessionMementoMapper` + `RuntimeSaveSlotManager`](src/main/java/game/application/state/GameSessionMementoMapper.java) | Sí | [`GameRuntimeLoadGameTest`](src/test/java/game/unit/application/GameRuntimeLoadGameTest.java) |
| Strategy | [`CombatSystem` + `PlayerCombatStyle`](src/main/java/game/domain/combat/CombatSystem.java) | Sí | [`GameRuntimeExtendedCommandsTest`](src/test/java/game/unit/application/GameRuntimeExtendedCommandsTest.java) |

## Ejecucion

### Prerrequisito
```bash
source setup-java.sh
```

### Consola
```bash
./play.sh
```

Alternativa Maven:
```bash
mvn exec:java -Dexec.mainClass="game.InteractiveGame"
```

### Web (JavaFX + WebView)
```bash
./play-gui.sh
```

Alternativa Maven:
```bash
mvn javafx:run
```

## Testing

### Ejecutar tests
```bash
mvn test
```

### Interpretacion de resultados esperados
- Baseline academico: 241 tests en verde, 0 fallos, 2 omitidos.
- 241/241 indica consistencia funcional y arquitectonica en la suite activa.
- Los 2 omitidos no equivalen a fallo funcional.
- `target/surefire-reports/*` se trata como evidencia generada, no como fuente canonica de documentacion.

## Deuda tecnica conocida
- Cobertura E2E: parcial (hay pruebas E2E de contrato runtime, falta E2E visual de navegador completo).
- STUBs explicitos en `GameRuntime`:
	- `rerenderCurrentScreen`
	- `filterCategory`
- `selectSaveSlot`: sin deuda abierta funcional; gestion centralizada en `RuntimeSaveSlotManager`.

## Documentacion canonica
- Producto: [docs/01-product/GDD_CANONICO.md](docs/01-product/GDD_CANONICO.md)
- Arquitectura: [docs/02-architecture/ARQUITECTURA_RUNTIME.md](docs/02-architecture/ARQUITECTURA_RUNTIME.md)
- Patrones: [docs/03-patterns](docs/03-patterns)
- Testing: [docs/04-testing/ESTRATEGIA_TESTING.md](docs/04-testing/ESTRATEGIA_TESTING.md)
- Auditoria vigente: [docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md](docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md)
- Inventario y migracion documental: [docs/06-reference](docs/06-reference)
