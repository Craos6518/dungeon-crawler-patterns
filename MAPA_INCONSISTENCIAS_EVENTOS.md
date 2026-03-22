# 🗺️ MAPA VISUAL DE INCONSISTENCIAS - Sistema de Eventos

## DIAGRAMA 1: FLUJO DE EVENTOS POR TIPO

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         EVENTOS DE COMBATE                                  │
└─────────────────────────────────────────────────────────────────────────────┘

COMBATE_INICIADO
  ├─ IntegratedCombatEngine [LÍNEA 76-83]
  │   └─ 5 claves: heroe, enemigo, vidaHeroe, vidaEnemigo, estrategia
  │
  ├─ InteractiveGame [LÍNEA 585-589]
  │   └─ 4 claves: atacante, defensor, heroe, enemigo  ⚠️ REDUNDANTE
  │
  └─ ExplorationDomainState [LÍNEA 227-231]
      └─ 4 claves: atacante, defensor, heroe, enemigo  ⚠️ REDUNDANTE

                         ❌ CONCLUSIÓN: 2 VERSIONES


ATAQUE_REALIZADO
  ├─ IntegratedCombatEngine [LÍNEA 239-243]
  │   └─ 5 claves: atacante, defensor, danio, vidaRestante, ronda  ✓
  │
  ├─ InteractiveGame [LÍNEA 644-647]
  │   └─ 3 claves: atacante, defensor, danio  ⚠️ INCOMPLETO
  │
  └─ CombatDomainState [LÍNEA 198-201]
      └─ 3 claves: atacante, defensor, danio  ⚠️ INCOMPLETO

                         ❌ CONCLUSIÓN: 2 VERSIONES (V1 con 5, V2 con 3)


COMBATE_FINALIZADO
  ├─ IntegratedCombatEngine [LÍNEA 292-296]
  │   └─ 3 claves: ganador, rondasTotales, comandosEjecutados
  │
  ├─ InteractiveGame [LÍNEA 717-718]
  │   └─ 1 clave: ganador  ✓ SIMPLE
  │
  └─ CombatDomainState [LÍNEA 176-177]
      └─ 1 clave: ganador  ✓ SIMPLE

                         ⚠️ CONCLUSIÓN: 2 VERSIONES (DETALLADA vs SIMPLE)
```

---

## DIAGRAMA 2: EVENTOS PROBLEMÁTICOS

```
┌────────────────────────────────────────────────────────────────────┐
│  EFECTO_APLICADO - EL EVENTO FANTASMA (Mismo tipo, múltiples significados)
└────────────────────────────────────────────────────────────────────┘

InteractiveGame [LÍNEA 1187-1190]:
┌─────────────────────────────────────┐
│ GameEvent(EFECTO_APLICADO)          │
├─────────────────────────────────────┤
│ personaje    = "Craos6518"          │
│ efecto       = "Veneno"             │
│ danio        = 3                    │
└─────────────────────────────────────┘
    ↓
    Propósito: "El veneno causó 3 de daño"


InteractiveGame [LÍNEA 1206-1209]:
┌─────────────────────────────────────┐
│ GameEvent(EFECTO_APLICADO)          │
├─────────────────────────────────────┤
│ personaje    = "Craos6518"          │
│ efecto       = "VenenoAplicado"     │
│ duracion     = 3                    │
└─────────────────────────────────────┘
    ↓
    Propósito: "Se aplicó envenenamiento por 3 turnos"


CombatDomainState [LÍNEA 359-362]:
┌─────────────────────────────────────┐
│ GameEvent(EFECTO_APLICADO)          │
├─────────────────────────────────────┤
│ personaje    = "Héroe"              │
│ efecto       = "VenenoAplicado"     │ ← IGUAL A L1206
│ duracion     = 3                    │
└─────────────────────────────────────┘
    ↓
    Propósito: "Se aplicó envenenamiento por 3 turnos"


PROBLEMA:
  Un observer viendo EFECTO_APLICADO debe manejar:
  • efecto = "Veneno" (buscar danio)
  • efecto = "VenenoAplicado" (buscar duracion)
  
  Sin contrato claro, es ambiguo.

  ❓ ¿Hay otros efectos con esta dualidad de estructura?
     (Buscar "EFECTO_APLICADO" en todo el código)
```

---

## DIAGRAMA 3: EVENTO CON 3 VERSIONES (ESTADO_CAMBIADO)

```
┌──────────────────────────────────────────────────────────────┐
│           ESTADO_CAMBIADO - El Evento Triplicado             │
└──────────────────────────────────────────────────────────────┘


VERSIÓN 1: IntegratedCombatEngine [LÍNEA 185-188]
  Caso: cambiarEstrategiaIA()
┌────────────────────────────────────┐
│ tipo     = "estrategia"            │
│ nuevaEstrategia = "Defensiva"      │
└────────────────────────────────────┘


VERSIÓN 2: InteractiveGame [LÍNEA 873-875]
  Caso: actualizarEstrategiaEnemiga()
┌────────────────────────────────────┐
│ sistema = "IA"                     │ ← DIFERENTE CLAVE
│ estrategia = "Defensiva"           │ ← DIFERENTE CLAVE
└────────────────────────────────────┘


VERSIÓN 3: InteractiveGame [LÍNEA 1223-1225]
  Caso: cambiarEstadoFlujo()
┌────────────────────────────────────┐
│ sistema = "GameFlow"               │
│ estado = "Combate"                 │ ← TOTALMENTE DIFERENTE
└────────────────────────────────────┘


PROBLEMA:
  Un observer suscritos a ESTADO_CAMBIADO debe escribir:
  
  if (evento.getDato("tipo") != null) {
    // Version 1
    String nuevaEstrategia = evento.getDato("nuevaEstrategia");
  } else if (evento.getDato("sistema") != null) {
    Object estrategia = evento.getDato("estrategia");
    Object estado = evento.getDato("estado");
    
    if (estrategia != null) {
      // Version 2
    } else if (estado != null) {
      // Version 3
    }
  }
  
  ❌ CÓDIGO FRÁGIL Y MANTENIBLE
```

---

## DIAGRAMA 4: LINEA DE TIEMPO - DÓNDE SE EMITEN EVENTOS EN COMBATE

```
ESCENARIO: Jugador ataca, enemigo contraataca


InteractiveGame.iniciarCombate() - Línea ~585
    │
    ├─► eventManager.notificar(COMBATE_INICIADO)
    │           ├─ atacante, defensor, heroe, enemigo
    │           └─ ⚠️ Usa "atacante"/"defensor" además de "heroe"/"enemigo"
    │
    └─ LOOP DE TURNOS
           │
           ├─► [TURNO 1 - JUGADOR ATACA]
           │       │
           │       ├─ new AttackCommand(heroe, enemigo)
           │       │
           │       └─► eventManager.notificar(ATAQUE_REALIZADO)
           │               ├─ atacante, defensor, danio
           │               └─ ⚠️ FALTA vidaRestante, ronda
           │
           ├─► [TURNO 2 - ENEMIGO ATACA]
           │       │
           │       ├─ enemyAI.decidirAccion()
           │       │       └─ new AttackCommand(enemigo, heroe)
           │       │
           │       └─► ❌ NO SE EMITE EVENTO
           │           (La emisión está en InteractiveGame pero no en AIController)
           │
           ├─► [FIN COMBATE - GANADOR]
           │       │
           │       └─► eventManager.notificar(COMBATE_FINALIZADO)
           │               └─ ganador
           │
           └─► eventManager.notificar(JUEGO_GUARDADO)
                       └─ tipo, archivo


PROBLEMA DETECTADO:
  No se emite ATAQUE_REALIZADO cuando el ENEMIGO ataca
  (solo cuando el jugador ataca)
  
  ⚠️ Asymmetría en los eventos
```

---

## DIAGRAMA 5: MATRIZ DE CLAVES POR EVENTO

```
┌──────────────────────────┬──────────────────────────────────────┐
│ EventType                │ Claves Esperadas                     │
├──────────────────────────┼──────────────────────────────────────┤
│ COMBATE_INICIADO         │ ✓ heroe, enemigo                     │
│                          │ ⚠️ +vidaHeroe, vidaEnemigo?          │
│                          │ ⚠️ +estrategia?                      │
├──────────────────────────┼──────────────────────────────────────┤
│ ATAQUE_REALIZADO         │ ✓ atacante, defensor, danio          │
│                          │ ⚠️ +vidaRestante?                    │
│                          │ ⚠️ +ronda?                           │
├──────────────────────────┼──────────────────────────────────────┤
│ ACCION_REALIZADA         │ ⚠️ actor ? personaje ?               │
│                          │ ✓ accion                             │
│                          │ ⚠️ +nombre? (solo para habilidades)  │
│                          │ ⚠️ +ronda?                           │
├──────────────────────────┼──────────────────────────────────────┤
│ EFECTO_APLICADO          │ ✓ personaje, efecto                  │
│                          │ ⚠️ danio (si efecto="Veneno")        │
│                          │ ⚠️ duracion (si efecto="VenenoAplicado") │
├──────────────────────────┼──────────────────────────────────────┤
│ ESTADO_CAMBIADO          │ ⚠️ tipo, nuevaEstrategia ?           │
│                          │ ⚠️ sistema, estrategia ?             │
│                          │ ⚠️ sistema, estado ?                 │
├──────────────────────────┼──────────────────────────────────────┤
│ PERSONAJE_MUERTO         │ ✓ personaje                          │
├──────────────────────────┼──────────────────────────────────────┤
│ COMBATE_FINALIZADO       │ ✓ ganador                            │
│                          │ ⚠️ +rondasTotales?                   │
│                          │ ⚠️ +comandosEjecutados?              │
├──────────────────────────┼──────────────────────────────────────┤
│ JUEGO_INICIADO           │ ✓ heroe, tema                        │
├──────────────────────────┼──────────────────────────────────────┤
│ JUEGO_GUARDADO           │ ✓ tipo, archivo                      │
├──────────────────────────┼──────────────────────────────────────┤
│ JUEGO_CARGADO            │ ✓ jugador, sala, tema                │
├──────────────────────────┼──────────────────────────────────────┤
│ JUEGO_TERMINADO          │ ✓ resultado                          │
├──────────────────────────┼──────────────────────────────────────┤
│ ITEM_USADO               │ ⚠️ usuario (no "actor")              │
│                          │ ✓ item                               │
└──────────────────────────┴──────────────────────────────────────┘

✓  = Consistente en todas partes
⚠️ = Inconsistente o falta en algunos lugares
❌ = Siempre falta
```

---

## DIAGRAMA 6: DEPENDENCIA DE OBSERVERS

```
┌────────────────────────────────────────────────────────────┐
│                    EventManager                            │
│                   (Singleton)                              │
└─┬──────────────────────────────────────────────────────────┘
  │
  ├──► CombatLogger
  │       │
  │       ├─ Se suscribe a: TODOS los eventos
  │       │
  │       └─ Requiere: Estructura CONSISTENTE de claves
  │           ❌ Probability de error si falta clave esperada
  │
  ├──► StatisticsTracker
  │       │
  │       ├─ Se suscribe a: ATAQUE_REALIZADO, PERSONAJE_MUERTO, ...
  │       │
  │       └─ Requiere: danio siempre presente
  │           ❌ Si InteractiveGame/CombatDomainState omiten
  │              vidaRestante, StatisticsTracker sigue OK
  │           ❌ Pero si omiten "danio", StatisticsTracker QUIEBRA
  │
  └──► UINotifier (si existe)
          │
          ├─ Se suscribe a: eventos de estado
          │
          └─ Requiere: estructura clara
              ❌ ESTADO_CAMBIADO confuso con 3 versiones

RIESGO IDENTIFICADO:
  • CombatLogger es robusto (probablemente maneja null)
  • StatisticsTracker es frágil (espera "danio")
  • Nuevo observer futuro puede quebrar si asume consistencia
```

---

## TABLA RÁPIDA DE FIXES NECESARIOS

```
┌──────────────────────────┬──────────────┬────────────────────┐
│ Evento                   │ Ubicación    │ Fix                │
├──────────────────────────┼──────────────┼────────────────────┤
│ ATAQUE_REALIZADO         │ L644-647     │ Agregar: ronda,    │
│ (InteractiveGame)        │ L198-201     │ vidaRestante       │
│                          │ (CombatDS)   │                    │
├──────────────────────────┼──────────────┼────────────────────┤
│ EFECTO_APLICADO          │ L1187-1190   │ Cambiar estructura │
│ (ambas versiones)        │ L1206-1209   │ o usar otro enum   │
│ (InteractiveGame)        │              │                    │
├──────────────────────────┼──────────────┼────────────────────┤
│ ESTADO_CAMBIADO          │ L185-188     │ Unificar formato   │
│ (3 versiones)            │ L873-875     │ "tipo" vs          │
│                          │ L1223-1225   │ "sistema"          │
├──────────────────────────┼──────────────┼────────────────────┤
│ ACCION_REALIZADA         │ L247-249     │ Siempre incluir     │
│ (falta personaje)        │ L253-254     │ "personaje"        │
├──────────────────────────┼──────────────┼────────────────────┤
│ DeathlCallback           │ L45          │ Fijar typo         │
│ (error compilación)      │              │                    │
└──────────────────────────┴──────────────┴────────────────────┘
```

---

**Conclusión:** Hay una clara falta de contrato explícito entre los **emisores** de eventos y los **consumidores** (observers). Esto ha generado 5 eventos con estructuras inconsistentes que pueden causar runtime errors.
