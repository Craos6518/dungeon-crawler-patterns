package game;

import game.application.ports.GameInputPort;
import game.application.ports.GameOutputPort;
import game.application.runtime.GameRuntime;
import game.ui.console.ConsoleGameAdapter;
import game.ui.console.StdConsoleInputPort;
import game.ui.console.StdConsoleOutputPort;

/**
 * Adaptador de consola legado.
 *
 * La logica de juego vive en GameRuntime y esta clase solo conecta puertos de IO.
 */
public class InteractiveGame {

    private final ConsoleGameAdapter consoleAdapter;

    public InteractiveGame() {
        this(new GameRuntime(), new StdConsoleInputPort(), new StdConsoleOutputPort());
    }

    public InteractiveGame(GameRuntime runtime, GameInputPort input, GameOutputPort output) {
        this.consoleAdapter = new ConsoleGameAdapter(runtime, input, output);
    }

    public void iniciar() {
        consoleAdapter.run();
    }
}
