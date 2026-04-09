# ⚠️ DOCUMENTO OBSOLETO — NO USAR

Este documento NO es fuente de verdad.

Fuente vigente:
👉 docs/01-product/GDD_CANONICO.md

Estado:
- Obsoleto desde: 2026-04-04
- Motivo: consolidación post-auditoría

Este archivo se conserva únicamente por trazabilidad histórica.

---

# ESTADO DOCUMENTAL
- Estado: obsoleto (legacy conservado por trazabilidad)
- Consolidado en: `docs/01-product/GDD_CANONICO.md`
- Fecha de reclasificacion: 2026-04-04
- Rama auditada: Flujo-de-mazmorra

# GDD - Fichas de Diseno (Primera Persona por Paneles)

## Estado del proyecto

- Decision de diseno: el juego usa interfaz por paneles de imagenes en primera persona.
- Se descarta la implementacion de jugabilidad 2D tipo Mario/Zelda.
- Este documento define la estructura oficial de fichas para el GDD.

## Vision de experiencia

- Perspectiva: primera persona guiada por escenas/paneles.
- Navegacion: transiciones entre paneles narrativos y paneles interactivos.
- Combate y eventos: representados en paneles contextuales (no desplazamiento lateral ni top-down 2D jugable).
- Lectura visual: prioridad a atmosfera, ilustracion, claridad de opciones y feedback de estado.

## Alcance y fuera de alcance

### En alcance

- Sistema de paneles de exploracion.
- Sistema de paneles de combate/evento.
- Inventario, estados y progresion mostrados en paneles UI.
- Mazmorras tematicas, guardianes, enemigos y finales.

### Fuera de alcance

- Movimiento continuo en mapas 2D jugables.
- Plataformas 2D tipo Mario.
- Exploracion top-down estilo Zelda clasico.
- Colisiones en tiempo real basadas en sprites 2D jugables.

---

## Plantilla 1: Ficha de Sistema (Panel)

### Identificacion

- ID:
- Nombre del sistema:
- Tipo de panel: Exploracion | Combate | Dialogo | Recompensa | Lore
- Prioridad: Alta | Media | Baja

### Objetivo

- Problema que resuelve:
- Valor para el jugador:

### Diseno de interaccion

- Entrada del jugador: click | seleccion | atajo
- Salida esperada: cambio de panel | evento | estado
- Estados del panel: idle | hover | activo | bloqueado
- Reglas de transicion:

### Datos y logica

- Inputs de datos:
- Outputs de datos:
- Validaciones:
- Casos borde:

### UI y feedback

- Jerarquia visual (titulo, texto, CTA):
- Feedback de error/exito:
- Sonido/FX (si aplica):

### Criterios de aceptacion

- [ ] El panel se renderiza con datos validos.
- [ ] Las opciones activas responden correctamente.
- [ ] Las transiciones no rompen el flujo narrativo.
- [ ] El jugador entiende que accion puede tomar.

---

## Plantilla 2: Ficha de Mazmorra

### Identificacion

- ID:
- Nombre:
- Tema:
- Nivel recomendado:
- Numero de salas:
- Guardian/Jefe:
- Efecto ambiental dominante:

### Narrativa

- Sinopsis breve:
- Historia de origen:
- Tono emocional:

### Estructura por paneles

- Panel de entrada:
- Paneles de combate clave:
- Panel de evento/objeto:
- Panel de jefe:
- Panel de cierre/final:

### Gameplay asociado

- Riesgos principales:
- Recompensas principales:
- Curva de dificultad:

### Criterios de aceptacion

- [ ] La mazmorra tiene inicio, desarrollo y cierre claros.
- [ ] El jefe refleja el tema y mecanicas esperadas.
- [ ] El efecto ambiental afecta decisiones de juego.

---

## Plantilla 3: Ficha de Sala

### Identificacion

- ID sala:
- Nombre:
- Tipo: entrada | combate | tesoro | objeto | jefe | bloqueada
- Tema:

### Contenido

- Descripcion visual del panel:
- Enemigos:
- Objetos interactivos:
- Efecto de estado posible:

### Flujo

- Condicion de entrada:
- Condicion de salida:
- Conexiones (sala anterior/siguiente):

### Criterios de aceptacion

- [ ] La sala comunica claramente su tipo.
- [ ] Hay feedback al completar la accion principal.
- [ ] El paso a la siguiente sala es coherente.

---

## Plantilla 4: Ficha de Enemigo

### Identificacion

- Nombre:
- Tipo: Menor | Elite | Sub-jefe | Jefe
- Tema:

### Diseno de combate

- Rol tactico:
- Patron IA:
- Efecto principal:
- Salas donde aparece:

### Balance

- Fortaleza principal:
- Debilidad principal:
- Riesgo para el jugador:

### Criterios de aceptacion

- [ ] Se diferencia de otros enemigos del tema.
- [ ] Su efecto principal se percibe en combate.
- [ ] Encaja en la curva de dificultad de la mazmorra.

---

## Plantilla 5: Ficha de Efecto de Estado

### Identificacion

- ID:
- Nombre:
- Tipo: debuff | buff
- Tema:

### Regla de juego

- Efecto exacto:
- Duracion:
- Apilamiento:
- Contramedidas:

### UX en panel

- Icono:
- Color:
- Texto corto de tooltip:
- Mensaje al aplicar/remover:

### Criterios de aceptacion

- [ ] El jugador entiende impacto y duracion.
- [ ] Se muestra claramente cuando inicia y termina.
- [ ] No genera ambiguedad en calculo de dano/defensa.

---

## Plantilla 6: Ficha de Guardian/Jefe

### Identificacion

- Nombre:
- Titulo:
- Artefacto asociado:
- Mazmorra:

### Narrativa

- Lore resumido:
- Motivacion o naturaleza:

### Combate

- Habilidades clave:
- Fases del combate:
- Estado o mecanica exclusiva:

### Recompensa y cierre

- Recompensa narrativa:
- Recompensa jugable:
- Impacto en el mundo:

### Criterios de aceptacion

- [ ] El jefe funciona como climax del tema.
- [ ] Las fases estan telegraphed visualmente.
- [ ] La recompensa justifica el desafio.

---

## Plantilla 7: Ficha de Final

### Identificacion

- ID final:
- Tema/Mazmorra:
- Titulo del final:

### Contenido narrativo

- Descripcion del desenlace:
- Cambio en el mundo:
- Mensaje emocional:

### Recompensa

- Desbloqueo:
- Uso en progresion:

### Criterios de aceptacion

- [ ] Cierra el arco de la mazmorra.
- [ ] Refuerza la fantasia del tema.
- [ ] Entrega recompensa clara y verificable.

---

## Checklist de consistencia GDD

- [ ] Todas las fichas usan IDs unicos.
- [ ] Toda mecanica tiene feedback visual en panel.
- [ ] Ninguna ficha depende de movimiento 2D jugable.
- [ ] La narrativa, dificultad y recompensas se alinean.
- [ ] Los estados alterados tienen reglas explicitas.

## Convencion recomendada de IDs

- Mazmorra: DNG-XXX (ej: DNG-FIRE-01)
- Sala: ROOM-XXX (ej: ROOM-FIRE-03)
- Enemigo: ENM-XXX (ej: ENM-DARK-REV)
- Estado: STS-XXX (ej: STS-POISON)
- Jefe: BOS-XXX (ej: BOS-ICE-KRY)
- Final: END-XXX (ej: END-FIRE)

## Nota final

Este archivo es la base para documentar el juego bajo el enfoque definitivo de primera persona por paneles. Si se define una nueva mecanica, debe registrarse con estas plantillas antes de pasar a implementacion.
