# Extracto de Informacion del Proyecto

## OVERVIEW

### Tipos de sala

| Tipo      | Funcion             | Descripcion                                        |
| --------- | ------------------- | -------------------------------------------------- |
| Entrada   | Inicio de recorrido | Punto de inicio de la mazmorra, sin enemigos.      |
| Combate   | Progreso principal  | Sala con enemigos, requiere victoria para avanzar. |
| Tesoro    | Recompensa          | Cofres con oro y objetos raros.                    |
| Objeto    | Utilidad            | Objetos unicos como llaves, pociones y artefactos. |
| Jefe      | Cierre de recorrido | Combate final contra el guardian de la mazmorra.   |
| Bloqueada | Control de ruta     | Requiere llave o condicion especial.               |

### Mazmorras predefinidas

| Demo | Mazmorra          | Tema      | Nivel | Salas | Efecto       | Estructura                                                                                                        |
| ---- | ----------------- | --------- | ----: | ----: | ------------ | ----------------------------------------------------------------------------------------------------------------- |
| 1    | Cueva del Inicio  | Cueva     |     1 |     4 | Baja         | Entrada de la Cueva -> Pasillo Oscuro -> Camara Principal -> Trono del Goblin                                     |
| 2    | Volcan Ardiente   | Fuego     |     3 |     5 | Quemadura    | Entrada Caliente -> Rio de Lava -> Camara de magma -> Forja Infernal -> Nido del dragon                           |
| 3    | Fortaleza Sombria | Oscuridad |     4 |     6 | Aturdimiento | Portal Oscuro -> Corredor Maldito -> Biblioteca Prohibida -> Calabozos -> Salon de Huesos -> Trono de las Sombras |

---

## DUNGEOS

### Tipos de mazmorras con caracteristicas

| Tema      | Nombre                  | Nivel | Salas | Jefe       | Efecto principal | Caracteristicas                                                |
| --------- | ----------------------- | ----: | ----: | ---------- | ---------------- | -------------------------------------------------------------- |
| Fuego     | Volcan de Ignareth      |     1 |     7 | Pyraxis    | Quemadura        | Obsidiana, lava, calor extremo, criaturas de fuego.            |
| Hielo     | Catacumbas de Glaciurvh |     2 |     8 | Kryovaleth | Congelamiento    | Escarcha, niebla helada, baja movilidad, silencio absoluto.    |
| Veneno    | Pantanos de Viridax     |     3 |     8 | Arachnovex | Envenenamiento   | Esporas, toxicidad, desgaste progresivo, corrupcion biologica. |
| Oscuridad | Ciudadela de Umbrakar   |     4 |    10 | Malachar   | Aturdimiento     | Sombras, confusion, estructura mutable, memoria inestable.     |

### Historia y estructura por mazmorra

#### Volcan de Ignareth (Fuego)

- Historia: Pyraxis, Salamandra Ancestral, custodia la Piedra del Fuego Eterno.
- Estructura: entrada -> combates intermedios -> tesoro/objeto -> combate avanzado -> trono del jefe.

#### Catacumbas de Glaciurvh (Hielo)

- Historia: Kryovaleth protege el Cristal del Hielo Primordial en un santuario congelado.
- Estructura: entrada -> combates de control -> tesoro/objeto -> sala bloqueada -> combate de alto riesgo -> jefe final.

#### Pantanos de Viridax (Veneno)

- Historia: Arachnovex domina una zona viva y corrupta nacida de la Semilla de la Vida Corrupta.
- Estructura: entrada -> combates toxicos -> tesoro/objeto -> combate de acumulacion -> guarida del jefe.

#### Ciudadela de Umbrakar (Oscuridad)

- Historia: fortaleza surgida del Fragmento de la Oscuridad Absoluta; Malachar encarna el olvido.
- Estructura: entrada -> corredores de combate -> tesoro/objeto -> bloqueos y laberinto -> checkpoint -> trono final.

### Enemigos por tema de mazmorra

#### Fuego

- Drake Menor (Menor): Quemadura -8 hp/turno.
- Salamandra (Elite): Quemadura -12 hp/turno.
- Drake Mayor (Sub-jefe): Quemadura masiva.
- Pyraxis (Jefe): Quemadura + dano directo.

#### Hielo

- Elemental Hielo (Menor): Ralentizacion -SPD.
- Lobo Artico (Menor): Congelacion (turno perdido).
- Wyvern de Hielo (Sub-jefe): Congelacion + aliento.
- Kryovaleth (Jefe): Congelacion masiva + SPD.

#### Oscuridad

- Sombra (Menor): Aturdimiento (turno perdido).
- Vigia Oscuro (Elite): Aturdimiento + dano alto.
- Revenant (Sub-jefe): Aturdimiento + drenaje de HP.
- Malachar (Jefe): Borrar Memoria (rollback).

#### Veneno

- Arana Menor (Menor): Veneno -5 hp/turno.
- Batracio (Menor): Veneno corrosivo -8 hp.
- Serpiente (Sub-jefe): Veneno apilado x2.
- Arachnovex (Jefe): Veneno apilado + tela.

### Efectos de estado

| Efecto                      | Tipo   | Regla principal                                                            |
| --------------------------- | ------ | -------------------------------------------------------------------------- |
| Quemadura                   | Debuff | -8 a -15 HP por turno, 3 turnos, mitigable por resistencia/pocion de agua. |
| Ralentizacion / Congelacion | Debuff | -50% velocidad por 2 turnos, congelacion total puede hacer perder 1 turno. |
| Aturdimiento                | Debuff | Salta turno, no ataca ni defiende durante 1 turno.                         |
| Veneno                      | Debuff | -5 HP por turno, apilable hasta x3, dura 5 turnos.                         |
| Fortaleza                   | Buff   | +15 defensa por 2 turnos.                                                  |

---

## Lore

### Historia del mundo

En Eranthia, el Archimago Valdur Thessanor forjo cuatro artefactos: Piedra del Fuego Eterno, Cristal del Hielo Primordial, Semilla de la Vida Corrupta y Fragmento de la Oscuridad Absoluta. Cada artefacto fue sellado en una mazmorra. Tras siglos de estabilidad, los sellos se debilitan y la corrupcion regresa. El Consejo de Magos de Valdrath convoca a un heroe de codigo noble para restaurar el equilibrio.

### Historia de las mazmorras

- Volcan de Ignareth: fuego ancestral, rios de lava y memoria abrasada.
- Catacumbas de Glaciurvh: hielo eterno, silencio y desgaste progresivo de voluntad.
- Pantanos de Viridax: vida corrompida, veneno acumulativo y desgaste biologico.
- Ciudadela de Umbrakar: arquitectura cambiante, sombras y perdida de memoria.

### Historia de los guardianes

- Pyraxis: guardiana maldita de la primera llama del mundo; protege la Piedra del Fuego Eterno.
- Kryovaleth: dragon de invierno formado por siglos de nieve y dolor; protege el Cristal del Hielo Primordial.
- Arachnovex: reina tejedora que consume aventureros y refleja almas en sus ojos; protege la Semilla de la Vida Corrupta.
- Malachar: encarnacion del olvido y del vacio; protege el Fragmento de la Oscuridad Absoluta.

### Los finales

| Tema      | Titulo del final                    | Resultado narrativo                                                                            | Recompensa            |
| --------- | ----------------------------------- | ---------------------------------------------------------------------------------------------- | --------------------- |
| Fuego     | El Corazon del Volcan se ha Apagado | La lava se enfria, el volcan entra en letargo y el continente evita una erupcion catastrofica. | Armadura de Obsidiana |
| Hielo     | El Invierno Eterno se Derrite       | Se rompe el hechizo de frio perpetuo y la primavera regresa a las tierras congeladas.          | Espada Glacial        |
| Oscuridad | La Sombra se Disipa                 | La ciudadela se libera de su dominio sombrio y los espiritus atrapados son liberados.          | Daga de las Sombras   |
| Veneno    | El Pantano Purificado               | La corrupcion se neutraliza, el agua se limpia y el ecosistema recupera su equilibrio natural. | Arco de Tejo Toxico   |

---

## Nota de direccion de diseno

Este extracto se alinea con la decision de proyecto: interfaz en primera persona por paneles de imagenes y no jugabilidad 2D tipo Mario o Zelda.
