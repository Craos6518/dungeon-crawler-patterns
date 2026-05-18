# Diagramas y Exportaciones - Resumen

**Fecha**: 17 de mayo de 2026  
**Estado**: Completo

## Archivos Generados

### 1. Diagrama de Estados del Sistema (General)

- **Ubicación**: [docs/02-architecture/diagramas/DIAGRAMA DE ESTADOS – Sistema General.txt](../02-architecture/diagramas/DIAGRAMA%20DE%20ESTADOS%20%E2%80%93%20Sistema%20General.txt)
- **Formato**: Mermaid (stateDiagram)
- **Imagen PNG**: [docs/02-architecture/diagramas/DIAGRAMA DE ESTADOS – Sistema General.png](../02-architecture/diagramas/DIAGRAMA%20DE%20ESTADOS%20%E2%80%93%20Sistema%20General.png)
- **Descripción**: Flujo completo del juego desde perspectiva del jugador, mostrando 8 estados principales:
  - Menu → Selección de Héroe/Slots → Exploración → Combate/Tesoro/Inventario → Fin del Juego

**Estados**:
| Estado | Descripción | Transiciones |
|--------|-------------|--------------|
| Menu | Punto de entrada | → Heroe, → Slots |
| Selección Héroe | Config inicial (clase, tema) | → Exploración |
| Selección Slots | Carga partidas persistidas | → Exploración |
| Exploración | Hub central del loop | → Combate, → Inventario, → GameOver |
| Combate | Motor de turnos | → Tesoro, → GameOver, → Exploración |
| Tesoro | Recompensas post-combate | → Exploración |
| Inventario | Gestión de items | → Exploración |
| Fin del Juego | Condición terminal | → Menu |

---

### 2. Diagrama UML de Casos de Uso (5 en total)

- **Ubicación**: [docs/02-architecture/diagramas/DIAGRAMA UML – Casos de Uso.txt](../02-architecture/diagramas/DIAGRAMA%20UML%20%E2%80%93%20Casos%20de%20Uso.txt)
- **Formato**: Mermaid (graph TB)
- **Imagen PNG**: [docs/02-architecture/diagramas/DIAGRAMA UML – Casos de Uso.png](../02-architecture/diagramas/DIAGRAMA%20UML%20%E2%80%93%20Casos%20de%20Uso.png)
- **Descripción**: Diagrama de interacción entre actores, sistema y casos de uso

**Casos de Uso**:

#### UC-01: Iniciar Partida

- **Actores**: Jugador, GameRuntime
- **Flujo**: Selecciona clase héroe → selecciona tema campaña → crea sesión
- **Patrones**: Factory Method, Abstract Factory, Memento

#### UC-02: Explorar Mazmorra

- **Actores**: Jugador, Sistema
- **Flujo**: Avanza salas → resuelve contenido procedural → progresión campaña
- **Patrones**: Builder, Observer, Command

#### UC-03: Resolver Combate

- **Actores**: Jugador, Sistema
- **Flujo**: Ejecuta acciones → motor procesa estrategia → resultado
- **Patrones**: Strategy, Decorator, Command, Observer

#### UC-04: Gestionar Inventario

- **Actores**: Jugador
- **Flujo**: Abre inventario → navega estructura → usa/vende items
- **Patrones**: Composite, Facade, Memento

#### UC-05: Guardar y Cargar Partida

- **Actores**: Jugador, RuntimeSaveSlotManager
- **Flujo**: Guarda/carga sesión → persiste snapshots → restaura estado
- **Patrones**: Memento, Facade

---

### 3. Interfaz Exportada en JPG

- **Ubicación**: [docs/06-reference/INTERFAZ_DUNGEON_CRAWLER.jpg](./INTERFAZ_DUNGEON_CRAWLER.jpg)
- **Formato**: JPG (calidad 90%) - **126 KB**
- **Resolución**: 1400 × 1000 píxeles
- **Contenido**: Captura completa de la presentación web interactiva que incluye:
  - Encabezado del proyecto
  - Stack tecnológico (Java 17, Maven, Mermaid)
  - Métricas (11 patrones, 221 tests, 160 clases)
  - Arquitectura de capas (UI, Application, Domain, Persistence)
  - Patrones implementados agrupados por tipo
  - Referencias de integración

#### Alternativa PNG:

- **Ubicación**: [docs/06-reference/INTERFAZ_DUNGEON_CRAWLER.png](./INTERFAZ_DUNGEON_CRAWLER.png)
- **Formato**: PNG - **364 KB**
- **Contenido**: Idéntico a JPG (formato sin pérdida para mayor fidelidad)

---

## Resumen de Contenido

### Diagrama de Estados (Sistema General)

```
[*] ---> Menu
         ├---> Selección Héroe -----> Exploración
         └---> Selección Slots -----> Exploración

Exploración (Hub Central)
├---> Combate ----> Tesoro ----> Exploración
│      └----> GameOver
├---> Inventario ----> Exploración
└---> GameOver -------> Menu
```

### Diagrama de Casos de Uso (5 Total)

```
Jugador
   ├---> UC-01: Iniciar Partida (Factory, Abstract Factory, Memento)
   ├---> UC-02: Explorar Mazmorra (Builder, Observer, Command)
   ├---> UC-03: Resolver Combate (Strategy, Decorator, Command, Observer)
   ├---> UC-04: Gestionar Inventario (Composite, Facade, Memento)
   └---> UC-05: Guardar/Cargar (Memento, Facade)
```

---

## Navegación

| Elemento            | Ubicación                                                                                                                             |
| ------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| Estados del Sistema | [DIAGRAMA DE ESTADOS – Sistema General.txt](../02-architecture/diagramas/DIAGRAMA%20DE%20ESTADOS%20%E2%80%93%20Sistema%20General.txt) |
| Casos de Uso        | [DIAGRAMA UML – Casos de Uso.txt](../02-architecture/diagramas/DIAGRAMA%20UML%20%E2%80%93%20Casos%20de%20Uso.txt)                     |
| Interfaz JPG        | [INTERFAZ_DUNGEON_CRAWLER.jpg](./INTERFAZ_DUNGEON_CRAWLER.jpg)                                                                        |
| Interfaz PNG        | [INTERFAZ_DUNGEON_CRAWLER.png](./INTERFAZ_DUNGEON_CRAWLER.png)                                                                        |
| Especificación      | [Especificacion_Requerimientos_Sistema_ISO29148.md](../01-product/Especificacion_Requerimientos_Sistema_ISO29148.md)                  |
| GDD Canónico        | [GDD_CANONICO.md](../01-product/GDD_CANONICO.md)                                                                                      |

---

## Notas Técnicas

- **Diagramas Mermaid**: Renderizables en GitHub, Confluence, Markdown y navegadores modernos
- **Formato JPG**: Optimizado para presentaciones y documentos (126 KB)
- **Formato PNG**: Ideal para documentación técnica sin compresión (364 KB)
- **Exportación**: Realizada via Firefox headless (1400×1000 píxeles)
- **Conversión**: ImageMagick v7 con configuración de calidad 90%

---

Generado automáticamente el **17 de mayo de 2026** como parte de la auditoria técnica y visual del proyecto Dungeon Crawler Patterns.
