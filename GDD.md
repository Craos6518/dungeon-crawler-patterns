# GAME DESIGN DOCUMENT

## Dungeon Crawler Academico - Patrones de Diseno en Java

## 1. Resumen del Juego

Dungeon Crawler Academico es un videojuego por consola desarrollado en Java 17 cuyo objetivo es demostrar la implementacion e integracion de patrones de diseno de software en un sistema interactivo.

El jugador controla un heroe que explora una mazmorra tematica, enfrenta enemigos en combates por turnos, recolecta objetos y administra inventario.

El alcance esta orientado a arquitectura y mantenibilidad, no a complejidad RPG avanzada.

## 2. Caracteristicas Principales

- Combate por turnos con decisiones del jugador.
- Exploracion por salas con eventos y encuentros.
- Inventario jerarquico basado en contenedores.
- Efectos de estado aplicados durante combate.
- IA de enemigos configurable por estrategias.
- Sistema de eventos desacoplado.
- Guardado y restauracion de estado de partida.

## 3. Mecanicas del Juego

### Exploracion

El jugador recorre salas de la mazmorra. Cada sala puede incluir enemigos, objetos y eventos.

### Combate

El combate es por turnos. Acciones del jugador:

- atacar
- defender
- usar objeto
- usar habilidad

Los enemigos actuan de forma automatica segun su logica de combate.

### Inventario

El inventario soporta estructura compuesta.

```text
Mochila
 |- Pocion
 |- Espada
 `- Bolsa
    |- Gema
    `- Pergamino
```

### Efectos de Estado

Se contemplan efectos como veneno, quemadura y fortaleza para modificar comportamiento durante varios turnos.

## 4. Objetivos del Jugador

Objetivo principal:

- Sobrevivir a la exploracion, derrotar enemigos y recolectar objetos.

Objetivos secundarios:

- Mejorar equipamiento.
- Superar encuentros especiales.
- Encontrar tesoros raros.

## 5. Estilo Visual y Presentacion

Interfaz textual en consola con menus interactivos.

Recursos de presentacion:

- Simbolos ASCII y texto estructurado.
- Indicadores visuales (iconos de consola) para eventos.
- Mensajes de combate y estado legibles.

## 6. Tecnologia y Plataforma

- Java 17
- Maven
- JUnit 5
- VS Code
- Git / GitHub

Ejecucion: terminal Linux, Windows y macOS.

## 7. Arquitectura y Patrones

Patrones implementados en la arquitectura del proyecto:

- Factory Method
- Abstract Factory
- Builder
- Composite
- Decorator
- Facade
- Command
- Strategy
- Observer
- State
- Memento

## 8. Personajes Jugables

- Guerrero: alta vida y resistencia.
- Mago: menor vida, alto dano.
- Arquero: balance entre dano y supervivencia.

## 9. Enemigos por Tema

- Tema fuego: criaturas de lava y jefes draconicos.
- Tema hielo: entidades de frio y control.
- Tema veneno: criaturas toxicas y desgaste.
- Tema oscuridad: enemigos de dano sostenido.

## 10. Objetos del Juego

- Armas: espada, arco, baston.
- Consumibles: pocion de vida, antidoto, pergaminos.
- Tesoros: gemas, reliquias y artefactos.

## 11. Estados del Juego

- Menu principal
- Exploracion
- Combate
- Inventario
- Game Over

Cada estado encapsula reglas y comportamiento del sistema en su contexto.

## 12. Trazabilidad GDD -> Implementacion

Estado actual alineado con codigo del proyecto:

- Heroes seleccionables (Guerrero, Mago, Arquero): implementado.
- Mazmorra por tema y salas: implementado.
- Combate por turnos: implementado.
- Acciones de combate (atacar, defender, usar objeto, usar habilidad): implementado en flujo interactivo.
- Inventario jerarquico (Composite): implementado.
- Eventos de juego (Observer): implementado.
- Guardado/carga (Memento): implementado como funcionalidad parcial en partida interactiva.
- Estados del juego (State): implementado en modulo de estados y reflejado en flujo interactivo.

Referencias de codigo:

- `dungeon-crawler-patterns/src/main/java/game/InteractiveGame.java`
- `dungeon-crawler-patterns/src/main/java/game/state/game/GameStateContext.java`
- `dungeon-crawler-patterns/src/main/java/game/command/actions/`
- `dungeon-crawler-patterns/src/main/java/game/events/observer/`
- `dungeon-crawler-patterns/src/main/java/game/persistence/memento/`

## 13. Integrantes

Autor principal:

Andres Felipe Martinez Henao

Curso:

Patrones de Diseno de Software

## 14. Publico Objetivo

- Estudiantes de ingenieria de software.
- Cursos de arquitectura y patrones.
- Contextos academicos orientados a diseño orientado a objetos.

## Videos
https://www.youtube.com/watch?v=qa6GA5p9nQ0&list=PLN9W6BC54TJJr3erMptodGOQFX7gWfKTM

https://www.youtube.com/watch?v=om59cwR7psI&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq