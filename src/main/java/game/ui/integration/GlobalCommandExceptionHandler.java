package game.ui.integration;

import game.application.dto.UiCommandResponse;
import game.application.runtime.InvalidRuntimeCommandException;
import game.domain.DomainRuleViolationException;
import game.ui.GameViewModel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manejo centralizado de errores de comandos UI.
 */
public class GlobalCommandExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalCommandExceptionHandler.class.getName());

    public UiCommandResponse handle(String action, RuntimeException ex, GameViewModel currentState) {
        String safeMessage;

        if (ex instanceof InvalidCommandException
            || ex instanceof InvalidRuntimeCommandException
            || ex instanceof DomainRuleViolationException) {
            safeMessage = ex.getMessage();
            LOGGER.log(Level.INFO, "Comando rechazado. action={0}, reason={1}", new Object[]{action, safeMessage});
        } else {
            safeMessage = "Error interno procesando comando.";
            LOGGER.log(Level.SEVERE, "Fallo inesperado ejecutando action=" + action, ex);
        }

        return UiCommandResponse.error(safeMessage, currentState);
    }
}