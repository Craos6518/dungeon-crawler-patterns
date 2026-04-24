# Dungeon Crawler Patterns

Estado documental:

- Tipo: indice operativo del repositorio
- Fuente de verdad de documentacion: docs/README.md

Proyecto academico en Java 17 orientado a demostrar patrones de diseno en runtime real (web y consola), con trazabilidad a codigo y pruebas.

## Proposito academico

- Evidenciar patrones ejecutandose en flujo productivo, no solo en demos.
- Mantener coherencia entre arquitectura, codigo y evidencia de tests.
- Permitir defensa tecnica y onboarding sin depender de lectura completa del codigo.

## Fuente de verdad por concepto

| Concepto                      | Fuente de verdad unica                         |
| ----------------------------- | ---------------------------------------------- |
| Indice documental             | `docs/README.md`                               |
| Producto (GDD)                | `docs/01-product/GDD_CANONICO.md`              |
| Arquitectura                  | `docs/02-architecture/ARQUITECTURA_RUNTIME.md` |
| Patrones                      | `docs/03-patterns/*.md`                        |
| Testing y metricas operativas | `docs/04-testing/ESTRATEGIA_TESTING.md`        |
| Auditoria de cierre           | `docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md` |

Decision de centralizacion para patrones:

- Modelo canonico vigente: documentos por patron (`docs/03-patterns/*.md`).
- `docs/03-patterns/PATRONES_UNIFICADOS.md` se mantiene como documento derivado de consulta rapida (no canonico).

## Resumen arquitectonico

- Runtime unico: `GameRuntime`.
- Estado de sesion: `GameSession`.
- Flujo de pantallas: `GameStateContext` + `GameFlowState`.
- Persistencia: `GameSessionMementoMapper` + `GameCaretaker` + `RuntimeSaveSlotManager`.
- Eventos: `EventManager` + `EventContractValidator` + observers de sesion.

## Patrones implementados (11 en el proyecto)

| Patrón               | Implementación                                                                                                              | Evidencia principal                                                                                                           | Artefacto visual estable                                                                                                                                              |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| State                | [`GameSession` + `GameStateContext`](src/main/java/game/application/state/GameSession.java)                                 | [`GameRuntimeStateFlowIntegrationTest`](src/test/java/game/integration/behavioral/GameRuntimeStateFlowIntegrationTest.java)   | [`DIAGRAMA DE ESTADOS – Combate.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20ESTADOS%20%E2%80%93%20Combate.png)                                               |
| Observer             | [`EventManager` + `SessionEventFeedObserver`](src/main/java/game/application/state/GameSessionFactory.java)                 | [`EventObserversRuntimeIntegrationTest`](src/test/java/game/integration/behavioral/EventObserversRuntimeIntegrationTest.java) | [`DIAGRAMA INTEGRADO COMPLETO.png`](docs/02-architecture/diagramas/DIAGRAMA%20INTEGRADO%20COMPLETO.png)                                                               |
| Decorator            | [`CombatStatusDecoratorPipeline`](src/main/java/game/domain/combat/CombatStatusDecoratorPipeline.java)                      | [`CombatDecoratorIntegrationTest`](src/test/java/game/unit/domain/combat/CombatDecoratorIntegrationTest.java)                 | [`DIAGRAMA – SUBSISTEMA DECORATOR.png`](docs/02-architecture/diagramas/DIAGRAMA%20%E2%80%93%20SUBSISTEMA%20DECORATOR.png)                                             |
| Composite            | [`Inventory` + `ItemComponent`](src/main/java/game/domain/inventory/Inventory.java)                                         | [`UseItemUseCaseCompositeHierarchyTest`](src/test/java/game/unit/application/UseItemUseCaseCompositeHierarchyTest.java)       | [`DIAGRAMA DE CLASES – Arquitectura Base Completa.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20Arquitectura%20Base%20Completa.png)       |
| Builder (procedural) | [`ProceduralDungeonGenerator`](src/main/java/game/dungeon/builder/ProceduralDungeonGenerator.java)                          | [`BuilderPatternTest`](src/test/java/game/unit/creational/BuilderPatternTest.java)                                            | [`DIAGRAMA DE CLASES – Arquitectura Base Completa.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20Arquitectura%20Base%20Completa.png)       |
| Memento              | [`GameSessionMementoMapper` + `RuntimeSaveSlotManager`](src/main/java/game/application/state/GameSessionMementoMapper.java) | [`MementoPatternTest`](src/test/java/game/unit/behavioral/MementoPatternTest.java)                                            | [`DIAGRAMA INTEGRADO COMPLETO.png`](docs/02-architecture/diagramas/DIAGRAMA%20INTEGRADO%20COMPLETO.png)                                                               |
| Strategy             | [`CombatSystem` + `AIStrategy`](src/main/java/game/domain/combat/CombatSystem.java)                                         | [`StrategyPatternTest`](src/test/java/game/unit/behavioral/StrategyPatternTest.java)                                          | [`DIAGRAMA DE CLASES – SISTEMA DE COMBATE DETALLADO.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20SISTEMA%20DE%20COMBATE%20DETALLADO.png) |
| Factory Method       | [`PersonajeFactory` + factories concretas](src/main/java/game/domain/personaje/factory/PersonajeFactory.java)               | [`FactoryMethodTest`](src/test/java/game/unit/creational/FactoryMethodTest.java)                                              | [`DIAGRAMA DE CLASES – Arquitectura Base Completa.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20Arquitectura%20Base%20Completa.png)       |
| Abstract Factory     | [`DungeonThemeFactory` + themes concretos](src/main/java/game/dungeon/theme/DungeonThemeFactory.java)                       | [`AbstractFactoryTest`](src/test/java/game/unit/creational/AbstractFactoryTest.java)                                          | [`DIAGRAMA DE CLASES – Arquitectura Base Completa.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20Arquitectura%20Base%20Completa.png)       |
| Facade               | [`CombatFacade`](src/main/java/game/patterns/combat/facade/CombatFacade.java)                                               | [`FacadePatternTest`](src/test/java/game/unit/structural/FacadePatternTest.java)                                              | [`DIAGRAMA DE CLASES – SISTEMA DE COMBATE DETALLADO.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20SISTEMA%20DE%20COMBATE%20DETALLADO.png) |
| Command              | [`Command` + `CommandInvoker`](src/main/java/game/patterns/command/actions/Command.java)                                    | [`CommandPatternTest`](src/test/java/game/unit/behavioral/CommandPatternTest.java)                                            | [`DIAGRAMA DE CLASES – SISTEMA DE COMBATE DETALLADO.png`](docs/02-architecture/diagramas/DIAGRAMA%20DE%20CLASES%20%E2%80%93%20SISTEMA%20DE%20COMBATE%20DETALLADO.png) |

Para evitar divergencias, la metrica oficial de pruebas se mantiene unicamente en:

- `docs/04-testing/ESTRATEGIA_TESTING.md` (fecha de corte + sello de ejecucion).

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
mvn exec:java -Dexec.mainClass="game.ui.GameWebApplication"
```

### Web (JavaFX + WebView)

```bash
./play-gui.sh
```

Alternativa Maven:

```bash
mvn exec:java -Dexec.mainClass="game.ui.GameWebApplication"
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

- Fuente unica de metricas: `docs/04-testing/ESTRATEGIA_TESTING.md`.
- La auditoria referencia esa misma fuente para evitar divergencia de conteos.
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
