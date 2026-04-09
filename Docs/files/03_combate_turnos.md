# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/README.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# Mockup 03 — Combate por Turnos

**Patrón relacionado:** Command (acciones), State (`CombatState`), Observer (log), Strategy (IA enemigo)  
**Estado del juego:** `COMBATE`

---

## Vista general

```
sala 3 — caverna de fuego                                [ tema: fuego ]
```

---

## Combatientes

```
┌─────────────────────────────────┐       ┌─────────────────────────────────┐
│  🗡️  Guerrero                   │       │  🐉  Drake de Lava              │
│      turno: jugador             │  vs   │      enemigo jefe               │
│      [ fortaleza activa ]       │       │                                 │
│                                 │       │                                 │
│  vida                           │       │  vida                           │
│  ████████████████████░░░░░░░░   │       │  ██████████████░░░░░░░░░░░░░░   │
│  72 / 100 hp        ataque: 18  │       │  43 / 100 hp        ataque: 22  │
└─────────────────────────────────┘       └─────────────────────────────────┘
         ^^ barra verde                            ^^ barra rojo-naranja
```

---

## Acciones del jugador

```
┌──────────────────────────────────────┐
│  acciones del jugador                │
│                                      │
│  > [ 1 ] atacar          ← activa   │
│    [ 2 ] defender                    │
│    [ 3 ] usar objeto                 │
│    [ 4 ] usar habilidad              │
│                                      │
│  ingresa el numero de accion         │
└──────────────────────────────────────┘
```

---

## Registro de combate

```
┌──────────────────────────────────────┐
│  registro de combate                 │
│                                      │
│  turno 4 — jugador ataca             │
│  Drake recibe 18 de daño         🔴  │
│  Drake contraataca                   │
│  Guerrero recibe 22 de daño      🔴  │
│  [veneno] Drake pierde 5 hp/turno 🟣 │
│  [fortaleza] defensa +15 activa   🔵 │
│                                      │
│  turno 5 — esperando accion...       │
└──────────────────────────────────────┘
```

---

## Efectos de estado

| Efecto       | Objetivo  | Duración     | Valor          |
|--------------|-----------|--------------|----------------|
| fortaleza    | Guerrero  | 2 turnos     | defensa +15    |
| veneno       | Drake     | 3 turnos     | -5 hp/turno    |
| quemadura    | Drake     | 3 turnos     | -8 hp/turno    |

---

## Diagrama de flujo de turno

```
Inicio de turno
      │
      ├─ ¿turno del jugador?
      │       │
      │       └── mostrar acciones → esperar input
      │               │
      │               ├── [1] AtacarCommand.execute()
      │               ├── [2] DefenderCommand.execute()
      │               ├── [3] UsarObjetoCommand.execute()
      │               └── [4] UsarHabilidadCommand.execute()
      │
      └─ ¿turno del enemigo?
              │
              └── EnemyStrategy.decidirAccion()
                      │
                      ├── AggressiveStrategy → golpe fuerte
                      ├── DefensiveStrategy  → ataque + curación
                      └── RandomStrategy     → acción aleatoria
```

---

## Notas de implementación

| Elemento             | Patrón / Clase Java                              |
|----------------------|--------------------------------------------------|
| Acciones del jugador | `AttackCommand`, `DefendCommand` (Command)       |
| IA del enemigo       | `CombatStrategy` interface (Strategy)            |
| Log de combate       | `CombatLogger implements CombatObserver`         |
| Aplicación de efectos| `EffectDecorator` (Decorator)                    |
| Estado activo        | `CombatState implements GameState`               |

---

## Condiciones de fin de combate

```
Victoria  → hp enemigo = 0 → transición a sala de tesoro
Derrota   → hp jugador = 0 → transición a GameOverState
Huida     → acción especial → regresa a ExplorationState (penalización)
```
