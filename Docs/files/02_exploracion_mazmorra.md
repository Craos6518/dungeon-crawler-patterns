# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/README.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# Mockup 02 — Exploración de Mazmorra

**Patrón relacionado:** State (`ExplorationState`), Observer (eventos de sala), Facade  
**Estado del juego:** `EXPLORACION`

---

## Vista general

```
mazmorra — tema fuego                              sala 3/9 | hp: 72/100
```

---

## Mapa de salas

```
┌──────────────────────────────────────────────────────────────────┐
│  mapa de la mazmorra                                             │
│                                                                  │
│   ┌──────┐     ┌──────┐     ┌──────┐                            │
│   │  🏁  │─────│  👜  │─────│  ⚔️  │  ← sala actual (azul)      │
│   │entrad│     │tesor.│     │ aqui │                            │
│   └──────┘     └──────┘     └──┬───┘                            │
│                                │                                 │
│   ┌──────┐     ┌──────┐     ┌──┴───┐                            │
│   │  ❓  │─────│  ❓  │─────│  🧪  │                            │
│   │  ?   │     │  ?   │     │objet.│                            │
│   └──────┘     └──────┘     └──┬───┘                            │
│                                │                                 │
│                             ┌──┴───┐                            │
│                             │  💀  │  ← jefe (borde rojo)       │
│                             │ jefe │                            │
│                             └──────┘                            │
│                                                                  │
│  leyenda:  [azul] actual  [gris] visitada  [rojo] jefe  [?] bloqueada │
└──────────────────────────────────────────────────────────────────┘
```

---

## Panel de descripción de sala

```
┌──────────────────────────────────────────────────────────────────┐
│  sala 3 — camara de lava                                         │
│                                                                  │
│  El calor es sofocante. Chorros de lava iluminan las paredes     │
│  de roca negra. En el centro, un Drake de Lava bloquea el        │
│  paso hacia las profundidades.                                   │
│                                                                  │
│  [ enemigo presente ]  [ objeto cercano ]                        │
└──────────────────────────────────────────────────────────────────┘
```

---

## Estado del héroe (HUD)

```
┌──────────────────────────────────────────────────────────────────┐
│  estado del heroe                                                │
│                                                                  │
│  🗡️  Guerrero                            [ fortaleza activa ]    │
│      hp 72/100  ·  ataque 18  ·  defensa 25                     │
└──────────────────────────────────────────────────────────────────┘
```

---

## Acciones de exploración

```
┌─────────────────────┐  ┌─────────────────────┐
│  [ E ] explorar     │  │  [ C ] combatir      │
│        sala         │  │                      │
└─────────────────────┘  └─────────────────────┘
┌─────────────────────┐  ┌─────────────────────┐
│  [ I ] inventario   │  │  [ G ] guardar       │
│                     │  │        partida       │
└─────────────────────┘  └─────────────────────┘
┌──────────────────────────────────────────────┐
│  [ → ] avanzar a siguiente sala              │
└──────────────────────────────────────────────┘
```

---

## Tipos de sala

| Icono | Tipo         | Descripción                                  |
|-------|--------------|----------------------------------------------|
| 🏁    | Entrada      | Punto de inicio de la mazmorra               |
| ⚔️    | Combate      | Contiene uno o más enemigos                  |
| 👜    | Tesoro       | Objetos y recompensas disponibles            |
| 🧪    | Objeto       | Ítem especial o consumible                   |
| 💀    | Jefe         | Enemigo de alto nivel, recompensa mayor      |
| ❓    | Bloqueada    | No explorada, contenido desconocido          |

---

## Notas de implementación

| Elemento           | Patrón / Clase Java                              |
|--------------------|--------------------------------------------------|
| Estado de juego    | `ExplorationState implements GameState`          |
| Generación de mapa | `DungeonBuilder` (patrón Builder)                |
| Eventos de sala    | `RoomEventObserver`, `GameEventBus` (Observer)   |
| Acceso simplificado| `DungeonFacade` (patrón Facade)                  |

---

## Temas de mazmorra disponibles

```
Mazmorra 1: Fuego     → criaturas de lava, jefes draconicos
Mazmorra 2: Hielo     → entidades de frio y control
Mazmorra 3: Veneno    → criaturas toxicas y desgaste
Mazmorra 4: Oscuridad → enemigos de daño sostenido
```
