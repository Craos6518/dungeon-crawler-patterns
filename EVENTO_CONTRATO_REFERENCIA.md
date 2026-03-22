# Contrato de Eventos del Sistema

## Descripción General

Este documento define el contrato oficial para todos los eventos emitidos por el sistema. Cada evento debe cumplir estrictamente con el contrato definido para mantener consistencia entre todos los emisores.

---

## Eventos de Combate

### COMBATE_INICIADO
**Emitido cuando:** Inicia un nuevo combate  
**Emisores:** IntegratedCombatEngine, InteractiveGame

**Claves requeridas:**
- `heroe` (String): Nombre del héroe
- `enemigo` (String): Nombre del enemigo
- `vidaHeroe` (Integer): Vida actual del héroe
- `vidaEnemigo` (Integer): Vida actual del enemigo
- `estrategia` (String): Estrategia inicial (Ej: "Agresiva", "Defensiva", "Aleatoria")

**Ejemplo:**
```java
new GameEvent(EventType.COMBATE_INICIADO)
    .agregarDato("heroe", "Guerrero")
    .agregarDato("enemigo", "Orco")
    .agregarDato("vidaHeroe", 100)
    .agregarDato("vidaEnemigo", 50)
    .agregarDato("estrategia", "Agresiva")
```

---

### ATAQUE_REALIZADO
**Emitido cuando:** Un personaje realiza un ataque  
**Emisores:** IntegratedCombatEngine, InteractiveGame, CombatDomainState

**Claves requeridas:**
- `atacante` (String): Nombre del atacante
- `defensor` (String): Nombre del defensor
- `danio` (Integer): Daño aplicado
- `vidaRestante` (Integer): Vida actual del defensor después del ataque
- `ronda` (Integer): Número actual de ronda/turno

**Ejemplo:**
```java
new GameEvent(EventType.ATAQUE_REALIZADO)
    .agregarDato("atacante", "Guerrero")
    .agregarDato("defensor", "Orco")
    .agregarDato("danio", 25)
    .agregarDato("vidaRestante", 25)
    .agregarDato("ronda", 3)
```

---

### ACCION_REALIZADA
**Emitido cuando:** Un personaje realiza una acción especial (habilidad, defensa especial, etc)  
**Emisores:** IntegratedCombatEngine, InteractiveGame, CombatDomainState

**Claves requeridas:**
- `personaje` (String): Nombre del personaje que realiza la acción
- `accion` (String): Tipo de acción ("habilidad", "defensa_especial", "consumible", etc)
- `ronda` (Integer): Número actual de ronda/turno

**Claves opcionales:**
- `nombre` (String): Nombre específico de la acción o habilidad
- `efecto` (String): Efecto de la acción si aplica

**Ejemplo:**
```java
new GameEvent(EventType.ACCION_REALIZADA)
    .agregarDato("personaje", "Guerrero")
    .agregarDato("accion", "habilidad")
    .agregarDato("nombre", "Golpe Especial")
    .agregarDato("ronda", 2)
```

---

### EFECTO_APLICADO
**Emitido cuando:** Se aplica un efecto a un personaje (veneno, curacion, etc)  
**Emisores:** IntegratedCombatEngine, InteractiveGame, CombatDomainState

**Claves requeridas:**
- `personaje` (String): Nombre del personaje afectado
- `efecto` (String): Nombre del efecto en MAYUSCULA_SNAKE_CASE (ejemplos: "VENENO", "CURACION", "FORTALEZA")
- `duracion` (Integer): Duración en turnos del efecto

**Ejemplo:**
```java
new GameEvent(EventType.EFECTO_APLICADO)
    .agregarDato("personaje", "Guerrero")
    .agregarDato("efecto", "VENENO")
    .agregarDato("duracion", 3)
```

---

### ESTADO_CAMBIADO
**Emitido cuando:** Cambia el estado de la IA o del flujo del juego  
**Emisores:** IntegratedCombatEngine, InteractiveGame, CombatDomainState

**Sub-tipo 1: Cambio de estrategia de IA**
- `tipo` = "estrategia"
- `nuevaEstrategia` (String): Nombre de la nueva estrategia

```java
new GameEvent(EventType.ESTADO_CAMBIADO)
    .agregarDato("tipo", "estrategia")
    .agregarDato("nuevaEstrategia", "Defensiva")
```

**Sub-tipo 2: Cambio de flujo del juego**
- `tipo` = "flujo"
- `estado` (String): Nuevo estado de flujo (MenuPrincipal, Exploracion, Combate, etc)

```java
new GameEvent(EventType.ESTADO_CAMBIADO)
    .agregarDato("tipo", "flujo")
    .agregarDato("estado", "Combate")
```

---

### COMBATE_FINALIZADO
**Emitido cuando:** Termina un combate  
**Emisores:** IntegratedCombatEngine, InteractiveGame, CombatDomainState

**Claves requeridas:**
- `ganador` (String): Nombre del ganador
- `vencido` (String): Nombre del vencido
- `rondas` (Integer): Total de rondas jugadas

**Ejemplo:**
```java
new GameEvent(EventType.COMBATE_FINALIZADO)
    .agregarDato("ganador", "Guerrero")
    .agregarDato("vencido", "Orco")
    .agregarDato("rondas", 5)
```

---

## Eventos de Juego

### JUEGO_INICIADO
**Emitido cuando:** Inicia una nueva partida  
**Emisores:** InteractiveGame

**Claves requeridas:**
- `heroe` (String): Nombre del héroe
- `tema` (String): Tema de la mazmorra

**Ejemplo:**
```java
new GameEvent(EventType.JUEGO_INICIADO)
    .agregarDato("heroe", "Guerrero")
    .agregarDato("tema", "Fuego")
```

---

### JUEGO_GUARDADO
**Emitido cuando:** Se guarda la partida  
**Emisores:** InteractiveGame

**Claves requeridas:**
- `tipo` (String): Tipo de guardado ("manual" o "checkpoint-auto")
- `archivo` (String): Nombre del archivo guardado

**Ejemplo:**
```java
new GameEvent(EventType.JUEGO_GUARDADO)
    .agregarDato("tipo", "manual")
    .agregarDato("archivo", "mi-partida.save")
```

---

### JUEGO_CARGADO
**Emitido cuando:** Se carga una partida guardada  
**Emisores:** InteractiveGame

**Claves requeridas:**
- `jugador` (String): Nombre del jugador
- `sala` (Integer): Número de sala actual
- `tema` (String): Tema de la mazmorra

**Ejemplo:**
```java
new GameEvent(EventType.JUEGO_CARGADO)
    .agregarDato("jugador", "Guerrero")
    .agregarDato("sala", 3)
    .agregarDato("tema", "Fuego")
```

---

### JUEGO_TERMINADO
**Emitido cuando:** Termina la partida (victoria o derrota)  
**Emisores:** InteractiveGame, CombatDomainState

**Claves requeridas:**
- `resultado` (String): "Victoria" o "Derrota"

**Ejemplo:**
```java
new GameEvent(EventType.JUEGO_TERMINADO)
    .agregarDato("resultado", "Victoria")
```

---

## Guía de Implementación

### Principios Clave

1. **Consistencia en clave:** Una clave debe tener siempre el mismo nombre en todos los emisores para un mismo EventType
2. **Tipos de datos:** Mantener consistencia en tipos de datos (String, Integer, Boolean, etc)
3. **Nomenclatura:** Usar MAYUSCULA_SNAKE_CASE para valores de enumeraciones (ej: "VENENO", no "Veneno")
4. **Validación:** Siempre validar valores no null antes de agregarDato()
5. **Completitud:** Incluir TODAS las claves requeridas, incluso si alguna es null

### Validación de Valores

```java
// BIEN: Validar antes de agregar
if (efecto != null) {
    evento.agregarDato("efecto", efecto.getClass().getSimpleName());
}

// MAL: No hacer esto, genera nulls en el evento
evento.agregarDato("efecto", decorator.getClass().getSimpleName()); // Si decorator es null
```

### Ejemplo Completo de Evento Correcto

```java
// Validar valores
String nombreAtacante = combatante.getNombre();
String nombreDefensor = objetivo.getNombre();
int danioAplicado = calculo.getDanio();
int vidaRestanteDefensor = objetivo.getVida();
int numeroRonda = turnoActual;

// Si algún valor es crítico y podría ser null:
if (nombreAtacante == null || nombreDefensor == null || danioAplicado < 0) {
    // Loguear error y no emitir evento o emitir con valores por defecto
    logger.error("Datos incompletos en ataque");
    return;
}

// Emitir evento con todas las claves requeridas
eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
    .agregarDato("atacante", nombreAtacante)
    .agregarDato("defensor", nombreDefensor)
    .agregarDato("danio", danioAplicado)
    .agregarDato("vidaRestante", vidaRestanteDefensor)
    .agregarDato("ronda", numeroRonda));
```

---

## Actualización de Contrato

**Última revisión:** Fase de hardening de eventos  
**Cambios más recientes:**
- ✅ Estandarización de ATAQUE_REALIZADO en todos los emisores
- ✅ Consolidación de EFECTO_APLICADO (uso de "VENENO")
- ✅ Unificación de ESTADO_CAMBIADO con subtipo
- ✅ Normalización de ACCION_REALIZADA a usar "personaje"
- ✅ Inclusión de ronda/turno en eventos de combate

---

## Verificación de Compliance

Para verificar que un nuevo evento cumple el contrato:

1. ✓ ¿Todas las claves requeridas están presentes?
2. ✓ ¿Los tipos de datos coinciden con el contrato?
3. ✓ ¿Los valores de string siguen la nomenclatura esperada?
4. ✓ ¿Se validan valores null antes de agregarDato()?
5. ✓ ¿El EventType coincide con la descripción del evento?
