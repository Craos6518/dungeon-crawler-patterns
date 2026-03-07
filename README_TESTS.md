# Estrategia de Pruebas

El proyecto incluye **3 pruebas unitarias** y **1 prueba de integración**.

Ubicación de pruebas:

```
src/test/java/game
 ├─ unit/
 │   ├─ domain/CharacterDamageTest.java
 │   └─ combat/
 │      ├─ CombatTurnAlternationTest.java
 │      └─ CombatEndTest.java
 └─ integration/combat/CombatIntegrationTest.java
```

El objetivo no es cubrir todo el código, sino demostrar que el sistema es **testeable y verificable**.

---

# Pruebas Unitarias

## Test 1 — Reducción de Vida

Archivo:

```
game.unit.domain.CharacterDamageTest
```

Verifica que:

- recibirDanio() reduce correctamente la vida
- la vida nunca baja de 0

Ejemplo esperado:

```
HP inicial: 50
Daño recibido: 60
HP final: 0
```

---

## Test 2 — Alternancia de Turno

Archivo:

```
game.unit.combat.CombatTurnAlternationTest
```

Verifica que:

```
el atacante cambia en cada ronda
```

funciona correctamente.

---

## Test 3 — Finalización de Combate

Archivo:

```
game.unit.combat.CombatEndTest
```

Verifica que el combate termina cuando uno de los personajes llega a 0 HP.

Resultado esperado:

```
motor.combateFinalizado() == true
```

---

# Prueba de Integración

Archivo:

```
game.integration.combat.CombatIntegrationTest
```

Simula un combate completo entre:

```
Hero vs Enemy
```

Se valida que:

- los turnos alternan correctamente
- el daño se aplica correctamente
- el combate termina cuando uno muere

Resultado esperado:

```
Combate finalizado correctamente
```

---

# Objetivo de las pruebas

Demostrar que el sistema:

- es verificable
- mantiene invariantes
- respeta el modelo de dominio
- puede evolucionar sin romper funcionalidades existentes
