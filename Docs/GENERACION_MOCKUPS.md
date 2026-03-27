# Guia Auditada para Generacion de Mockups con IA

Este documento esta auditado y alineado exclusivamente con lo que el juego implementa hoy en runtime (consola), sin incluir contenido objetivo a futuro.

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

## 6. Proceso recomendado para Figma Make (no generico)

Para evitar resultados blandos, usar 2 fases separadas:

1. Fase Wireframe (estructura sin arte)
2. Fase Visual (aplicar estilo pixel art sobre estructura aprobada)

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
- Area central (ancho flexible): viewport de sala actual
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
    - 60% izquierda: zona de duelo heroe vs enemigo
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

- Estilo: pixel art 16-bit, alto contraste, bordes marcados.
- Forma UI: paneles rectangulares con marco pixel de 2 px.
- Sombra: offset corto (2 px x 2 px), sin blur moderno.
- Iconografia: pixel icons de 16x16 y 24x24.

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

## 10. Prompt maestro para Figma Make (copiar/pegar)

```text
Create a coherent UI kit and 3 desktop game screens for a retro 2D dungeon crawler.

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
- Theme loot and enemies only:
    Fire: Gema de Fuego, Espada Flamigera, Salamandra de Fuego, Orco Flamigero, Dragon de Fuego Ancestral.
    Ice: Cristal de Hielo, Baculo del Invierno, Lobo de Hielo, Orco Glacial, Dragon de Escarcha.
    Poison: Vial de Veneno, Daga del Asesino, Arana Venenosa, Orco Putrefacto, Hidra Toxica.
    Dark: Runa Oscura, Armadura de las Sombras, Sombra Errante, Caballero Oscuro, Senor de las Sombras.

Visual direction:
- 16-bit pixel-art UI, crisp borders, no modern blur-heavy glassmorphism.
- Use Press Start 2P (or similar pixel font) for headings and VT323 (or similar) for body text.
- Include 4 theme-ready color token sets (Fire/Ice/Poison/Dark) and keep component structure identical.

Do not include:
- puzzle mechanics
- key-door systems
- branching minimap node graphs
- non-implemented special gadgets
```

