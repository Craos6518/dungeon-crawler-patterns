# PROMPTS DEFINITIVOS — Generación de UI por Código (HTML/CSS)

## Decisión Arquitectónica (definitiva, no negociable)

| Decisión | Elección | Descartadas |
|---|---|---|
| **Herramienta de generación UI** | **Claude Artifacts** | v0.dev (pierde control del HTML), Bolt.new (complejidad innecesaria) |
| **Integración con Java** | **JavaFX WebEngine** | Swing+JxBrowser (licencias, overkill), Swing JPanel puro (retrabajo total) |

**Rationale:**
- Claude Artifacts respeta instrucciones estrictas y genera HTML/CSS puro sin "framework creep".
- JavaFX WebEngine carga el HTML tal cual, permite bridge JS↔Java vía `executeScript()`, y mantiene separación clara: UI = HTML, Lógica = Java.
- `GameViewModel` es el contrato de datos: Java envía estado como JSON → HTML solo renderiza.

> **Estrategia de 2 fases obligatoria:**
> Fase 1 → Wireframe (estructura, sin arte). Validar layout antes de continuar.
> Fase 2 → Estilo visual aplicado sobre la estructura aprobada.
> **No mezclar fases en un solo prompt. Esto rompe el resultado.**

---

## FASE 1: PROMPT DE WIREFRAME (Estructura HTML/CSS)

Pegar en **[Claude Artifacts](https://claude.ai)** — es la única herramienta validada para este flujo.

```text
You are an expert UI/UX engineer. Generate clean, semantic HTML5 + CSS3 code for a 
desktop dungeon crawler game interface. No frameworks, no React, no Tailwind. 
Pure HTML + CSS only, with clear class names.

--- HARD LAYOUT CONSTRAINTS ---

Canvas: 1366x768px fixed, desktop only.
Grid: 12 columns, 24px outer margins, 16px gutters, 8px spacing scale.
No animations yet. No colors yet. Use grayscale placeholder tones only (#111, #222, #333, #555, #888, #CCC, #EEE).
Use border-box sizing. Use CSS custom properties (--var) for all spacing and sizes.

--- SCREENS TO BUILD (3 total) ---

Build all 3 screens as separate <section> blocks in the same HTML file, 
each 1366x768px, stacked vertically.

=== SCREEN 1: EXPLORATION ===

Layout zones:
- HEADER (height: 80px, full width)
    Left block: dungeon name + theme label
    Center block: room progress "Sala X / Y"
    Right block: HP bar + gold amount

- LEFT SIDEBAR (width: 280px, height: calc(768px - 80px - 120px))
    Label: "MAPA"
    Linear minimap row: symbols [·] [⚔] [?] [?] [💀] connected by dashes
    Each symbol is a small bordered box (40x40px)

- CENTER PANEL (flexible width, same height as sidebars)
    Large card area (bordered rectangle)
    Inside: placeholder block labeled "ILUSTRACIÓN SALA (primera persona)"
    Below illustration: room name as h2, short description as paragraph

- RIGHT SIDEBAR (width: 300px, same height as left sidebar)
    Label: "INFORMACIÓN DE SALA"
    Fields listed vertically:
        Dificultad: [value]
        Tiene Tesoro: [Sí / No]
        Tiene Enemigo: [Sí / No]
    Empty space below for future event log

- FOOTER ACTIONS (height: 120px, full width)
    Primary button (larger): "Avanzar"
    Secondary buttons (smaller, same row): "Explorar" | "Inventario" | "Guardar"
    Tertiary button (smaller, distinct): "Forzar Combate"
    Align buttons centered horizontally with 16px gaps

=== SCREEN 2: COMBAT ===

Layout zones:
- HEADER: identical structure to Exploration screen.

- MAIN AREA (height: calc(768px - 80px - 160px - 80px)):
    Left 60%: Large duel card
        Placeholder block: "ILUSTRACIÓN DUELO (primera persona)"
        Below: enemy name as h2
    Right 40%: Enemy status panel
        Enemy name
        Enemy HP bar (labeled "HP Enemigo")
        Enemy type/tier badge (Menor / Elite / Jefe)
        Status effects row (icon placeholders)

- ACTION PANEL (height: 160px, full width):
    4 buttons equal width:
        "Atacar" (primary)
        "Defender" (secondary)
        "Usar Objeto" (secondary)
        "Habilidad" (secondary)

- COMBAT LOG (height: 80px, full width):
    Scrollable or static text area
    Label: "LOG DE COMBATE"
    3 placeholder log lines: "[Turno 1] ...", "[Turno 2] ...", "[Turno 3] ..."

=== SCREEN 3: INVENTORY ===

Layout zones:
- HEADER: identical structure to Exploration screen.

- BODY (height: calc(768px - 80px - 72px)):
    3-column layout:

    Left column (width: 200px): 
        Label: "CATEGORÍAS"
        Vertical list of category buttons:
            Consumible | Tesoro | Arma | Armadura | Runa | Gema
        Active state: highlighted border on selected category

    Center column (flexible):
        Label: "OBJETOS"
        Vertical scrollable list of item rows
        Each row: [icon placeholder 32x32] [item name] [item type badge]
        Show 5 placeholder items: 
            "Poción de Vida" (Consumible), "Antídoto" (Consumible),
            "Gema de Fuego" (Gema), "Espada Flamígera" (Arma), "Runa Oscura" (Runa)
        Selected item row: highlighted

    Right column (width: 300px):
        Label: "DETALLE"
        Icon placeholder (64x64)
        Item name as h2
        Item type label
        Description paragraph (placeholder text)
        Effect summary label ("Efecto: ...")

- FOOTER (height: 72px, full width):
    Primary button: "Usar"
    Secondary button: "Volver"
    Align left-center with 16px gap

--- OUTPUT REQUIREMENTS ---

- Single HTML file with embedded <style> block.
- Use CSS custom properties for all spacing: --spacing-1 (8px), --spacing-2 (16px), --spacing-3 (24px).
- Use semantic tags: <header>, <aside>, <main>, <footer>, <section>, <nav>.
- Every interactive element must have a unique id attribute.
- Add descriptive comments in CSS for each screen section.
- Do NOT add JavaScript yet.
- Do NOT apply game colors yet. Wireframe only.
```

---

## FASE 2: PROMPT DE ESTILO VISUAL (Dark Fantasy Retro)

Pegar SOLO después de que la estructura del Wireframe fue revisada y aprobada.
Pasar el HTML generado en Fase 1 como contexto en la **misma sesión de Claude** (no abrir una nueva conversación).

```text
Apply the final visual style to the existing wireframe HTML. 
Do not change the layout structure or component positions. 
Only add/modify colors, typography, borders, and micro-animations.

--- STYLE CONSTRAINTS ---

Aesthetic: Dark Fantasy Retro. High contrast. Pixel-art inspired UI frames.
No modern glassmorphism, no blurred backgrounds, no rounded corners (use sharp corners only).
Shadow: 2px 2px offset only (no blur). Example: box-shadow: 2px 2px 0px #000.
Borders: 1px-2px solid defined borders on all panels, cards and buttons.

--- TYPOGRAPHY ---

Import from Google Fonts:
    "Press Start 2P" → use for all titles, section labels, and primary button text.
    "VT323" → use for all body text, descriptions, log lines, values.

Font sizes:
    Section title (h1/label): 20px "Press Start 2P"
    Room/enemy name (h2): 16px "Press Start 2P"  
    Body text / description: 18px "VT323"
    Badge / micro-label: 14px "Press Start 2P"
    Log lines / values: 16px "VT323"
    Gold / small metadata: 14px "VT323"

--- DEFAULT ACTIVE THEME: FIRE ---

Apply these CSS custom properties as the active theme (--theme-*):

    --theme-primary: #E4572E;
    --theme-secondary: #FF9F1C;
    --theme-bg: #2B1A17;
    --theme-bg-panel: #1A0F0D;
    --theme-accent: #FFD166;
    --theme-text: #F5E6D3;
    --theme-text-dim: #99726A;
    --theme-border: #E4572E;
    --theme-border-dim: #5C2A1A;

Also define (but do not apply yet) the other 3 theme token sets as commented CSS classes:

    /* THEME: ICE
    --theme-primary: #4EA8DE;
    --theme-secondary: #90E0EF;
    --theme-bg: #102A43;
    --theme-bg-panel: #081B2B;
    --theme-accent: #CAF0F8;
    --theme-text: #E0F7FF;
    --theme-text-dim: #4A7B99;
    --theme-border: #4EA8DE;
    --theme-border-dim: #1A4B6E;
    */

    /* THEME: POISON
    --theme-primary: #588157;
    --theme-secondary: #7FB069;
    --theme-bg: #1B2A1E;
    --theme-bg-panel: #0F1A12;
    --theme-accent: #B7E4C7;
    --theme-text: #D8F3DC;
    --theme-text-dim: #3E6B44;
    --theme-border: #588157;
    --theme-border-dim: #2A4A2D;
    */

    /* THEME: DARK
    --theme-primary: #6C5CE7;
    --theme-secondary: #A29BFE;
    --theme-bg: #121420;
    --theme-bg-panel: #0A0C15;
    --theme-accent: #C9C9FF;
    --theme-text: #E8E8FF;
    --theme-text-dim: #4A4A8A;
    --theme-border: #6C5CE7;
    --theme-border-dim: #2A2060;
    */

--- BUTTON STATES ---

Define these CSS states for ALL buttons:

:default (idle):
    background: var(--theme-bg-panel);
    color: var(--theme-accent);
    border: 2px solid var(--theme-border-dim);
    box-shadow: 2px 2px 0px #000;

:hover:
    background: var(--theme-primary);
    color: var(--theme-bg);
    border-color: var(--theme-accent);
    box-shadow: 2px 2px 0px var(--theme-accent);

:active (pressed):
    background: var(--theme-secondary);
    transform: translate(1px, 1px);
    box-shadow: 1px 1px 0px #000;

:disabled:
    background: var(--theme-bg);
    color: var(--theme-text-dim);
    border-color: var(--theme-border-dim);
    opacity: 0.5;
    cursor: not-allowed;

Primary button (e.g. "Avanzar", "Atacar"):
    border-width: 3px;
    font-size: 14px;
    padding: 12px 24px;

Secondary button:
    border-width: 2px;
    font-size: 12px;
    padding: 8px 16px;

Tertiary button (e.g. "Forzar Combate"):
    border-width: 1px;
    border-style: dashed;
    font-size: 11px;
    padding: 6px 12px;
    color: var(--theme-text-dim);

--- HP BAR COMPONENT ---

HP bar: 
    outer container: border 2px solid var(--theme-border-dim), height 16px, background var(--theme-bg).
    inner fill: background var(--theme-primary), height 100%.
    loss segment: background #5C1A1A (dark red), right portion of bar.
Add CSS transition: width 0.3s ease on the fill element (for future JS hookup).

--- MINIMAP SYMBOLS ---

Each minimap symbol box [·], [⚔], [?], [💀]:
    40x40px, border 2px solid var(--theme-border-dim), centered content.
    [·] cleared: background var(--theme-bg-panel), color var(--theme-text-dim).
    [⚔] current: background var(--theme-primary), color var(--theme-bg), border var(--theme-accent).
    [?] pending: background var(--theme-bg), color var(--theme-text-dim), border dashed.
    [💀] final: background #300, color #F00, border var(--theme-primary).

--- PANEL CARDS ---

Room illustration card (center panel):
    border: 2px solid var(--theme-border);
    box-shadow: 4px 4px 0px var(--theme-border-dim);
    background: var(--theme-bg-panel);

All sidebars and panels:
    background: var(--theme-bg-panel);
    border: 1px solid var(--theme-border-dim);

--- MICRO-ANIMATIONS (CSS only, no JS) ---

Add the following CSS-only animations:
    Button hover: transition all 0.1s linear.
    Minimap pulse on [⚔] current room: 
        @keyframes pulse-border { 0%,100%{border-color:var(--theme-accent);} 50%{border-color:var(--theme-primary);} }
        animation: pulse-border 1.5s ease-in-out infinite;
    HP bar fill transition: transition width 0.3s ease.

--- OUTPUT REQUIREMENTS ---

- Return the complete updated single HTML file.
- Keep all id attributes intact from the wireframe.
- Keep all semantic HTML tags intact.
- All theme tokens as CSS custom properties on :root.
- The commented theme blocks (ICE, POISON, DARK) must be present in the <style> block for easy swap.
- Add a small "THEME SWITCHER" bar at the very top of the page (outside the game screens):
    4 buttons: [🔥 Fuego] [❄️ Hielo] [☠️ Veneno] [🌑 Oscuridad]
    Clicking each should add a theme class (theme-fire, theme-ice, theme-poison, theme-dark) to <body>.
    Include the minimal <script> block for this switcher ONLY.
```

---

## FASE 3 (OPCIONAL): PROMPT DE VARIANTES TEMÁTICAS

Para generar las 4 pantallas completas en las 4 temáticas:

```text
Duplicate the 3 final screens (Exploration, Combat, Inventory) into 4 complete 
visual theme variants: Fire, Ice, Poison, Dark.

Rules:
- Do not change any component positions, labels, or action names.
- Only change: background colors, border tints, accent colors, icon tints.
- Apply each theme's token set (defined in the CSS) to a separate screen group.
- Output: 12 total screens (3 screens × 4 themes) as separate <section> blocks 
  in one HTML file, labeled with a visible theme badge in the top-right corner.
- The theme badge text: "🔥 FUEGO" / "❄️ HIELO" / "☠️ VENENO" / "🌑 OSCURIDAD"
```

---

## Mapeo de Resultado al Proyecto Java

### Integración definitiva: JavaFX WebEngine

El HTML generado se carga directamente en un `WebView` de JavaFX. No se rehace nada en Swing.

```java
// Carga inicial del HTML en JavaFX
WebView webView = new WebView();
WebEngine engine = webView.getEngine();
engine.load(getClass().getResource("/ui/game.html").toExternalForm());
```

#### Flujo de datos: Java → HTML (push de estado)

```java
// Serializar GameViewModel a JSON y enviarlo a la UI
String json = new Gson().toJson(gameViewModel);
engine.executeScript("window.updateGameState(" + json + ")");
```

#### Flujo de comandos: HTML → Java (bridge de botones)

En el HTML generado, los botones deben llamar a una función global que Java intercepta:

```javascript
// En el HTML (se añade en Fase 2 del prompt)
function sendCommand(action, payload) {
    window.javabridge.dispatch(action, JSON.stringify(payload || {}));
}
document.getElementById('btn-avanzar').onclick = () => sendCommand('advanceRoom');
document.getElementById('btn-atacar').onclick  = () => sendCommand('attack');
```

```java
// En Java: registrar el objeto bridge antes de cargar el HTML
engine.getLoadWorker().stateProperty().addListener((obs, old, newState) -> {
    if (newState == Worker.State.SUCCEEDED) {
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("javabridge", new JavaBridge(gameController));
    }
});

// Clase bridge que recibe comandos desde el HTML
public class JavaBridge {
    private final GameController controller;
    public JavaBridge(GameController c) { this.controller = c; }

    public void dispatch(String action, String payloadJson) {
        switch (action) {
            case "advanceRoom"   -> controller.advanceRoom();
            case "attack"        -> controller.attack();
            case "defend"        -> controller.defend();
            case "searchTreasure"-> controller.searchTreasure();
            case "saveGame"      -> controller.saveGame();
            case "useItem"       -> controller.useItem(payloadJson);
            case "useSkill"      -> controller.useSkill(payloadJson);
            case "openInventory" -> controller.openInventory();
        }
    }
}
```

### Tabla de mapeo HTML → Java

| Elemento HTML (`id`) | Comando Java dispatched | Evento observer que refresca UI |
|---|---|---|
| `#screen-exploration` | — | `ESTADO_CAMBIADO` |
| `#screen-combat` | — | `COMBATE_INICIADO` |
| `#screen-inventory` | — | `ITEM_USADO` |
| `#btn-avanzar` | `advanceRoom()` | `ESTADO_CAMBIADO` |
| `#btn-explorar` | `searchTreasure()` | `ESTADO_CAMBIADO` |
| `#btn-guardar` | `saveGame(slot)` | `JUEGO_GUARDADO` |
| `#btn-forzar-combate` | `forceCombat()` | `COMBATE_INICIADO` |
| `#btn-atacar` | `attack()` | `COMBATE_FINALIZADO` |
| `#btn-defender` | `defend()` | `COMBATE_FINALIZADO` |
| `#btn-usar-objeto` | `useItem(itemId)` | `ITEM_USADO` |
| `#btn-habilidad` | `useSkill(skillId)` | `COMBATE_FINALIZADO` |
| Barras HP | — | `ESTADO_CAMBIADO` → `updateGameState(vm)` |
| Minimapa | — | `ESTADO_CAMBIADO` → derivado de `roomIndex/roomTotal` |
| Theme switcher (`body.theme-*`) | — | `GameViewModel.theme` al cargar mazmorra |
| Log de combate | — | `COMBATE_INICIADO`, `ITEM_USADO`, `COMBATE_FINALIZADO` |

### Regla de UI a respetar siempre

> La UI **solo dispara comandos**. El motor resuelve lógica y publica eventos. La UI se refresca desde `GameViewModel` nuevo vía `updateGameState()`, nunca por mutaciones locales.

---

*Documento alineado con GDD_Fichas_Primera_Persona.md y GENERACION_MOCKUPS.md.*
*Herramienta: Claude Artifacts. Integración: JavaFX WebEngine. Opciones descartadas: v0.dev, Bolt.new, Swing JPanel, JxBrowser.*
*Última actualización: 2026-03-29.*
