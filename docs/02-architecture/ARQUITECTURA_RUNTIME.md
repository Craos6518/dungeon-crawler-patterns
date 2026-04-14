# Arquitectura Runtime Canonica

- Fecha de creacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra
- Estado: vigente

## Objetivo
Describir la arquitectura que efectivamente corre en produccion (web y consola), excluyendo demos historicas.

## Navegacion documental

- Producto (GDD): docs/01-product/GDD_CANONICO.md
- Arquitectura: docs/02-architecture/ARQUITECTURA_RUNTIME.md
- Diagramas (galeria PNG + fuentes PlantUML): docs/02-architecture/diagramas/README.md
- Patrones: docs/03-patterns/README.md
- Testing: docs/04-testing/ESTRATEGIA_TESTING.md
- Auditoria: docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md

## Visualizacion de diagramas
- Las fuentes de diagramas en `docs/02-architecture/diagramas/*.txt` usan sintaxis PlantUML.
- Para visualizacion inmediata, abrir `docs/02-architecture/diagramas/README.md` (incluye los PNG).
- Para editar/re-renderizar fuentes, usar soporte PlantUML en VS Code.

## Componentes principales
- `game.application.runtime.GameRuntime`: orquestador de comandos UI -> casos de uso.
- `game.application.state.GameSession`: estado de sesion runtime.
- `game.state.game.GameStateContext`: contexto State para flujo de pantallas.
- `game.application.state.GameFlowState`: estados tipados de flujo.
- `game.ui.integration.GamePresenter`: mapeo estado interno -> `GameViewModel`.
- `game.application.runtime.RuntimePayloadValidator`: validacion estructural/tipada de comandos.
- `game.application.runtime.RuntimeSaveSlotManager`: save/load por slots y slot preferido.
- `game.application.runtime.CampaignSessionCoordinator`: reglas de campana y continuidad de heroe.

## Flujo canonicamente soportado
1. UI envia comando JSON.
2. `UiCommandDispatcher` valida estructura basica y delega a `GameRuntime`.
3. `GameRuntime` aplica validador de payload, ejecuta caso de uso y sincroniza sesion.
4. `GamePresenter` genera `GameViewModel` completo.
5. Web/Consola renderiza el estado actual.

## Fuentes de verdad
- Flujo de pantallas: `GameStateContext`.
- Estado de sesion: `GameSession`.
- Persistencia: `GameSessionMementoMapper` + `GameCaretaker`.
- Contrato de eventos: `EventContract` + `EventContractValidator`.

## Decisiones arquitectonicas cerradas
- `activeScreen` se mantiene como valor derivado para interoperabilidad, no como controlador de flujo.
- Observer productivo se registra en `GameSessionFactory` (`SessionEventFeedObserver`, `SessionEventCounterObserver`).
- Loop combate -> tesoro -> progresion esta integrado en `GameSession` y casos de uso, sin stubs funcionales en ese tramo.

## Deuda tecnica aceptada
- STUBs backend de UI:
  - `rerenderCurrentScreen`
  - `filterCategory`
- Cobertura E2E parcial (hay pruebas E2E de contrato, no automatizacion de navegador real end-to-end visual).
