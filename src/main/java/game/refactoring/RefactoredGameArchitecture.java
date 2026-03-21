package game.refactoring;

import game.domain.personaje.Personaje;
import game.dungeon.model.Dungeon;
import game.dungeon.theme.DungeonThemeFactory;
import game.events.observer.EventManager;
import game.events.observer.StatisticsTracker;
import game.items.model.ContainerItem;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameOriginator;
import game.state.domain.GameSessionData;
import game.state.domain.exploration.ExplorationDomainState;
import game.state.domain.combat.CombatDomainState;
import game.state.domain.setup.SetupDomainState;
import game.state.domain.endgame.EndGameDomainState;
import game.command.actions.CommandInvoker;

import java.util.Random;
import java.util.Scanner;

/**
 * Demostración de cómo InteractiveGame podría ser refactorizado
 * usando los nuevos estados de dominio.
 * 
 * Esta clase muestra la arquitectura mejorada donde la lógica procedimental
 * está encapsulada en estados reutilizables para cualquier interfaz de usuario.
 */
public class RefactoredGameArchitecture {
    
    private final Scanner scanner;
    private final Random random;
    private final EventManager eventManager;
    private final StatisticsTracker statistics;
    private CommandInvoker commandInvoker;
    private final GameOriginator originator;
    private GameCaretaker caretaker;
    
    // Estado compartido de la sesión
    private GameSessionData sessionData;
    
    // Estados de dominio
    private SetupDomainState setupState;
    private ExplorationDomainState explorationState;
    private CombatDomainState combatState;
    private EndGameDomainState endGameState;
    
    public RefactoredGameArchitecture() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.eventManager = EventManager.getInstance();
        this.statistics = new StatisticsTracker();
        this.commandInvoker = new CommandInvoker();
        this.originator = new GameOriginator("Jugador");
        this.caretaker = new GameCaretaker("./game-saves/");
        this.sessionData = new GameSessionData();
        
        initializeStates();
    }
    
    private void initializeStates() {
        // SetupState - configuración inicial
        setupState = new SetupDomainState(sessionData, this::onSetupComplete);
        setupState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);
        
        // ExplorationState - exploración de mazmorra
        explorationState = new ExplorationDomainState(
            sessionData,
            random,
            this::onInitiateCombat,
            this::onVictory
        );
        explorationState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);
        
        // CombatState - combate
        combatState = new CombatDomainState(
            sessionData,
            random,
            statistics,
            this::onDeath,
            this::onTreasure,
            this::onSaveCheckpoint
        );
        combatState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);
        
        // EndGameState - Game Over
        endGameState = new EndGameDomainState(
            this::onReturnToMenu,
            this::onNewGame,
            this::onRestoreCheckpoint
        );
        endGameState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);
    }
    
    // Callbacks de SetupState
    private void onSetupComplete() {
        System.out.println("Setup completado. Iniciando exploración...");
    }
    
    // Callbacks de ExplorationState
    private void onInitiateCombat(Personaje enemigo, boolean esJefe) {
        combatState.iniciarCombate(enemigo, esJefe);
    }
    
    private void onVictory() {
        System.out.println("\n🎊 ¡¡¡VICTORIA!!! 🎊");
        System.out.println("Has completado la mazmorra: " + sessionData.getMazmorra().getNombre());
    }
    
    // Callbacks de CombatState
    private void onDeath() {
        endGameState.mostrarOpcionesGameOver(caretaker, originator);
    }
    
    private void onTreasure() {
        SimpleItem loot = random.nextBoolean() 
            ? sessionData.getTemaActual().crearTesoroRaro()
            : sessionData.getTemaActual().crearTesoroComun();
        sessionData.getInventario().agregar(loot);
        sessionData.setOroAcumulado(sessionData.getOroAcumulado() + loot.getValorTotal());
        
        System.out.println("\n🏆 SALA DE TESORO");
        System.out.println("Loot: " + loot.getNombre() + " (valor: " + loot.getValorTotal() + ")");
    }
    
    private void onSaveCheckpoint() {
        System.out.println("✓ Checkpoint automático guardado");
    }
    
    // Callbacks de EndGameState
    private void onReturnToMenu() {
        System.out.println("Regresando al menú principal...");
    }
    
    private void onNewGame() {
        System.out.println("Iniciando nueva partida...");
        sessionData = new GameSessionData();
        initializeStates();
    }
    
    private void onRestoreCheckpoint() {
        System.out.println("✅ Checkpoint restaurado. Continuando...");
    }
    
    /**
     * Ejemplo de flujo de juego usando estados de dominio
     */
    public void runGameFlow() {
        System.out.println("🎮 DUNGEON CRAWLER - Versión Refactorizada con Estados de Dominio");
        
        // 1. Setup
        if (!setupState.ejecutar()) {
            return; // Usuario canceló
        }
        
        // 2. Exploración
        boolean continuarExploracion = explorationState.ejecutar();
        
        // El flujo es manejado por los callbacks y estados internos
        System.out.println("Fin de la sesión de juego.");
    }
    
    public static void main(String[] args) {
        RefactoredGameArchitecture game = new RefactoredGameArchitecture();
        game.runGameFlow();
    }
}
