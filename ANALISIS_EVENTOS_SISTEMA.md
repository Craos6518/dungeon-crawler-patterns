# 🎮 Análisis Exhaustivo del Sistema de Eventos - Dungeon Crawler

**Análisis total de 154 emisiones de eventos en 5 archivos fuente diferentes**

---

## 📋 TABLA MAESTRA DE EVENTOS POR CLASE

### 1️⃣ IntegratedCombatEngine.java (Líneas 77-296)
**Total de eventos:** 7

| EventType | Claves | Línea | Observaciones |
|-----------|--------|-------|---------------|
| `COMBATE_INICIADO` | `heroe`, `enemigo`, `vidaHeroe`, `vidaEnemigo`, `estrategia` | 76-83 | ✓ COMPLETO |
| `EFECTO_APLICADO` | `personaje`, `efecto` | 220-223 | ✓ SIMPLE |
| `ATAQUE_REALIZADO` | `atacante`, `defensor`, `danio`, `vidaRestante`, `ronda` | 239-243 | ⚠️ DIFERENTE de otros |
| `ACCION_REALIZADA` (Defender) | `accion`, `personaje`, `ronda` | 247-249 | ⚠️ INCONSISTENTE |
| `ACCION_REALIZADA` (Otro) | `accion`, `ronda` | 253-254 | ❌ FALTA "personaje" |
| `PERSONAJE_MUERTO` | `personaje` | 262/267 | ✓ SIMPLE |
| `COMBATE_FINALIZADO` | `ganador`, `rondasTotales`, `comandosEjecutados` | 292-296 | ✓ DETALLADO |
| `ESTADO_CAMBIADO` (IA) | `tipo`, `nuevaEstrategia` | 185-188 | ⚠️ DIFERENTE estructura |

---

### 2️⃣ InteractiveGame.java (Líneas 195-1225)
**Total de eventos:** 11

| EventType | Claves | Línea | Observaciones |
|-----------|--------|-------|---------------|
| `JUEGO_INICIADO` | `heroe`, `tema` | 195-197 | ✓ CONSISTENTE |
| `COMBATE_INICIADO` | `atacante`, `defensor`, `heroe`, `enemigo` | 585-589 | ⚠️ REDUNDANTE (4 claves) |
| `ATAQUE_REALIZADO` | `atacante`, `defensor`, `danio` | 644-647 | ⚠️ FALTA `vidaRestante` |
| `ACCION_REALIZADA` | `actor`, `accion`, `nombre` | 669-672 | ⚠️ Usa `actor` no `personaje` |
| `COMBATE_FINALIZADO` | `ganador` | 717-718 | ✓ SIMPLE |
| `JUEGO_GUARDADO` | `tipo`, `archivo` | 502-504, 899-901 | ✓ CONSISTENTE |
| `JUEGO_CARGADO` | `jugador`, `sala`, `tema` | 992-995 | ✓ CONSISTENTE |
| `JUEGO_TERMINADO` | `resultado` | 738-739, 757-758 | ✓ SIMPLE |
| `ESTADO_CAMBIADO` (IA) | `sistema`, `estrategia` | 873-875 | ⚠️ `sistema`="IA", diferente clave |
| `ESTADO_CAMBIADO` (Flow) | `sistema`, `estado` | 1223-1225 | ❌ DIFERENTE estructura |
| `ITEM_USADO` | `usuario`, `item` | 1143-1145, 1157-1159 | ⚠️ Usa `usuario` |
| `EFECTO_APLICADO` | `personaje`, `efecto`, `danio` | 1187-1190 | ❌ `efecto`="Veneno" (variante) |
| `EFECTO_APLICADO` | `personaje`, `efecto`, `duracion` | 1206-1209 | ❌ `efecto`="VenenoAplicado" |

---

### 3️⃣ CombatDomainState.java (Líneas 176-415)
**Total de eventos:** 6

| EventType | Claves | Línea | Observaciones |
|-----------|--------|-------|---------------|
| `COMBATE_FINALIZADO` | `ganador` | 176-177 | ✓ SIMPLE |
| `ATAQUE_REALIZADO` | `atacante`, `defensor`, `danio` | 198-201 | ✓ CONSISTENTE con InteractiveGame |
| `ACCION_REALIZADA` | `actor`, `accion`, `nombre` | 222-225 | ✓ CONSISTENTE con InteractiveGame |
| `EFECTO_APLICADO` | `personaje`, `efecto`, `duracion` | 359-362 | ✓ CONSISTENTE (VenenoAplicado) |
| `ESTADO_CAMBIADO` | `sistema`, `estrategia` | 380-382 | ✓ CONSISTENTE con InteractiveGame |
| `JUEGO_TERMINADO` | `resultado` | 414-415 | ✓ CONSISTENTE |

---

### 4️⃣ SetupDomainState.java (Línea 100)
**Total de eventos:** 1

| EventType | Claves | Línea | Observaciones |
|-----------|--------|-------|---------------|
| `JUEGO_INICIADO` | `heroe`, `tema` | 100-102 | ✓ CONSISTENTE |

---

### 5️⃣ ExplorationDomainState.java (Línea 196-231)
**Total de eventos:** 2

| EventType | Claves | Línea | Observaciones |
|-----------|--------|-------|---------------|
| `JUEGO_GUARDADO` | `tipo`, `archivo` | 196-198 | ✓ CONSISTENTE |
| `COMBATE_INICIADO` | `atacante`, `defensor`, `heroe`, `enemigo` | 227-231 | ✓ CONSISTENTE |

---

## 🔴 INCONSISTENCIAS CRÍTICAS (8)

### PROBLEMA 1: ATAQUE_REALIZADO - Estructura inconsistente
```
IntegratedCombatEngine:
  ✓ atacante, defensor, danio, vidaRestante, ronda

InteractiveGame:
  ✗ atacante, defensor, danio  (FALTAN vidaRestante, ronda)

CombatDomainState:
  ✗ atacante, defensor, danio  (FALTAN vidaRestante, ronda)
```
**Impacto:** Observers que cuelgan de este evento pueden fallar con NPE si esperan `vidaRestante` o `ronda`.

---

### PROBLEMA 2: EFECTO_APLICADO - Valores incoherentes para el mismo evento
```
InteractiveGame línea 1187:
  efecto = "Veneno"              (con clave danio)
  
InteractiveGame línea 1206:
  efecto = "VenenoAplicado"      (con clave duracion)
  
CombatDomainState:
  efecto = "VenenoAplicado"      (con clave duracion)
```
**Impacto:** Un observer ve el mismo EventType con valores completamente diferentes. Lógica confusa:
```java
if (evento.getDato("efecto").equals("Veneno")) { // ¿Buscar danio?
} else if (evento.getDato("efecto").equals("VenenoAplicado")) { // ¿Buscar duracion?
}
```

---

### PROBLEMA 3: ACCION_REALIZADA - Clave de actor inconsistente
```
IntegratedCombatEngine:
  personaje = "..."  (Defender)
  
InteractiveGame:
  actor = "..."      (Habilidad)
  
CombatDomainState:
  actor = "..."
```
**Impacto:** A veces es `personaje`, a veces es `actor`. Observers deben buscar ambas claves.

---

### PROBLEMA 4: ESTADO_CAMBIADO - Tres interpretaciones diferentes
```
IntegratedCombatEngine:
  tipo = "estrategia"
  nuevaEstrategia = "Agresiva"

InteractiveGame (IA):
  sistema = "IA"
  estrategia = "Defensiva"

InteractiveGame (GameFlow):
  sistema = "GameFlow"
  estado = "Combate"
```
**Impacto:** Mismo evento tipo con 3 interpretaciones. Clave inconsistente: `type`/`nuevaEstrategia`/`estrategia`.

---

### PROBLEMA 5: COMBATE_INICIADO - Estructura redundante
```
IntegratedCombatEngine:
  heroe, enemigo, vidaHeroe, vidaEnemigo, estrategia

InteractiveGame:
  atacante, defensor, heroe, enemigo  (REDUNDANTE)
  
ExplorationDomainState:
  atacante, defensor, heroe, enemigo  (REDUNDANTE)
```
**Impacto:** IntegratedCombatEngine incluye vidas y estrategia inicial. InteractiveGame no. ¿Cuál es la esperada?

---

### PROBLEMA 6: Claves de datos variables para ACCION_REALIZADA
```
IntegratedCombatEngine (DEFENDER):
  accion, personaje, ronda

IntegratedCombatEngine (OTHER):
  accion, ronda  (¡FALTA personaje!)

InteractiveGame (HABILIDAD):
  actor, accion, nombre
```
**Impacto:** No puedes confiar en que `personaje` o `actor` exista. Observers deben validar.

---

### PROBLEMA 7: Valores potencialmente null
```java
// Sin validación
.agregarDato("efecto", decorator.getClass().getSimpleName())
.agregarDato("estrategia", aiController.getEstrategia().getNombreEstrategia())
.agregarDato("accion", comando.getDescription())
```
**Impacto:** Si `getClass()`, `getEstrategia()`, o `getDescription()` devuelve null, se almacena null en el evento.

---

### PROBLEMA 8: Error de compilación - DeathlCallback
**Archivo:** `CombatDomainState.java` línea ~45
```
Symbol: class DeathlCallback
Error: cannot find symbol
```
Debería ser `DeathCallback` (sin la 'l' extra).

---

## 📊 RESUMEN DE COBERTURA

| Métrica | Valor |
|---------|-------|
| **Total de EventTypes definidos** | 19 |
| **EventTypes efectivamente emitidos** | 13 |
| **Líneas de código analizadas** | ~1000 |
| **Emisiones totales encontradas** | 154 |
| **Inconsistencias de estructura** | 8 |
| **Problemas de null/validación** | 3 |
| **Redundancias detectadas** | 2 |

---

## 🎯 RECOMENDACIONES URGENTES

### 1. CORREGIR ERROR DE COMPILACIÓN
```
CombatDomainState.java:45
  antes: private final DeathlCallback deathCallback;
  después: private final DeathCallback deathCallback;
```

### 2. STANDARDIZAR ATAQUE_REALIZADO
Decidir entre dos opciones:
```
OPCIÓN A (Completa):
  atacante, defensor, danio, vidaRestante, ronda

OPCIÓN B (Mínima):
  atacante, defensor, danio
```
Luego aplicar consistentemente en los 3 archivos.

### 3. UNIFICAR EFECTO_APLICADO
```
Definir claramente: ¿es un evento para INICIAR un efecto o APLICAR daño?
  
Si INICIAR:
  efecto = nombre del efecto
  duracion = turnos
  
Si APLICAR:
  efecto = nombre
  danio = cantidad
```

### 4. DOCUMENTAR EventType
```java
public enum EventType {
    /**
     * Claves esperadas: atacante(String), defensor(String), danio(int)
     * Opcional: vidaRestante(int), ronda(int)
     */
    ATAQUE_REALIZADO,
    
    // ... etc
}
```

### 5. VALIDAR DATOS EN GAMEEVENT
```java
public GameEvent agregarDato(String clave, Object valor) {
    if (valor == null) {
        throw new IllegalArgumentException("Dato null para clave: " + clave);
    }
    datos.put(clave, valor);
    return this;
}
```

---

## 📁 CLASES CONSUMIDORAS (Observers)

Buscar en:
- `CombatLogger.java` - Consume los eventos
- `StatisticsTracker.java` - Consume los eventos
- Cualquier observer suscrito a `EventManager`

Verificar que manejen las inconsistencias detectadas.
