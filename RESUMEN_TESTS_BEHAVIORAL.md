# Tests de Patrones de Comportamiento

Este documento resume los tests unitarios e de integración creados para los patrones de comportamiento.

## Tests Unitarios

### 1. CommandPatternTest (11 tests)
Ubicación: `src/test/java/game/unit/behavioral/CommandPatternTest.java`

**Cobertura:**
- ✅ `testAttackCommandExecution` - Verifica que el comando de ataque ejecuta correctamente
- ✅ `testDefendCommandExecution` - Verifica que el comando de defensa ejecuta y cambia su estado
- ✅ `testCannotExecuteCommandOnDeadCharacter` - Valida que no se ejecutan comandos sobre personajes muertos
- ✅ `testCommandInvokerHistory` - Verifica que el historial de comandos se mantiene
- ✅ `testCommandDescription` - Comprueba las descripciones de los comandos
- ✅ `testUseItemCommand` - Valida el uso de items mediante comandos
- ✅ `testSkillCommand` - Verifica la ejecución de habilidades
- ✅ `testInvokerThrowsExceptionWhenExecutingInvalidCommand` - Manejo de errores
- ✅ `testClearHistory` - Limpieza del historial

**Patrones validados:** Command (GoF)

---

### 2. StrategyPatternTest (11 tests)
Ubicación: `src/test/java/game/unit/behavioral/StrategyPatternTest.java`

**Cobertura:**
- ✅ `testAggressiveStrategyAttacksMostHealth` - Verifica que la estrategia agresiva prioriza al enemigo con más vida
- ✅ `testDefensiveStrategyDefendsWhenLowHealth` - Valida defensa cuando la vida es baja
- ✅ `testDefensiveStrategyAttacksWeakestWhenHealthy` - Ataque al más débil cuando la vida es alta
- ✅ `testIntelligentStrategyPrioritizesWeakEnemies` - Estrategia inteligente elimina enemigos débiles
- ✅ `testRandomStrategySelectsAnyEnemy` - Estrategia aleatoria funciona correctamente
- ✅ `testAIControllerChangeStrategy` - Cambio dinámico de estrategias
- ✅ `testAIControllerDelegatesDecision` - El controlador delega correctamente
- ✅ `testStrategyThrowsExceptionWithEmptyEnemyList` - Manejo de errores (lista vacía)
- ✅ `testStrategyThrowsExceptionWithNullEnemyList` - Manejo de errores (lista null)
- ✅ `testAIControllerRequiresNonNullStrategy` - Validación de estrategia no nula

**Patrones validados:** Strategy (GoF)

---

### 3. ObserverPatternTest (13 tests)
Ubicación: `src/test/java/game/unit/behavioral/ObserverPatternTest.java`

**Cobertura:**
- ✅ `testEventManagerIsSingleton` - Verifica implementación Singleton
- ✅ `testObserverReceivesEvents` - Observer recibe notificaciones
- ✅ `testMultipleObserversReceiveEvents` - Múltiples observers funcionan
- ✅ `testObserverCanUnsubscribe` - Desuscripción funciona correctamente
- ✅ `testEventTypeSpecificSubscription` - Suscripción por tipo de evento
- ✅ `testStatisticsTrackerCountsCorrectly` - Contador de estadísticas funciona
- ✅ `testEventHistoryIsKept` - Historial de eventos se mantiene
- ✅ `testEventManagerCanBeDisabled` - Sistema se puede deshabilitar
- ✅ `testGameEventStoresData` - Eventos almacenan datos correctamente
- ✅ `testCombatLoggerFormatting` - Logger formatea correctamente

**Patrones validados:** Observer (GoF), Singleton (GoF)

---

### 4. MementoPatternTest (14 tests)
Ubicación: `src/test/java/game/unit/behavioral/MementoPatternTest.java`

**Cobertura:**
- ✅ `testCreateMemento` - Creación de memento
- ✅ `testRestoreFromMemento` - Restauración de estado
- ✅ `testMementoPreservesState` - Preservación de estado completa
- ✅ `testCaretakerStoresMultipleMementos` - Almacenamiento múltiple
- ✅ `testCaretakerGetLastMemento` - Obtención del último memento
- ✅ `testCaretakerThrowsExceptionWhenEmpty` - Manejo de errores
- ✅ `testCaretakerThrowsExceptionForInvalidIndex` - Validación de índices
- ✅ `testSaveToDisk` - Persistencia en disco
- ✅ `testLoadFromDisk` - Carga desde disco
- ✅ `testListSavedGames` - Listado de guardados
- ✅ `testDeleteSavedGame` - Eliminación de guardados
- ✅ `testMementoIsImmutable` - Inmutabilidad del memento
- ✅ `testClearHistory` - Limpieza del historial

**Patrones validados:** Memento (GoF), Builder (GoF)

---

## Tests de Integración

### 5. BehavioralPatternsIntegrationTest (6 tests)
Ubicación: `src/test/java/game/integration/behavioral/BehavioralPatternsIntegrationTest.java`

**Cobertura:**
- ✅ `testCommandWithObserverIntegration` - Command + Observer trabajan juntos
- ✅ `testStrategyWithCommandIntegration` - Strategy genera Commands
- ✅ `testMementoWithGameStateIntegration` - Memento guarda/restaura estado
- ✅ `testFullIntegrationScenario` - Todos los patrones trabajan juntos
- ✅ `testStrategyChange` - Cambio dinámico de estrategia en combate

**Patrones validados:** Integración de Command, Strategy, Observer, Memento

---

## Resumen Estadís tico

- **Total de tests:** 55 (49 unitarios + 6 integración)
- **Tests Command:** 11
- **Tests Strategy:** 11  
- **Tests Observer:** 13
- **Tests Memento:** 14
- **Tests Integración:** 6

## Filosofía de Testing

Siguiendo README_TESTS.md:
- ✅ Tests demuestran que el sistema es **testeable y verificable**
- ✅ Tests validan **patrones de diseño** (no solo cobertura de código)
- ✅ Tests unitarios prueban componentes individuales
- ✅ Tests de integración validan interacciones entre patrones
- ✅ **No se usan mocks** (testing con implementaciones reales)
- ✅ Tests siguen principios **AAA** (Arrange, Act, Assert)

## Comandos

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar solo tests de comportamiento
mvn test -Dtest="game.unit.behavioral.*Test"

# Ejecutar solo tests de integración behavioral
mvn test -Dtest="game.integration.behavioral.*Test"

# Ejecutar test específico
mvn test -Dtest="CommandPatternTest"
```

## Integración con tests existentes

Los tests de comportamiento se suman a los existentes:
- **Tests creacionales:** AbstractFactoryTest, BuilderPatternTest, FactoryMethodTest
- **Tests estructurales:** CompositePatternTest, DecoratorPatternTest, FacadePatternTest
- **Tests dominio:** CharacterDamageTest
- **Tests combate:** CombatTurnAlternationTest, CombatEndTest
- **Tests integración:** CombatIntegrationTest
- **Tests comportamiento:** CommandPatternTest, StrategyPatternTest, ObserverPatternTest, MementoPatternTest, StatePatternTest

**Total proyecto: 107 tests pasando ✅**
