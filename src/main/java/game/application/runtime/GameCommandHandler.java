package game.application.runtime;

import game.application.dto.UiCommand;
import game.ui.GameViewModel;

/**
 * Puerto de aplicacion para procesar comandos de juego y exponer estado serializable.
 */
public interface GameCommandHandler {

    boolean supportsAction(String action);

    void handleCommand(UiCommand command);

    GameViewModel presentViewModel();

    void registrarMensajeSistema(String mensaje);
}