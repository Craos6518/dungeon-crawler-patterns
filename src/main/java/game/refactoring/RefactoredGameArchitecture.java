package game.refactoring;

import game.command.actions.CommandInvoker;
import game.domain.personaje.Personaje;
import game.events.observer.EventManager;
import game.events.observer.StatisticsTracker;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import game.state.domain.GameSessionData;
import game.state.domain.combat.CombatDomainState;
import game.state.domain.endgame.EndGameDomainState;
import game.state.domain.exploration.ExplorationDomainState;
import game.state.domain.setup.SetupDomainState;

import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Runtime principal del juego basado en DomainStates.
 *
 * La logica de gameplay queda encapsulada en estados de dominio reutilizables,
 * mientras esta clase solo orquesta el flujo de alto nivel en consola.
 */
public class RefactoredGameArchitecture {

    private final Scanner scanner;
    private final Random random;
    private final EventManager eventManager;
    private final StatisticsTracker statistics;
    private CommandInvoker commandInvoker;
    private final GameOriginator originator;
    private GameCaretaker caretaker;

    // Estado compartido de la sesion
    private GameSessionData sessionData;

    // Estados de dominio
    private SetupDomainState setupState;
    private ExplorationDomainState explorationState;
    private CombatDomainState combatState;
    private EndGameDomainState endGameState;

    // Control de flujo
    private boolean juegoActivo;
    private boolean solicitarNuevaPartida;
    private boolean solicitarVolverMenu;
    private boolean solicitarReanudarExploracion;

    public RefactoredGameArchitecture() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.eventManager = EventManager.getInstance();
        this.statistics = new StatisticsTracker();
        this.commandInvoker = new CommandInvoker();
        this.originator = new GameOriginator("Jugador");
        this.caretaker = new GameCaretaker("./game-saves/");
        this.sessionData = new GameSessionData();

        this.juegoActivo = true;
        this.solicitarNuevaPartida = false;
        this.solicitarVolverMenu = false;
        this.solicitarReanudarExploracion = false;

        initializeStates();
    }

    private void initializeStates() {
        setupState = new SetupDomainState(sessionData, this::onSetupComplete);
        setupState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);

        explorationState = new ExplorationDomainState(
            sessionData,
            random,
            this::onInitiateCombat,
            this::onVictory
        );
        explorationState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);

        combatState = new CombatDomainState(
            sessionData,
            random,
            statistics,
            this::onDeath,
            this::onTreasure,
            this::onSaveCheckpoint
        );
        combatState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);

        endGameState = new EndGameDomainState(
            this::onReturnToMenu,
            this::onNewGame,
            this::onRestoreCheckpoint
        );
        endGameState.inyectarDependencias(scanner, eventManager, commandInvoker, originator, caretaker);
    }

    private void resetSessionForNewRun() {
        this.sessionData = new GameSessionData();
        this.commandInvoker = new CommandInvoker();
        initializeStates();
    }

    // Callbacks de SetupState
    private void onSetupComplete() {
        System.out.println("Setup completado. Iniciando exploracion...");
    }

    // Callbacks de ExplorationState
    private void onInitiateCombat(Personaje enemigo, boolean esJefe) {
        combatState.iniciarCombate(enemigo, esJefe);
    }

    private void onVictory() {
        System.out.println("\n🎊 ¡¡¡VICTORIA!!! 🎊");
        System.out.println("Has completado la mazmorra: " + sessionData.getMazmorra().getNombre());
        System.out.println("Regresando al menu principal...");
        solicitarVolverMenu = true;
    }

    // Callbacks de CombatState
    private void onDeath() {
        solicitarNuevaPartida = false;
        solicitarVolverMenu = false;
        solicitarReanudarExploracion = false;

        boolean continuar = endGameState.mostrarOpcionesGameOver(caretaker, originator);
        if (!continuar && !solicitarNuevaPartida && !solicitarVolverMenu) {
            solicitarVolverMenu = true;
        }
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
        GameMemento checkpoint = crearMementoSesion();
        caretaker.guardarEnMemoria(checkpoint);

        try {
            caretaker.guardarEnDisco(checkpoint, "checkpoint-auto");
        } catch (RuntimeException ex) {
            System.out.println("⚠️  No se pudo guardar checkpoint en disco: " + ex.getMessage());
        }

        System.out.println("✓ Checkpoint automatico guardado");
    }

    // Callbacks de EndGameState
    private void onReturnToMenu() {
        solicitarVolverMenu = true;
    }

    private void onNewGame() {
        solicitarNuevaPartida = true;
    }

    private void onRestoreCheckpoint() {
        try {
            GameMemento checkpoint = caretaker.obtenerUltimoMemento();
            restaurarSesionDesdeMemento(checkpoint);
            solicitarReanudarExploracion = true;
            System.out.println("✅ Checkpoint restaurado. Continuando...");
        } catch (RuntimeException ex) {
            System.out.println("❌ No se pudo restaurar el checkpoint: " + ex.getMessage());
        }
    }

    private GameMemento crearMementoSesion() {
        int salaGuardada = sessionData.getSalaActual() + 1;
        int hpHeroe = sessionData.getHeroe().getVida();
        int experienciaHeroe = sessionData.getHeroe().getExperiencia();

        return new GameMemento.Builder()
            .nombreJugador(sessionData.getHeroe().getNombre())
            .nivelActual(sessionData.getHeroe().getNivel())
            .salaActual(salaGuardada)
            .agregarEstadoPersonaje("vida", hpHeroe)
            .agregarEstadoPersonaje("experiencia", experienciaHeroe)
            .agregarEstadoPersonaje("nivel", sessionData.getHeroe().getNivel())
            .agregarEstadoMazmorra("salaActualIndex", sessionData.getSalaActual())
            .agregarEstadoMazmorra("oro", sessionData.getOroAcumulado())
            .agregarEstadoMazmorra("enemigosDerrota", sessionData.getEnemigosDerrota())
            .agregarEstadoMazmorra("estadoActual", "Exploracion")
            .build();
    }

    private void restaurarSesionDesdeMemento(GameMemento memento) {
        originator.restaurar(memento);

        Personaje heroe = sessionData.getHeroe();
        if (heroe == null) {
            return;
        }

        int nivelGuardado = memento.getNivelActual();
        while (heroe.getNivel() < nivelGuardado) {
            heroe.subirNivel();
        }

        Map<String, Object> estadoPersonaje = memento.getEstadoPersonaje();
        int hpGuardado = valorEntero(estadoPersonaje, "vida", heroe.getVida());
        int expGuardada = valorEntero(estadoPersonaje, "experiencia", 0);

        heroe.ganarExperiencia(-heroe.getExperiencia());
        heroe.ganarExperiencia(expGuardada);

        heroe.curar(Integer.MAX_VALUE);
        int hpActual = heroe.getVida();
        if (hpActual > hpGuardado) {
            heroe.recibirDanio(hpActual - hpGuardado);
        }

        Map<String, Object> estadoMazmorra = memento.getEstadoMazmorra();
        int salaGuardada = valorEntero(estadoMazmorra, "salaActualIndex", Math.max(0, memento.getSalaActual() - 1));
        int totalSalas = sessionData.getMazmorra() != null ? sessionData.getMazmorra().getSalas().size() : 1;
        int salaFinal = Math.min(Math.max(0, salaGuardada), Math.max(0, totalSalas - 1));

        sessionData.setSalaActual(salaFinal);
        sessionData.setOroAcumulado(valorEntero(estadoMazmorra, "oro", sessionData.getOroAcumulado()));
        sessionData.setEnemigosDerrota(valorEntero(estadoMazmorra, "enemigosDerrota", sessionData.getEnemigosDerrota()));
        sessionData.setTurnosVenenoHeroe(0);
        sessionData.setDanioVenenoHeroe(0);
        sessionData.setDefensaHeroeActiva(false);
    }

    private int valorEntero(Map<String, Object> mapa, String clave, int porDefecto) {
        Object valor = mapa.get(clave);
        if (valor instanceof Number n) {
            return n.intValue();
        }
        return porDefecto;
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 DUNGEON CRAWLER - DomainStates Runtime");
        System.out.println("=".repeat(60));
        System.out.println("1. Nueva partida");
        System.out.println("2. Salir");
    }

    private int leerOpcion(int min, int max) {
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                return max;
            }
            String input = scanner.nextLine().trim();
            try {
                int opcion = Integer.parseInt(input);
                if (opcion >= min && opcion <= max) {
                    return opcion;
                }
                System.out.println("❌ Opcion invalida. Elige entre " + min + " y " + max);
            } catch (NumberFormatException ex) {
                System.out.println("❌ Entrada invalida. Ingresa un numero.");
            }
        }
    }

    public void runGameFlow() {
        while (juegoActivo) {
            mostrarMenuPrincipal();
            int opcionMenu = leerOpcion(1, 2);

            if (opcionMenu == 2) {
                juegoActivo = false;
                break;
            }

            resetSessionForNewRun();
            if (!setupState.ejecutar()) {
                continue;
            }

            solicitarNuevaPartida = false;
            solicitarVolverMenu = false;
            solicitarReanudarExploracion = false;

            while (juegoActivo) {
                boolean exploro = explorationState.ejecutar();

                if (solicitarNuevaPartida) {
                    solicitarNuevaPartida = false;
                    break;
                }

                if (solicitarVolverMenu) {
                    solicitarVolverMenu = false;
                    break;
                }

                if (solicitarReanudarExploracion) {
                    solicitarReanudarExploracion = false;
                    continue;
                }

                if (!exploro) {
                    break;
                }

                // Si exploration devolvio true significa victoria y regreso al menu.
                break;
            }
        }

        System.out.println("\nFin de la sesion de juego.");
        scanner.close();
    }

    public static void main(String[] args) {
        RefactoredGameArchitecture game = new RefactoredGameArchitecture();
        game.runGameFlow();
    }
}
