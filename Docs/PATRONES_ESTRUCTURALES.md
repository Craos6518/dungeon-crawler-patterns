# Patrones Estructurales Implementados

Este documento describe la implementación de los patrones estructurales en el proyecto Dungeon Crawler.

---

## 1. Patrón Composite - Sistema de Inventario

### Propósito
Permite tratar objetos individuales y composiciones de objetos de manera uniforme, creando estructuras jerárquicas tipo árbol.

### Implementación

#### Estructura de Clases

```
ItemComponent (abstracto)
├── SimpleItem (hoja)
└── ContainerItem (compuesto)
```

#### Componente Base: ItemComponent

**Ubicación**: `game.items.model.ItemComponent`

Define la interfaz común para todos los items:
- `getValorTotal()` - Calcula el valor total
- `getPesoTotal()` - Calcula el peso total
- `mostrarDetalle()` - Muestra información del item
- `agregar(ItemComponent)` - Agrega un item (solo para contenedores)
- `remover(ItemComponent)` - Remueve un item (solo para contenedores)

#### Hoja: SimpleItem

**Ubicación**: `game.items.model.SimpleItem`

Representa un item individual que **no puede contener** otros items.

Ejemplos:
- Espadas
- Pociones
- Monedas
- Armaduras

```java
SimpleItem espada = new SimpleItem("Espada de Hierro", "Arma básica", "Arma", 100, 5);
// Valor: 100
// Peso: 5
```

#### Compuesto: ContainerItem

**Ubicación**: `game.items.model.ContainerItem`

Representa un contenedor que **puede almacenar** otros items (simples o contenedores).

Ejemplos:
- Mochilas
- Cofres
- Bolsas

```java
ContainerItem mochila = new ContainerItem("Mochila", "Mochila de aventurero", 10, 2);
mochila.agregar(espada);
mochila.agregar(pocion);

// Valor total = suma de valores de contenido
// Peso total = peso propio + suma de pesos de contenido
```

#### Características Especiales

1. **Capacidad Máxima**: Los contenedores tienen límite de items
2. **Anidamiento**: Los contenedores pueden contener otros contenedores
3. **Cálculo Recursivo**: Valor y peso se calculan recursivamente
4. **Operaciones Seguras**: Lanza excepciones si se intenta agregar items a SimpleItem

### Tests Implementados

**Ubicación**: `game.unit.structural.CompositePatternTest`

- ✅ Items simples tienen valor y peso directo
- ✅ Contenedores calculan valor y peso total del contenido
- ✅ Contenedores pueden contener otros contenedores (anidamiento)
- ✅ Contenedores respetan capacidad máxima
- ✅ Contenedores permiten remover items
- ✅ Items simples no permiten agregar otros items
- ✅ Contenedores muestran detalles del contenido

---

## 2. Patrón Decorator - Sistema de Efectos de Estado

### Propósito
Permite agregar responsabilidades adicionales a objetos de forma dinámica, sin modificar su clase original.

### Implementación

#### Estructura de Clases

```
Personaje (componente base)
└── CharacterDecorator (decorador base)
    ├── PoisonEffect
    ├── BurnEffect
    ├── StunEffect
    └── StrengthEffect
```

#### Decorador Base: CharacterDecorator

**Ubicación**: `game.effects.status.CharacterDecorator`

Extiende de `Personaje` y encapsula otro `Personaje`, delegando todas las operaciones.

Métodos abstractos que deben implementar los decoradores concretos:
- `aplicarEfecto()` - Lógica del efecto al inicio del turno
- `getDescripcionEfecto()` - Descripción del efecto aplicado

#### Decorador Concreto: PoisonEffect

**Ubicación**: `game.effects.status.PoisonEffect`

Aplica daño por veneno al inicio de cada turno.

```java
PoisonEffect envenenado = new PoisonEffect(guerrero, 5, 3);
// Daño: 5 HP por turno
// Duración: 3 turnos

envenenado.aplicarEfecto(); // Reduce 5 HP
```

#### Decorador Concreto: BurnEffect

**Ubicación**: `game.effects.status.BurnEffect`

Aplica daño por fuego al inicio de cada turno.

```java
BurnEffect quemado = new BurnEffect(guerrero, 8, 2);
// Daño: 8 HP por turno
// Duración: 2 turnos
```

#### Decorador Concreto: StunEffect

**Ubicación**: `game.effects.status.StunEffect`

Impide que el personaje pueda atacar durante la duración del efecto.

```java
StunEffect aturdido = new StunEffect(guerrero, 2);
// El personaje no puede atacar por 2 turnos
// El daño del ataque es 0 mientras duré el aturdimiento
```

#### Decorador Concreto: StrengthEffect

**Ubicación**: `game.effects.status.StrengthEffect`

Amplifica el daño infligido por el personaje.

```java
StrengthEffect fortalecido = new StrengthEffect(guerrero, 2.0, 3);
// Multiplicador: 2.0 (doble daño)
// Duración: 3 turnos
```

#### Características Especiales

1. **Encadenamiento**: Los decoradores se pueden apilar
   ```java
   BurnEffect quemado = new BurnEffect(guerrero, 5, 3);
   PoisonEffect envenenado = new PoisonEffect(quemado, 3, 5);
   // Ambos efectos aplican
   ```

2. **Duración Temporal**: Cada efecto tiene un contador de turnos

3. **Nombre Modificado**: Los personajes muestran sus efectos activos en su nombre
   ```
   "Héroe [Envenenado]"
   "Goblin [Aturdido]"
   ```

4. **Referencia al Base**: Se puede obtener el personaje sin decoradores con `getPersonajeBase()`

### Tests Implementados

**Ubicación**: `game.unit.structural.DecoratorPatternTest`

- ✅ PoisonEffect aplica daño cada turno
- ✅ BurnEffect aplica daño por fuego
- ✅ StunEffect impide atacar
- ✅ StrengthEffect amplifica daño
- ✅ Decoradores se encadenan correctamente
- ✅ Decorador mantiene referencia al personaje base
- ✅ Efectos no aplican si el personaje muere
- ✅ Nombre del personaje incluye efectos activos

---

## 3. Patrón Facade - Sistema de Combate

### Propósito
Proporciona una interfaz simplificada para un subsistema complejo, ocultando sus detalles de implementación.

### Implementación

#### Fachada: CombatFacade

**Ubicación**: `game.combat.facade.CombatFacade`

Simplifica la interacción con el motor de combate (`MotorCombate`), ocultando su complejidad.

#### Métodos de la Fachada

##### 1. Iniciar Combate
```java
facade.iniciarCombate(heroe, enemigo);
```
Oculta:
- Creación del `MotorCombate`
- Inicialización del log
- Validación de estado

##### 2. Ejecutar Ronda Individual
```java
ResultadoAtaque resultado = facade.ejecutarRonda();
```
Oculta:
- Aplicación de efectos de estado
- Ejecución del ataque
- Registro en el log
- Verificación de fin de combate

##### 3. Ejecutar Combate Completo
```java
Personaje ganador = facade.ejecutarCombateCompleto();
```
Oculta:
- Loop de combate
- Conteo de rondas
- Finalización automática

##### 4. Obtener Estadísticas
```java
EstadisticasCombate stats = facade.obtenerEstadisticas();
// Incluye:
// - Total de rondas
// - Daño total infligido
// - Ganador
```

##### 5. Obtener Log de Combate
```java
List<String> log = facade.obtenerLogCombate();
facade.imprimirLog(); // Imprime en consola
```

#### Características Especiales

1. **API Simple**: Solo 3 métodos principales para usar el sistema completo

2. **Log Automático**: Registra todos los eventos del combate
   ```
   === COMBATE INICIADO ===
   Héroe vs Goblin
   HP: 100 vs 50
   
   --- Ronda 1 ---
   Héroe ataca a Goblin → 20 de daño (HP restante: 30)
   ...
   === COMBATE FINALIZADO ===
   Ganador: Héroe
   ```

3. **Integración con Efectos**: Aplica automáticamente efectos de estado (patrón Decorator)

4. **Estadísticas**: Proporciona resumen del combate

5. **Control de Estado**: Previene combates simultáneos

6. **Reinicio**: Permite limpiar el estado para un nuevo combate

#### Comparación: Con vs Sin Facade

**Sin Facade (complejo):**
```java
MotorCombate motor = new MotorCombate(heroe, enemigo);
while (!motor.combateFinalizado()) {
    Personaje atacante = motor.getAtacanteActual();
    if (atacante instanceof CharacterDecorator) {
        ((CharacterDecorator) atacante).aplicarEfecto();
    }
    ResultadoAtaque resultado = motor.ejecutarRonda();
    // Procesar resultado...
    // Actualizar UI...
    // Registrar log...
}
Personaje ganador = motor.obtenerGanador();
```

**Con Facade (simple):**
```java
CombatFacade facade = new CombatFacade();
facade.iniciarCombate(heroe, enemigo);
Personaje ganador = facade.ejecutarCombateCompleto();
facade.imprimirLog();
```

### Tests Implementados

**Ubicación**: `game.unit.structural.FacadePatternTest`

- ✅ Facade permite iniciar combate fácilmente
- ✅ Facade ejecuta rondas correctamente
- ✅ Facade ejecuta combate completo hasta el final
- ✅ Facade genera log detallado del combate
- ✅ Facade proporciona estadísticas del combate
- ✅ Facade no permite dos combates simultáneos
- ✅ Facade permite reiniciar para nuevo combate
- ✅ Facade lanza excepción si se ejecuta ronda sin iniciar combate
- ✅ Facade retorna null para ganador si combate no ha finalizado
- ✅ Facade oculta complejidad del motor de combate

---

## Resumen de Tests

### Tests Totales: 44
- **Patrones Creacionales**: 15 tests
- **Patrones Estructurales**: 25 tests
  - Composite: 7 tests
  - Decorator: 8 tests
  - Facade: 10 tests
- **Dominio y Combate**: 4 tests

### Resultado
```
[INFO] Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## Beneficios de los Patrones Estructurales

### 1. Composite
- ✅ Trata items simples y contenedores uniformemente
- ✅ Permite estructuras jerárquicas arbitrariamente complejas
- ✅ Simplifica el código cliente (no necesita distinguir tipos)
- ✅ Facilita agregar nuevos tipos de items

### 2. Decorator
- ✅ Extiende funcionalidad sin modificar clases existentes
- ✅ Permite combinaciones flexibles de efectos
- ✅ Evita explosión de subclases
- ✅ Cumple el principio Open/Closed

### 3. Facade
- ✅ Simplifica la interfaz de un subsistema complejo
- ✅ Reduce el acoplamiento entre código cliente y subsistema
- ✅ Facilita el uso del sistema de combate
- ✅ Mejora la mantenibilidad

---

## Arquitectura Limpia

Todos los patrones estructurales respetan los principios del proyecto:

- ✅ **SRP** (Responsabilidad única)
- ✅ **OCP** (Abierto/Cerrado)
- ✅ **LSP** (Sustitución de Liskov)
- ✅ **ISP** (Segregación de interfaces)
- ✅ **DIP** (Inversión de dependencias)

Los patrones están **completamente integrados** con los patrones creacionales y de comportamiento existentes en el proyecto.
