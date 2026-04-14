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
- `docs/legacy/GDD.md` (retirado)
- `docs/legacy/Extracto_Proyecto_DungeonCrawler.md` (retirado)
- `docs/legacy/GDD_Fichas_Primera_Persona.md` (retirado)

Decision canonica:
- Consolidar contenido de producto en `docs/01-product/GDD_CANONICO.md`.
- Retirar los tres archivos originales del repositorio activo para eliminar duplicacion.

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
- `docs/01-product/Especificacion_Requerimientos_Sistema_ISO29148.md`
- `docs/02-architecture/diagramas/*.png`
- `docs/02-architecture/diagramas/*.txt`
- `.vscode/README.md`

### Obsoleto (conservado por trazabilidad)
- derivados legacy retirados de `Docs/` tras la consolidacion del 2026-04-10
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
