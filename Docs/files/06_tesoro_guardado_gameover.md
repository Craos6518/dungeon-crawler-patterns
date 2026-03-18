# Mockup 06 — Tesoro / Guardado / Game Over

**Patrón relacionado:** Memento (guardado/carga), State (`GameOverState`)  
**Estados del juego:** `COMBATE` → victoria, `GAME_OVER`

---

## 6A — Sala de Tesoro (post-victoria)

### Resultado del combate

```
┌──────────────────────────────────────────────────────────────────┐
│  sala de tesoro — drake derrotado                    victoria ✓  │
│                                                                  │
│                     💀 → 🏆                                     │
│                Drake de Lava eliminado                           │
│           exp ganada: +350  ·  oro: +120                         │
└──────────────────────────────────────────────────────────────────┘
```

### Objetos encontrados (loot)

```
┌──────────────────────────────────────────────────────────────────┐
│  objetos encontrados                                             │
│                                                                  │
│  ⚔️  Espada de Fuego                               [ rara ]     │
│      daño: 28  ·  quemadura al golpear                          │
│                                                                  │
│  🧪  Pocion de Vida Mayor                          [ comun ]    │
│      restaura 60 hp                                             │
│                                                                  │
│  💎  Gema de Lava                                  [ comun ]    │
│      tesoro — valor: 80 oro                                     │
│                                                                  │
│  ┌──────────────────────┐  ┌──────────────────────┐             │
│  │  tomar todo          │  │  seleccionar         │             │
│  └──────────────────────┘  └──────────────────────┘             │
└──────────────────────────────────────────────────────────────────┘
```

### Resumen de partida

```
salas exploradas      4 / 9
enemigos derrotados   5
oro acumulado         220
objetos recolectados  6
hp actual             72 / 100  ✓
```

### Guardado automático (Memento)

```
Estado de partida capturado tras la victoria.
✓ checkpoint guardado — sala 3
```

---

## 6B — Pantalla de Guardado / Carga

### Ranuras de guardado

```
┌──────────────────────────────────────────────────────────────────┐
│  ranuras de guardado (memento)                                   │
│                                                                  │
│  > 🗡️  Guerrero — sala 3              [ guardado automático ]   │
│        hp 72/100  ·  4 salas  ·  mazmorra fuego                 │
│        hoy, 14:32                                                │
│                                                                  │
│    🏹  Arquero — sala 1               [ guardado manual ]       │
│        hp 60/75   ·  1 sala   ·  mazmorra fuego                 │
│        ayer, 20:10                                               │
│                                                                  │
│    📭  ranura vacía                                              │
│                                                                  │
│  ┌──────────────────────┐  ┌──────────────────────┐             │
│  │  guardar aqui        │  │  cargar              │             │
│  └──────────────────────┘  └──────────────────────┘             │
└──────────────────────────────────────────────────────────────────┘
```

### Diagrama Memento

```
GameCaretaker
    └── saves: List<GameMemento>
            │
            ├── save(game.createMemento())
            └── restore(memento) → game.setStateFrom(memento)

GameMemento
    └── campos: heroHp, heroAtk, heroDef, currentRoom,
                inventory, effects, dungeonTheme, timestamp
```

---

## 6C — Pantalla Game Over

```
┌──────────────────────────────────────────────────────────────────┐
│  game over                                                       │
│                                                                  │
│                        💀                                       │
│                el guerrero ha caido                              │
│           derrotado por Drake de Lava en sala 5                  │
│                                                                  │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────┐│
│  │      5       │ │      8       │ │     220      │ │    14    ││
│  │salas explor. │ │  enemigos    │ │  oro ganado  │ │  turnos  ││
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────┘│
│                                                                  │
│  [ R ] cargar ultimo guardado                                    │
│  [ M ] volver al menu                                            │
│  [ N ] nueva partida                          ← resaltado rojo  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Flujo de transición de estados

```
CombatState
    │
    ├── victoria (hp_enemigo = 0)
    │       └── → TreasureRoomState → ExplorationState
    │                   └── GameMemento.save() (automático)
    │
    └── derrota (hp_jugador = 0)
            └── → GameOverState
                    ├── [ R ] GameMemento.restore() → ExplorationState
                    ├── [ M ] → MenuState
                    └── → [ N ] → MenuState (nueva partida)
```

---

## Notas de implementación

| Elemento               | Patrón / Clase Java                                  |
|------------------------|------------------------------------------------------|
| Guardado de partida    | `GameMemento`, `GameCaretaker`                       |
| Restauración de estado | `game.setStateFrom(memento)` (Memento)               |
| Transición game over   | `GameStateContext.setState(new GameOverState())`     |
| Generación de loot     | `ItemFactory.createLoot(EnemyType)` (Factory Method) |
| Siguiente mazmorra     | `DungeonFactory.create(DungeonTheme.ICE)` (Abstract Factory) |
