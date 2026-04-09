# Inventario Normalizado de Documentacion

- Fecha de creacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra
- Estado: vigente

## 1) Reprocesamiento del inventario
- Archivos detectados por extension documental: 73.
- `.md`: 24.
- `.txt`: 49.
- `.adoc`: 0.

## 2) Reclasificacion obligatoria
`target/surefire-reports/*` no se clasifica como documentacion canonica.

Clasificacion correcta:
- Evidencia generada (no versionable).
- Uso: respaldo de ejecuciones puntuales.
- No uso: fuente de verdad de metricas en documentos permanentes.

## 3) Fragmentacion detectada (explicita)
Existe fragmentacion del GDD en tres archivos superpuestos:
- `Docs/GDD.md`
- `Docs/Extracto_Proyecto_DungeonCrawler.md`
- `Docs/GDD_Fichas_Primera_Persona.md`

Decision canonica:
- Consolidar contenido de producto en `docs/01-product/GDD_CANONICO.md`.
- Mantener los tres archivos originales como legacy (sin eliminarlos).

## 4) Estado documental por archivo (vigente/historico/obsoleto)

### Vigente (canonico)
- `README.md`
- `docs/01-product/GDD_CANONICO.md`
- `docs/02-architecture/ARQUITECTURA_RUNTIME.md`
- `docs/03-patterns/*.md`
- `docs/04-testing/ESTRATEGIA_TESTING.md`
- `docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md`
- `docs/06-reference/INVENTARIO_NORMALIZADO_2026-04-04.md`
- `docs/06-reference/MAPA_MIGRACION_2026-04-04.md`

### Historico
- `Docs/AUDITORIA_TECNICA_FORMAL_2026-04-04.md`
- `Docs/CAMBIOS_REALIZADOS_2026-03-31.md`
- `Docs/DESARROLLO.md`
- `Docs/DISEÑO.md`
- `Docs/ESTADO_PROYECTO.md`
- `Docs/EVENTOS.md`
- `Docs/GUIA_USUARIO.md`
- `Docs/INTERFACES_JUEGO_ESTADO_2026-03-31.md`
- `Docs/review_report.md`
- `Docs/files/*.md`
- `Docs/Diagramas/*.txt`
- `.vscode/README.md`

### Obsoleto (conservado por trazabilidad)
- `Docs/GDD.md` (consolidado)
- `Docs/Extracto_Proyecto_DungeonCrawler.md` (consolidado)
- `Docs/GDD_Fichas_Primera_Persona.md` (consolidado)
- `Docs/PROMPTS_DEFINITIVOS_UI.md` (no fuente de verdad de producto)
- `.github/copilot-instructions.md` (instrucciones de asistencia, no documento de producto)

## 5) Priorizacion de Javadoc faltante

### Critico (runtime/state/persistencia)
Clases objetivo criticas ya normalizadas en esta actualizacion:
- `src/main/java/game/application/runtime/RuntimePayloadValidator.java`
- `src/main/java/game/application/runtime/RuntimeSaveSlotManager.java`
- `src/main/java/game/application/runtime/CampaignSessionCoordinator.java`
- `src/main/java/game/events/observer/EventContractValidator.java`
- `src/main/java/game/domain/combat/CombatStatusDecoratorPipeline.java`
- `src/main/java/game/state/game/GameStateContext.java`
- `src/main/java/game/application/state/GameFlowState.java`

### Medio (logica de dominio)
Pendiente de documentacion Javadoc profunda en clases de dominio legado:
- `src/main/java/game/domain/personaje/Personaje.java`
- `src/main/java/game/domain/personaje/Guerrero.java`
- `src/main/java/game/domain/personaje/Mago.java`
- `src/main/java/game/domain/personaje/Arquero.java`
- `src/main/java/game/domain/personaje/EnemigoBasico.java`
- `src/main/java/game/domain/personaje/Orco.java`
- `src/main/java/game/domain/personaje/Dragon.java`

### Bajo (package-info/tests)
- `package-info` sin Javadoc en modulos de soporte.
- multiples clases de test sin Javadoc de cabecera.

## 6) Deuda real identificada
- STUBs en `GameRuntime`: `rerenderCurrentScreen`, `filterCategory` (documentados explicitamente).
- Cobertura E2E parcial: hay validacion E2E de contrato runtime, falta automatizacion visual full-browser.
