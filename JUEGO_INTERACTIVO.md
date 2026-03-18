# 🎮 Guía del Juego Interactivo

## Inicio Rápido

### Opción 1: Script (Linux/Mac)
```bash
./play.sh
```

### Opción 2: Maven
```bash
mvn exec:java -Dexec.mainClass="game.InteractiveGame"
```

### Opción 3: Desde compilado
```bash
mvn compile
java -cp target/classes game.InteractiveGame
```

---

## 📖 Cómo Jugar

### 1. Menú Principal
- **Nueva Partida**: Comienza una aventura desde cero
- **Cargar Partida**: Continúa desde un guardado previo
- **Ver Estadísticas**: Consulta tus logros acumulados
- **Salir**: Cierra el juego

### 2. Creación del Personaje

**Elige tu héroe:**
- **Guerrero** (HP: 150, Ataque: 25)
  - ✅ Alta resistencia
  - ✅ Bueno para principiantes
  - ❌ Daño moderado

- **Mago** (HP: 100, Ataque: 35)
  - ✅ Alto daño mágico
  - ❌ Frágil, requiere estrategia
  - ✅ Ideal para jugadores experimentados

- **Arquero** (HP: 120, Ataque: 28)
  - ✅ Balanceado
  - ✅ Versátil en combate
  - ✅ Buena opción para cualquier estilo

### 3. Selección de Mazmorra

Cada tema genera enemigos, tesoros y un jefe final único:

- **🔥 Fuego**: Elementales de fuego, salamandras, Dragón de Lava
- **❄️ Hielo**: Gigantes de hielo, yetis, Yeti Ancestral
- **🌑 Oscuridad**: Espectros, sombras, Señor de las Sombras
- **☠️ Veneno**: Arañas, plantas carnívoras, Araña Reina

**Dificultad progresiva:**
- Salas 1-3: Enemigos básicos
- Sala 4: Enemigos más fuertes
- Sala 5: Jefe final (mucho más difícil)

### 4. Exploración

En cada sala puedes:

1. **Avanzar**: Progresa a la siguiente sala
   - 70% de probabilidad de encontrar enemigo
   - 30% de avanzar sin combate

2. **Buscar tesoro**: Busca items valiosos
   - 30% tesoro raro (alto valor)
   - 40% tesoro común (valor medio)
   - 30% no encontrar nada

3. **Abrir inventario**: Ve tus items
   - Durante exploración: Solo consulta
   - Durante combate: Puedes usar pociones

4. **Guardar partida**: Guarda tu progreso
   - Puedes cargar después desde el menú

### 5. Sistema de Combate

**Tu turno:**
- **Atacar**: Inflige daño al enemigo basado en tu ataque
- **Defender**: Reduce el daño del siguiente ataque enemigo (no implementado completamente)
- **Abrir inventario**: Usa una Poción de Vida (+50 HP)

**Turno del enemigo:**
- La IA enemiga elige automáticamente su acción
- Enemigos básicos: Estrategia defensiva (más conservadores)
- Jefes finales: Estrategia agresiva (atacan siempre)

**Efectos de estado:**
- Algunos enemigos pueden tener veneno (pierden HP cada turno)
- Los efectos se aplican automáticamente

### 6. Inventario

Tu mochila puede contener:
- **Pociones de Vida**: Restauran 50 HP
- **Antídotos**: Curan veneno (no implementado en combate)
- **Tesoros**: Gemas, monedas, items raros (valor monetario)

**Estadísticas del inventario:**
- Valor total (en oro)
- Peso total (en kg)
- Capacidad: 20 items máximo

### 7. Victoria y Derrota

**Victoria (completar las 5 salas):**
- Estadísticas finales
- Enemigos derrotados
- HP restante

**Derrota (HP llega a 0):**
- Progreso alcanzado
- Enemigos derrotados antes de caer
- Puedes intentar nuevamente

---

## 🎯 Patrones de Diseño en Acción

Cada decisión que tomas activa uno o más patrones:

| Acción | Patrones Activados |
|--------|-------------------|
| Elegir héroe | **Factory Method** |
| Elegir tema | **Abstract Factory** |
| Explorar mazmorra | **Builder**, **State** |
| Atacar/Defender | **Command** |
| Comportamiento IA | **Strategy** |
| Efectos en combate | **Decorator** |
| Sistema de combate | **Facade** |
| Gestionar inventario | **Composite** |
| Recibir notificaciones | **Observer** |
| Guardar/Cargar | **Memento** |

---

## 💡 Consejos

1. **Guarda frecuentemente**: Especialmente antes de enfrentar jefes
2. **Gestiona tus pociones**: Solo tienes 2 inicialmente
3. **Busca tesoros**: Puede darte items útiles o valiosos
4. **Los magos son de alto riesgo/alta recompensa**: Mucho daño pero poca vida
5. **Los jefes son muy poderosos**: Asegúrate de tener suficiente HP

---

## 🐛 Notas de Implementación

Algunas funcionalidades están simplificadas para enfocarse en los patrones:

- ✅ **Completamente funcional**: Combate, inventario, guardado/carga, exploración
- ⚠️ **Parcialmente implementado**: Uso de items durante combate (solo pociones)
- ⚠️ **Simplificado**: Defensa reduce daño (no animado visualmente)
- ⚠️ **Limitado**: Carga de partida restaura datos pero requiere nueva partida para continuar

El objetivo es **demostrar los patrones en un contexto jugable**, no crear un juego comercial completo.

---

## 📊 Estadísticas

El sistema **Observer** recopila automáticamente:
- Ataques totales realizados
- Daño total causado y recibido
- Combates ganados
- Comandos ejecutados

Accede desde el menú principal → "Ver Estadísticas"

---

## 🎓 Valor Académico

Este juego interactivo **demuestra la aplicación práctica** de:

1. **Todos los 10 patrones trabajando juntos** en una aplicación funcional
2. **Arquitectura limpia** donde cada patrón tiene responsabilidad clara
3. **Código mantenible** que permite agregar nuevas características fácilmente
4. **Casos de uso reales** de cada patrón en contexto de videojuego

Ideal para:
- Presentaciones académicas con demo en vivo
- Experimentación con los patrones
- Entender cómo los patrones colaboran entre sí

---

**¡Disfruta tu aventura y que tengas suerte en las mazmorras! 🏆**
