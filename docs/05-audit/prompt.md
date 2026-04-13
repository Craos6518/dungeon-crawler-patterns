# PROMPT DE REMEDIACIÓN AUTÓNOMA — Dungeon Crawler Patterns
## Una iteración = un patrón remediado y llevado a producción

---

## ROL Y MANDATO

Eres un ingeniero de software Java senior con acceso completo al repositorio.
Tu único objetivo en esta ejecución es **remediar UN patrón de diseño** llevándolo
a integración productiva real: código conectado al runtime, tests que lo validan,
documentación que refleja la realidad.

**No consultes. No esperes decisiones. No propongas opciones.**
Toma la decisión más robusta por defecto y ejecútala.
La única decisión válida es integrar al flujo productivo real.
Declarar algo como "demostrativo" está **prohibido** como remediación.

---

## REGLAS INAMOVIBLES

1. Un solo patrón por ejecución
2. Leer `docs/05-audit/AUDIT_PROGRESS.md` al inicio para seleccionar el patrón con estado `⏳ Pendiente` de menor calificación de auditoría que aún no esté marcado como `✅ Remediado`
3. Nunca repetir un patrón ya marcado `✅ Remediado`
4. Al terminar, actualizar `AUDIT_PROGRESS.md` con estado `✅ Remediado` y calificación obtenida
5. La ejecución no termina hasta que `mvn test` pase con 0 fallos

---

## CRITERIO DE 10/10 (no negociable)

Un patrón está remediado cuando cumple **todo** lo siguiente:

- El patrón participa en el flujo real: `GameRuntime` → casos de uso → dominio
- El código productivo usa las clases del patrón, no las bypasea
- Las clases del patrón que existían solo en tests o módulos aislados están conectadas al runtime
- Los tests cubren: flujo feliz desde runtime, casos borde, escenarios negativos y al menos un test de integración end-to-end que pase por el patrón
- La documentación en `docs/03-patterns/<patron>.md` describe exactamente las clases y métodos reales
- `mvn test` pasa con 0 fallos y 0 regresiones

---

## PASOS DE EJECUCIÓN

### PASO 0 — SELECCIÓN
1. Leer `docs/05-audit/AUDIT_PROGRESS.md`
2. Identificar el patrón con estado `⏳ Pendiente` y menor calificación de auditoría
3. Leer su sección de fallos críticos y acciones concretas en este mismo documento
4. Confirmar en output: "Patrón seleccionado: [nombre]. Calificación auditoría: [n]/10."

### PASO 1 — VERIFICACIÓN DE ESTADO ACTUAL
Antes de tocar código:
1. Leer los archivos clave del patrón identificados en la auditoría
2. Ejecutar los tests del patrón: `mvn test -Dtest=<PatternTest>,<IntegrationTest> -q`
3. Confirmar qué fallos siguen presentes en el código actual

### PASO 2 — INTEGRACIÓN PRODUCTIVA (código)
Conectar el patrón al flujo real según las instrucciones de la sección específica.
Para cada archivo a modificar:
1. Leer el archivo completo antes de editarlo
2. Aplicar el cambio
3. Compilar: `mvn compile -q`
4. Si el build falla, corregir antes de continuar

### PASO 3 — TESTS
Añadir o corregir tests según las instrucciones de la sección específica.
Después de cada test nuevo: `mvn test -Dtest=<NuevoTest> -q`
Si falla, corregir en el mismo paso antes de continuar.

### PASO 4 — DOCUMENTACIÓN
Actualizar `docs/03-patterns/<patron>.md`:
- Reflejar clases y métodos reales del flujo productivo
- Corregir o añadir diagrama con la cadena real de invocación
- Eliminar referencias a clases que no existen o que cambiaron de rol
- Actualizar metadatos (rama, fecha)

### PASO 5 — VALIDACIÓN FINAL
```
mvn test -q
```
- Si hay fallos: corregirlos. No avanzar hasta tener 0 fallos.
- Reportar: "X tests, 0 fallos, 0 errores."

### PASO 6 — AUTOEVALUACIÓN
Responder con evidencia de archivo y línea:
1. ¿El patrón está en el flujo productivo? ¿Qué clase lo invoca y desde dónde?
2. ¿Los tests cubren flujo feliz, bordes, negativos y end-to-end?
3. ¿La documentación refleja exactamente el código?
4. ¿Quedan clases del patrón sin conectar al runtime? ¿Cuáles?
5. Calificación estimada: 1–10

### PASO 7 — ACTUALIZAR PROGRESO
Editar `docs/05-audit/AUDIT_PROGRESS.md`:
- Cambiar estado del patrón de `⏳ Pendiente` a `✅ Remediado`
- Registrar nueva calificación
- Si la calificación es menor a 10, registrar exactamente qué falta y por qué no se completó

---

## INSTRUCCIONES DE REMEDIACIÓN POR PATRÓN

---

### FACADE (auditoría: 4/10)

**Problema central:** `CombatFacade` existe pero `GameRuntime` y `GameSession` usan
`Combat` del dominio directamente. La Facade no está en ningún flujo real.

**Lo que hay que hacer en código:**

`src/main/java/game/application/state/GameSession.java`
- Reemplazar el campo de tipo `Combat` por un campo de tipo `CombatFacade`
- Delegar todas las operaciones de combate a través de `CombatFacade`
- Asegurarse de que `CombatFacade` expone los mismos puntos de entrada
  que `Combat` necesita para los use cases

`src/main/java/game/application/state/GameSessionFactory.java`
- Construir e inyectar `CombatFacade` en lugar de construir `Combat` directamente
- `CombatFacade` internamente puede construir los subsistemas que necesita

`src/main/java/game/application/runtime/GameRuntime.java`
- Verificar que los comandos de combate (attack, defend, skill, flee)
  lleguen a `GameSession` y de ahí a `CombatFacade`
- No deben existir rutas que bypaseen la Facade

`src/main/java/game/patterns/combat/facade/CombatFacade.java`
- Verificar que expone métodos suficientes para cubrir todas las acciones
  de combate que usa el runtime (attack, defend, useSkill, applyBuff,
  getStatus, isCombatOver, etc.)
- Si falta algún método, añadirlo delegando al subsistema correspondiente

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/structural/FacadePatternTest.java`
- Añadir test: aplicar `CharacterDecorator` en `ejecutarRonda` y verificar
  que el efecto se refleja en el resultado de la ronda
- Añadir test: ejecutar la misma secuencia de combate una vez vía `CombatFacade`
  y otra vez invocando los subsistemas directamente; verificar que
  el estado final (HP, experiencia, resultado) es idéntico

`src/test/java/game/integration/structural/FacadeIntegrationTest.java` (crear)
- Test: crear `GameSession` → iniciar combate → ejecutar 3 rondas vía
  los comandos de `GameRuntime` → verificar que la HP del jugador
  y del enemigo cambiaron correctamente → verificar que el combate termina
  cuando HP llega a 0

**Lo que hay que hacer en documentación:**

`docs/03-patterns/facade.md`
- Actualizar diagrama: `GameRuntime` → `GameSession` → `CombatFacade`
  → `[Combat, CombatSystem, TurnManager, CombatStatusDecoratorPipeline]`
- Listar todos los métodos públicos reales de `CombatFacade`
- Listar los subsistemas que encapsula con sus rutas reales
- Actualizar rama y fecha

---

### STATE (auditoría: 5/10)

**Problema central:** los estados concretos (`MenuState`, `ExplorationState`,
`CombatState`, `InventoryState`, `GameOverState`) y `GameRuntimeCoordinator`
existen pero las transiciones en `GameRuntime` usan strings directos.
`GameStateContext` existe pero solo sincroniza pantallas, no encapsula
comportamiento por estado.

**Lo que hay que hacer en código:**

`src/main/java/game/application/runtime/GameRuntime.java`
- Eliminar las comparaciones de strings de pantalla (`"menu"`, `"combat"`, etc.)
- Reemplazarlas por delegación a `GameStateContext.transition(NuevoEstado)`
- El estado concreto activo decide qué acciones son válidas en cada momento

`src/main/java/game/application/runtime/GameRuntimeCoordinator.java`
- Conectarlo como orquestador del ciclo de vida de `GameRuntime`
- Debe gestionar la creación inicial de la sesión y la secuencia de estados
  hasta llegar al estado de exploración

`src/main/java/game/application/usecase/CombatUseCaseSupport.java`
`src/main/java/game/application/usecase/AdvanceTurnUseCase.java`
`src/main/java/game/application/usecase/RetreatCombatUseCase.java`
- Reemplazar strings de estado hardcodeados por llamadas a
  `gameSession.getStateContext().transitionTo(EstadoConcreto)`

`src/main/java/game/domain/state/MenuState.java` (y demás estados concretos)
- Verificar que cada estado implementa correctamente las acciones válidas
  y lanza excepción o no-op para acciones inválidas en ese estado
- Por ejemplo: `MenuState` no debe aceptar comandos de combate

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/behavioral/StatePatternTest.java`
- Añadir test: intentar acción de combate desde `MenuState` →
  verificar que se rechaza o retorna estado de error, no crash
- Añadir test: transición inválida (por ejemplo Menu → GameOver directo sin
  pasar por Combat) → verificar que no es posible

`src/test/java/game/integration/behavioral/GameRuntimeStateFlowIntegrationTest.java`
- Añadir test de flujo completo:
  `Menu → Setup → Exploration → Combat → (victoria) → Exploration → GameOver`
  verificando que cada transición activa el estado correcto y que las
  acciones disponibles cambian en cada estado

`src/test/java/game/integration/behavioral/StateMementoIntegrationTest.java`
- Añadir test: guardar en estado `Exploration`, cargar, verificar que
  el estado restaurado es `Exploration` y no `Menu`

**Lo que hay que hacer en documentación:**

`docs/03-patterns/state.md`
- Actualizar diagrama con flujo real:
  `GameRuntime` → `GameStateContext` → `[MenuState, ExplorationState, CombatState, InventoryState, GameOverState]`
- Documentar qué transiciones son válidas desde cada estado
- Documentar cómo `GameRuntimeCoordinator` orquesta el arranque
- Actualizar rama y fecha

---

### COMMAND (auditoría: 6/10)

**Problema central:** `playerAttack` en `CombatSystem` no usa `AttackCommand` ni
registra en `CommandInvoker`. El turno enemigo detecta `AttackCommand` como tipo
pero aplica daño por ruta paralela sin ejecutar el comando.
`UseItemCommand` no está conectado al runtime.

**Lo que hay que hacer en código:**

`src/main/java/game/domain/combat/CombatSystem.java` — método `playerAttack`
- Crear `AttackCommand` con `(attacker=player, target=enemy)`
- Verificar con `command.canExecute()` antes de ejecutar
- Ejecutar vía `invoker.execute(attackCommand)`
- Obtener el resultado del daño desde el comando ejecutado
- Eliminar la lógica de cálculo de daño directo que existe fuera del comando

`src/main/java/game/domain/combat/CombatSystem.java` — método `enemyTurn` / `enemyAction`
- En la rama donde se detecta `AttackCommand`, llamar `invoker.execute(comando)`
- El daño debe venir del comando ejecutado, no de una ruta paralela
- El historial del invoker debe quedar con la acción del enemigo registrada

`src/main/java/game/patterns/command/actions/UseItemCommand.java`
- Implementar `execute()` llamando a `Inventory.useItem(itemId)` del jugador
- Conectar desde `GameRuntime` para que el comando `useItem` del runtime
  cree y ejecute un `UseItemCommand` vía el `CommandInvoker` del combate

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/behavioral/CommandPatternTest.java`
- Añadir test de `undoLastCommand`: ejecutar `AttackCommand`, llamar undo,
  verificar que se lanza `UnsupportedOperationException` con mensaje claro
  que explique por qué el ataque no es reversible
- Añadir test de `undoLastN`: encolar 3 comandos, llamar `undoLastN(2)`,
  verificar que solo queda 1 en historial
- Añadir test de `UseItemCommand`: crear `UseItemCommand` con un ítem
  de curación, ejecutar en combate, verificar que el HP del jugador aumentó

`src/test/java/game/unit/domain/combat/CombatSystemTest.java`
- Añadir test: llamar `playerAttack` → verificar que
  `invoker.getHistory().size() == 1` después del ataque
- Añadir test: simular turno enemigo con estrategia agresiva →
  verificar que `invoker.getHistory()` contiene el `AttackCommand` del enemigo

`src/test/java/game/integration/behavioral/BehavioralPatternsIntegrationTest.java`
- Corregir cualquier test que realice ataque manual después de un ataque
  por comando (dobla el daño artificialmente)
- Reemplazar por: ataque vía comando → verificar daño → verificar historial

**Lo que hay que hacer en documentación:**

`docs/03-patterns/command.md`
- Añadir diagrama de ruta completa:
  `AIStrategy → crea AttackCommand → CommandInvoker.execute() → daño real`
- Documentar uso de `LevelUpCommand` desde `Player.gainExperience()`
- Documentar `UseItemCommand` y su conexión al runtime
- Cambiar descripción de reversibilidad a: "reversibilidad parcial —
  `AttackCommand` no soporta undo por diseño; `DefendCommand` y
  `SkillCommand` sí"
- Actualizar lista de tests relevantes
- Actualizar rama y fecha

---

### FACTORY METHOD (auditoría: 6/10)

**Problema central:** `DragonFactory` y `OrcoFactory` están definidas pero las
theme factories (`FireThemeFactory`, `IceThemeFactory`, `PoisonThemeFactory`,
`DarkThemeFactory`) crean enemigos con `new` directamente ignorando estas factories.

**Lo que hay que hacer en código:**

`src/main/java/game/domain/dungeon/factory/FireThemeFactory.java`
`src/main/java/game/domain/dungeon/factory/IceThemeFactory.java`
`src/main/java/game/domain/dungeon/factory/PoisonThemeFactory.java`
`src/main/java/game/domain/dungeon/factory/DarkThemeFactory.java`
- Reemplazar `new Dragon(...)` / `new Orco(...)` / `new EnemigoBasico(...)`
  por llamadas a `DragonFactory.create(params)` / `OrcoFactory.create(params)`
  / `EnemigoBasicoFactory.create(params)` según corresponda al tema
- Cada theme factory delega la construcción del enemigo a la
  `PersonajeFactory` concreta correspondiente
- Mantener el comportamiento observable idéntico: mismos stats, mismo tipo

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/behavioral/FactoryMethodTest.java`
- Añadir test para `DragonFactory`: crear dragón, verificar que es instancia
  del tipo correcto y que sus stats base son los esperados
- Añadir test para `OrcoFactory`: crear orco, verificar tipo y stats base
- Añadir test de integración con theme factory: crear `FireThemeFactory`,
  llamar `createEnemy()`, verificar que internamente usó `DragonFactory`
  (inspeccionar tipo del enemigo creado)

`src/test/java/game/unit/application/GameRuntimeHeroSelectionTest.java`
- Añadir aserción que conecte `heroType` → clase Java creada:
  seleccionar héroe tipo `GUERRERO` → verificar que `player.getPersonaje()`
  es instancia de la clase producida por `GuerreroFactory`

**Lo que hay que hacer en documentación:**

`docs/03-patterns/factory-method.md`
- Actualizar diagrama con ruta completa:
  `GameSessionFactory` → `PersonajeFactory` (héroes)
  `DungeonThemeFactory` → `PersonajeFactory` concreta (enemigos)
- Eliminar presentación de factories de enemigos como si fueran independientes
  del flujo de tema; ahora están integradas como delegados
- Documentar `createSessionForThemeRandomized` y `createPlayerForHero`
- Actualizar metadato de rama y fecha

---

### DECORATOR (auditoría: 6/10)

**Problema central:** `BurnEffect` y `StunEffect` están implementados pero no están
en `CombatStatusDecoratorPipeline` ni en el flujo de combate productivo.
Los módulos legacy (`IntegratedCombatEngine`, `ExplorationDomainState`) usan
`CharacterDecorator` de forma desacoplada del runtime principal.

**Lo que hay que hacer en código:**

`src/main/java/game/domain/combat/CombatStatusDecoratorPipeline.java`
- Añadir soporte para `BurnEffect`:
  instanciar y aplicar cuando el estado del combate incluye efecto de quemadura
- Añadir soporte para `StunEffect`:
  instanciar y aplicar cuando el estado del combate incluye efecto de aturdimiento
- Los efectos de quemadura y aturdimiento deben tener una condición de activación
  real (por ejemplo: ciertos tipos de enemigos o skills los infligen)

`src/main/java/game/domain/combat/Combat.java`
- Verificar que `applyStackingBuff` maneja `BurnEffect` y `StunEffect`
- Añadir límite de acumulaciones máximo (ej. máximo 3 stacks del mismo efecto)
  y lanzar advertencia o rechazar si se supera
- Verificar que `startPlayerTurn` aplica daño de `BurnEffect` si está activo
- Verificar que `StunEffect` activo hace que el turno del afectado se salte

`src/main/java/game/application/usecase/ApplyCombatBuffUseCase.java`
- Emitir evento `EFECTO_APLICADO` después de aplicar un buff exitosamente

`src/main/java/game/combat/engine/IntegratedCombatEngine.java` (legacy)
`src/main/java/game/state/domain/exploration/ExplorationDomainState.java` (legacy)
- Verificar si tienen uso real desde `GameRuntime` o `GameSession`
- Si no tienen uso: eliminarlos o moverlos a un paquete `legacy` con
  comentario en la clase indicando que no son parte del flujo activo
- Si tienen uso parcial: conectarlos al pipeline estándar de Decorator

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/domain/combat/CombatDecoratorIntegrationTest.java`
- Añadir test de `BurnEffect` en combate real:
  aplicar quemadura → avanzar turno → verificar daño periódico en HP
- Añadir test de `StunEffect`:
  aplicar aturdimiento → avanzar turno → verificar que el turno
  del afectado fue saltado (acción no ejecutada)
- Añadir test de límite de stacking:
  aplicar el mismo buff 4 veces → verificar que el 4to es rechazado
  o que el stack no supera el máximo configurado
- Añadir test de falta de recurso para buff:
  intentar aplicar buff cuando el jugador no tiene el recurso requerido →
  verificar que el buff no se aplica y el estado del jugador no se corrompe

`src/test/java/game/unit/application/ApplyCombatBuffUseCaseTest.java`
- Añadir test: aplicar buff exitoso → verificar que el evento
  `EFECTO_APLICADO` fue emitido al `EventPublisher`

**Lo que hay que hacer en documentación:**

`docs/03-patterns/decorator.md`
- Actualizar diagrama con cadena completa:
  `GameRuntime` → `ApplyCombatBuffUseCase` / `SetCombatStyleUseCase`
  → `Combat` → `CombatStatusDecoratorPipeline`
  → `[PoisonEffect, StrengthEffect, GuardEffect, BurnEffect, StunEffect]`
- Documentar los métodos críticos del flujo real:
  `applyStackingBuff`, `startPlayerTurn`, `applyOutgoingModifiers`,
  `applyIncomingMitigation`
- Documentar política de stacking máximo
- Documentar conexión con sistema de eventos (`EFECTO_APLICADO`)
- Actualizar rama y fecha

---

### COMPOSITE (auditoría: 7/10)

**Problema central:** `GameSessionMementoMapper` serializa el inventario como lista
plana de `SimpleItem`, perdiendo la estructura de árbol de `ContainerItem`.
Al cargar una partida guardada, toda la jerarquía anidada se pierde.

**Lo que hay que hacer en código:**

`src/main/java/game/domain/inventory/Inventory.java`
- Añadir método `exportTree()`: devuelve el `ContainerItem` raíz completo
  con toda su jerarquía de hijos
- Añadir método `importTree(ItemComponent root)`: reemplaza el árbol actual
  por el árbol recibido, preservando la jerarquía

`src/main/java/game/application/state/GameSessionMementoMapper.java`
- Método de serialización del inventario (~L48):
  reemplazar la serialización de lista plana por serialización recursiva del árbol
  Formato: `{ "type": "container"|"simple", "name": "...", "children": [...] }`
  Usar `exportTree()` como punto de entrada
- Método de restauración del inventario (~L229):
  reemplazar la restauración plana por reconstrucción recursiva:
  leer tipo → si `container` crear `ContainerItem` y procesar hijos recursivamente,
  si `simple` crear `SimpleItem`
  Usar `importTree(raizReconstruida)` para restaurar

**Lo que hay que hacer en tests:**

`src/test/java/game/integration/structural/CompositeIntegrationTest.java` (crear)
- Test de persistencia de jerarquía (el más importante):
  1. Crear árbol anidado:
     `ContainerItem("Mochila")` con hijos
     `[SimpleItem("Poción"), ContainerItem("Bolsa Secreta") con [SimpleItem("Gema"), SimpleItem("Llave")]]`
  2. Asignar ese inventario al jugador en una sesión
  3. Llamar `SaveGameUseCase.save(slot=1)`
  4. Modificar el inventario (añadir un ítem al nivel raíz)
  5. Llamar `LoadGameUseCase.load(slot=1)`
  6. Verificar que la jerarquía original se restauró:
     `Mochila` tiene exactamente 2 hijos directos,
     `Bolsa Secreta` tiene exactamente 2 hijos,
     los nombres de los ítems son los correctos

- Test negativo de persistencia:
  Guardar árbol anidado → corromper el nodo `ContainerItem` en el JSON guardado
  reemplazándolo por un objeto plano → cargar →
  verificar que se lanza `SaveDataCorruptionException`

`src/test/java/game/unit/domain/inventory/InventoryTest.java`
- Añadir test de `exportTree` / `importTree`:
  exportar árbol → modificar árbol en memoria (agregar ítem) →
  importar árbol exportado → verificar que el árbol volvió al estado original

**Lo que hay que hacer en documentación:**

`docs/03-patterns/composite.md`
- Añadir `GameSessionMementoMapper` al inventario de clases relevantes,
  con nota de que su política de serialización es recursiva
- Añadir `Item` (wrapper de dominio) a las clases
- Documentar métodos reales de `Inventory`:
  `simpleItems`, `collectSimpleItems`, `removeSimpleItemRecursive`,
  `replaceItems`, `exportTree`, `importTree`
- Actualizar diagrama con:
  `GameRuntime` → `UseItemUseCase` → `Inventory` (árbol Composite)
  `GameSessionMementoMapper` ↔ `Inventory.exportTree()` / `importTree()`
- Actualizar rama y fecha

---

### OBSERVER (auditoría: 7/10)

**Problema central:** `SessionEventFeedObserver` y `SessionEventCounterObserver` son
instancias estáticas compartidas en `GameSessionFactory`. Si se crean dos sesiones,
los observers de la primera sesión reciben también los eventos de la segunda.
`EventManager` es Singleton con estado mutable no sincronizado.

**Lo que hay que hacer en código:**

`src/main/java/game/application/state/GameSessionFactory.java`
- Reemplazar la instanciación estática de `SessionEventFeedObserver` y
  `SessionEventCounterObserver` por instanciación local dentro del método
  de creación de sesión (`createSession()` o equivalente)
- Cada sesión debe tener sus propias instancias de observers, no compartidas
- Esto asegura que los contadores y logs son independientes por sesión

`src/main/java/game/application/observer/SessionEventFeedObserver.java`
`src/main/java/game/application/observer/SessionEventCounterObserver.java`
- Si actualmente la sesión se bindea vía setter mutable:
  cambiar a constructor inmutable: `new SessionEventFeedObserver(session)`
- Eliminar cualquier setter de sesión para evitar rebind posterior

`src/main/java/game/infrastructure/events/observer/EventManager.java`
- Hacer `getInstance()` thread-safe:
  usar inicialización en campo estático final (`private static final EventManager INSTANCE = new EventManager()`)
  o enum Singleton si el diseño lo permite
- Añadir `reset()` package-private para uso en tests que necesiten
  limpiar el estado entre pruebas (evitar contaminación en suite)

**Lo que hay que hacer en tests:**

`src/test/java/game/integration/behavioral/EventObserversRuntimeIntegrationTest.java`
- Añadir test de aislamiento entre sesiones:
  1. Crear sesión A → emitir 3 eventos desde sus use cases
  2. Crear sesión B → emitir 2 eventos desde sus use cases
  3. Verificar que `sessionA.getEventCounter()` == 3
  4. Verificar que `sessionB.getEventCounter()` == 2
  5. Verificar que `sessionA.getEventLog()` no contiene eventos de B

- Añadir test de doble suscripción:
  intentar registrar el mismo observer dos veces en `EventManager` →
  emitir un evento → verificar que `onEvent` se llamó exactamente una vez,
  no dos

**Lo que hay que hacer en documentación:**

`docs/03-patterns/observer.md`
- Separar explícitamente en el inventario de clases:
  "Observers productivos de sesión" vs "Observers de infraestructura (monitoring/test)"
- Documentar la política de instanciación: un observer por sesión, no estático
- Añadir nota sobre thread-safety de `EventManager`
- Actualizar rama y fecha

---

### STRATEGY (auditoría: 7/10)

**Problema central:** `AdaptiveAIController`, `CombatDomainState` e
`IntegratedCombatEngine` existen con implementaciones de Strategy pero no están
conectados al runtime. Falta cobertura de los umbrales de cambio de estrategia
y de validaciones negativas en `SetCombatStyleUseCase`.

**Lo que hay que hacer en código:**

`src/main/java/game/ai/AdaptiveAIController.java`
- Verificar si su lógica de selección de estrategia adaptativa
  (cambio según HP/ronda/historial) está duplicada o es superior
  a la implementación en `CombatSystem`
- Si es superior: conectarla al `CombatSystem` reemplazando la
  selección de estrategia actual
- Si es equivalente o inferior: mover la lógica útil a `CombatSystem`
  y eliminar `AdaptiveAIController` como clase independiente

`src/main/java/game/application/usecase/SetCombatStyleUseCase.java`
- Verificar que existe validación para estilo `null`:
  lanzar `IllegalArgumentException("Combat style cannot be null")`
- Verificar que existe validación para estilo no reconocido
- Verificar que existe validación cuando el jugador no tiene suficiente
  recurso (maná o energía) para activar el estilo solicitado:
  retornar resultado de error o lanzar excepción tipificada

`src/main/java/game/state/domain/CombatDomainState.java` (legacy)
`src/main/java/game/combat/engine/IntegratedCombatEngine.java` (legacy)
- Verificar que ninguno tiene llamadas activas desde `GameRuntime` o `GameSession`
- Si tienen uso activo: conectarlos al flujo estándar de Strategy en `CombatSystem`
- Si no tienen uso: eliminar las clases para reducir deuda

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/behavioral/StrategyPatternTest.java`
- Añadir test de umbral de selección agresiva:
  crear enemigo con HP al 80%, verificar que `CombatSystem` selecciona
  `AggressiveStrategy`
- Añadir test de umbral de selección defensiva:
  reducir HP del enemigo por debajo del umbral configurado,
  verificar que `CombatSystem` cambia a `DefensiveStrategy`
- Añadir test de estilo nulo:
  llamar `SetCombatStyleUseCase` con `style=null` →
  verificar `IllegalArgumentException`
- Añadir test de estilo sin recurso:
  intentar activar estilo que requiere maná cuando el jugador tiene 0 maná →
  verificar resultado de error y que el estilo del jugador no cambió

`src/test/java/game/unit/domain/combat/CombatSystemTest.java`
- Añadir test: iniciar combate con enemigo en HP alto → atacar varias veces →
  verificar que cuando HP cae al umbral, la próxima selección de estrategia
  retorna `DefensiveStrategy`

**Lo que hay que hacer en documentación:**

`docs/03-patterns/strategy.md`
- Documentar umbrales de selección de estrategia IA (valor exacto del umbral)
- Documentar las validaciones de `SetCombatStyleUseCase`
- Si se eliminó `AdaptiveAIController`: removerlo del inventario de clases
- Si se integró: documentar su rol y desde dónde se invoca
- Actualizar rama y fecha

---

### BUILDER (auditoría: 8/10)

**Problema central:** `DungeonDirector` existe pero `GameSessionFactory` llama
`Dungeon.fromTheme()` directamente sin pasar por el Director.
`Dungeon.fromTheme()` está acoplado a `ConcreteDungeonBuilder` en lugar
de usar la interfaz `DungeonBuilder`.

**Lo que hay que hacer en código:**

`src/main/java/game/application/state/GameSessionFactory.java`
- Reemplazar la llamada directa a `Dungeon.fromTheme(tema, seed)` por
  uso del `DungeonDirector`:
  ```java
  DungeonDirector director = new DungeonDirector(new ConcreteDungeonBuilder());
  Dungeon dungeon = director.buildForTheme(tema, seed);
  ```
- Asegurarse de que el resultado es idéntico al anterior

`src/main/java/game/domain/dungeon/Dungeon.java` — método `fromTheme()`
- Cambiar la referencia interna de `ConcreteDungeonBuilder` por la
  interfaz `DungeonBuilder`
- El método puede seguir existiendo como factory method de conveniencia,
  pero internamente debe usar la interfaz, no el tipo concreto

`src/main/java/game/domain/dungeon/DungeonDirector.java`
- Verificar que implementa `buildForTheme(tema, seed)` produciendo
  la misma mazmorra que `Dungeon.fromTheme()` para los mismos parámetros
- Si el método no existe: crearlo

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/creational/BuilderPatternTest.java` (o equivalente)
- Añadir test de equivalencia:
  construir mazmorra con `DungeonDirector` y con `Dungeon.fromTheme()`
  para mismo tema y seed → verificar que número de salas, tema y
  distribución de enemigos son idénticos
- Añadir test de determinismo:
  mismo tema + mismo seed → misma mazmorra en dos construcciones distintas
  (verificar cantidad de salas y tipo de enemigos)
- Añadir test de perfil:
  construir con `DungeonDirector` usando perfil de dificultad "hard" →
  verificar que los enemigos tienen stats más altos que con perfil "easy"

**Lo que hay que hacer en documentación:**

`docs/03-patterns/builder.md`
- Actualizar diagrama con ruta productiva:
  `GameSessionFactory` → `DungeonDirector` → `ConcreteDungeonBuilder`
  → `ProceduralDungeonGenerator` → `Dungeon`
- Documentar `buildForTheme(tema, seed)` en `DungeonDirector`
- Documentar la relación `fromTheme()` → interfaz `DungeonBuilder`
- Actualizar rama y fecha

---

### ABSTRACT FACTORY (auditoría: 8/10)

**Problema central:** existe una ruta legacy en `game.state.domain` que usa
`DungeonThemeFactory` en paralelo al flujo productivo de `GameSessionFactory`.
Falta tests de contrato explícito que verifiquen el mapeo `theme → fábrica correcta`
en runtime y que los productos (enemigos, loot) cumplen las propiedades del tema.

**Lo que hay que hacer en código:**

`src/main/java/game/state/domain/` (paquete legacy)
- Identificar todas las clases que instancian o usan `DungeonThemeFactory`
  o cualquiera de sus implementaciones (`FireThemeFactory`, etc.)
- Verificar si esas clases están activas desde `GameRuntime` o `GameSession`:
  buscar referencias hacia ellas desde el flujo principal
- Si no tienen referencias activas: eliminarlas
- Si tienen referencias activas: redirigirlas al flujo estándar de
  `GameSessionFactory` eliminando la duplicidad

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/creational/AbstractFactoryPatternTest.java` (o equivalente)
- Añadir test de mapeo `theme → fábrica` en runtime:
  para cada tema (`FIRE`, `ICE`, `POISON`, `DARK`) llamar a
  `GameSessionFactory.createSessionForTheme(tema)` e inspeccionar
  qué `DungeonThemeFactory` concreta se instanció
  (verificar tipo de la factory via el tipo de los enemigos producidos)

- Añadir test de contrato de productos por tema:
  `FireThemeFactory` → enemigo creado tiene resistencia al fuego > 0
  `IceThemeFactory` → enemigo creado tiene resistencia al hielo > 0
  `PoisonThemeFactory` → enemigo creado tiene resistencia al veneno > 0
  `DarkThemeFactory` → enemigo creado tiene resistencia a la oscuridad > 0

- Añadir test de contrato de loot por tema:
  `IceThemeFactory.createLoot()` → el loot resultante contiene al menos
  un ítem con tipo o nombre relacionado al tema de hielo

**Lo que hay que hacer en documentación:**

`docs/03-patterns/abstract-factory.md`
- Confirmar que las rutas legacy del paquete `game.state.domain` ya no existen
  (o documentar cuáles quedan y por qué si no se pudieron eliminar)
- Añadir sección de contratos: qué garantiza cada factory concreta
  (tipos de enemigos esperados, resistencias, tipos de loot)
- Actualizar diagrama con mapeo completo:
  `tema` → `GameSessionFactory` → `DungeonThemeFactory concreta`
  → `[enemigos, loot]` con sus propiedades garantizadas
- Actualizar rama y fecha

---

### MEMENTO (auditoría: 8/10)

**Problema central:** `GameOriginator` coexiste con `GameSessionMementoMapper`
como segundo flujo de Originator que puede divergir. `GameMemento` no tiene
campo de versión, lo que hace imposible detectar incompatibilidades de esquema
al cargar partidas de versiones anteriores.

**Lo que hay que hacer en código:**

`src/main/java/game/infrastructure/persistence/memento/GameOriginator.java`
- Verificar si tiene referencias activas desde `GameRuntime`, `GameSession`
  o cualquier use case productivo
- Si no tiene referencias: eliminar la clase
- Si tiene referencias: redirigirlas a `GameSessionMementoMapper` y
  eliminar `GameOriginator`

`src/main/java/game/application/state/GameMemento.java`
- Añadir campo `schemaVersion` (tipo `String`, valor actual `"1.0"`)
- Si es un `record`: añadir el campo al constructor del record

`src/main/java/game/application/state/GameSessionMementoMapper.java`
- Método `toMemento()`: incluir `schemaVersion = "1.0"` en el memento creado
- Método `fromMemento()`: leer `schemaVersion` del memento recibido;
  si el valor no es `"1.0"` (o la versión actual esperada),
  lanzar `SaveDataCorruptionException("Incompatible schema version: " + version)`

**Lo que hay que hacer en tests:**

`src/test/java/game/unit/behavioral/MementoPatternTest.java`
- Añadir test de esquema incompatible:
  crear memento con `schemaVersion = "0.9"` →
  llamar `fromMemento()` → verificar `SaveDataCorruptionException`
  con mensaje que incluya la versión recibida

- Añadir test de versión correcta:
  crear memento con `schemaVersion = "1.0"` →
  llamar `fromMemento()` → verificar que la restauración ocurre sin excepción

`src/test/java/game/unit/application/SaveLoadUseCaseTest.java`
- Añadir test de corrupción de datos:
  llamar `SaveGameUseCase.save(slot=1)` → obtener el memento del slot →
  corromper manualmente el campo `schemaVersion` →
  llamar `LoadGameUseCase.load(slot=1)` →
  verificar `SaveDataCorruptionException`

- Añadir test de slot vacío:
  llamar `LoadGameUseCase.load(slot=99)` sin guardar antes →
  verificar `SaveSlotNotFoundException`

**Lo que hay que hacer en documentación:**

`docs/03-patterns/memento.md`
- Añadir `GameSessionMementoMapper` como clase central del patrón
  (Originator real del sistema productivo)
- Añadir al inventario: `SessionSnapshotStore`, `SaveSlotNotFoundException`,
  `SaveDataCorruptionException`
- Documentar la validación de `schemaVersion` en `fromMemento()`
- Documentar `UseCaseTransactionSupport` como segundo uso de Memento
  (rollback de use cases)
- Confirmar que `GameOriginator` fue eliminado (o documentar su estado
  si no se pudo eliminar y por qué)
- Actualizar rama y fecha

---

## TABLA DE PROGRESO

Agregar esta sección al final de `docs/05-audit/AUDIT_PROGRESS.md`
al inicio de la primera ejecución de remediación:

```markdown
## Estado de remediación

| Patrón           | Calificación Auditoría | Estado           | Nueva Calificación |
|------------------|------------------------|------------------|--------------------|
| Facade           | 4                      | ⏳ Pendiente      | —                  |
| State            | 5                      | ⏳ Pendiente      | —                  |
| Command          | 6                      | ⏳ Pendiente      | —                  |
| Factory Method   | 6                      | ⏳ Pendiente      | —                  |
| Decorator        | 6                      | ⏳ Pendiente      | —                  |
| Composite        | 7                      | ⏳ Pendiente      | —                  |
| Observer         | 7                      | ⏳ Pendiente      | —                  |
| Strategy         | 7                      | ⏳ Pendiente      | —                  |
| Builder          | 8                      | ⏳ Pendiente      | —                  |
| Abstract Factory | 8                      | ⏳ Pendiente      | —                  |
| Memento          | 8                      | ⏳ Pendiente      | —                  |
```