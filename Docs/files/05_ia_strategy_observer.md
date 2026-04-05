# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/README.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# Mockup 05 — IA de Enemigos (Strategy + Observer)

**Patrón relacionado:** Strategy (lógica de combate), Observer (registro de eventos)  
**Estado del juego:** `COMBATE` — vista debug/académica

---

## Vista general

```
IA de enemigos — patrón strategy                         [ debug view ]
```

---

## Enemigo activo

```
┌──────────────────────────────────────────────────────────────────┐
│  enemigo activo                                                  │
│                                                                  │
│  🐉  Drake de Lava                                               │
│      jefe — tema fuego                                           │
│      hp: 43/100                     fase: agresiva               │
└──────────────────────────────────────────────────────────────────┘
```

---

## Estrategias disponibles

```
┌──────────────────────────────────────────────────────────────────┐
│  estrategia asignada                                             │
│                                                                  │
│  > AggressiveStrategy                              [ activa ]    │
│    Ataca siempre con el mayor daño posible.                      │
│    Prioriza daño sobre defensa.                                  │
│    Se activa cuando hp > 50%.                                    │
│                                                                  │
│    DefensiveStrategy                               [ inactiva ]  │
│    Alterna entre atacar y curarse.                               │
│    Se activa cuando hp < 50%.                                    │
│                                                                  │
│    RandomStrategy                                  [ inactiva ]  │
│    Acción aleatoria cada turno.                                  │
│    Enemigos de baja dificultad.                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Historial de decisiones IA

```
┌──────────────────────────────────────────────────────────────────┐
│  historial de decisiones IA                                      │
│                                                                  │
│  t1  AggressiveStrategy  → golpe fuerte       (22 dmg)          │
│  t2  AggressiveStrategy  → golpe fuerte       (19 dmg)          │
│  t3  AggressiveStrategy  → aliento de fuego   (28 dmg)          │
│  t4  AggressiveStrategy  → golpe fuerte       (22 dmg)          │
│                                                                  │
│  próxima evaluación: hp < 50 → cambio a DefensiveStrategy        │
└──────────────────────────────────────────────────────────────────┘
```

---

## Efectos de estado activos

```
┌──────────────────────────────────────────────────────────────────┐
│  efectos de estado activos                                       │
│                                                                  │
│  quemadura  (drake)    →  3 turnos restantes   [ -8 hp/turno ]  │
│  fortaleza  (guerrero) →  2 turnos restantes   [ defensa +15 ]  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Observers registrados

```
┌──────────────────────────────────────────────────────────────────┐
│  observers registrados                                           │
│                                                                  │
│  CombatLogger            ✓  registra cada acción en log          │
│  EffectApplier           ✓  aplica veneno/quemadura por turno    │
│  StateTransitionWatcher  ✓  detecta cambio de estrategia IA      │
└──────────────────────────────────────────────────────────────────┘
```

---

## Diagrama Strategy

```
CombatStrategy (interface)
    │
    ├── AggressiveStrategy   → decidirAccion() → golpe fuerte / habilidad
    ├── DefensiveStrategy    → decidirAccion() → ataque + curación alternados
    └── RandomStrategy       → decidirAccion() → Math.random() entre acciones

Enemy
    └── strategy: CombatStrategy   ← intercambiable en tiempo de ejecución
        └── setStrategy(new DefensiveStrategy())  // cuando hp < 50%
```

---

## Diagrama Observer

```
CombatEventBus (Subject)
    │
    ├── subscribe(CombatLogger)
    ├── subscribe(EffectApplier)
    └── subscribe(StateTransitionWatcher)
          │
          └── notifyAll(CombatEvent)
                  │
                  ├── CombatLogger.update()          → imprime en log
                  ├── EffectApplier.update()         → aplica efecto
                  └── StateTransitionWatcher.update()→ evalúa cambio IA
```

---

## Notas de implementación

| Elemento               | Patrón / Clase Java                                   |
|------------------------|-------------------------------------------------------|
| Lógica de combate IA   | `CombatStrategy`, `AggressiveStrategy`, `DefensiveStrategy` |
| Cambio dinámico de IA  | `enemy.setStrategy(strategy)` en tiempo de ejecución |
| Registro de eventos    | `CombatLogger implements CombatObserver`              |
| Aplicación de efectos  | `EffectApplier implements CombatObserver`             |
| Bus de eventos         | `CombatEventBus` (Subject del patrón Observer)        |
