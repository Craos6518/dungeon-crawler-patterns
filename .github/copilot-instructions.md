# Instrucciones del Proyecto – Sistema de Combate por Turnos

## Contexto

Java 17 / JUnit 5 únicamente. Proyecto académico (Universidad Tecnológica de Pereira - Patrones de Diseño). Arquitectura limpia sin frameworks externos.

**Estado actual:** ✅ Proyecto completo con 131 tests pasando, 10 patrones implementados, y juego interactivo jugable.

## Principios Fundamentales

**Inversión de dependencias estricta**:
- `MotorCombate` depende SOLO de la abstracción `Personaje`
- Nunca usar `instanceof` para lógica de negocio
- Cada personaje encapsula su propio cálculo de daño

**Inmutabilidad donde corresponde**:
- `ResultadoAtaque` es un `record` inmutable con: `atacante`, `defensor`, `daño`, `vidaRestanteDefensor`
- Usar `List.of()` para colecciones inmutables cuando sea posible
- Preferir `List<>` sobre arrays para colecciones

**Separación de responsabilidades**:
- Motor controla turnos y determina fin de combate
- Personajes calculan su propio daño en `atacar(Personaje objetivo)`
- Sin lógica de impresión mezclada con dominio

## Patrones de Diseño Obligatorios

**Implementar TODOS excepto Singleton** (10 patrones activos):

**Creacionales**:
- **Factory Method**: Para creación flexible de personajes individuales
- **Builder**: Para construcción paso a paso de mazmorras
- **Abstract Factory**: Para familias temáticas coherentes (Fire, Ice, Dark, Poison)

**Estructurales**:
- **Composite**: Para sistema jerárquico de inventario (items simples + contenedores)
- **Decorator**: Para efectos de estado dinámicos sobre personajes
- **Facade**: Para interfaz simplificada del sistema de combate

**Comportamiento**:
- **Command**: Para encapsular acciones con historial
- **Strategy**: Para comportamientos de IA intercambiables
- **Observer**: Para sistema de eventos desacoplado
- **Memento**: Para guardado/restauración de estado

**Al sugerir soluciones**: Priorizar el patrón apropiado según el contexto. Ver [INTEGRACION_PATRONES.md](../INTEGRACION_PATRONES.md).

## Convenciones de Nomenclatura

**UpperCamelCase obligatorio** para todas las clases:
- Efectos terminan en `Effect`: `BurnEffect`, `PoisonEffect`, `StrengthEffect`
- Factories terminan en `Factory`: `GuerreroFactory`, `FireThemeFactory`
- Strategies terminan en `Strategy`: `AggressiveStrategy`, `DefensiveStrategy`
- Commands terminan en `Command`: `AttackCommand`, `UseItemCommand`

**Paquetes**: usar minúsculas sin guiones: `game.effects.status`, `game.domain.personaje.factory`

## Manejo de Errores

**Pragmático sin doble trabajo**:
- Excepciones **unchecked** (RuntimeException) para errores de programación (estado inválido, precondiciones violadas)
- `Optional<T>` para ausencia legítima de valores (búsqueda sin resultado)
- Validaciones explícitas con mensajes claros en `IllegalArgumentException` o `IllegalStateException`
- No usar excepciones checked (eliminar complejidad innecesaria)

```java
// ✅ Correcto
public Personaje buscarPersonaje(String nombre) {
    return personajes.stream()
        .filter(p -> p.getNombre().equals(nombre))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Personaje no encontrado: " + nombre));
}

// ✅ Correcto
public Optional<Item> buscarItem(String nombre) {
    return inventario.stream()
        .filter(i -> i.getNombre().equals(nombre))
        .findFirst();
}
```

## Restricciones Específicas

**Al modificar `MotorCombate`**:
- No detectar tipos concretos
- No calcular daño directamente
- Solo alternar turnos y verificar condiciones de finalización
- Exponer método `iniciar()` como punto de entrada

**Al crear clases de personaje**:
- Implementar `atacar()`, `recibirDanio(int)`, `estaVivo()`, `getVida()`
- Sin referencia al motor desde el personaje
- Límite de 20 líneas por método

**Prohibido absolutamente**:
- Pattern Singleton (explícitamente excluido del proyecto)
- Estado global o variables estáticas con lógica de negocio
- Métodos estáticos para lógica de dominio
- Lógica compleja en constructores

## Documentación JavaDoc

**Obligatorio para proyecto académico**:
- Todas las **interfaces públicas** con JavaDoc completo
- Todas las **clases que implementan patrones** con:
  - Descripción del patrón aplicado
  - Rol en el patrón (Factory, ConcreteProduct, Context, Strategy, etc.)
  - Ejemplo de uso si aplica
- Métodos públicos con descripción, `@param`, `@return`, `@throws` cuando corresponda
- Privados/package-private: JavaDoc opcional, priorizar código auto-documentado

```java
/**
 * Factory Method para crear personajes tipo Guerrero.
 * <p>
 * Este patrón permite crear guerreros con configuraciones específicas
 * sin exponer la lógica de construcción al cliente.
 * 
 * @see PersonajeFactory
 * @see Guerrero
 */
public class GuerreroFactory implements PersonajeFactory {
    /**
     * Crea un nuevo guerrero con las estadísticas base configuradas.
     *
     * @param nombre el nombre del guerrero
     * @return instancia de Guerrero configurada
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    @Override
    public Personaje crearPersonaje(String nombre) {
        // ...
    }
}
```

## Testing

Cada componente debe tener:
- Test unitario de comportamiento aislado
- Test de integración simulando flujo completo
- Verificación de alternancia de turnos (para motor)
- Verificación de finalización correcta
- Tests de patrones específicos (ver `README_TESTS.md`)

**Ejecutar**: `mvn clean test`

**Nombres de tests**: `nombreDelPatternTest` (ej: `FactoryMethodTest`, `DecoratorPatternTest`)

## Referencias

- [INTEGRACION_PATRONES.md](../INTEGRACION_PATRONES.md) - Cómo los 10 patrones trabajan juntos
- [RESUMEN_IMPLEMENTACION.md](../RESUMEN_IMPLEMENTACION.md) - Estructura completa del proyecto
- [PATRONES_CREACIONALES.md](../../Docs/PATRONES_CREACIONALES.md) - Detalle de Factory Method, Builder, Abstract Factory
- [PATRONES_ESTRUCTURALES.md](../../Docs/PATRONES_ESTRUCTURALES.md) - Detalle de Composite, Decorator, Facade
- [README_TESTS.md](../README_TESTS.md) - Guía completa de testing
