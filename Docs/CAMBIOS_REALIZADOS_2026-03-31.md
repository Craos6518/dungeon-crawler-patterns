# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/05-audit/AUDITORIA_CIERRE_2026-04-04.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# Cambios realizados - Persistencia y carga robusta (2026-03-31)

## Objetivo
Endurecer el sistema de guardado/carga para que no dependa del happy path y responda con errores de dominio claros en vez de errores tecnicos de Java.

## Problemas detectados
1. Carga de slot inexistente terminaba en FileNotFoundException.
2. Carga de guardado valido podia fallar por incompatibilidad de estructura al restaurar sobre sesion/dungeon distinta.
3. Falta de clasificacion de errores (slot vacio vs guardado corrupto vs error de I/O).

## Cambios implementados

### 1) Carga por tema del guardado (evita mismatch de dungeon)
Archivo: src/main/java/game/application/runtime/GameRuntime.java

- Se reforzo el flujo de loadGame para:
  - validar existencia de slot antes de leer,
  - leer memento,
  - resolver tema desde el memento,
  - crear nueva sesion con ese tema,
  - restaurar estado sobre esa sesion.

Resultado:
- Se evita el falso "guardado corrupto" cuando la sesion activa no coincide con la estructura del save.

### 2) Prevalidacion de slot en caso de uso
Archivo: src/main/java/game/application/usecase/LoadGameUseCase.java

- Se agrego validacion de existencia fisica del archivo antes de cargar.
- Se mantiene restauracion transaccional via restoreFromMemento.

Resultado:
- Si el slot no existe, el flujo falla de forma controlada con error de dominio.

### 3) Hardening del GameCaretaker
Archivo: src/main/java/game/persistence/memento/GameCaretaker.java

- Se refactorizo resolucion de ruta con metodos:
  - resolveSaveFile
  - normalizeSaveName
- Se agregaron validaciones:
  - nombre de archivo invalido,
  - archivo inexistente,
  - archivo no legible,
  - formato no compatible/corrupto.
- Se reemplazo RuntimeException generica por errores de dominio especificos.
- Se agrego existeEnDisco para validaciones de precondicion.

Resultado:
- Clasificacion robusta de fallos y mensajes de negocio legibles.

### 4) Excepciones de dominio especificas para persistencia
Archivos:
- src/main/java/game/persistence/memento/SaveSlotNotFoundException.java
- src/main/java/game/persistence/memento/SaveDataCorruptionException.java

- SaveSlotNotFoundException: slot vacio/no existente.
- SaveDataCorruptionException: archivo corrupto o formato incompatible.

Resultado:
- Los errores de persistencia quedan tipados y diferenciables.

### 5) Validacion estructural del memento
Archivo: src/main/java/game/application/state/GameSessionMementoMapper.java

- Al guardar se agrego metadata estructural:
  - schemaVersion
  - totalRooms
- Al restaurar en modo estricto se valida:
  - secciones obligatorias presentes,
  - schemaVersion soportado,
  - totalRooms valido,
  - compatibilidad de estructura de mazmorra.

Resultado:
- Se protege la invariante de restauracion y se detectan guardados incompatibles con motivo explicito.

## Pruebas agregadas/actualizadas

### Save/Load use case
Archivo: src/test/java/game/unit/application/SaveLoadUseCaseTest.java

- Nuevo caso: loadRejectsMissingSlotAndKeepsSessionUntouched.
- Verifica:
  - mensaje de "Slot vacio",
  - estado de sesion no mutado si falla la carga.

### Memento
Archivo: src/test/java/game/unit/behavioral/MementoPatternTest.java

- Nuevo caso: carga de slot inexistente lanza error de dominio.
- Nuevo caso: carga de archivo corrupto lanza error de dominio.

## Resultado de validacion
Ejecucion de pruebas objetivo con resultado exitoso:
- SaveLoadUseCaseTest
- MementoPatternTest
- GameRuntimeLoadGameTest
- UiCommandDispatcherContractTest

Estado final:
- Persistencia mas robusta ante slot faltante, archivo corrupto e incompatibilidad estructural.
- Mensajes de error alineados con dominio.
- Sin regresion detectada en flujo de UI/Runtime cubierto por tests.
