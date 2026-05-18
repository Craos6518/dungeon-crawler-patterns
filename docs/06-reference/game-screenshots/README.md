# Capturas de Pantalla del Juego – Dungeon Crawler

## Estado: ✅ COMPLETO CON ESTILOS VISUALES

Todas las 9 pantallas del juego han sido exportadas en formato **PNG y JPG** con los **estilos CSS completamente aplicados**.

### Pantallas Incluidas

| # | Pantalla | Descripción | Archivo |
|---|----------|-------------|---------|
| 0 | **MENÚ PRINCIPAL** | Interfaz inicial con opciones de nueva partida, cargar, estadísticas y lore | `screen_0.*` |
| 1 | **EXPLORACIÓN** | Vista de exploración de mazmorra con mapa, inventario y acciones | `screen_1.*` |
| 2 | **COMBATE** | Sistema de combate por turnos contra enemigos | `screen_2.*` |
| 3 | **INVENTARIO** | Gestión de items y equipamiento del héroe | `screen_3.*` |
| 4 | **SELECCIÓN DE HÉROE** | Pantalla de selección del personaje jugador | `screen_4.*` |
| 5 | **ESTADÍSTICAS** | Estadísticas generales del juego y progreso | `screen_5.*` |
| 6 | **TESORO** | Recompensas obtenidas al ganar combates | `screen_6.*` |
| 7 | **GUARDADO** | Sistema de guardado/carga de partidas (Memento) | `screen_7.*` |
| 8 | **GAME OVER** | Pantalla final cuando el héroe es derrotado | `screen_8.*` |

### Características Técnicas

- **Resolución**: 1600×1000 píxeles
- **Tema Aplicado**: Fire (Fuego) con colores primarios: naranja-rojo y amarillo dorado
- **Estilos**: CSS 100% inlinado (46 KB de estilos aplicados)
- **JavaScript**: Interactividad inlinada (61 KB de lógica)
- **Formato**: PNG (para máxima calidad) + JPG (para optimización web)
- **Tamaño Total**: ~750 KB (PNG) + ~640 KB (JPG)

### Método de Exportación

1. Lectura de archivos originales:
   - `src/main/resources/ui/game.html` (42 KB)
   - `src/main/resources/ui/styles/game.css` (46 KB)
   - `src/main/resources/ui/scripts/game.js` (61 KB)

2. Inlining de CSS y JavaScript directamente en HTML

3. Generación de 9 variantes HTML (una por screen activo)

4. Captura con Firefox Headless (1600×1000)

5. Conversión PNG→JPG con calidad 85%

### Uso

Las pantallas pueden ser utilizadas en:
- Documentación del proyecto
- Presentaciones
- Manual del usuario
- Portfolio de desarrollo
- Análisis de diseño UI/UX

---

**Generado**: 2025-05-17  
**Proyecto**: Dungeon Crawler Patterns (Universidad Tecnológica de Pereira)  
**Versión**: 1.0 (Completa con 11 patrones de diseño)

