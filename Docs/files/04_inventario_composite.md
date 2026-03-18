# Mockup 04 — Inventario (Patrón Composite)

**Patrón relacionado:** Composite (estructura de objetos), Decorator (efectos en ítems)  
**Estado del juego:** `INVENTARIO`

---

## Vista general

```
inventario — guerrero                               6 / 20 slots usados
```

---

## Árbol de objetos (Composite)

```
┌──────────────────────────────────────────────────────────────────┐
│  estructura de objetos (composite)                               │
│                                                                  │
│  📦 Mochila                          [contenedor]                │
│  │                                                               │
│  ├── ⚔️  Espada de Hierro            [arma]       ← seleccionado │
│  │                                                               │
│  ├── 🧪  Pocion de Vida              [consumible]                │
│  │                                                               │
│  └── 👜  Bolsa                       [contenedor]                │
│          │                                                       │
│          ├── 💎  Gema Rubi           [tesoro]                    │
│          │                                                       │
│          └── 📜  Pergamino           [consumible]                │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## Detalle del objeto seleccionado

```
┌──────────────────────────────────────┐
│  detalle del objeto                  │
│                                      │
│  ⚔️  Espada de Hierro                │
│      arma — una mano                 │
│                                      │
│  daño base       18 pts              │
│  durabilidad     85%                 │
│  peso            2.5 kg              │
│  estado          equipada ✓          │
└──────────────────────────────────────┘
```

---

## Acciones disponibles

```
┌─────────────────────┐  ┌─────────────────────┐
│  equipar            │  │  descartar           │
└─────────────────────┘  └─────────────────────┘
┌─────────────────────┐  ┌─────────────────────┐
│  mover              │  │  inspeccionar        │
└─────────────────────┘  └─────────────────────┘
```

---

## Tipos de ítem

| Icono | Tipo         | Descripción                                    |
|-------|--------------|------------------------------------------------|
| ⚔️    | Arma         | Espada, arco, bastón — modifica stat de ataque |
| 🧪    | Consumible   | Pociones, pergaminos — uso único               |
| 💎    | Tesoro       | Gemas, reliquias — valor en oro                |
| 📦    | Contenedor   | Mochila, bolsa — nodo compuesto del árbol      |

---

## Estructura Composite en código

```
ItemComponent (interface)
    │
    ├── ItemLeaf          → objetos simples (espada, pocion, gema)
    │     └── métodos: getWeight(), getValue(), use()
    │
    └── ItemContainer     → contenedores (mochila, bolsa)
          └── métodos: add(), remove(), getChildren(), getWeight()
```

---

## Ejemplo de árbol en consola

```
Mochila (total: 5.1 kg)
 ├── Espada de Hierro    [arma]       2.5 kg
 ├── Pocion de Vida      [consumible] 0.3 kg
 └── Bolsa (total: 2.3 kg)
     ├── Gema Rubi       [tesoro]     0.1 kg
     └── Pergamino       [consumible] 0.2 kg
```

---

## Notas de implementación

| Elemento             | Patrón / Clase Java                                |
|----------------------|----------------------------------------------------|
| Estructura de árbol  | `ItemComponent`, `ItemLeaf`, `ItemContainer`       |
| Efectos en ítems     | `PoisonedWeaponDecorator`, `EnchantedDecorator`    |
| Creación de objetos  | `ItemFactory` (Factory Method)                     |
| Clonado de ítems     | `ItemPrototype.clone()` (Prototype)                |
