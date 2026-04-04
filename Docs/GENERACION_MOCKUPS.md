# Guia Auditada para Generacion de Mockups con IA (Opcion 1)

Este documento esta auditado y alineado exclusivamente con lo que el juego implementa hoy en runtime (consola), sin incluir contenido objetivo a futuro.

Decision oficial de interfaz:

- Se adopta Opcion 1: interfaz por tarjetas/pantallas estaticas con botones de accion.
- No se adopta Opcion 2 como runtime actual: exploracion en mapa 2D jugable con movimiento libre.
- Se permite arte de fondo estilo sala (primera persona) solo como ilustracion contextual, no como escenario navegable.

## 1. Alcance real hoy

- Plataforma actual: juego de consola (sin render 2D ingame).
- Runtime principal: DomainStates.
- Exploracion: avance por salas secuenciales con eventos probabilisticos.
- Temas disponibles: Fuego, Hielo, Oscuridad, Veneno.

## 2. Estructura real de mazmorras

### 2.1 Cantidad de salas

- Cada partida genera proceduralmente entre 4 y 8 salas normales.
- El jefe se encuentra en la ultima sala recorrida del arreglo de salas.
- No existe hoy una malla tipo grafo ni rutas ramificadas visibles para el jugador.

### 2.2 Representacion actual de mapa

El mapa que muestra el juego es lineal:

- `[⚔]` sala actual
- `[·]` sala ya recorrida
- `[?]` sala pendiente
- `[💀]` ultima sala (encuentro final)

Ejemplo de salida:

```text
Mapa: [·]-[⚔]-[?]-[?]-[💀]
```

### 2.3 Tipos de sala que SI existen como dato

En el modelo actual, una sala tiene:

- nombre
- descripcion
- dificultad
- `tieneTesoro` (boolean)
- `tieneEnemigo` (boolean)

No existe un tipo enum visible de sala como Entrada/Tesoro/Objeto/Puzle/Bloqueada para logica de gameplay.

## 3. Flujo real de acontecimientos en exploracion

En cada sala, el jugador puede:

1. Avanzar a la siguiente sala.
2. Explorar sala / buscar tesoro.
3. Abrir inventario.
4. Guardar partida.
5. Forzar combate.
6. Volver al menu.

Reglas actuales:

- Al avanzar hay probabilidad de encontrar enemigo.
- Buscar tesoro puede dar tesoro comun, raro o nada.
- Si la sala es la ultima, el enemigo generado es el jefe del tema.
- No hay puzles implementados como sistema jugable en el loop actual.

## 4. Objetos reales disponibles hoy

## 4.1 Inventario inicial confirmado

- Pocion de Vida
- Antidoto

## 4.2 Loot por tema (Abstract Factory)

### Fuego
- Tesoro comun: Gema de Fuego
- Tesoro raro: Espada Flamigera
- Enemigo basico: Salamandra de Fuego
- Enemigo medio: Orco Flamigero
- Jefe: Dragon de Fuego Ancestral

### Hielo
- Tesoro comun: Cristal de Hielo
- Tesoro raro: Baculo del Invierno
- Enemigo basico: Lobo de Hielo
- Enemigo medio: Orco Glacial
- Jefe: Dragon de Escarcha

### Veneno
- Tesoro comun: Vial de Veneno
- Tesoro raro: Daga del Asesino
- Enemigo basico: Arana Venenosa
- Enemigo medio: Orco Putrefacto
- Jefe: Hidra Toxica

### Oscuridad
- Tesoro comun: Runa Oscura
- Tesoro raro: Armadura de las Sombras
- Enemigo basico: Sombra Errante
- Enemigo medio: Caballero Oscuro
- Jefe: Senor de las Sombras


## 5. Eventos reales relevantes

Eventos usados por el sistema observer y flujo actual:

- `JUEGO_INICIADO`
- `COMBATE_INICIADO`
- `COMBATE_FINALIZADO`
- `ITEM_USADO`
- `JUEGO_GUARDADO`
- `JUEGO_CARGADO`
- `JUEGO_TERMINADO`
- `ESTADO_CAMBIADO`

Existen otros tipos definidos en enum, pero no todos estan conectados al loop principal de gameplay actual.

## 6. Proceso recomendado para IA de diseno (no generico)

Para evitar resultados blandos, usar 2 fases separadas:

1. Fase Wireframe (estructura sin arte)
2. Fase Visual (aplicar estilo retro sobre estructura aprobada)

No mezclar ambas fases en un solo prompt inicial.

## 7. Wireframes obligatorios (fase 1)

Generar exactamente 3 pantallas base con layout consistente:

1. Exploracion
2. Combate
3. Inventario

### 7.1 Sistema de layout comun

- Frame desktop: 1366x768
- Grid: 12 columnas
- Margen externo: 24 px
- Gutter: 16 px
- Escala base: 8 px

### 7.2 Regla de jerarquia visual

- Nivel 1: Area de accion principal (centro)
- Nivel 2: Estado del jugador y contexto de sala (arriba)
- Nivel 3: Acciones y botones (abajo)
- Nivel 4: Metadatos (oro, contador de salas, tema)

### 7.3 Layout exacto por pantalla

#### Pantalla 1: Exploracion

- Header superior (alto 80 px):
    - izquierda: nombre de mazmorra + tema
    - centro: progreso de sala `x/y`
    - derecha: HP y oro
- Columna izquierda (ancho 280 px): minimapa lineal
- Area central (ancho flexible): tarjeta principal de sala (imagen estatica + overlay de texto)
- Columna derecha (ancho 300 px): panel contextual
    - nombre sala
    - descripcion corta
    - dificultad
    - indicadores: `tieneTesoro`, `tieneEnemigo`
- Footer acciones (alto 120 px):
    - boton primario: Avanzar
    - boton secundario: Explorar
    - boton secundario: Inventario
    - boton secundario: Guardar
    - boton terciario: Forzar combate

#### Pantalla 2: Combate

- Header igual a exploracion para consistencia.
- Zona central dividida 60/40:
    - 60% izquierda: tarjeta visual de duelo heroe vs enemigo (estatica)
    - 40% derecha: panel de estado del enemigo
- Panel inferior (alto 160 px): acciones de combate
    - Atacar
    - Defender
    - Usar objeto
    - Habilidad
- Subpanel de log (alto 80 px) debajo de acciones para ultimos eventos.

#### Pantalla 3: Inventario

- Header igual a exploracion para consistencia.
- Cuerpo en 3 columnas:
    - izquierda: categorias (`Consumible`, `Tesoro`, `Arma`, `Armadura`, `Runa`, `Gema`)
    - centro: lista de items
    - derecha: detalle del item seleccionado
- Footer:
    - boton primario: Usar
    - boton secundario: Volver

## 8. Especificacion visual (fase 2)

Aplicar estilo solo cuando wireframe este aprobado.

### 8.1 Direccion artistica concreta

- Estilo: dark fantasy retro, alto contraste, bordes marcados.
- Forma UI: paneles rectangulares, tarjetas con marco consistente.
- Sombra: offset corto (2 px x 2 px), sin blur moderno.
- Iconografia: iconos 16x16 y 24x24 con lectura clara.
- Animacion minima: hover/pressed en botones, cambio de barra de HP, flash corto de dano.

### 8.2 Tipografia

- UI: "Press Start 2P" o alternativa pixel equivalente.
- Texto secundario: "VT323" o equivalente monoespaciada retro.
- Jerarquia:
    - titulo seccion: 20 px
    - labels: 14 px
    - valores: 16 px
    - microtexto: 12 px

### 8.3 Tokens de color por tema (UI + escenario)

#### Fuego
- Primario: #E4572E
- Secundario: #FF9F1C
- Fondo oscuro: #2B1A17
- Acento: #FFD166

#### Hielo
- Primario: #4EA8DE
- Secundario: #90E0EF
- Fondo oscuro: #102A43
- Acento: #CAF0F8

#### Veneno
- Primario: #588157
- Secundario: #7FB069
- Fondo oscuro: #1B2A1E
- Acento: #B7E4C7

#### Oscuridad
- Primario: #6C5CE7
- Secundario: #A29BFE
- Fondo oscuro: #121420
- Acento: #C9C9FF

## 9. Consistencia obligatoria entre mockups

Estas reglas son duras, no opcionales:

- Misma grilla, mismos margenes y misma altura de header/footer en las 3 pantallas.
- Misma posicion del bloque de HP y oro.
- Mismo estilo de botones: primario/ secundario/terciario.
- Misma escala de iconos para minimapa y estados.
- Misma nomenclatura de acciones del juego real.
- No inventar mecanicas nuevas (llaves, puzles, puertas bloqueadas, gadgets especiales).

## 10. Prompts listos para IA de diseno

Los siguientes prompts estan optimizados para generar la interfaz de Opcion 1.

### 10.1 Prompt maestro (estructura + estilo)

```text
Create a coherent UI kit and 3 desktop game screens for a linear dungeon crawler that uses static room cards and action buttons (no free movement map).

Hard constraints:
- Use 1366x768 frames, 12-column grid, 24px margins, 16px gutters, 8px spacing scale.
- Build first as low-fidelity wireframes (no textures), then apply visual style.
- Keep identical header/footer structure across all screens.
- Screens required: Exploration, Combat, Inventory.

Gameplay canon (must match current implementation):
- Dungeon rooms are linear, not a node graph.
- Run length is 4 to 8 rooms.
- Last room is final encounter.
- Minimap state symbols: [⚔] current, [·] cleared, [?] pending, [💀] final.
- Initial inventory: Pocion de Vida, Antidoto.
- Exploration actions: Avanzar, Explorar, Inventario, Guardar, Forzar combate.
- Combat actions: Atacar, Defender, Usar objeto, Habilidad.
- Theme loot and enemies only:
    Fire: Gema de Fuego, Espada Flamigera, Salamandra de Fuego, Orco Flamigero, Dragon de Fuego Ancestral.
    Ice: Cristal de Hielo, Baculo del Invierno, Lobo de Hielo, Orco Glacial, Dragon de Escarcha.
    Poison: Vial de Veneno, Daga del Asesino, Arana Venenosa, Orco Putrefacto, Hidra Toxica.
    Dark: Runa Oscura, Armadura de las Sombras, Sombra Errante, Caballero Oscuro, Senor de las Sombras.

Visual direction:
- Dark fantasy + retro UI, crisp borders, no modern blur-heavy glassmorphism.
- Use Press Start 2P (or similar pixel font) for headings and VT323 (or similar) for body text.
- Include 4 theme-ready color token sets (Fire/Ice/Poison/Dark) and keep component structure identical.

Interaction model:
- The central room is a static illustration card in first-person perspective.
- Do not render top-down navigation gameplay.
- Add only micro-animations: button hover/press, HP bar transitions, hit flash.

Do not include:
- puzzle mechanics
- key-door systems
- branching minimap node graphs
- non-implemented special gadgets
- joystick / WASD controls
```

### 10.2 Prompt de solo wireframe (fase 1)

```text
Create low-fidelity wireframes only (no textures, no final colors) for 3 desktop screens: Exploration, Combat, Inventory.

Mandatory layout system:
- 1366x768
- 12-column grid
- 24px outer margins
- 16px gutters
- 8px spacing scale

Exploration screen:
- Header 80px: dungeon+theme (left), room progress x/y (center), HP+gold (right)
- Left 280px: linear minimap panel
- Center flexible: static room card with title + short description
- Right 300px: room metadata (difficulty, tieneTesoro, tieneEnemigo)
- Footer 120px actions: Avanzar (primary), Explorar, Inventario, Guardar, Forzar combate

Combat screen:
- Same header
- Main area 60/40: duel card (left), enemy status panel (right)
- Action panel 160px: Atacar, Defender, Usar objeto, Habilidad
- Log panel 80px below action panel

Inventory screen:
- Same header
- 3 columns: categories, item list, item detail
- Footer: Usar (primary), Volver

Keep the same component positions across all three screens.
```

### 10.3 Prompt de visual final (fase 2)

```text
Apply a final visual style to the approved wireframes.

Style constraints:
- Dark fantasy retro interface
- Crisp panel borders and card containers
- High contrast text readability
- No modern glossy or heavy glass effects
- Button states: idle, hover, pressed, disabled
- HP bar and enemy HP bar with clear loss segment

Theme tokens (must keep same layout, only swap palette accents):
- Fire: #E4572E #FF9F1C #2B1A17 #FFD166
- Ice: #4EA8DE #90E0EF #102A43 #CAF0F8
- Poison: #588157 #7FB069 #1B2A1E #B7E4C7
- Dark: #6C5CE7 #A29BFE #121420 #C9C9FF

Output components:
- UI kit page (buttons, bars, cards, badges, minimap symbols)
- Exploration screen (final)
- Combat screen (final)
- Inventory screen (final)
```

### 10.4 Prompt para variantes por tema

```text
Duplicate the 3 final screens into 4 visual theme variants: Fire, Ice, Poison, Dark.
Do not move components. Only change color accents, background illustration, and icon tint according to each theme.
Keep text labels and action names identical.
```

## 11. Vinculacion de UI con imagenes y motor del juego

Esta seccion define como conectar lo visual con el runtime sin romper la arquitectura actual.

### 11.1 Contrato de datos que la UI necesita

La UI por tarjetas debe consumir un objeto de estado de pantalla construido desde DomainStates:

```text
GameViewModel
- dungeonName
- theme
- roomIndex
- roomTotal
- hpActual
- hpMax
- gold
- roomName
- roomDescription
- roomDifficulty
- hasTreasure
- hasEnemy
- playerActions[]
- minimapSymbols[]
- inventoryItems[]
- combatInfo (opcional)
```

### 11.2 Mapeo de imagenes (tarjetas estaticas)

- Clave recomendada: `theme + roomDifficulty + combatFlag`.
- Ejemplo de ruta: `assets/cards/fire/room_normal_explore.png`.
- Si no existe imagen especifica, usar fallback por tema: `assets/cards/fire/default.png`.
- No bloquear flujo por falta de asset; registrar warning y seguir con fallback.

### 11.3 Mapeo de botones a comandos del motor

Botones de exploracion:

- Avanzar -> comando `advanceRoom()`
- Explorar -> comando `searchTreasure()`
- Inventario -> comando `openInventory()`
- Guardar -> comando `saveGame(slot)`
- Forzar combate -> comando `forceCombat()`

Botones de combate:

- Atacar -> comando `attack()`
- Defender -> comando `defend()`
- Usar objeto -> comando `useItem(itemId)`
- Habilidad -> comando `useSkill(skillId)`

Regla de UI:

- La UI solo dispara comandos.
- El motor resuelve logica y publica eventos.
- La UI se refresca desde estado nuevo, no por mutaciones locales ad hoc.

### 11.4 Mapeo de descripciones y texto contextual

- `roomName` y `roomDescription` se toman de la sala actual.
- Texto de panel derecho: dificultad + flags (`hasTreasure`, `hasEnemy`).
- El log inferior de combate consume eventos recientes (`COMBATE_INICIADO`, `ITEM_USADO`, `COMBATE_FINALIZADO`).

### 11.5 Mapeo de pociones y loot al inventario

Inventario inicial:

- Pocion de Vida
- Antidoto

Render recomendado por item:

- `itemName`
- `itemType` (Consumible, Tesoro, Arma, Armadura, Runa, Gema)
- `itemRarity` (si aplica)
- `itemDescription`
- `itemEffectSummary`
- `itemIconKey`

Al usar pocion:

- UI envia `useItem(itemId)`.
- Motor aplica efecto y emite `ITEM_USADO`.
- UI actualiza barra HP, cantidad restante y log.

Al obtener loot:

- Motor resuelve drop por tema (Abstract Factory).
- UI muestra notificacion breve (toast/panel) con nombre de item.
- Item aparece en lista de inventario al recargar `GameViewModel`.

### 11.6 Mapeo de minimapa lineal

- Derivar simbolos desde `roomIndex` y `roomTotal`.
- Reglas:
    - sala actual: `[⚔]`
    - salas previas: `[·]`
    - salas futuras: `[?]`
    - ultima sala: `[💀]`

Ejemplo: `Mapa: [·]-[⚔]-[?]-[?]-[💀]`.

### 11.7 Eventos observer que deben refrescar UI

Refrescar al menos en:

- `ESTADO_CAMBIADO`
- `COMBATE_INICIADO`
- `COMBATE_FINALIZADO`
- `ITEM_USADO`
- `JUEGO_GUARDADO`
- `JUEGO_CARGADO`

Recomendacion tecnica:

- Implementar un adaptador `GamePresenter` que traduzca eventos y estado de dominio a `GameViewModel`.
- Mantener renderer desacoplado de logica para poder cambiar Swing/JavaFX sin tocar gameplay.

