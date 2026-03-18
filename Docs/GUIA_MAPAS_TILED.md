# Guia de Mapas en Tiled para Dungeon Crawler (Java)

Esta guia consolida la configuracion recomendada para crear mapas con tiles de 32x32 y prepararlos para lectura simple desde Java.

---

## 1. Configuracion correcta para tu proyecto

Con assets de **32x32**, usa:

### Mapa
- Orientacion: **Orthogonal**
- Formato de capa: **CSV**
- Orden de pintado: **Right Down** (Derecha Abajo)

### Tamano del patron (Tile Size)
- Ancho: **32 px**
- Alto: **32 px**

---

## 2. Tamano del mapa

Si usas:

```txt
30 x 20 tiles
```

equivale a:

```txt
960 x 640 px
```

Esto esta bien para una sala pequena.

### Recomendacion por tipo

| Tipo de mapa | Tamano |
|---|---|
| Sala pequena | 30x20 |
| Sala media | 40x30 |
| Mapa completo | 80x60 |

Para este proyecto academico:

**30x20 es perfecto.**

---

## 3. No uses Infinito

Usa mapa **Fijo**.

Mapas infinitos generan JSON mas complejo y no es necesario para este juego.

---

## 4. Capas que debes crear

Crea estas capas:

| Capa logica | Tipo en Tiled | Por que |
|---|---|---|
| Background | **Capa de Imagen** | fondo grande, no tiles |
| Floor | **Capa de Patrones** | grid de tiles caminables |
| Walls | **Capa de Patrones** | tiles con colision |
| Props | **Capa de Patrones** | decoracion hecha con tiles |
| Enemies | **Capa de Objetos** | puntos de spawn con coordenadas |

Arquitectura recomendada:

```txt
Background -> imagen decorativa
Floor      -> suelo caminable
Walls      -> colisiones
Props      -> props/decoracion
Enemies    -> spawn
```

Esto facilita la lectura desde Java.

---

## 5. Como usar los backgrounds del pack de cavernas

No los pongas como tiles.

Haz:

```txt
Add Layer -> Image Layer
```

Luego selecciona uno de:

```txt
background_1.png
background_2.png
background_3.png
background_4.png
background_5.png
```

Cada sala puede usar un fondo distinto.

---

## 6. Como importar el tileset

Menu:

```txt
Map -> New Tileset
```

Configura:

```txt
Tile width: 32
Tile height: 32
Spacing: 0
Margin: 0
```

Luego selecciona el PNG del pack.

---

## 7. Exportacion (clave para Java)

Guarda el mapa como:

```txt
.tmj
```

o

```txt
.json
```

Para tu motor, JSON suele ser mejor.

Ejemplo de capa en JSON:

```json
{
  "name": "Floor",
  "data": [1, 1, 1, 1, 1, 1, 1]
}
```

El juego solo necesita leer el array.

---

## 8. Renderizado basico en Java

```java
for (int y = 0; y < height; y++) {
    for (int x = 0; x < width; x++) {
        int tileID = map[y][x];
        drawTile(tileID, x * 32, y * 32);
    }
}
```

---

## 9. Error comun a evitar

No pongas todo en una sola capa.

Eso rompe:
- colisiones
- spawn
- logica

Siempre separa capas por responsabilidad.

---

## 10. Recomendacion importante para tu dungeon crawler

Crea **10 a 15 salas** distintas en Tiled.

Luego el `DungeonBuilder` conecta salas pre-hechas. Asi logras sensacion procedural sin complejizar de mas el proyecto.

---

## Tipos de capa correctos en Tiled

Si eliges mal el tipo de capa, luego tu motor no podra interpretar bien el mapa.

| Capa logica | Tipo en Tiled | Por que |
|---|---|---|
| Background | **Capa de Imagen** | fondo grande, no tiles |
| Floor | **Capa de Patrones** | grid de tiles |
| Walls | **Capa de Patrones** | colisiones |
| Props | **Capa de Patrones** | decoracion |
| Enemies | **Capa de Objetos** | spawns y coordenadas |

### 1) Background -> Capa de Imagen

Uso:
- fondos grandes
- parallax
- cavernas

Ejemplo:

```txt
cave_background_01.png
```

### 2) Floor -> Capa de Patrones

Uso:
- suelo
- caminos
- agua
- lava

### 3) Walls -> Capa de Patrones

Uso:
- paredes
- rocas
- obstaculos

Tu motor usa esta capa para colisiones.

### 4) Props -> Capa de Patrones

Uso:
- rocas
- hongos
- antorchas
- estalactitas

No deben afectar gameplay principal.

### 5) Enemies -> Capa de Objetos

Uso:
- spawns
- triggers
- puntos especiales

Ejemplos de objetos:

```txt
slime_spawn
bat_spawn
boss_spawn
```

Cada objeto tiene:

```txt
x
y
type
```

Tu motor lo usa para instanciar enemigos.

---

## Estructura final recomendada

```txt
Background   (Image Layer)

Floor        (Tile Layer)
Walls        (Tile Layer)
Props        (Tile Layer)

Enemies      (Object Layer)
```

Orden visual:

```txt
Background
Floor
Walls
Props
Enemies
```

---

## Recomendacion adicional (opcional pero muy util)

Agrega tambien:

```txt
Items   (Object Layer)
Doors   (Object Layer)
```

Esto ayuda a:
- generar loot procedural
- conectar salas de la mazmorra
