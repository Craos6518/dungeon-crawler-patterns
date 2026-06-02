# 🎭 Eranthia: Dungeon Crawler Patterns - Presentación Académica

Presentación web interactiva de alta fidelidad para la defensa del proyecto **Eranthia: Dungeon Crawler Patterns** en la Universidad Tecnológica de Pereira.

## 📋 Contenido

Una presentación completa con **23 slides** que cubren:

1. **Portada** - Introducción y contexto del proyecto
2. **Problema** - Limitaciones de aprender patrones de forma aislada
3. **Solución** - Visión general de Eranthia
4. **Flujo del Juego** - Diagrama completo de la experiencia del usuario
5. **Historias de Usuario** - 5 HU principales
6. **Requerimientos Funcionales** - RF-01 a RF-05
7. **Requerimientos No Funcionales** - RNF-01 a RNF-05
8. **Casos de Uso** - Actor principal y casos de uso
9. **Arquitectura General** - Capas de arquitectura limpia
10. **Patrones de Diseño** - Resumen de 11 patrones implementados
11. **Pattern: State** - GameStateContext y transiciones
12. **Pattern: Strategy** - AIStrategy intercambiable
13. **Pattern: Command** - Encapsulación de acciones
14. **Pattern: Composite** - Estructura jerárquica de inventario
15. **Pattern: Decorator** - Efectos dinámicos
16. **Pattern: Memento** - Guardado y restauración
17. **Pattern: Observer** - Sistema de eventos
18. **Principios SOLID** - S, O, L, I, D
19. **Pilares POO** - Encapsulamiento, Herencia, Polimorfismo, Abstracción
20. **Evidencia Técnica** - Métricas de calidad (221 tests, 100% éxito)
21. **Demostración** - Demos en vivo
22. **Conclusiones** - Resumen de logros
23. **Preguntas** - Cierre elegante

## 🚀 Uso

### Opción 1: Abrir directamente en navegador

```bash
# En Linux/macOS
open eranthia-presentation.html

# O en cualquier navegador
firefox eranthia-presentation.html
google-chrome eranthia-presentation.html
```

### Opción 2: Servir con un servidor local

```bash
# Con Python 3
python -m http.server 8000

# Con Node.js (http-server)
npx http-server

# Con Live Server en VS Code
# Instalar extensión: "Live Server"
# Click derecho en archivo → "Open with Live Server"
```

### Opción 3: Modo presentación full-screen

1. Abrir en navegador
2. Presionar `F11` para pantalla completa
3. Presionar `ESC` para salir

## ⌨️ Controles de Navegación

| Acción          | Atajo                      |
| --------------- | -------------------------- |
| Siguiente slide | `→` o `Espacio`            |
| Slide anterior  | `←`                        |
| Primera slide   | `Inicio`                   |
| Última slide    | `Fin`                      |
| Indicadores     | Click en puntos inferiores |

Navegación visual disponible en la parte inferior de la pantalla.

## 🎨 Características Visuales

### Diseño

- **Estética RPG Premium**: Inspirado en Diablo IV, Darkest Dungeon, Baldur's Gate 3
- **Paleta de colores**: Dorados sutiles, azul acero, púrpura mágico, fondos oscuros elegantes
- **Tipografía**: Cinzel (títulos), EB Garamond (cuerpo)
- **Textura**: Ruido fractal sutil para profundidad

### Animaciones

- **Transiciones suaves**: Cubic-bezier para movimiento elegante
- **Fade In**: Elementos aparecen gradualmente
- **Slide In**: Contenido entra desde los lados
- **Contadores**: Métricas se animan al llegar al slide
- **Hover Effects**: Interactividad visual en cards
- **60 FPS**: Animaciones optimizadas

### Variantes de Slides

- **Dark**: Fondo oscuro elegante (predeterminado)
- **Purple Accent**: Tonos púrpura para magia/patrones
- **Blue Accent**: Acero azul para arquitectura

## 📱 Responsive Design

La presentación se adapta a diferentes tamaños de pantalla:

| Dispositivo | Ancho   | Optimización |
| ----------- | ------- | ------------ |
| Desktop     | 1920px+ | Óptimo       |
| Laptop      | 1366px+ | Óptimo       |
| Tablet      | 768px+  | Adaptado     |
| Móvil       | < 768px | Reducido     |

**Nota**: Se recomienda usar en desktop o laptop para experiencia óptima (16:9 o superior).

## 🛠️ Requisitos Técnicos

### Navegadores Soportados

- Chrome/Chromium 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### Recursos Necesarios

- Conexión a internet (para cargar fuentes de Google)
- JavaScript habilitado
- 50MB de espacio en memoria

### Sin Dependencias Externas

✅ HTML5 puro
✅ CSS3 vanilla
✅ JavaScript vanilla (sin frameworks)
✅ Sin librerías externas

## 📦 Estructura del Proyecto

```
presentation/
├── eranthia-presentation.html    # Presentación completa (única página)
├── README.md                      # Este archivo
└── index.html                     # Alias para compatibilidad
```

**Nota**: La presentación está contenida en un único archivo HTML con CSS incrustado y JavaScript vanilla. No requiere construcción ni dependencias.

## 🎯 Recomendaciones de Uso

### Para la Presentación

1. **Antes de la presentación**:
   - Probar en el navegador a usar
   - Verificar conexión (para fuentes de Google)
   - Practicar con el tiempo

2. **Durante la presentación**:
   - Usar pantalla completa (F11)
   - Mantener ritmo de 15 minutos
   - Permitir 2-3 minutos por slide promedio
   - Usar notas personales para contexto

3. **Conexión remota**:
   - Servir localmente y compartir pantalla
   - O cargar en servidor web

### Optimización

- **Caché**: Primera carga descarga fuentes → carga rápido después
- **Memoria**: Bajo consumo de RAM (~50MB)
- **CPU**: Animaciones optimizadas 60 FPS

## 🔧 Personalización

Para modificar la presentación, editar directamente en `eranthia-presentation.html`:

### Cambiar colores

```css
:root {
  --gold: #c9a84c; /* Color primario */
  --dark: #6c5ce7; /* Acentos púrpura */
  --stone: #0e0c0a; /* Fondo principal */
  /* ... más variables */
}
```

### Agregar/Editar slides

```html
<!-- SLIDE N: TITULO -->
<div class="slide dark">
  <div class="slide-content">
    <h1 class="slide-title">Título</h1>
    <!-- Contenido -->
  </div>
</div>
```

### Modificar animaciones

```css
.slide {
  transition: all 0.7s cubic-bezier(0.34, 1.56, 0.64, 1);
  /* Ajustar duración y curva */
}
```

## 📊 Estadísticas del Proyecto

- **Slides**: 23
- **Líneas de HTML**: ~1600
- **Líneas de CSS**: ~900
- **Líneas de JavaScript**: ~100
- **Tamaño Total**: ~120KB
- **Tiempo de Carga**: < 2s
- **Animaciones**: 15+

## 🎓 Contexto Académico

**Curso**: Patrones de Diseño de Software
**Universidad**: Universidad Tecnológica de Pereira
**Defensa**: Proyecto Eranthia Dungeon Crawler Patterns
**Duración**: 15 minutos máximo

## 📝 Contenido Técnico Cubierto

✅ 11 Patrones de Diseño GoF
✅ Principios SOLID
✅ Programación Orientada a Objetos
✅ Arquitectura Limpia
✅ 221 Pruebas Automatizadas
✅ Java 17 + JUnit 5
✅ Videojuego RPG funcional

## 🎬 Consejos de Presentación

1. **Ritmo**: ~40 segundos por slide (21 min total recomendado)
2. **Énfasis**: Dar más tiempo a slides de patrones (11-17)
3. **Demo**: Intercalar con demostraciones del juego
4. **Interacción**: Invitar preguntas entre secciones

## 📞 Contacto

**Autor**: Andrés Felipe Martínez Henao
**Institución**: Universidad Tecnológica de Pereira
**Proyecto**: Eranthia: Dungeon Crawler Patterns

---

**Última actualización**: Junio 2026
**Estado**: ✅ Producción
**Versión**: 1.0
