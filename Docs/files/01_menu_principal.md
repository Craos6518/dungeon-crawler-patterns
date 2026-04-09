# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/README.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# Mockup 01 — Menú Principal / Selección de Héroe

**Patrón relacionado:** Factory Method (creación de héroes), Memento (cargar partida)  
**Estado del juego:** `MENU_PRINCIPAL`

---

## Vista general

```
┌─────────────────────────────────────────────────────────────────┐
│           patrones de diseño — java 17                          │
│                                                                 │
│           dungeon crawler academico                             │
│           elige tu heroe para comenzar                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## Panel de selección de héroes

```
┌───────────────────┐  ┌───────────────────┐  ┌───────────────────┐
│       🗡️           │  │       🔮           │  │       🏹           │
│     Guerrero      │  │      Mago         │  │     Arquero       │
│  alta vida y      │  │  menor vida,      │  │  balance daño y   │
│  resistencia      │  │  alto daño        │  │  supervivencia    │
│                   │  │                   │  │                   │
│ vida       ██████ │  │ vida    ███░░░░   │  │ vida   █████░░   │
│            100    │  │          55       │  │         75        │
│ ataque  ████░░░   │  │ ataque  ███████   │  │ ataque █████░░   │
│           18      │  │           30      │  │          24       │
│ defensa ████████  │  │ defensa █░░░░░░   │  │ defensa ████░░   │
│           25      │  │            8      │  │          15       │
│ veloc.  ███░░░░   │  │ veloc.  █████░░   │  │ veloc.  █████░   │
│           10      │  │           22      │  │          20       │
│                   │  │                   │  │                   │
│  [ SELECCIONADO ] │  │                   │  │                   │
└───────────────────┘  └───────────────────┘  └───────────────────┘
       ^^ borde azul indica selección activa
```

---

## Acciones del menú

```
┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│  [ 1 ] nueva        │  │  [ 2 ] cargar       │  │  [ 3 ] ver          │  │  [ 4 ] salir        │
│        partida      │  │        partida      │  │        estadisticas │  │                     │
└─────────────────────┘  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘
   ^^ resaltado azul
```

---

## Notas de implementación

| Elemento              | Patrón / Clase Java                          |
|-----------------------|----------------------------------------------|
| Selección de héroe    | `PersonajeFactory`, `GuerreroFactory`, `MagoFactory`, `ArqueroFactory` |
| Flujo de nueva partida| `InteractiveGame.nuevaPartida()`             |
| Carga de partida      | `GameMemento`, `GameCaretaker`, `InteractiveGame.cargarPartida()` |
| Opción de estadísticas| `InteractiveGame.mostrarEstadisticas()`      |

---

## Flujo de navegación

```
Menú Principal
    │
    ├── [1] Nueva partida → seleccionar héroe → elegir tema → exploración
    ├── [2] Cargar partida → GameMemento.restore() → estado restaurado en sesión
    ├── [3] Ver estadísticas → resumen global → menú principal
    └── [4] Salir → cierre seguro del loop principal
```
