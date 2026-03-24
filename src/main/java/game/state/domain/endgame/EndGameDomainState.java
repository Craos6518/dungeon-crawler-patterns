package game.state.domain.endgame;

import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import game.state.domain.AbstractDomainGameState;

/**
 * Estado de dominio que encapsula la lógica de fin de juego (Game Over).
 * Responsable de:
 * - Mostrar opciones al jugador cuando muere
 * - Permitir cargar desde checkpoint
 * - Permitir volver al menú
 * - Permitir iniciar nueva partida
 * 
 * Completamente independiente y reutilizable.
 */
public class EndGameDomainState extends AbstractDomainGameState {
    
    /**
     * Callback cuando el jugador elige volver al menú
     */
    public interface MenuCallback {
        void volverAlMenu();
    }
    
    /**
     * Callback cuando el jugador elige nueva partida
     */
    public interface NewGameCallback {
        void iniciarNuevaPartida();
    }
    
    /**
     * Callback cuando el jugador restaura desde checkpoint
     */
    public interface CheckpointRestoreCallback {
        void restaurarCheckpoint();
    }
    
    private final MenuCallback menuCallback;
    private final NewGameCallback newGameCallback;
    private final CheckpointRestoreCallback checkpointCallback;
    
    public EndGameDomainState(
        MenuCallback menuCallback,
        NewGameCallback newGameCallback,
        CheckpointRestoreCallback checkpointCallback
    ) {
        this.menuCallback = menuCallback;
        this.newGameCallback = newGameCallback;
        this.checkpointCallback = checkpointCallback;
    }
    
    @Override
    public boolean ejecutar() {
        // Los opciones de game over se ejecutan inmediatamente después de derrota
        // Este método no se llama directamente
        return true;
    }
    
    @Override
    public String getNombreEstado() {
        return "EndGame";
    }
    
    /**
     * Muestra las opciones de game over y maneja la elección del usuario
     * @param caretaker Para acceso a checkpoints
     * @param originator Para restauración de sesión
     * @return true si el jugador quiere continuar jugando, false si quiere salir
     */
    public boolean mostrarOpcionesGameOver(GameCaretaker caretaker, GameOriginator originator) {
        System.out.println("\nOpciones:");
        System.out.println("1. [R] Cargar último checkpoint en memoria");
        System.out.println("2. [M] Volver al menú principal");
        System.out.println("3. [N] Nueva partida");

        int opcion = leerOpcion(1, 3);
        switch (opcion) {
            case 1 -> {
                if (caretaker.getCantidadMementos() == 0) {
                    System.out.println("No hay checkpoint en memoria para restaurar.");
                    return false;
                }
                GameMemento ultimo = caretaker.obtenerUltimoMemento();
                originator.restaurar(ultimo);
                checkpointCallback.restaurarCheckpoint();
                System.out.println("✅ Checkpoint restaurado. Continuas en exploración.");
                return true;
            }
            case 2 -> {
                System.out.println("Regresando al menú principal...");
                menuCallback.volverAlMenu();
                return false;
            }
            case 3 -> {
                System.out.println("Iniciando nueva partida...");
                newGameCallback.iniciarNuevaPartida();
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
