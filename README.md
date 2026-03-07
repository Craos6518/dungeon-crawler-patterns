# Dungeon Crawler Académico — Patrones de Diseño en Java

Proyecto académico desarrollado en **Java 17** para demostrar la aplicación correcta de **patrones de diseño de software** dentro de un videojuego simple tipo **Dungeon Crawler por turnos** ejecutado en consola.

El objetivo principal **no es crear un juego complejo**, sino **diseñar una arquitectura limpia, mantenible y defendible académicamente**.

---

## 🎯 Características Principales

✅ **10 patrones de diseño** implementados y completamente integrados  
✅ **107 tests unitarios** que validan cada patrón  
✅ **Tests de integración** que demuestran colaboración entre patrones  
✅ **Arquitectura limpia** con separación clara de responsabilidades  
✅ **Sistema de combate completo** que conecta todos los patrones  

---

# Objetivo del Proyecto

Demostrar el uso correcto de patrones de diseño en un sistema realista mediante:

- Separación clara de responsabilidades
- Dominio bien modelado
- Uso correcto de abstracciones
- Código testeable
- Arquitectura extensible

El juego se ejecuta completamente en **consola** para reducir complejidad gráfica y concentrar el esfuerzo en la **arquitectura del software**.

---

# Tecnologías Utilizadas

- **Java 17 (OpenJDK)**
- **Maven 3.6+**
- **JUnit 5** (testing)
- **VSCode** (editor recomendado)
- **Git / GitHub** (control de versiones)

**Nota sobre diagramas:** Los diagramas UML del proyecto están disponibles en la carpeta `../Docs/Diagramas/` en formato imagen (PNG) y código fuente PlantUML (TXT). PlantUML no es necesario para compilar o ejecutar el proyecto.

Sistema desarrollado en:

```
Fedora 43 KDE
OpenJDK 17
VSCode
```

---

# Requisitos y Configuración

## Requisitos Previos

- **Java 17 o superior** (OpenJDK o Temurin recomendado)
- **Maven 3.6+**
- Editor de código (VSCode recomendado)

## Configuración de Java 17

### Verificar instalación de Java

```bash
java -version
```

Debe mostrar versión 17 o superior:
```
openjdk version "17.0.x" ...
```

### Configurar JAVA_HOME (Linux/macOS)

Si Maven reporta "JAVA_HOME not set", configura la variable de entorno:

1. **Encontrar la ubicación de Java 17:**
   ```bash
   ls /usr/lib/jvm/
   ```

2. **Exportar JAVA_HOME temporalmente:**
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
   ```

3. **Hacer permanente (opcional)** agregando a `~/.bashrc` o `~/.zshrc`:
   ```bash
   echo 'export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk' >> ~/.bashrc
   source ~/.bashrc
   ```

### Configurar JAVA_HOME (Windows)

1. Panel de Control → Sistema → Configuración avanzada del sistema
2. Variables de entorno → Nueva variable del sistema
3. Nombre: `JAVA_HOME`
4. Valor: Ruta a JDK 17 (ej: `C:\Program Files\Java\jdk-17`)

### Verificar configuración

```bash
echo $JAVA_HOME          # Linux/macOS
echo %JAVA_HOME%         # Windows
mvn -version             # Debe mostrar Java 17
```

## Instalación del Proyecto

```bash
# Clonar repositorio
git clone [URL_DEL_REPO]
cd dungeon-crawler-patterns

# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ver cobertura (opcional)
mvn test jacoco:report
```

Para más detalles sobre configuración en VSCode, ver [CONFIGURACION_VSCODE.md](CONFIGURACION_VSCODE.md).

---

# Patrones de Diseño Implementados

El proyecto implementa múltiples patrones clásicos del libro **Gang of Four**.

## Patrones Creacionales

### Factory Method

Responsable de crear personajes del juego.

Ejemplo:

```
HeroFactory
ConcreteHeroFactory
```

Permite desacoplar la lógica de creación de personajes.

---

### Builder

Generación paso a paso de la mazmorra.

```
DungeonBuilder
ConcreteDungeonBuilder
```

Permite separar la **construcción** de la **representación final**.

---

### Abstract Factory

Creación de familias de objetos coherentes según el **tema de la mazmorra**.

Ejemplo:

```
DungeonThemeFactory
FireThemeFactory
PoisonThemeFactory
```

Cada fábrica crea:

- enemigos
- tesoros
- elementos temáticos

---

## Patrones Estructurales

### Composite

Sistema jerárquico de inventario.

```
Item
 ├─ SimpleItem
 └─ ContainerItem
```

Permite tratar objetos simples y contenedores de forma uniforme.

---

### Decorator

Sistema de **efectos de estado** aplicados a personajes.

Ejemplos:

```
PoisonEffect
BurnEffect
StunEffect
```

Los efectos modifican dinámicamente el comportamiento del personaje.

---

### Facade

El sistema de combate se simplifica mediante una fachada.

```
CombatFacade
```

Oculta la complejidad del motor de combate.

---

## Patrones de Comportamiento

### Command

Encapsula cada acción del juego como un objeto ejecutable.

```
Command (interface)
 ├─ AttackCommand
 ├─ DefendCommand
 ├─ UseItemCommand
 ├─ SkillCommand
 └─ CommandInvoker (historial)
```

**Beneficios:**
- Desacopla emisor de receptor
- Permite historial de acciones (undo potencial)
- Facilita logging y replay
- Testeable individualmente

**Implementación:**
- `Command.java` - Interfaz base
- `CommandInvoker.java` - Gestor de historial
- 5 comandos concretos

---

### Strategy

Define algoritmos de comportamiento de IA intercambiables.

```
AIStrategy (interface)
 ├─ AggressiveStrategy    // Ataca al más fuerte
 ├─ DefensiveStrategy     // Defiende cuando vida < 30%
 ├─ IntelligentStrategy   // Elimina débiles primero
 ├─ RandomStrategy        // Comportamiento aleatorio
 └─ AIController          // Contexto que usa estrategias
```

**Beneficios:**
- Comportamientos intercambiables en runtime
- Enemigos pueden cambiar estrategia dinámicamente
- Extensible (fácil agregar nuevas estrategias)
- Sin condicionales complejos (if/else chains)

**Implementación:**
- `AIStrategy.java` - Interfaz base
- `AIController.java` - Contexto
- 4 estrategias concretas

---

### Observer

Sistema de eventos desacoplado para notificaciones del juego.

```
EventManager (Singleton)
 ├─ GameEvent / EventType
 └─ Observers:
     ├─ CombatLogger         // Logs de combate
     ├─ StatisticsTracker    // Métricas del juego
     └─ UINotifier           // Notificaciones UI
```

**Beneficios:**
- Comunicación desacoplada entre componentes
- Múltiples observers sin duplicar código
- Soporta suscripción por tipo de evento
- Historial de eventos mantenido

**Implementación:**
- `EventManager.java` (Singleton)
- `GameEvent.java` / `EventType.java` (enum)
- 3 observers concretos
- Sistema habilitado/deshabilitado en runtime

---

### State

Modela los diferentes estados del juego y sus transiciones.

```
GameState (interface)
 ├─ MenuState
 ├─ ExplorationState
 ├─ CombatState
 ├─ InventoryState
 ├─ GameOverState
 └─ GameStateContext    // Mantiene estado actual
```

**Beneficios:**
- Encapsula comportamiento específico de cada estado
- Transiciones explícitas y controladas
- Sin condicionales complejos basados en flags
- Cada estado es testeable individualmente

**Implementación:**
- `GameState.java` - Interfaz base
- `GameStateContext.java` - Contexto que mantiene estado
- 5 estados concretos

---

### Memento

Permite guardar y restaurar el estado completo del juego.

```
GameMemento (immutable)
 ├─ GameOriginator      // Objeto principal del juego
 └─ GameCaretaker       // Gestor de mementos
     ├─ Memoria (historial de checkpoints)
     └─ Disco (serialización)
```

**Beneficios:**
- Encapsula estado interno sin exponerlo
- Soporte save/load (memoria y disco)
- Historial de checkpoints
- Immutable (using Builder pattern)

**Implementación:**
- `GameMemento.java` - Memento inmutable con Builder
- `GameOriginator.java` - Objeto cuyo estado se guarda
- `GameCaretaker.java` - Gestor con persistencia en disco
- Serializable para guardado permanente

Evita condicionales complejos y centraliza el comportamiento de cada estado.

---

## 🔗 Integración de Patrones

El proyecto no solo implementa patrones individuales, sino que demuestra cómo **múltiples patrones colaboran** para crear un sistema cohesivo.

### IntegratedCombatEngine: Hub de Integración

Esta clase es el **corazón de la integración**, conectando 5 patrones simultáneamente:

```java
IntegratedCombatEngine motor = new IntegratedCombatEngine(
    heroe,              // Del Factory Method
    enemigo,            // Del Abstract Factory
    new AggressiveStrategy()  // Patrón Strategy
);
```

**Patrones integrados en el combate:**

1. **Command**: Cada acción (ataque, defensa, uso de item) es un comando ejecutable con historial
2. **Strategy**: El enemigo usa IA intercambiable que puede cambiar durante el combate
3. **Observer**: Todos los eventos se notifican automáticamente a múltiples listeners
4. **Decorator**: Los efectos de estado se aplican automáticamente cada turno
5. **Facade**: La complejidad del motor está oculta tras una API simple

**Flujo de integración:**

```
Creacionales (Factory, Builder, Abstract Factory)
         ↓
    Personajes + Mazmorra + Items
         ↓
Estructurales (Composite, Decorator)
         ↓
    Inventario + Efectos aplicados
         ↓
Comportamiento (Command, Strategy, Observer)
         ↓
    Combate ejecutado con eventos
         ↓
Persistencia (Memento)
         ↓
    Estado guardado/restaurado
```

Ver documentación completa: [INTEGRACION_PATRONES.md](INTEGRACION_PATRONES.md)

---

### Memento

Permite guardar y restaurar el estado del juego.

```
GameMemento
GameCaretaker
```

Se utiliza para implementar el sistema de guardado de partidas.

---

# Arquitectura del Proyecto

El proyecto se organiza en capas claras:

```
game
 ├─ Main.java
 ├─ domain/
 │   └─ personaje/
 │      ├─ Personaje.java
 │      ├─ Guerrero.java
 │      └─ EnemigoBasico.java
 ├─ combat/
 │   ├─ engine/
 │   │   └─ MotorCombate.java
 │   └─ model/
 │       └─ ResultadoAtaque.java
 ├─ ai/strategy/
 ├─ items/inventory/
 ├─ dungeon/builder/
 ├─ effects/status/
 ├─ command/actions/
 ├─ state/game/
 ├─ persistence/memento/
 └─ ui/console/
```

Esto evita dependencias cruzadas innecesarias.

---

# Ejecución del Proyecto

## Demo Básica (Patrones Creacionales)

Compilar:

```bash
cd dungeon-crawler-patterns
mvn compile
```

Ejecutar demo básica:

```bash
mvn exec:java -Dexec.mainClass="game.Main"
```

## 🎮 Demo INTEGRADA (Todos los Patrones)

**¡RECOMENDADO!** Esta demo muestra cómo los 10 patrones trabajan juntos en un sistema cohesivo:

```bash
mvn exec:java -Dexec.mainClass="game.demo.IntegracionCompletaDemo"
```

O usando el Main con parámetro:

```bash
mvn exec:java -Dexec.mainClass="game.Main" -Dexec.args="--integracion"
```

### Qué verás en la demo integrada:

1. **FASE 1: PREPARACIÓN**
   - ⚔️ Factory Method creando personajes
   - 🎒 Composite construyendo inventario jerárquico
   - 🏰 Builder generando mazmorras temáticas
   - 🎯 Abstract Factory creando enemigos coherentes
   - ✨ Decorator aplicando efectos de estado

2. **FASE 2: COMBATE INTEGRADO**
   - 📡 Observer notificando eventos en tiempo real
   - ⚔️ Command encapsulando cada acción
   - 🧠 Strategy decidiendo comportamiento de IA
   - 🔄 Decorator aplicando efectos automáticamente
   - 🏆 Facade simplificando la interfaz

3. **FASE 3: POST-COMBATE**
   - 📊 Estadísticas acumuladas por observers
   - 💾 Memento guardando/restaurando estado

Ver documentación completa en: [INTEGRACION_PATRONES.md](INTEGRACION_PATRONES.md)

---

# Testing y Calidad

El proyecto cuenta con **107 tests** que validan todos los patrones implementados.

## Tests Unitarios (51)

### Patrones Creacionales (3 suites)
- **AbstractFactoryTest** - Factorías temáticas de dungeons
- **BuilderPatternTest** - Construcción de mazmorras
- **FactoryMethodTest** - Creación de personajes

### Patrones Estructurales (3 suites)
- **CompositePatternTest** - Inventario jerárquico de items
- **DecoratorPatternTest** - Efectos de estado temporales
- **FacadePatternTest** - Interfaz simplificada del sistema de combate

### Patrones de Comportamiento (4 suites)
- **CommandPatternTest** (11 tests) - Encapsulación de acciones
- **StrategyPatternTest** (11 tests) - Comportamientos de IA
- **ObserverPatternTest** (13 tests) - Sistema de eventos
- **MementoPatternTest** (14 tests) - Guardado/restauración de estado

### Tests de Dominio y Combate (2 suites)
- **CharacterDamageTest** - Mecánicas de daño
- **CombatTurnAlternationTest** / **CombatEndTest** - Flujo de combate

## Tests de Integración (2)

- **CombatIntegrationTest** - Combate completo con múltiples patrones
- **BehavioralPatternsIntegrationTest** (6 tests) - Interacción entre Command, Strategy, Observer y Memento

## Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests de comportamiento
mvn test -Dtest="game.unit.behavioral.*Test"

# Test específico
mvn test -Dtest="CommandPatternTest"
```

Ver documentación completa en:
- [RESUMEN_TESTS_BEHAVIORAL.md](RESUMEN_TESTS_BEHAVIORAL.md)
- [README_TESTS.md](README_TESTS.md)

---

# Filosofía del Proyecto

Este proyecto sigue un principio claro:

> **Un sistema pequeño, pero arquitectónicamente correcto, es superior a un sistema grande pero mal diseñado.**

Se prioriza:

- claridad
- separación de responsabilidades
- extensibilidad
- pruebas unitarias

sobre la complejidad del juego.

---

# Autor

Proyecto académico desarrollado por:

**Andrés**

Curso: Patrones de Diseño de Software
