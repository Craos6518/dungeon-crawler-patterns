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

## Patrones implementados (11 en el proyecto)

| Patrón | Implementación | Evidencia principal |
|--------|---------------|---------------------|
| State | [`GameSession` + `GameStateContext`](src/main/java/game/application/state/GameSession.java) | [`GameRuntimeStateFlowIntegrationTest`](src/test/java/game/integration/behavioral/GameRuntimeStateFlowIntegrationTest.java) |
| Observer | [`EventManager` + `SessionEventFeedObserver`](src/main/java/game/application/state/GameSessionFactory.java) | [`EventObserversRuntimeIntegrationTest`](src/test/java/game/integration/behavioral/EventObserversRuntimeIntegrationTest.java) |
| Decorator | [`CombatStatusDecoratorPipeline`](src/main/java/game/domain/combat/CombatStatusDecoratorPipeline.java) | [`CombatDecoratorIntegrationTest`](src/test/java/game/unit/domain/combat/CombatDecoratorIntegrationTest.java) |
| Composite | [`Inventory` + `ItemComponent`](src/main/java/game/domain/inventory/Inventory.java) | [`UseItemUseCaseCompositeHierarchyTest`](src/test/java/game/unit/application/UseItemUseCaseCompositeHierarchyTest.java) |
| Builder (procedural) | [`ProceduralDungeonGenerator`](src/main/java/game/dungeon/builder/ProceduralDungeonGenerator.java) | [`BuilderPatternTest`](src/test/java/game/unit/creational/BuilderPatternTest.java) |
| Memento | [`GameSessionMementoMapper` + `RuntimeSaveSlotManager`](src/main/java/game/application/state/GameSessionMementoMapper.java) | [`MementoPatternTest`](src/test/java/game/unit/behavioral/MementoPatternTest.java) |
| Strategy | [`CombatSystem` + `AIStrategy`](src/main/java/game/domain/combat/CombatSystem.java) | [`StrategyPatternTest`](src/test/java/game/unit/behavioral/StrategyPatternTest.java) |
| Factory Method | [`PersonajeFactory` + factories concretas](src/main/java/game/domain/personaje/factory/PersonajeFactory.java) | [`FactoryMethodTest`](src/test/java/game/unit/creational/FactoryMethodTest.java) |
| Abstract Factory | [`DungeonThemeFactory` + themes concretos](src/main/java/game/dungeon/theme/DungeonThemeFactory.java) | [`AbstractFactoryTest`](src/test/java/game/unit/creational/AbstractFactoryTest.java) |
| Facade | [`CombatFacade`](src/main/java/game/combat/facade/CombatFacade.java) | [`FacadePatternTest`](src/test/java/game/unit/structural/FacadePatternTest.java) |
| Command | [`Command` + `CommandInvoker`](src/main/java/game/command/actions/Command.java) | [`CommandPatternTest`](src/test/java/game/unit/behavioral/CommandPatternTest.java) |

Suite de evidencia ejecutada para estos 11 patrones:
- 103 tests
- 0 fallos
- 0 errores
- BUILD SUCCESS

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

## Empaquetado nativo (jpackage)

Objetivo del empaquetado:
- Incluir runtime propio (JRE) generado con `jlink`.
- Incluir JavaFX dentro del runtime de la app.
- Evitar dependencia de Java instalado en el sistema objetivo.

### Linux (.deb / .rpm)

Script incluido:
```bash
./package-linux.sh
```

Variantes:
```bash
./package-linux.sh --type deb
./package-linux.sh --type rpm
./package-linux.sh --type app-image
```

Salida:
- `target/packages/*.deb`
- `target/packages/*.rpm`

Notas:
- Para `.deb` se requiere `dpkg-deb`.
- Para `.rpm` se requiere `rpmbuild`.

### Windows (.exe)

Script incluido (PowerShell):
```powershell
.\package-windows.ps1
```

Variantes:
```powershell
.\package-windows.ps1 -Type exe
.\package-windows.ps1 -Type app-image
```

Salida:
- `target/packages/*.exe`

Notas:
- Para `.exe`, `jpackage` requiere WiX Toolset en `PATH`.
- El `.exe` debe generarse en Windows.

## Testing

### Ejecutar tests
```bash
mvn test
```

### Interpretacion de resultados esperados
- Baseline academico: 205 tests en verde, 0 fallos, 0 omitidos.
- 205/205 indica consistencia funcional y arquitectonica en la suite activa.
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
