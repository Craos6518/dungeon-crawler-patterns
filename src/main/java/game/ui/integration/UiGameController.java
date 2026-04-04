package game.ui.integration;

import game.application.runtime.GameCommandHandler;
import game.application.runtime.GameRuntime;
import game.application.dto.UiCommand;
import game.ui.GameViewModel;

/**
 * Adaptador UI que delega toda la logica al runtime unico de aplicacion.
 */
public class UiGameController implements GameCommandHandler {

    private final GameRuntime runtime;

    public UiGameController() {
        this(new GameRuntime());
    }

    public UiGameController(GameRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public synchronized boolean supportsAction(String action) {
        return runtime.supportsAction(action);
    }

    @Override
    public synchronized void handleCommand(UiCommand command) {
        runtime.handleCommand(command);
    }

    @Override
    public synchronized GameViewModel presentViewModel() {
        return runtime.presentViewModel();
    }

    @Override
    public synchronized void registrarMensajeSistema(String mensaje) {
        runtime.registrarMensajeSistema(mensaje);
    }
}
