package game.ui.integration;

import game.application.runtime.InvalidRuntimeCommandException;

/**
 * Excepcion de validacion para comandos UI invalidos.
 */
public class InvalidCommandException extends InvalidRuntimeCommandException {

    public InvalidCommandException(String message) {
        super(message);
    }
}