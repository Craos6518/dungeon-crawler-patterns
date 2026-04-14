const PROJECT_STATS = {
  tests: 203,
  classes: 160,
};

const PATTERNS = [
  {
    id: "abstract-factory",
    name: "Abstract Factory",
    category: "Creacional",
    problem:
      "Genera familias consistentes de contenido tematico de mazmorra sin mezclar reglas entre temas.",
    classes: [
      "game.dungeon.theme.DungeonThemeFactory",
      "game.dungeon.theme.FireThemeFactory",
      "game.dungeon.theme.IceThemeFactory",
      "game.dungeon.theme.DarkThemeFactory",
      "game.dungeon.theme.PoisonThemeFactory",
      "game.application.state.GameSessionFactory",
    ],
    miniDiagram:
      "GameSessionFactory -> DungeonThemeFactory -> [Fire, Ice, Dark, Poison]",
    tests: ["src/test/java/game/unit/creational/AbstractFactoryTest.java"],
    mermaid: `classDiagram
      class DungeonThemeFactory {
        <<interface>>
      }
      DungeonThemeFactory <|.. FireThemeFactory
      DungeonThemeFactory <|.. IceThemeFactory
      DungeonThemeFactory <|.. DarkThemeFactory
      DungeonThemeFactory <|.. PoisonThemeFactory
      GameSessionFactory --> DungeonThemeFactory : resolveThemeFactory()`,
  },
  {
    id: "builder",
    name: "Builder",
    category: "Creacional",
    problem:
      "Construye mazmorras procedurales paso a paso y reproducibles por seed.",
    classes: [
      "game.dungeon.builder.DungeonBuilder",
      "game.dungeon.builder.ConcreteDungeonBuilder",
      "game.dungeon.builder.ProceduralDungeonGenerator",
      "game.domain.exploration.Dungeon",
      "game.application.state.GameSessionFactory",
    ],
    miniDiagram:
      "GameSessionFactory -> Dungeon.fromTheme -> ProceduralDungeonGenerator -> DungeonBuilder",
    tests: [
      "src/test/java/game/unit/creational/BuilderPatternTest.java",
      "src/test/java/game/unit/creational/ProceduralDungeonSeedDeterminismTest.java",
    ],
    mermaid: `classDiagram
      GameSessionFactory --> Dungeon : fromTheme(...)
      Dungeon --> ProceduralDungeonGenerator : generar(...)
      ProceduralDungeonGenerator --> DungeonBuilder
      DungeonBuilder <|.. ConcreteDungeonBuilder`,
  },
  {
    id: "factory-method",
    name: "Factory Method",
    category: "Creacional",
    problem:
      "Crea heroes y enemigos sin acoplar la logica de negocio a constructores concretos.",
    classes: [
      "game.domain.personaje.factory.PersonajeFactory",
      "game.domain.personaje.factory.GuerreroFactory",
      "game.domain.personaje.factory.ArqueroFactory",
      "game.domain.personaje.factory.MagoFactory",
      "game.domain.personaje.factory.DragonFactory",
      "game.domain.personaje.factory.EnemigoBasicoFactory",
      "game.domain.personaje.factory.OrcoFactory",
    ],
    miniDiagram:
      "PersonajeFactory -> [Guerrero, Arquero, Mago, Dragon, EnemigoBasico, Orco]",
    tests: ["src/test/java/game/unit/creational/FactoryMethodTest.java"],
    mermaid: `classDiagram
      class PersonajeFactory {
        <<interface>>
        +crearPersonaje(nombre)
      }
      PersonajeFactory <|.. GuerreroFactory
      PersonajeFactory <|.. ArqueroFactory
      PersonajeFactory <|.. MagoFactory
      PersonajeFactory <|.. DragonFactory
      PersonajeFactory <|.. EnemigoBasicoFactory
      PersonajeFactory <|.. OrcoFactory
      GameSessionFactory --> PersonajeFactory`,
  },
  {
    id: "composite",
    name: "Composite",
    category: "Estructural",
    problem:
      "Representa inventario anidado para operar de forma uniforme sobre contenedores e items simples.",
    classes: [
      "game.items.model.ItemComponent",
      "game.items.model.ContainerItem",
      "game.items.model.SimpleItem",
      "game.domain.inventory.Inventory",
      "game.application.usecase.UseItemUseCase",
    ],
    miniDiagram: "Inventory -> ContainerItem(root) -> ItemComponent[Simple|Container]",
    tests: [
      "src/test/java/game/unit/structural/CompositePatternTest.java",
      "src/test/java/game/unit/application/UseItemUseCaseCompositeHierarchyTest.java",
    ],
    mermaid: `classDiagram
      ItemComponent <|-- ContainerItem
      ItemComponent <|-- SimpleItem
      Inventory --> ContainerItem : root
      UseItemUseCase --> Inventory`,
  },
  {
    id: "decorator",
    name: "Decorator",
    category: "Estructural",
    problem:
      "Aplica efectos de estado acumulables en combate sin inflar condicionales en el core.",
    classes: [
      "game.effects.status.CharacterDecorator",
      "game.effects.status.PoisonEffect",
      "game.effects.status.StrengthEffect",
      "game.effects.status.GuardEffect",
      "game.effects.status.BurnEffect",
      "game.effects.status.StunEffect",
      "game.domain.combat.CombatStatusDecoratorPipeline",
    ],
    miniDiagram:
      "Combat -> DecoratorPipeline -> CharacterDecorator -> [Poison|Strength|Guard|Burn|Stun]",
    tests: [
      "src/test/java/game/unit/structural/DecoratorPatternTest.java",
      "src/test/java/game/unit/domain/combat/CombatDecoratorIntegrationTest.java",
    ],
    mermaid: `classDiagram
      Combat --> CombatStatusDecoratorPipeline
      CombatStatusDecoratorPipeline --> CharacterDecorator
      CharacterDecorator <|-- PoisonEffect
      CharacterDecorator <|-- StrengthEffect
      CharacterDecorator <|-- GuardEffect
      CharacterDecorator <|-- BurnEffect
      CharacterDecorator <|-- StunEffect`,
  },
  {
    id: "facade",
    name: "Facade",
    category: "Estructural",
    problem:
      "Proporciona punto de entrada unico para combate, ocultando subsistemas internos (Combat, CombatSystem, TurnManager).",
    classes: [
      "game.patterns.combat.facade.CombatFacade",
      "game.domain.combat.Combat",
      "game.domain.combat.CombatSystem",
      "game.domain.turn.TurnManager",
      "game.domain.combat.CombatStatusDecoratorPipeline",
      "game.application.state.GameSession",
    ],
    miniDiagram:
      "GameRuntime -> GameSession.combat() -> CombatFacade -> [Combat|CombatSystem|TurnManager|DecoratorPipeline]",
    tests: [
      "src/test/java/game/unit/structural/FacadePatternTest.java",
      "src/test/java/game/integration/structural/FacadeIntegrationTest.java",
    ],
    mermaid: `flowchart LR
      Runtime[GameRuntime]
      Session[GameSession]
      Facade[CombatFacade]
      Combat[Combat]
      System[CombatSystem]
      Turn[TurnManager]
      Pipeline[CombatStatusDecoratorPipeline]
      
      Runtime --> Session
      Session --> Facade
      Facade --> Combat
      Combat --> System
      Combat --> Turn
      Combat --> Pipeline`,
  },
  {
    id: "command",
    name: "Command",
    category: "Comportamiento",
    problem:
      "Encapsula acciones jugables en comandos ejecutables, reversibles y validables.",
    classes: [
      "game.patterns.command.actions.Command",
      "game.patterns.command.actions.CommandInvoker",
      "game.patterns.command.actions.AttackCommand",
      "game.patterns.command.actions.DefendCommand",
      "game.patterns.command.actions.UseItemCommand",
      "game.patterns.command.actions.SkillCommand",
      "game.patterns.command.actions.LevelUpCommand",
    ],
    miniDiagram: "CommandInvoker -> Command -> [Attack|Defend|UseItem|Skill|LevelUp]",
    tests: ["src/test/java/game/unit/behavioral/CommandPatternTest.java"],
    mermaid: `classDiagram
      class Command {
        <<interface>>
        +execute()
        +undo()
        +canExecute() boolean
        +getDescription() String
      }
      Command <|.. AttackCommand
      Command <|.. DefendCommand
      Command <|.. UseItemCommand
      Command <|.. SkillCommand
      Command <|.. LevelUpCommand
      CommandInvoker --> Command : ejecutarComando()`,
  },
  {
    id: "observer",
    name: "Observer",
    category: "Comportamiento",
    problem:
      "Reacciona a eventos de juego desacoplados de emisores, con aislamiento garantizado por sesion (multi-sesion safe).",
    classes: [
      "game.infrastructure.events.observer.EventManager",
      "game.infrastructure.events.observer.EventContractValidator",
      "game.infrastructure.events.observer.CombatLogger",
      "game.infrastructure.events.observer.StatisticsTracker",
      "game.infrastructure.events.observer.UINotifier",
      "game.application.observer.SessionEventFeedObserver",
      "game.application.observer.SessionEventCounterObserver",
      "game.application.state.GameSessionFactory",
    ],
    miniDiagram:
      "GameSessionFactory -> EventManager (local) + Observers (con referencia a sesion) -> Eventos desacoplados",
    tests: [
      "src/test/java/game/unit/behavioral/ObserverPatternTest.java",
      "src/test/java/game/integration/behavioral/EventObserversRuntimeIntegrationTest.java",
    ],
    mermaid: `sequenceDiagram
      participant Factory as GameSessionFactory
      participant Manager as EventManager (Local)
      participant Session as GameSession
      participant Observer as SessionEventFeedObserver
      
      Factory->>Manager: new EventManager()
      Factory->>Session: new GameSession(manager)
      Factory->>Observer: new SessionEventFeedObserver(session)
      Factory->>Manager: suscribir(observer)
      
      Note over Session, Manager: Durante el juego
      Session->>Manager: notificar(GameEvent)
      Manager->>Observer: onEvent(event)`,
  },
  {
    id: "strategy",
    name: "Strategy",
    category: "Comportamiento",
    problem:
      "IA enemiga adaptativa (selecciona estrategia segun vida) y estilo tactico del jugador intercambiables en runtime.",
    classes: [
      "game.ai.strategy.AIStrategy",
      "game.ai.strategy.AggressiveStrategy",
      "game.ai.strategy.IntelligentStrategy",
      "game.ai.strategy.DefensiveStrategy",
      "game.ai.strategy.RandomStrategy",
      "game.domain.combat.CombatSystem",
      "game.domain.combat.PlayerCombatStyle",
      "game.application.usecase.SetCombatStyleUseCase",
    ],
    miniDiagram:
      "CombatSystem -> selectEnemyStrategy(hp%) -> [Aggressive|Intelligent|Defensive] + PlayerCombatStyle cambiable",
    tests: [
      "src/test/java/game/unit/behavioral/StrategyPatternTest.java",
      "src/test/java/game/unit/domain/combat/CombatSystemTest.java",
    ],
    mermaid: `classDiagram
      Combat --> CombatSystem : selectEnemyStrategy()
      CombatSystem --> AIStrategy : uses
      AIStrategy <|.. AggressiveStrategy
      AIStrategy <|.. DefensiveStrategy
      AIStrategy <|.. IntelligentStrategy
      AIStrategy <|.. RandomStrategy
      Combat --> PlayerCombatStyle : activeStyle
      SetCombatStyleUseCase --> Combat : setCombatStyle(key)`,
  },
  {
    id: "state",
    name: "State",
    category: "Comportamiento",
    problem:
      "Modela estados de juego y runtime para transiciones explicitas sin if/else masivos.",
    classes: [
      "game.state.game.GameState",
      "game.state.game.GameStateContext",
      "game.state.game.MenuState",
      "game.state.game.CombatState",
      "game.state.game.ExplorationState",
      "game.state.game.InventoryState",
      "game.state.game.GameOverState",
      "game.state.game.runtime.MenuRuntimeState",
      "game.state.game.runtime.AdventureRuntimeState",
      "game.state.game.runtime.SetupRuntimeState",
      "game.application.state.GameFlowState",
    ],
    miniDiagram:
      "GameStateContext -> GameState -> [Menu|Combat|Exploration|Inventory|GameOver|Runtime states]",
    tests: [
      "src/test/java/game/unit/behavioral/StatePatternTest.java",
      "src/test/java/game/integration/behavioral/GameRuntimeStateFlowIntegrationTest.java",
    ],
    mermaid: `classDiagram
      class GameState {
        <<interface>>
      }
      GameState <|.. MenuState
      GameState <|.. CombatState
      GameState <|.. ExplorationState
      GameState <|.. InventoryState
      GameState <|.. GameOverState
      GameState <|.. MenuRuntimeState
      GameState <|.. AdventureRuntimeState
      GameState <|.. SetupRuntimeState
      GameStateContext --> GameState : estadoActual`,
  },
  {
    id: "memento",
    name: "Memento",
    category: "Comportamiento",
    problem:
      "Persiste y restaura sesiones completas (progresion, inventario, combate activo) con validacion de versiones.",
    classes: [
      "game.application.state.GameMemento",
      "game.application.state.GameSessionMementoMapper",
      "game.infrastructure.persistence.memento.GameCaretaker",
      "game.application.usecase.SaveGameUseCase",
      "game.application.usecase.LoadGameUseCase",
      "game.application.usecase.UseCaseTransactionSupport",
    ],
    miniDiagram:
      "GameSessionMementoMapper (Originator) <-> GameMemento <- GameCaretaker | Transaccion rollback",
    tests: [
      "src/test/java/game/unit/behavioral/MementoPatternTest.java",
      "src/test/java/game/integration/behavioral/StateMementoIntegrationTest.java",
    ],
    mermaid: `graph TD
      A[GameRuntime] --> B(Save/Load UseCase)
      B --> C[GameSession]
      C -->|createSnapshot| D(GameSessionMementoMapper)
      D -->|builds| E[GameMemento v1.0]
      E -.->|persisted by| F[GameCaretaker]
      F -->|disk/memory| G[(Save Slots)]
      H[UseCaseTransactionSupport] -->|snapshot| C
      H -->|restore on error| C`,
  },
];

const grid = document.getElementById("pattern-grid");
const filters = Array.from(document.querySelectorAll(".filter"));
const modal = document.getElementById("pattern-modal");
const modalBackdrop = document.getElementById("modal-backdrop");
const modalClose = document.getElementById("modal-close");
const modalCategory = document.getElementById("modal-category");
const modalTitle = document.getElementById("modal-title");
const modalProblem = document.getElementById("modal-problem");
const modalClasses = document.getElementById("modal-classes");
const modalTests = document.getElementById("modal-tests");
const modalMermaid = document.getElementById("modal-mermaid");

let activeFilter = "all";
let mermaidInitialized = false;

function initStats() {
  document.getElementById("stat-patterns").textContent = String(PATTERNS.length);
  document.getElementById("stat-tests").textContent = String(PROJECT_STATS.tests);
  document.getElementById("stat-classes").textContent = String(PROJECT_STATS.classes);
}

function visiblePatterns() {
  if (activeFilter === "all") {
    return PATTERNS;
  }
  return PATTERNS.filter((pattern) => pattern.category === activeFilter);
}

function createList(items) {
  const fragment = document.createDocumentFragment();
  for (const value of items) {
    const li = document.createElement("li");
    li.textContent = value;
    fragment.appendChild(li);
  }
  return fragment;
}

function createCard(pattern, index) {
  const card = document.createElement("article");
  card.className = "pattern-card";
  card.style.animationDelay = `${index * 70}ms`;

  const badge = document.createElement("span");
  badge.className = `category-badge category-${pattern.category}`;
  badge.textContent = pattern.category;

  const title = document.createElement("h3");
  title.textContent = pattern.name;

  const problem = document.createElement("p");
  problem.className = "pattern-problem";
  problem.textContent = pattern.problem;

  const classList = document.createElement("ul");
  classList.className = "pattern-classes";
  classList.appendChild(createList(pattern.classes.slice(0, 3)));

  const mini = document.createElement("p");
  mini.className = "mini-diagram";
  mini.textContent = pattern.miniDiagram;

  const action = document.createElement("button");
  action.className = "card-action";
  action.type = "button";
  action.textContent = "Ver detalle";
  action.addEventListener("click", () => openModal(pattern));

  card.appendChild(badge);
  card.appendChild(title);
  card.appendChild(problem);
  card.appendChild(classList);
  card.appendChild(mini);
  card.appendChild(action);

  return card;
}

function renderCards() {
  grid.innerHTML = "";
  const data = visiblePatterns();

  if (data.length === 0) {
    const empty = document.createElement("p");
    empty.textContent = "No hay patrones para este filtro.";
    grid.appendChild(empty);
    return;
  }

  data.forEach((pattern, index) => {
    grid.appendChild(createCard(pattern, index));
  });
}

function activateFilter(nextFilter) {
  activeFilter = nextFilter;
  for (const button of filters) {
    button.classList.toggle("active", button.dataset.filter === activeFilter);
  }
  renderCards();
}

function ensureMermaidInitialized() {
  if (!window.mermaid || mermaidInitialized) {
    return;
  }

  window.mermaid.initialize({
    startOnLoad: false,
    theme: "base",
    securityLevel: "strict",
    themeVariables: {
      fontFamily: "Source Sans 3",
      primaryColor: "#2a2234",
      primaryTextColor: "#f5efe2",
      primaryBorderColor: "#d0a955",
      lineColor: "#d0a955",
      tertiaryColor: "#15111e",
    },
  });

  mermaidInitialized = true;
}

async function renderMermaid(diagramSource) {
  modalMermaid.innerHTML = "";

  if (!window.mermaid) {
    const fallback = document.createElement("pre");
    fallback.className = "mermaid-fallback";
    fallback.textContent = diagramSource;
    modalMermaid.appendChild(fallback);
    return;
  }

  ensureMermaidInitialized();

  const node = document.createElement("div");
  node.className = "mermaid";
  node.textContent = diagramSource;
  modalMermaid.appendChild(node);

  try {
    await window.mermaid.run({ nodes: [node] });
  } catch (error) {
    modalMermaid.innerHTML = "";
    const fallback = document.createElement("pre");
    fallback.className = "mermaid-fallback";
    fallback.textContent = diagramSource;
    modalMermaid.appendChild(fallback);
  }
}

function openModal(pattern) {
  modalCategory.textContent = pattern.category;
  modalTitle.textContent = pattern.name;
  modalProblem.textContent = pattern.problem;

  modalClasses.innerHTML = "";
  modalClasses.appendChild(createList(pattern.classes));

  modalTests.innerHTML = "";
  modalTests.appendChild(createList(pattern.tests));

  modal.classList.add("visible");
  modal.setAttribute("aria-hidden", "false");
  document.body.classList.add("modal-open");

  renderMermaid(pattern.mermaid);
}

function closeModal() {
  modal.classList.remove("visible");
  modal.setAttribute("aria-hidden", "true");
  modalMermaid.innerHTML = "";
  document.body.classList.remove("modal-open");
}

function bindEvents() {
  filters.forEach((button) => {
    button.addEventListener("click", () => {
      activateFilter(button.dataset.filter || "all");
    });
  });

  modalClose.addEventListener("click", closeModal);
  modalBackdrop.addEventListener("click", closeModal);

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && modal.classList.contains("visible")) {
      closeModal();
    }
  });
}

initStats();
bindEvents();
renderCards();
