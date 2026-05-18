# Interfaz del Juego - Documentación de Estructura

**Archivo**: [src/main/resources/ui/game.html](../../src/main/resources/ui/game.html)  
**Tamaño**: 42 KB  
**Exportado**: 17 de mayo de 2026

## Exportaciones Visuales

| Formato | Tamaño | Ubicación                                                    | Resolución     |
| ------- | ------ | ------------------------------------------------------------ | -------------- |
| **JPG** | 97 KB  | [INTERFAZ_JUEGO_GAMEPLAY.jpg](./INTERFAZ_JUEGO_GAMEPLAY.jpg) | 1600 × 1000 px |
| **PNG** | 116 KB | [INTERFAZ_JUEGO_GAMEPLAY.png](./INTERFAZ_JUEGO_GAMEPLAY.png) | 1600 × 1000 px |

---

## Estructura de Pantallas (8 Screens)

La interfaz está dividida en **8 pantallas principales** (screens) que controlan todo el flujo del juego:

### **SCREEN 0 — MENÚ PRINCIPAL** (`#screen-menu`)

- **Punto de entrada** del juego
- **Acciones disponibles**:
  - `1` → Nueva partida (goToHeroSelect)
  - `2` → Cargar partida (openSaves)
  - `3` → Estadísticas (showStats)
  - `4` → Lore/Historia (openWorldLore)
  - `5` → Salir (exitGame)

### **SCREEN 1 — EXPLORACIÓN** (`#screen-exploration`)

- **Hub central del gameplay**
- **Layout**: Header + 3 columnas + Footer
  - **Header**: Nombre mazmorra, clase héroe, tema, sala actual, HP, oro
  - **Left sidebar**: Minimapa (salas exploradas)
  - **Center**: Ilustración de sala
  - **Right sidebar**: Información de sala, log de eventos
  - **Footer**: Botones de acción (Avanzar, Explorar, Inventario, Guardar, Forzar Combate)

**Datos bound:**

- `hdr-dungeon-name` → nombre de mazmorra
- `hdr-room-progress` → "Sala X / Y"
- `hdr-player-hp-fill` → barra de vida
- `hdr-gold` → oro disponible

### **SCREEN 2 — COMBATE** (`#screen-combat`)

- **Duelo por turnos** contra enemigo
- **Layout**: Header + Duelo (60%) + Enemy Panel (40%) + Action Panel + Combat Log
  - **Left (60%)**: Ilustración de duelo en primera persona
  - **Right (40%)**: Estado del enemigo, efectos, recursos, tácticas
  - **Footer**: Acciones de combate

**Acciones de combate**:

- Atacar (`attack`)
- Defender (`defend`)
- Usar Objeto (`openInventory`)
- Habilidad (`useSkill`)
- Tácticas → Estilos (Balanceado, Agresivo, Defensivo)
- Tácticas → Buffs (Poder, Guardia)
- Retirada (`retreatCombat`)

**Estado enemigo**:

- Nombre
- HP bar
- Tier/Rango
- 4 slots de efectos de estado
- Recurso del héroe (mana, energía, etc.)

### **SCREEN 3 — INVENTARIO** (`#screen-inventory`)

- **Gestión de objetos**
- **Layout**: Header + 3 columnas + Footer
  - **Left**: Categorías (Consumible → se puede expandir)
  - **Center**: Lista de items
  - **Right**: Detalle del item seleccionado

**Datos del item**:

- Nombre, tipo, descripción
- Efecto
- Valor total (`s3-item-valor`)
- Peso total (`s3-item-peso`)

**Acciones**:

- Usar item (`useItem`)
- Comprar poción (`buyHealthPotion`)
- Vender seleccionado (`sellSelectedItem`)
- Volver (`closeInventory`)

### **SCREEN 4 — SELECCIÓN DE HÉROE** (`#screen-hero`)

- **PASO 1**: Seleccionar clase (Guerrero, Mago, Arquero)
  - Data attribute: `data-hero="guerrero|mago|arquero"`
  - Action: `selectHero`

- **PASO 2**: Seleccionar mazmorra/tema
  - 4 temas disponibles:
    - 🕷️ **Poison**: Pantanos de Viridax (8 salas)
    - 🐲 **Ice**: Catacumbas de Glaciurvh (8 salas)
    - 🐉 **Fire**: Volcán de Ignareth (7 salas)
    - 👁️ **Dark**: Ciudadela de Umbrakar (10 salas)
  - Action: `heroNewGame` (data-theme)

### **SCREEN 5 — ESTADÍSTICAS** (`#screen-stats`)

- **Panel informativo** de sesión actual
- **Datos mostrados**:
  - Clase héroe e ícono
  - HP actual
  - Tipo mazmorra
  - Stats generales

### **SCREEN 6 — TESORO** (`#screen-treasure`)

- **Recompensas post-combate**
- **Elementos**:
  - Banner de victoria
  - Lista de loot recibido
  - Checkpoint guardado automáticos
  - Botón "Continuar"

### **SCREEN 7 — GUARDADO (Memento)** (`#screen-saves`)

- **Gestión de 3 slots** de guardado
- **Estructura**:
  - 3 rows de slots (vacío o con datos guardados)
  - Acciones: Seleccionar slot, cargar, borrar

### **SCREEN 8 — GAME OVER** (`#screen-gameover`)

- **Pantalla terminal** (derrota)
- **Información**:
  - Motivo de derrota
  - Stats finales (salas exploradas, enemigos, oro, turnos)
- **Acciones**:
  - `[ R ]` → Cargar último guardado
  - `[ M ]` → Volver al menú
  - `[ N ]` → Nueva partida

---

## Ventana Modal — LORE NARRATIVO

### **Story Window** (`#lore-window-overlay`)

- Ventana modal superpuesta
- **Contenido**:
  - Eyebrow: "CRÓNICAS"
  - Título y subtítulo
  - Body: Texto narrativo
  - Botón: Continuar

---

## Contrato de Integración Java ↔ JavaScript

### **Screen Switching**

```javascript
document
  .querySelectorAll(".screen")
  .forEach((s) => s.classList.remove("active"));
document.getElementById("screen-exploration").classList.add("active");
```

### **Data Binding**

Los elementos con `data-bind="..."` se actualizan desde Java:

```javascript
engine.executeScript("window.updateGameState(" + json + ")");
```

### **Action Dispatch**

Todos los botones llevan `data-action="<id>"`:

```javascript
webEngine.setOnAlert(e -> dispatcher.handle(e.getData()))
```

Botones automáticamente envían su acción:

```javascript
document
  .querySelectorAll("[data-action]")
  .forEach((b) =>
    b.addEventListener("click", () => window.alert(b.dataset.action)),
  );
```

### **Item Value/Weight**

**IMPORTANTE**: Usar `getValorTotal()` y `getPesoTotal()`, NO `getValor()` / `getPeso()` (legacy)

```javascript
s3-item-valor → item.getValorTotal()   (SimpleItem/ItemComponent)
s3-item-peso  → item.getPesoTotal()    (SimpleItem/ItemComponent)
```

---

## Temas Visuales (CSS Classes)

### **Body theme class**

```html
<body class="theme-fire">
  <!-- puede ser: fire, ice, poison, dark -->
</body>
```

Cada tema modifica:

- Colores primarios y secundarios
- Acentos
- Bordes
- Luces y sombras

---

## Elementos Clave del DOM

| ID                     | Propósito              | Bind/Update                                       |
| ---------------------- | ---------------------- | ------------------------------------------------- |
| `#screen-*`            | Contenedor de pantalla | Clase `.active`                                   |
| `#hdr-dungeon-name`    | Nombre mazmorra        | `state.dungeonName`                               |
| `#hdr-room-progress`   | Progreso de salas      | `'Sala ' + state.room + ' / ' + state.totalRooms` |
| `#hdr-player-hp-fill`  | Barra de vida          | `style.width = state.playerHpPct + '%'`           |
| `#hdr-gold`            | Oro disponible         | `'⬡ ' + state.gold + ' Monedas'`                  |
| `#s1-minimap-row`      | Minimapa de salas      | Items renderizados                                |
| `#s1-event-log`        | Log de exploración     | Entradas nuevas (aria-live)                       |
| `#s2-enemy-hp-text`    | HP del enemigo         | `state.enemy.hp + ' / ' + state.enemy.hpMax`      |
| `#s2-status-effects`   | 4 slots de efectos     | `.status-slot[data-effect]`                       |
| `#s2-combat-log`       | Log de combate         | Entradas nuevas (aria-live)                       |
| `#s3-item-rows`        | Lista de items         | Items renderizados dinámicamente                  |
| `#s3-detail-panel`     | Detalle de item        | Datos del item seleccionado                       |
| `#treasure-loot-list`  | Lista de tesoro        | Items obtenidos                                   |
| `#lore-window-overlay` | Modal de lore          | `.lore-overlay` visible/hidden                    |

---

## Dependencias de Archivos

```
ui/
├── game.html                (MAIN — este archivo)
├── styles/
│   └── game.css            (Estilos + variables CSS + temas)
└── scripts/
    └── game.js             (Integración Java + Event handlers)
```

**Fuentes externas** (Google Fonts):

- `Press Start 2P` (titles, retro)
- `VT323` (secondary, monospace retro)

---

## Accesibilidad (a11y)

- Atributos `aria-label` en secciones principales
- `aria-live="polite"` en logs (eventos, combate)
- `aria-pressed` en botones táctiles (hero cards)
- `aria-expanded` en menús desplegables
- `role="dialog"` y `aria-modal` en ventanas modales

---

## Estados Visuales de Botones

Todos los botones tienen `data-state="default"` que puede cambiar a:

- `"disabled"` → Botón deshabilitado
- `"loading"` → Animación de carga
- `"active"` → Estado presionado

Clases CSS asociadas:

```css
.btn.btn--primary   /* Botón primario (destacado) */
.btn.btn--ghost     /* Botón secundario (fantasma) */
.btn--disabled      /* Estado deshabilitado */
```

---

## Notas de Desarrollo

1. **No mezclat lógica**: El HTML contiene SOLO estructura y binding. Toda la lógica va en Java.
2. **Data attributes**: Se usan para dispatch de acciones (`data-action`) y parámetros (`data-theme`, `data-hero`).
3. **Live logs**: `aria-live="polite"` permite que screen readers anuncien cambios automáticamente.
4. **Tema dinámico**: El body class cambia según el tema de campaña actual.
5. **Responsive**: El layout usa flexbox con gaps y variables CSS (`--spacing-*`).

---

## Documentos Relacionados

- [Especificación de Requerimientos](../01-product/Especificacion_Requerimientos_Sistema_ISO29148.md)
- [Arquitectura Runtime](../02-architecture/ARQUITECTURA_RUNTIME.md)
- [GDD Canónico](../01-product/GDD_CANONICO.md)
- Código fuente: [src/main/resources/ui/](../../src/main/resources/ui/)
- Backend Java: [src/main/java/game/ui/](../../src/main/java/game/ui/)

---

_Documentación generada automáticamente el 17 de mayo de 2026._
