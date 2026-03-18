# Mockups — Dungeon Crawler Academico

**Proyecto:** Dungeon Crawler Academico  
**Curso:** Patrones de Diseño de Software  
**Autor:** Andres Felipe Martinez Henao  
**Tecnología:** Java 17 · Maven · JUnit 5

---

## Índice de pantallas

| # | Archivo                          | Pantalla                        | Patrones clave                      |
|---|----------------------------------|---------------------------------|-------------------------------------|
| 1 | `01_menu_principal.md`           | Menú Principal / Selec. Héroe   | Factory Method, Memento             |
| 2 | `02_exploracion_mazmorra.md`     | Exploración + Mapa de salas     | State, Observer, Facade, Builder    |
| 3 | `03_combate_turnos.md`           | Combate por turnos              | Command, State, Observer, Strategy  |
| 4 | `04_inventario_composite.md`     | Inventario jerárquico           | Composite, Decorator, Factory       |
| 5 | `05_ia_strategy_observer.md`     | IA de enemigos (debug view)     | Strategy, Observer                  |
| 6 | `06_tesoro_guardado_gameover.md` | Tesoro / Guardado / Game Over   | Memento, State, Factory Method      |

---

## Flujo completo de estados

```
MENU_PRINCIPAL
      │
      ├── nueva partida → selección de héroe
      │         └── EXPLORACION ──────────────────────────────┐
      │                 │                                      │
      │                 ├── explorar sala → evento Observer    │
      │                 ├── COMBATE                            │
      │                 │       ├── victoria → tesoro          │
      │                 │       │     └── Memento.save()       │
      │                 │       │     └── EXPLORACION ─────────┘
      │                 │       └── derrota → GAME_OVER
      │                 │                         ├── cargar → EXPLORACION
      │                 │                         └── menu  → MENU_PRINCIPAL
      │                 └── INVENTARIO → regresa a EXPLORACION
      │
      └── cargar partida → Memento.restore() → EXPLORACION
```

---

## Trazabilidad de patrones

| Patrón          | Pantalla(s) donde se evidencia            |
|-----------------|-------------------------------------------|
| Factory Method  | Menú principal (creación de héroes)       |
| Factory Method  | Combate (loot), Exploración (salas)       |
| Builder         | Exploración (construcción de mazmorra)    |
| Composite       | Inventario (árbol de objetos)             |
| Decorator       | Inventario (efectos en ítems)             |
| Facade          | Exploración (acceso simplificado)         |
| Command         | Combate (acciones del jugador)            |
| Strategy        | IA enemigos (lógica intercambiable)       |
| Observer        | Combate y exploración (log de eventos)    |
| State           | Todos (transición entre estados)          |
| Memento         | Guardado / Game Over                      |
