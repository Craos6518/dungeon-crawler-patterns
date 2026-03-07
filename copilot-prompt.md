# Instrucciones para Copilot – Proyecto Combate por Turnos

## Contexto

- Proyecto académico en Java 17.
- Arquitectura limpia y minimalista.
- Sin frameworks externos salvo JUnit 5.

## Reglas de diseño

- `MotorCombate` debe depender exclusivamente de la abstracción `Personaje`.
- No usar `instanceof`.
- No usar estado global.
- No usar métodos estáticos para lógica de negocio.
- `ResultadoAtaque` debe ser un `record` inmutable.
- Métodos pequeños (máx. 20 líneas).
- No lógica compleja en constructores.
- Alternancia de turnos controlada por el motor, no por personajes.
- No mezclar lógica de impresión con lógica de dominio.
- Cumplir SRP básico.

## Estructura esperada

### `Personaje` (abstracto)

Métodos:

- `atacar(Personaje objetivo)`
- `recibirDanio(int cantidad)`
- `estaVivo()`
- `getVida()`

### `Guerrero` / `EnemigoBasico`

- Implementan ataque con fórmula simple.
- No conocen al motor.

### `ResultadoAtaque` (record)

Debe contener:

- `atacante`
- `defensor`
- `daño`
- `vidaRestanteDefensor`

Inmutable.

### `MotorCombate`

Responsable de:

- Alternar turnos.
- Determinar fin del combate.
- Ejecutar ronda.
- Exponer método `iniciar()`.

No debe:

- Saber tipos concretos.
- Tener lógica de cálculo de daño.

## Testing

Debe permitir:

- Test de reducción de vida.
- Test de alternancia de turno.
- Test de finalización de combate.
- Test de integración ejecutando combate completo.
