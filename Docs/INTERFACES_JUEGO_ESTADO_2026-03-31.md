# Interfaces del juego - Estado actual (2026-03-31)

Este documento consolida las interfaces de usuario del proyecto, separando lo implementado, lo parcial y lo pendiente.

## 1) Interfaces de usuario existentes

### 1.1 Interfaz Web (JavaFX WebView + HTML)
Base tecnica:
- src/main/resources/ui/game.html
- src/main/java/game/ui/GameWebApplication.java
- src/main/java/game/ui/integration/UiCommandDispatcher.java
- src/main/java/game/application/runtime/GameRuntime.java

Estado: Implementada y operativa.

Pantallas implementadas:
1. Menu principal (screen-menu)
2. Exploracion (screen-exploration)
3. Combate (screen-combat)
4. Inventario (screen-inventory)

Comandos funcionales conectados:
- startGame, loadGame, saveGame
- advanceRoom, searchTreasure, forceCombat
- attack, defend, useSkill, useItem
- openInventory, closeInventory

### 1.2 Interfaz Consola
Base tecnica:
- src/main/java/game/InteractiveGame.java
- src/main/java/game/ui/console/ConsoleGameAdapter.java

Estado: Implementada (adaptador legado), funcional para flujo basico runtime.

Flujos disponibles:
- Exploracion/combate/inventario
- Guardar/cargar slot 1-3
- Forzar combate

## 2) Interfaces implementadas parcialmente

### 2.1 Filtro de categorias de inventario
Evidencia:
- game.html usa data-action="filterCategory" solo en cliente.
- GameRuntime registra filterCategory como No-op.

Estado: Parcial.
Detalle:
- Visualmente existe, pero no hay logica de filtrado real en backend.

### 2.2 Seleccion de item en inventario
Evidencia:
- game.html maneja selectItem de forma local JS.
- Runtime tiene comando selectItem, pero en web no se despacha actualmente.

Estado: Parcial.
Detalle:
- La seleccion visual funciona, pero no sincroniza siempre con backend via comando dedicado.

### 2.3 Accion rerenderCurrentScreen
Evidencia:
- Registrada en GameRuntime como No-op.

Estado: Parcial / reservada.
Detalle:
- Existe en contrato, pero no ejecuta logica porque la UI ya recibe estado completo en cada push.

### 2.4 Menu principal en consola
Evidencia:
- ConsoleGameAdapter no expone flujo especifico de seleccion de tema/startGame como la UI web.

Estado: Parcial.
Detalle:
- Consola opera bien en gameplay, pero la experiencia de menu no esta al mismo nivel que web.

## 3) Interfaces pendientes por implementar

Pendientes detectados al contrastar mockups Docs/files/01..06 con la UI real.

1. Seleccion de heroe/clase (Guerrero/Mago/Arquero) en UI web.
- En web actual se selecciona tema de mazmorra, no heroe.

2. Pantalla de estadisticas del jugador/partida desde menu.
- Presente en mockup 01 como accion, no presente en game.html.

3. Vista de IA (debug academico de Strategy + Observer).
- Definida en mockup 05, no existe pantalla dedicada en UI web.

4. Pantalla de tesoro post-victoria dedicada.
- Mockup 06 define vista de loot/seleccion; actualmente el flujo pasa por eventos/inventario sin pantalla propia.

5. Pantalla de slots avanzada (metadatos por ranura).
- Mockup 06 propone lista con estado de ranuras y resumen (fecha, hp, sala, tema).
- UI actual ofrece botones Cargar Slot 1/2/3 y selector para Guardar, sin panel detallado.

6. Pantalla de Game Over dedicada.
- Mockup 06 define opciones [R]/[M]/[N].
- UI web actual solo maneja pantallas menu/exploration/combat/inventory.

7. Inventario tipo arbol Composite en UI web.
- Mockup 04 propone navegacion jerarquica (contenedores/hijos).
- UI actual presenta lista plana de items.

## 4) Resumen ejecutivo

Implementado hoy y estable:
- UI web principal de 4 pantallas (menu, exploracion, combate, inventario).
- Runtime de comandos unificado.
- Modo consola funcional para juego basico.

Brechas principales:
- Pantallas academicas avanzadas (IA debug, tesoro dedicada, game over dedicada).
- Inventario jerarquico visual y filtros reales.
- Flujo de menu/heroe/estadisticas completo en web y alineado en consola.
